package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dao.CarrierPaymentParameterDAO;
import com.jbhunt.finance.carrierpayment.autopay.dto.*;
import com.jbhunt.finance.carrierpayment.autopay.entity.*;
import com.jbhunt.finance.carrierpayment.autopay.repository.*;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ActivityUtil;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.CalcAmountUtil;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargePredicateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.INVOICE_STATUS_REJECTED;
import static com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants.ICS;
import static com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants.JBI;
import static com.jbhunt.finance.carrierpayment.autopay.util.payment.CarrierPaymentPredicateUtil.*;
import static com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargeCommonUtil.isNullOrEmpty;


@Slf4j
@Service
public class AutoPayService {


    @Autowired
    private PaymentStateService paymentStateService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ChargeDecisionService chargeDecisionService;

    @Autowired
    private AutoPayEditsService autoPayEditsService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    CarrierPaymentProcessEventRepository carrierPaymentProcessEventRepository;

    @Autowired
    PaymentStateLogRepository stateLogRepository;

    @Autowired
    private ElasticModificationService elasticModificationService;

    @Autowired
    private CarrierPaymentParameterDAO carrierPaymentParameterDAO;

    @Autowired
    private JBIAutoPayService jbiAutoPayService;

    @Autowired
    private AutopayValidationService autopayValidationService;
    @Autowired
    private AutopayChargeStatusRules autopayChargeStatusRules;
    @Autowired
    private PaymentShipmentRepository paymentShipmentRepository;
    @Autowired
    private ProducerTemplate producerTemplate;
    @Autowired
    private PaymentHelperService paymentHelperService;
    @Autowired
    private LockUserService lockUserService;
    @Autowired
    private SupplierService supplierService;

    public boolean checkRulesForAutopay(Payment payment, List<CarrierInvoiceDetail> invoiceDetail, List<ParameterListingDTO> parameterListings, LocalDateTime scanTimestamp) {
        log.info("INSIDE checkRulesForAutopay : ");
        AtomicBoolean doAutoPay = new AtomicBoolean(false);
        //Auto Pay New WS Changes Start
        // Auto Pay Archived load changes Start
        if (isLoadArchived(payment)) {
            return doAutoPay.get();
        }
        // Auto Pay Archived load changes End
        // Bill To Parameter validation
        if (autopayValidationService.fetchBillToCodes(payment)) {
            return doAutoPay.get();
        }
        //If Supplier ID is not present in Workday then No Auto Pay - Starts
        if (!supplierIDIsPresent(payment)) {
            return doAutoPay.get();
        }
        //If dispatch miles are zero/null then No Auto Pay
        if (autopayValidationService.zeroMileDispatchValidation(payment)) {
            return doAutoPay.get();
        }
        //Aging start date check
        if (payment.getGroupFlowTypeCode().equalsIgnoreCase(ICS) && !autopayValidationService.validateCalculatedInvoiceDate(payment, scanTimestamp)) {
            return doAutoPay.get();
        }
        //If Supplier ID is not present in Workday then No Auto Pay - End
        //Check for not being JBI - JBI changes
        if (!payment.getGroupFlowTypeCode().equalsIgnoreCase(JBI) && checkIfTheInvoiceAlreadyPresentAndProcessed(payment, invoiceDetail, invoiceRepository)) {
            return doAutoPay.get();
        }
        BigDecimal thresholdAmount = getThresholdAmount(parameterListings);
        BigDecimal totalInvoiceAmount = invoiceDetail.stream()
                .map(CarrierInvoiceDetail::getCarrierChargeUnitRateAmount).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("AUTOPAY TOTAL INVOICE AMOUNT IS :: " + totalInvoiceAmount);
        if (!fetchParameterForAutoApprove(payment, doAutoPay, thresholdAmount)) {
            return doAutoPay.get();
        }
        if (!validateActionedChargeList(payment)) {
            return doAutoPay.get();
        }
        if (autopayChargeStatusRules.validateBookedAndInvoiceAmountsVarianceForLTL(payment)) {
            return doAutoPay.get();
        }
        log.info(" Eligible for AutoPay after checking that load doesn't have any approved/rejected Chargecode for LTL and" +
                " and load doesn't have any rejected code for FTL  -------  Approved/Rejected Charge code Check ");
        // New Change for FTL - if FTL has any Duplicate Charge Code at Tendered or at Invoice Side then No Auto Pay - Starts
        // New Change for LTL - if LTL has any Duplicate Charge Code at Tendered then No Auto Pay - Starts
        List<ParameterListingDTO> parameterAAprChgGrp = getRequiredParameterList(parameterListings, CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGEGROUP);
        if (!payment.getGroupFlowTypeCode().equalsIgnoreCase(JBI) && !validateDuplicateInvoiceCharge(payment, invoiceDetail, parameterAAprChgGrp)) {
            return doAutoPay.get();
        }
        // New Change for FTL - if FTL has any Duplicate Charge Code at Tendered or at Invoice Side then No Auto Pay - Ends
        // New Change for LTL - if LTL has any Duplicate Charge Code at Tendered then No Auto Pay - Ends
        //  New Change for LTL - if LTL has any Duplicate Accessorial Charge Code at Invoice Side then No Auto Pay - Ends
        // ONLY CHARGES that are NOT ACTIONED
        BigDecimal totalChargesAmount = CalcAmountUtil.calculateChargeAmountForAutoPay(payment);
        log.info("AUTOPAY TOTAL CHARGE AMOUNT IS :: " + totalChargesAmount);
        ParameterListingDTO parameterAutoAprTolerance = getRequiredParameter(parameterListings, CarrierPaymentConstants.SPECIFICATION_AUTOPAY_TOLERANCE);
        if (!validateToleranceAmuontOnTotalAmmount(parameterAutoAprTolerance, totalChargesAmount)) {
            return doAutoPay.get();
        }
        log.info("AUTOPAY MIN TOLERANCE IS :: " + parameterAutoAprTolerance.getMinNumberValue());
        log.info("AUTOPAY MAX TOLERANCE IS :: " + parameterAutoAprTolerance.getMaxNumberValue());
        BigDecimal totalChargesAmountMax = totalChargesAmount.add(parameterAutoAprTolerance.getMaxNumberValue());
        BigDecimal totalChargesAmountMin = totalChargesAmount.add(parameterAutoAprTolerance.getMinNumberValue());
        log.info("TotalChargesAmountMax IS :: " + totalChargesAmountMax);
        log.info("TotalChargesAmountMin IS :: " + totalChargesAmountMin);
        if (!validateIfInvoiceAmountIsNullOrLessThenThreshold(totalInvoiceAmount, thresholdAmount, payment)) {
            return doAutoPay.get();
        }
        log.info(" Eligible for AutoPay after  ------  TotalThreshold Amount Check ");

        //Check for not being JBI - JBI changes
        if (!payment.getGroupFlowTypeCode().equalsIgnoreCase(JBI) && !validateChargeAmountsGreaterAndLesser(totalInvoiceAmount, totalChargesAmountMax, totalChargesAmountMin, payment)) {
            return doAutoPay.get();
        }
        log.info(" Eligible for AutoPay after First Check ------  Range Tolerance Check ");
        if (!checkPercentageThreshold(payment, totalChargesAmount, totalInvoiceAmount, parameterListings)) {
            return doAutoPay.get();
        }
        log.info(" Eligible for AutoPay after Second Check ----- CheckPercentageThreshold ");
        if (!checkExactListMatch(payment, invoiceDetail, parameterAAprChgGrp)) {
            return doAutoPay.get();
        }
        log.info(" Eligible for AutoPay after Third Check ------  CheckExactListMatch ");
        if (checkChargeThreshold(payment, invoiceDetail, parameterAAprChgGrp, parameterListings)) {
            log.info(" Eligible for AutoPay after Fourth and Fifth Check ------  checkChargeThreshold ");
            doAutoPay.set(true);
            log.info("AutoPay Eligible");
        }
        return doAutoPay.get();
    }

    // Auto Pay Archived load changes Start
    private boolean isLoadArchived(Payment payment) {
        if (chargeDecisionService.getCountOfRecords(payment.getLoadNumber()) == 0) {
            log.info(" AutoPay failed reason is -------  " + CarrierPaymentConstants.ACT_AUTOPAY_FAIL_LDARCHIVED + " ---- as Load is Archived ");
            logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_LDARCHIVED);
            return true;
        } else {
            return false;
        }
    }
    // Auto Pay Archived load changes End

    private boolean supplierIDIsPresent(Payment payment) {
        var supplier = supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(payment.getScacCode(), payment.getCurrencyCode());
        if (null != supplier && null != supplier.getSupplierID()) {
            return true;
        } else {
            log.info(" AutoPay not Eligible ");
            log.info(" AutoPay failed -------  " + CarrierPaymentConstants.ACT_AUTOPAY_FAIL_SUPPNOTWD + " ---- as Supplier ID is not present in Workday ");
            logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_SUPPNOTWD);
            return false;
        }
    }


    private boolean checkIfTheInvoiceAlreadyPresentAndProcessed(Payment payment, List<CarrierInvoiceDetail> invoiceDetail, InvoiceRepository invoiceRepository) {
        Optional<CarrierInvoiceDetail> carrierInvoiceDetail = invoiceDetail.stream().findFirst();
        if (carrierInvoiceDetail.isPresent()) {
            CarrierInvoiceDetail invoiceDetailObject = carrierInvoiceDetail.get();
            CarrierInvoiceHeader carrierInvoiceHeader = invoiceDetailObject.getCarrierInvoiceHeader();
            if (carrierInvoiceHeader != null) {
                Integer approvedInvCount = invoiceRepository.retrieveApprovedIvcByScacAndCarrierInvoiceNumberExtended(carrierInvoiceHeader.getScacCode(),
                        carrierInvoiceHeader.getCarrierInvoiceNumber());
                if (approvedInvCount > 0) {
                    log.info("AutoPay not Eligible The Invoice Number " + carrierInvoiceHeader.getCarrierInvoiceNumber() + " has already been used by this Carrier");
                    logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_DUPLICATE_INVOICE);
                    return true;
                }
            }
        }
        return false;
    }

    private BigDecimal getThresholdAmount(List<ParameterListingDTO> parameterListings) {
        BigDecimal thresholdAmount = null;
        for (ParameterListingDTO parameterListing : parameterListings) {
            if (null != parameterListing.getParameterSpecificationTypeCode() &&
                    parameterListing.getParameterSpecificationTypeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_LMT)) {
                thresholdAmount = parameterListing.getMinNumberValue();
                break;
            }
        }
        return thresholdAmount;
    }

    public List<ParameterListingDTO> getRequiredParameterList(List<ParameterListingDTO> parameterListings, String constantToCheck) {
        List<ParameterListingDTO> parameterListingDTOList = new ArrayList<>();
        for (ParameterListingDTO parameterListing : parameterListings) {
            if (null != parameterListing.getParameterSpecificationTypeCode() &&
                    parameterListing.getParameterSpecificationTypeCode().trim().equalsIgnoreCase(constantToCheck)) {
                parameterListingDTOList.add(parameterListing);
            }
        }
        return parameterListingDTOList;
    }

    private ParameterListingDTO getRequiredParameter(List<ParameterListingDTO> parameterListings, String constantToCheck) {
        ParameterListingDTO parameterListingDTO = new ParameterListingDTO();
        for (ParameterListingDTO parameterListing : parameterListings) {
            if (null != parameterListing.getParameterSpecificationTypeCode() &&
                    parameterListing.getParameterSpecificationTypeCode().trim().equalsIgnoreCase(constantToCheck)) {
                parameterListingDTO = parameterListing;
                break;
            }
        }
        return parameterListingDTO;
    }

// Auto Pay New WS Changes End

    private boolean fetchParameterForAutoApprove(Payment payment, AtomicBoolean doAutoPay, BigDecimal thresholdAmount) {
        if (null != thresholdAmount) {
            return true;
        } else {
            doAutoPay.set(false);
            log.info(" Logging AutoPay failure -------  " + CarrierPaymentConstants.ACT_AUTOPAY_FAIL_OTHER + " ---- UnKnown Error ");
            logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_OTHER);
            return false;
        }
    }

    public void acknowledgeInvoicesForAutoPay(Optional<Payment> payment) {
        if (payment.isPresent()) {
            List<CarrierInvoiceHeader> paperInvoices = payment.get().getCarrierInvoiceHeaderList().stream()
                    .filter(invoice -> invoice.getInvoiceSourceTypeCode()
                            .equalsIgnoreCase(CarrierPaymentConstants.PAPER) &&
                            !invoice.getInvoiceStatusCode()
                                    .equalsIgnoreCase(INVOICE_STATUS_REJECTED))
                    .collect(Collectors.toList());
            Optional.ofNullable(paperInvoices).filter(list -> !list.isEmpty())
                    .ifPresent(paperInvoiceList -> {
                        log.info("checkAutopayForEDI :: paperInvoiceList size :: "
                                + paperInvoiceList.size());
                        paperInvoices.stream().forEach(paperInvoice -> paperInvoice
                                .setInvoiceStatusCode(CarrierPaymentConstants.INVOICE_STATUS_ACKLNGD));
                        log.info("checkAutopayForEDI :: paperInvoiceList Status :: "
                                + paperInvoices.get(0).getInvoiceStatusCode());
                        invoiceRepository.saveAll(paperInvoices);
                    });
        }
    }

    private boolean validateToleranceAmuontOnTotalAmmount(ParameterListingDTO parameterAutoAprTolerance, BigDecimal totalChargesAmount) {
        return (null != parameterAutoAprTolerance && null != parameterAutoAprTolerance.getMinNumberValue()
                && null != parameterAutoAprTolerance.getMaxNumberValue() && totalChargesAmount.compareTo(BigDecimal.ZERO) != 0);

    }

    private boolean validateActionedChargeList(Payment payment) {
        List<Charge> actionedChargeList = new ArrayList<>();
        actionedChargeList = getActionedChargeList(payment, actionedChargeList);
        if (actionedChargeList.isEmpty()) {
            return true;
        } else {
            log.info(" Logging AutoPay failure reason when load does have any approved/rejected Chargecode for LTL and" +
                    " load does have any rejected code for FTL  -------  Approved/Rejected Charge code Check ");
            logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_PREVDECSN);
            paymentHelperService.setPaymentStatusToReroute(payment);
            return false;
        }
    }

    private boolean validateDuplicateInvoiceCharge(Payment payment, List<CarrierInvoiceDetail> invoiceDetail, List<ParameterListingDTO> parameterAAprChgGrp) {
        List<Charge> originalTenderedChargeList = Optional.ofNullable(payment.getCarrierPaymentChargeList())
                .map(chargeList -> chargeList.stream()
                        .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                        .filter(decision -> decision.getChargeDecisionCode() == null).collect(Collectors.toList()))
                .orElse(new ArrayList<>());
        boolean checkDuplicateTenderedChargeCode = false;
        checkDuplicateTenderedChargeCode = checkDuplicatesInTenderedList(originalTenderedChargeList, checkDuplicateTenderedChargeCode);
        log.info(payment.getGroupFlowTypeCode() + " - Tendered Charge Code List having duplicates charge code?, answer is  " + checkDuplicateTenderedChargeCode);


        boolean checkDuplicateInvoiceChargeCode = false;
        checkDuplicateInvoiceChargeCode = isCheckDuplicateInvoiceChargeCode(payment, invoiceDetail, checkDuplicateInvoiceChargeCode);

        //  New Change for LTL - if LTL has any Duplicate Accessorial Charge Code at Invoice Side then No Auto Pay - Starts


        boolean checkDuplicateAccessorialInvoiceLTL = false;

        checkDuplicateAccessorialInvoiceLTL = isCheckDuplicateAccessorialInvoiceLTL(payment, invoiceDetail, parameterAAprChgGrp, checkDuplicateAccessorialInvoiceLTL);
        if (!(checkDuplicateTenderedChargeCode || (checkDuplicateInvoiceChargeCode || checkDuplicateAccessorialInvoiceLTL))) {
            return true;
        } else {
            log.info(" Logging AutoPay failure reason when FTL has any Duplicate Charge Code at Tendered or at Invoice Side" +
                    " or when LTL load has any Duplicate Charge Code at Tendered Side -------  Duplicate Charge code Check ");
            logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_DUPECHGECD);
            paymentHelperService.setPaymentStatusToReroute(payment);
            return false;
        }
    }

    private boolean validateIfInvoiceAmountIsNullOrLessThenThreshold(BigDecimal totalInvoiceAmount, BigDecimal thresholdAmount, Payment payment) {
        if (null != totalInvoiceAmount && IS_LESSER.test(totalInvoiceAmount, thresholdAmount)) {
            return true;
        } else {
            log.info(" Logging AutoPay failure reason for   ------  TotalThreshold Amount Check ");
            logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_EXEDTHRHLD);
            paymentHelperService.setPaymentStatusToReroute(payment);
            return false;
        }
    }

    private boolean validateChargeAmountsGreaterAndLesser(BigDecimal totalInvoiceAmount, BigDecimal totalChargesAmountMax, BigDecimal totalChargesAmountMin, Payment payment) {
        boolean shouldAutoPay = AND.test(IS_GREATER.test(totalInvoiceAmount, totalChargesAmountMin),
                IS_LESSER.test(totalInvoiceAmount, totalChargesAmountMax));
        if (!shouldAutoPay) {
            log.info(" Logging AutoPay failure reason for First Check ------  Range Tolerance Check  ");
            logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_EXEDTOLNCE);
            paymentHelperService.setPaymentStatusToReroute(payment);
        }
        return shouldAutoPay;
    }

    private boolean isCheckDuplicateAccessorialInvoiceLTL(Payment payment, List<CarrierInvoiceDetail> invoiceDetail, List<ParameterListingDTO> parameterAAprChgGrp, boolean checkDuplicateAccessorialInvoiceLTL) {
        if (null != payment.getGroupFlowTypeCode() && payment.getGroupFlowTypeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.LTL)) {
            log.info("Checking if LTL has Duplicate Accessorial Charge Code at Invoice Side");
            List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterTransportationList = new ArrayList<>();
            if ((null != invoiceDetail && !invoiceDetail.isEmpty())
                    && (null != parameterAAprChgGrp && !parameterAAprChgGrp.isEmpty())) {
                finalInvoiceDetailMinusParameterTransportationList = getFinalInvoiceDetailMinusParameterList(invoiceDetail,
                        parameterAAprChgGrp, finalInvoiceDetailMinusParameterTransportationList);
            }

            checkDuplicateAccessorialInvoiceLTL = checkDuplicatesInInvoiceList(finalInvoiceDetailMinusParameterTransportationList, checkDuplicateAccessorialInvoiceLTL);
            log.info(payment.getGroupFlowTypeCode() + " -  Invoice Charge Code List having duplicates accessorial charge code?, answer is   " + checkDuplicateAccessorialInvoiceLTL);
        }
        return checkDuplicateAccessorialInvoiceLTL;
    }

    private boolean isCheckDuplicateInvoiceChargeCode(Payment payment, List<CarrierInvoiceDetail> invoiceDetail, boolean checkDuplicateInvoiceChargeCode) {
        if (null != payment.getGroupFlowTypeCode() && !payment.getGroupFlowTypeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.LTL)) {
            log.info("Checking if FTL has Duplicate Charge Code at Tendered or at Invoice Side");
            checkDuplicateInvoiceChargeCode = checkDuplicatesInInvoiceList(invoiceDetail, checkDuplicateInvoiceChargeCode);
            log.info(payment.getGroupFlowTypeCode() + " -  Invoice Charge Code List having duplicates charge code?, answer is   " + checkDuplicateInvoiceChargeCode);
        }
        return checkDuplicateInvoiceChargeCode;
    }

    private List<Charge> getActionedChargeList(Payment payment, List<Charge> actionedChargeList) {
        if (null != payment.getGroupFlowTypeCode()) {
            if (payment.getGroupFlowTypeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.LTL)) {
                actionedChargeList = Optional.ofNullable(payment.getCarrierPaymentChargeList())
                        .map(chargeList -> chargeList.stream()
                                .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                                .filter(decision -> decision.getChargeDecisionCode() != null).collect(Collectors.toList()))
                        .orElse(new ArrayList<>());
            } else {
                actionedChargeList = Optional.ofNullable(payment.getCarrierPaymentChargeList())
                        .map(chargeList -> chargeList.stream()
                                .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                                .filter(decision -> decision.getChargeDecisionCode() != null
                                        && decision.getChargeDecisionCode().trim().equalsIgnoreCase(CarrierPaymentConstants.REJECT_STATUS)).collect(Collectors.toList()))
                        .orElse(new ArrayList<>());
            }
        }
        return actionedChargeList;
    }
    // Auto Pay Changes End

    public void logAutoPayFailureActivity(Payment payment, String actDetailTypeCode) {
        log.info("Inside logAutoPayFailureActivity Method");
        log.info("Inside saveActivityAutoPayFailure method start: Type:" + actDetailTypeCode);
        activityService.saveActivityAutoPayFailure(payment, CarrierPaymentConstants.ACT_AUTOPAY, CarrierPaymentConstants.ACT_AUTOPAY_FAIL,
                CarrierPaymentConstants.ACT_APP, CarrierPaymentConstants.SYSTEM, actDetailTypeCode);
    }


    private TxnStatusDTO saveAutopayDetails(ChargeDTO chargeDTO, Optional<Payment> payment) {
        TxnStatusDTO transactionStatus = chargeDecisionService.chargeDecision(chargeDTO, payment.get());
        if (payment.isPresent()) {
            log.info("INSIDE performAutopay :: payment status :: " + payment.get().getStatusFlowTypeCode());
            if (transactionStatus.isSuccess()) {
                payment.get().setStatusFlowTypeCode(CarrierPaymentConstants.AUTO_APPROVED);
                paymentStateService.createNewPaymentStateAndSavePayment(payment.get());
                log.info("END performAutopay :: payment status :: " + payment.get().getStatusFlowTypeCode());
            } else {
                log.info("END performAutopay :: AUTOPAY VALIDATION FAILED :: " + transactionStatus.getErrorList());
            }
        }
        return transactionStatus;
    }


    private boolean performAutopay(Optional<Payment> payment, CarrierInvoiceHeader ediInvoice) {

        AtomicBoolean autoPayStatus = new AtomicBoolean(false);
        payment.filter(pay -> Objects.equals(pay.getStatusFlowTypeCode(), CarrierPaymentConstants.PENDING_LTL)
                        || Objects.equals(pay.getStatusFlowTypeCode(), CarrierPaymentConstants.PENDING_AP)
                        || Objects.equals(pay.getStatusFlowTypeCode(), CarrierPaymentConstants.REROUTE))

                .ifPresent(paymentForAutoApprove -> {

                            ChargeDTO chargeDTO = populateChargeDTO(payment, ediInvoice);

                            TxnStatusDTO transactionStatus = saveAutopayDetails(chargeDTO, payment);
                            autoPayStatus.set(transactionStatus.isSuccess());

                        }
                );

        if (autoPayStatus.get()) {
            // ACKNOWLEDGE PREVIOUS PAPER INVOICES
            acknowledgeInvoicesForAutoPay(payment);
        }
        return autoPayStatus.get();
    }

    public ChargeDTO populateChargeDTO(Optional<Payment> payment, CarrierInvoiceHeader ediInvoice) {
        ChargeDTO chargeDTO = new ChargeDTO();
        chargeDTO.setInvoiceDate(ediInvoice.getInvoiceDate().toString());
        chargeDTO.setInvoiceNumber(ediInvoice.getCarrierInvoiceNumber());
        chargeDTO.setHeaderId(ediInvoice.getCarrierInvoiceHeaderId());
        chargeDTO.setChargeDecisionCode(CarrierPaymentConstants.APPROVE);
        if (payment.isPresent()) {
            chargeDTO.setLoadNumber(payment.get().getLoadNumber());
            chargeDTO.setDispatchNumber(payment.get().getDispatchNumber());
            chargeDTO.setScacCode(payment.get().getScacCode());
            chargeDTO.setPaymentId(payment.get().getCarrierPaymentId());
            List<Charge> charges = payment.get().getCarrierPaymentChargeList().stream()
                    .filter(chargeDecision -> chargeDecision.getChargeDecisionCode() == null)
                    .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null).collect(Collectors.toList());
            List<Integer> chargeIds = charges.stream().map(Charge::getChargeId).collect(Collectors.toList());
            chargeDTO.setChargeIdList(chargeIds);
            chargeDTO.setAutoPay(true);
            chargeDTO.setWorkFlowGroupType(payment.get().getGroupFlowTypeCode());
            chargeDTO.setProjectCode(payment.get().getProjectCode());
            Optional.ofNullable(payment.get().getDispatchTokenId()).map(Integer::valueOf).ifPresent(chargeDTO::setJobId);
            if (null != payment.get().getCarrierPaymentShipmentID() && null != payment.get().getCarrierPaymentShipmentID().getLoadToken()) {
                chargeDTO.setOrderID(payment.get().getCarrierPaymentShipmentID().getLoadToken());
            }
        }
        return chargeDTO;
    }

    // Auto Pay Changes Start

    // Check Duplicate Charge Code in TenderedList
    private boolean checkDuplicatesInTenderedList(List<Charge> originalTenderedChargeList, boolean checkDuplicateTenderedChargeCode) {
        if (null != originalTenderedChargeList && !originalTenderedChargeList.isEmpty()) {
            log.info(" Inside checkDuplicatesInTenderedList");
            for (int i = 0; i < originalTenderedChargeList.size(); i++) {
                for (int j = i + 1; j < originalTenderedChargeList.size(); j++) {
                    if (originalTenderedChargeList.get(i).getChargeCode().trim().equalsIgnoreCase(originalTenderedChargeList.get(j).getChargeCode().trim())) {
                        checkDuplicateTenderedChargeCode = true;
                        log.info(" Inside checkDuplicatesInTenderedList" + checkDuplicateTenderedChargeCode);
                        return checkDuplicateTenderedChargeCode;
                    }
                }
            }
        }
        return checkDuplicateTenderedChargeCode;
    }

    // Check Duplicate Charge Code in InvoiceList
    private boolean checkDuplicatesInInvoiceList(List<CarrierInvoiceDetail> invoiceDetail, boolean checkDuplicateInvoiceChargeCode) {
        log.info(" Inside checkDuplicatesInInvoiceList");
        if (null != invoiceDetail && !invoiceDetail.isEmpty()) {
            for (int i = 0; i < invoiceDetail.size(); i++) {
                for (int j = i + 1; j < invoiceDetail.size(); j++) {
                    if (invoiceDetail.get(i).getChargeCode().trim().equalsIgnoreCase(invoiceDetail.get(j).getChargeCode().trim())) {
                        checkDuplicateInvoiceChargeCode = true;
                        return checkDuplicateInvoiceChargeCode;
                    }
                }
            }
        }
        return checkDuplicateInvoiceChargeCode;
    }

    // Check Percentage Threshold Amount
    private boolean checkPercentageThreshold(Payment payment, BigDecimal totalChargesAmount, BigDecimal totalInvoiceAmount, List<ParameterListingDTO> parameterListings) {

        log.info(" Inside Second Check method for AutoPay ------- checkPercentageThreshold()");
        boolean checkPercentageThresholdVal = false;
        if (null != totalChargesAmount && totalChargesAmount.compareTo(BigDecimal.ZERO) > 0
                && null != totalInvoiceAmount && totalInvoiceAmount.compareTo(BigDecimal.ZERO) > 0) {

            if (totalInvoiceAmount.compareTo(totalChargesAmount) > 0) {

                log.info(" Inside Second Check method for AutoPay when Invoice Amount > Tendered Amount ------- checkPercentageThreshold()");

                ParameterListingDTO parameterAutoAprOvr = getRequiredParameter(parameterListings, CarrierPaymentConstants.SPECIFICATION_AUTOPAY_OVER_PERCENTAGE);

                checkPercentageThresholdVal = isCheckPercentageThresholdVal(totalChargesAmount, totalInvoiceAmount, checkPercentageThresholdVal, parameterAutoAprOvr);
            } else {
                checkPercentageThresholdVal = true;
            }
        }
        log.info(" Exiting Second Check method for AutoPay ------- checkPercentageThreshold() " +
                " and value of checkPercentageThresholdVal is  " + checkPercentageThresholdVal);
        if (!checkPercentageThresholdVal) {
            log.info(" Logging AutoPay failure reason for Second Check ----- CheckPercentageThreshold ");
            logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_EXEDPCNTT);
            paymentHelperService.setPaymentStatusToReroute(payment);
        }
        return checkPercentageThresholdVal;
    }

    private boolean isCheckPercentageThresholdVal(BigDecimal totalChargesAmount, BigDecimal totalInvoiceAmount, boolean checkPercentageThresholdVal, ParameterListingDTO parameterAutoAprOvr) {
        BigDecimal percentageThresholdAmount;
        if (null != parameterAutoAprOvr && null != parameterAutoAprOvr.getMinNumberValue() &&
                null != parameterAutoAprOvr.getMaxNumberValue()) {
            if (parameterAutoAprOvr.getMaxNumberValue().compareTo(BigDecimal.ZERO) > 0) {
                log.info(" Inside Second Check method for AutoPay ------- isCheckPercentageThresholdVal()");
                percentageThresholdAmount = (totalChargesAmount.multiply(parameterAutoAprOvr.getMaxNumberValue()).divide(new BigDecimal(100)));
                log.info("PercentageThresholdAmount IS :: " + percentageThresholdAmount);
                if (totalInvoiceAmount.compareTo(percentageThresholdAmount.add(totalChargesAmount)) <= 0)
                    checkPercentageThresholdVal = true;
            } else if (parameterAutoAprOvr.getMaxNumberValue().compareTo(BigDecimal.ZERO) == 0) {
                checkPercentageThresholdVal = true;
            }
        }
        return checkPercentageThresholdVal;
    }

    // Comparing and Matching Invoice and Tendered Charge Code List
    private boolean checkExactListMatch(Payment payment, List<CarrierInvoiceDetail> invoiceDetail,
                                        List<ParameterListingDTO> parameterAAprChgGrp) {

        log.info(" Inside Third Check method for AutoPay ------- checkExactListMatch()");
        boolean checkExactListMatchVal = false;

        if (!isNullOrEmpty(parameterAAprChgGrp)) {

            List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList = new ArrayList<>(0);
            List<Charge> finalTenderedChargeListMinusParameterList = new ArrayList<>(0);
            List<CarrierInvoiceDetail> invoiceDetailTransportationGrpList = new ArrayList<>(0);
            List<Charge> tenderedTransportationGrpChargeList = new ArrayList<>(0);

            log.info(" Inside Third Check method for AutoPay, parameterAAprChgGrp is Not Empty ------- checkExactListMatch()");

            getTenderedTransGrpChgList(payment, parameterAAprChgGrp, tenderedTransportationGrpChargeList);

            getInvoiceDtlTransGrpList(invoiceDetail, parameterAAprChgGrp, invoiceDetailTransportationGrpList);

            finalInvoiceDetailMinusParameterList = getFinalInvoiceDtlMinusParamList(invoiceDetail, parameterAAprChgGrp, finalInvoiceDetailMinusParameterList);

            finalTenderedChargeListMinusParameterList = getFinalTenderedChrgMinusParamList(payment, parameterAAprChgGrp, finalTenderedChargeListMinusParameterList);

            BigDecimal invoiceTransportationGroupAmount = invoiceDetailTransportationGrpList.stream().map(CarrierInvoiceDetail::getCarrierChargeUnitRateAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal tenderedChargeTransportationGroupAmount = tenderedTransportationGrpChargeList.stream().map(Charge::getTotalApprovedChargeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            boolean tenderedOrInvoiceChargeIsNotEmptyOrNotZero = invoiceAndTenderedChargeTransportationGroupAmountValidation(invoiceTransportationGroupAmount, tenderedChargeTransportationGroupAmount, invoiceDetailTransportationGrpList, tenderedTransportationGrpChargeList);
            if (payment.getGroupFlowTypeCode().equalsIgnoreCase(JBI)) {
                boolean groupListFlag;
                boolean invoiceListFlag;
                if (!isNullOrEmpty(invoiceDetailTransportationGrpList)) {
                    groupListFlag = jbiAutoPayService.jBIInvoiceAndTenderedChargeTransportationGroupAmountValidation(invoiceTransportationGroupAmount, tenderedChargeTransportationGroupAmount, invoiceDetailTransportationGrpList, tenderedTransportationGrpChargeList);
                } else {
                    groupListFlag = true;
                }
                if ((isNullOrEmpty(finalInvoiceDetailMinusParameterList))) {
                    log.info(" Inside Third Check method for JBI AutoPay, list are Empty ------- checkExactListMatch()");
                    invoiceListFlag = true;
                } else {
                    invoiceListFlag = jbiAutoPayService.validateInvoiceAndTenderedList(finalInvoiceDetailMinusParameterList, finalTenderedChargeListMinusParameterList, checkExactListMatchVal);
                }
                if (groupListFlag && invoiceListFlag) {
                    checkExactListMatchVal = true;
                }

            } else if (tenderedOrInvoiceChargeIsNotEmptyOrNotZero) {
                if ((isNullOrEmpty(finalInvoiceDetailMinusParameterList) && isNullOrEmpty(finalTenderedChargeListMinusParameterList))) {
                    log.info(" Inside Third Check method for AutoPay, when finalInvoiceDetailMinusParameterList " +
                            "and finalTenderedChargeListMinusParameterList list are Empty ------- checkExactListMatch()");
                    checkExactListMatchVal = true;
                    return checkExactListMatchVal;
                } else
                    log.info(" Inside Third Check method for AutoPay, when finalInvoiceDetailMinusParameterList " +
                            "and finalTenderedChargeListMinusParameterList list are Not Empty ------- checkExactListMatch()");

                checkExactListMatchVal = validateInvoiceAndTenderedList(finalInvoiceDetailMinusParameterList, finalTenderedChargeListMinusParameterList, checkExactListMatchVal);

            }
        }
        log.info(" Exiting Third Check method for AutoPay ------- checkExactListMatch() " +
                " and value of checkExactListMatchVal is  " + checkExactListMatchVal);
        if (!checkExactListMatchVal) {
            log.info(" Logging AutoPay failure reason for Third Check ------  CheckExactListMatch ");
            logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_CHGLSTEXCP);
            paymentHelperService.setPaymentStatusToReroute(payment);
        }
        return checkExactListMatchVal;

    }

    private Boolean validateInvoiceAndTenderedList(List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList, List<Charge> finalTenderedChargeListMinusParameterList, boolean checkExactListMatchVal) {

        for (CarrierInvoiceDetail carrierInvoiceDetail : finalInvoiceDetailMinusParameterList) {
            if (checkFinalInvoiceAndTenderedList(carrierInvoiceDetail, finalTenderedChargeListMinusParameterList)) {
                checkExactListMatchVal = true;
            } else if (carrierInvoiceDetail.getCarrierChargeUnitRateAmount().compareTo(BigDecimal.ZERO) == 0) {
                checkExactListMatchVal = true;
            } else {
                checkExactListMatchVal = false;
                return checkExactListMatchVal;
            }
        }

        for (Charge charge : finalTenderedChargeListMinusParameterList) {
            if (checkFinalTenderedAndInvoiceList(charge, finalInvoiceDetailMinusParameterList)) {
                checkExactListMatchVal = true;
            } else {
                checkExactListMatchVal = false;
                return checkExactListMatchVal;
            }
        }

        return checkExactListMatchVal;
    }

    public static boolean checkFinalInvoiceAndTenderedList(CarrierInvoiceDetail carrierInvoiceDetail, List<Charge> finalTenderedChargeListMinusParameterList) {
        for (Charge charge : finalTenderedChargeListMinusParameterList) {
            if (carrierInvoiceDetail.getChargeCode().trim().equalsIgnoreCase(charge.getChargeCode().trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkFinalTenderedAndInvoiceList(Charge charge, List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList) {
        for (CarrierInvoiceDetail carrierInvoiceDetail : finalInvoiceDetailMinusParameterList) {
            if (carrierInvoiceDetail.getChargeCode().trim().equalsIgnoreCase(charge.getChargeCode().trim())) {
                return true;
            }
        }
        return false;

    }

    private boolean invoiceAndTenderedChargeTransportationGroupAmountValidation(BigDecimal invoiceTransportationGroupAmount, BigDecimal tenderedChargeTransportationGroupAmount, List<CarrierInvoiceDetail> invoiceDetailTransportationGrpList, List<Charge> tenderedTransportationGrpChargeList) {
        return ((isNullOrEmpty(tenderedTransportationGrpChargeList) &&
                isNullOrEmpty(invoiceDetailTransportationGrpList)) ||
                (tenderedTransportationGrpChargeList.isEmpty() &&
                        invoiceTransportationGroupAmount.compareTo(BigDecimal.ZERO) == 0)
                || (invoiceDetailTransportationGrpList.isEmpty() &&
                tenderedChargeTransportationGroupAmount.compareTo(BigDecimal.ZERO) == 0) ||
                (!isNullOrEmpty(tenderedTransportationGrpChargeList)
                        && !isNullOrEmpty(invoiceDetailTransportationGrpList)));
    }

    private List<Charge> getFinalTenderedChrgMinusParamList(Payment payment, List<ParameterListingDTO> parameterAAprChgGrp, List<Charge> finalTenderedChargeListMinusParameterList) {
        if (null != payment && !payment.getCarrierPaymentChargeList().isEmpty()) {
            finalTenderedChargeListMinusParameterList = getFinalTenderedChargeListMinusParameterList(payment,
                    parameterAAprChgGrp, finalTenderedChargeListMinusParameterList);
        }
        return finalTenderedChargeListMinusParameterList;
    }

    private List<CarrierInvoiceDetail> getFinalInvoiceDtlMinusParamList(List<CarrierInvoiceDetail> invoiceDetail, List<ParameterListingDTO> parameterAAprChgGrp, List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList) {
        if (null != invoiceDetail && !invoiceDetail.isEmpty()) {
            finalInvoiceDetailMinusParameterList = getFinalInvoiceDetailMinusParameterList(invoiceDetail,
                    parameterAAprChgGrp, finalInvoiceDetailMinusParameterList);
        }
        return finalInvoiceDetailMinusParameterList;
    }

    private void getInvoiceDtlTransGrpList(List<CarrierInvoiceDetail> invoiceDetail, List<ParameterListingDTO> parameterAAprChgGrp, List<CarrierInvoiceDetail> invoiceDetailTransportationGrpList) {
        Optional.ofNullable(invoiceDetail).filter(list -> !list.isEmpty())
                .ifPresent(invoiceDetailList ->
                        invoiceDetailList.forEach(invoiceDetal -> {
                            log.info(" boolean value when comparing Invoice Detail and Parameter charges " + ChargePredicateUtil.CARRERAPGROUP_EDIT_CHARGES.test(invoiceDetal, parameterAAprChgGrp));
                            if (ChargePredicateUtil.CARRERAPGROUP_EDIT_CHARGES.test(invoiceDetal, parameterAAprChgGrp)) {
                                invoiceDetailTransportationGrpList.add(invoiceDetal);
                            }
                        })
                );
    }

    private void getTenderedTransGrpChgList(Payment payment, List<ParameterListingDTO> parameterAAprChgGrp, List<Charge> tenderedTransportationGrpChargeList) {
        List<Charge> tenderedChargeList = Optional.ofNullable(payment.getCarrierPaymentChargeList())
                .map(chargeList -> chargeList.stream()
                        .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                        .filter(decision -> decision.getChargeDecisionCode() == null).collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        tenderedChargeList.forEach(charge -> {
            log.info(" boolean value when comparing tendered and Parameter charges " + ChargePredicateUtil.APGROUP_EDIT_CHARGES.test(charge, parameterAAprChgGrp));
            if (ChargePredicateUtil.APGROUP_EDIT_CHARGES.test(charge, parameterAAprChgGrp)) {
                tenderedTransportationGrpChargeList.add(charge);
            }
        });
    }

    /* Checking Charge Threshold is required or not and comparing each final Invoice List charge code Threshold Amount
     ** against Parameter Threshold amount */

    private boolean checkChargeThreshold(Payment payment, List<CarrierInvoiceDetail> invoiceDetail,
                                         List<ParameterListingDTO> parameterAAprChgGrp, List<ParameterListingDTO> parameterListings) {

        log.info(" Inside Fourth and Fifth Check method for AutoPay ------- checkChargeThreshold()");
        boolean checkChargeThresholdVal = false;


        List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList = new ArrayList<>();
        List<Charge> finalTenderedChargeListMinusParameterList = new ArrayList<>();


        List<ParameterListingDTO> parameterAutoAprChargeThresholdReq = getRequiredParameterList(parameterListings, CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGE_THRESHOLD_REQUIRED);


        if (null != parameterAutoAprChargeThresholdReq
                && parameterAutoAprChargeThresholdReq.size() == 1) {

            log.info(" Inside Fourth and Fifth Check method for AutoPay, when parameterAutoAprChargeThresholdReq is Not Empty ------- checkChargeThreshold()");

            finalInvoiceDetailMinusParameterList = getFinalInvoiceDetailMinusParameterList(invoiceDetail,
                    parameterAAprChgGrp, finalInvoiceDetailMinusParameterList);

            if (!payment.getCarrierPaymentChargeList().isEmpty()) {
                finalTenderedChargeListMinusParameterList = getFinalTenderedChargeListMinusParameterList(payment,
                        parameterAAprChgGrp, finalTenderedChargeListMinusParameterList);
            }

            List<ParameterListingDTO> parameterAutoAprChargeThresholdRange = getRequiredParameterList(parameterListings, CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGE_THRESHOLD_RANGE);

            if (parameterAutoAprChargeThresholdReq.get(0).getSpecificationSub().trim()
                    .equalsIgnoreCase("True")) {
                log.info(" Inside Fourth and Fifth Check method for AutoPay, when parameterAutoAprChargeThresholdReq is True ------- checkChargeThreshold()");
                if (!isNullOrEmpty(finalInvoiceDetailMinusParameterList) && !isNullOrEmpty(parameterAutoAprChargeThresholdRange)) {
                    checkChargeThresholdVal = checkEachChargeCodeThresholdRangeForLTL(finalInvoiceDetailMinusParameterList, parameterAutoAprChargeThresholdRange);
                } else {
                    checkChargeThresholdVal = true;
                }
            } else {
                checkChargeThresholdVal = checkEachChargeCodeThresholdRangeAgainstFinalInvoiceAndTenderedListForFTL(finalInvoiceDetailMinusParameterList, parameterAutoAprChargeThresholdRange, finalTenderedChargeListMinusParameterList);

            }
        }
        log.info(" Exiting Fourth and Fifth Check method for AutoPay ------- checkChargeThreshold() " +
                " and value of checkChargeThresholdVal is   " + checkChargeThresholdVal);
        if (!checkChargeThresholdVal) {
            log.info(" Logging AutoPay failure reason for Fourth and Fifth Check ------  checkChargeThreshold ");
            logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_CHGEXEDTOL);
            paymentHelperService.setPaymentStatusToReroute(payment);
        }
        return checkChargeThresholdVal;
    }

    private boolean checkEachChargeCodeThresholdRangeAgainstFinalInvoiceAndTenderedListForFTL(List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList, List<ParameterListingDTO> parameterAutoAprChargeThresholdRange, List<Charge> finalTenderedChargeListMinusParameterList) {
        if (!isNullOrEmpty(finalInvoiceDetailMinusParameterList) && !isNullOrEmpty(parameterAutoAprChargeThresholdRange)) {
            for (CarrierInvoiceDetail carrierInvoiceDetail : finalInvoiceDetailMinusParameterList) {
                if (checkEachChargeCodeThresholdRangeAgainstFinalInvoiceListForFTL(carrierInvoiceDetail, parameterAutoAprChargeThresholdRange)) {
                    return true;
                }
            }
            for (Charge charge : finalTenderedChargeListMinusParameterList) {
                if (checkEachChargeCodeThresholdRangeAgainstFinalTenderedListForFTL(charge, parameterAutoAprChargeThresholdRange)) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    private List<Charge> getFinalTenderedChargeListMinusParameterList(Payment payment, List<ParameterListingDTO> parameterAAprChgGrp, List<Charge> finalTenderedChargeListMinusParameterList) {
        log.info(" Inside --------   getFinalTenderedChargeListMinusParameterList()");
        List<Charge> tenderedChargeList = Optional.ofNullable(payment.getCarrierPaymentChargeList())
                .map(chargeList -> chargeList.stream()
                        .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                        .filter(decision -> decision.getChargeDecisionCode() == null).collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        if (null != tenderedChargeList && !tenderedChargeList.isEmpty()) {
            for (Charge charge : tenderedChargeList) {
                if (checkMatchInTenderedChargeAndParameterList(charge, parameterAAprChgGrp)) {
                    finalTenderedChargeListMinusParameterList.add(charge);
                }
            }
            if (null != finalTenderedChargeListMinusParameterList && finalTenderedChargeListMinusParameterList.size() > 1) {
                finalTenderedChargeListMinusParameterList = finalTenderedChargeListMinusParameterList.stream()
                        .sorted(Comparator.comparing(Charge::getChargeCode))
                        .collect(Collectors.toList());
            }
        }
        log.info(" Exiting --------   getFinalTenderedChargeListMinusParameterList()");
        return finalTenderedChargeListMinusParameterList;
    }

    private List<CarrierInvoiceDetail> getFinalInvoiceDetailMinusParameterList(List<CarrierInvoiceDetail> invoiceDetail,
                                                                               List<ParameterListingDTO> parameterAAprChgGrp,
                                                                               List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList) {
        log.info(" Inside --------  getFinalInvoiceDetailMinusParameterList()");
        for (CarrierInvoiceDetail carrierInvoiceDetail : invoiceDetail) {
            if (checkMatchInInvoiceDetailAndParameterList(carrierInvoiceDetail, parameterAAprChgGrp)) {
                finalInvoiceDetailMinusParameterList.add(carrierInvoiceDetail);
            }
        }
        if (null != finalInvoiceDetailMinusParameterList && finalInvoiceDetailMinusParameterList.size() > 1) {
            finalInvoiceDetailMinusParameterList = finalInvoiceDetailMinusParameterList.stream()
                    .sorted(Comparator.comparing(CarrierInvoiceDetail::getChargeCode))
                    .collect(Collectors.toList());
        }
        log.info(" Exiting --------  getFinalInvoiceDetailMinusParameterList()");
        return finalInvoiceDetailMinusParameterList;
    }

    private boolean checkEachChargeCodeThresholdRangeAgainstFinalInvoiceListForFTL(CarrierInvoiceDetail carrierInvoiceDetail, List<ParameterListingDTO> parameterAutoAprChargeThresholdRange) {
        boolean checkEachChargeCodeThresholdRangeAgainstFinalInvoiceListForFTLVal = false;
        log.info(" Inside --------  checkEachChargeCodeThresholdRangeAgainstFinalInvoiceListForFTL()");
        for (ParameterListingDTO parameterListingDTO : parameterAutoAprChargeThresholdRange) {
            if (carrierInvoiceDetail.getChargeCode().trim().equalsIgnoreCase(parameterListingDTO.getSpecificationSub().trim())) {
                if (carrierInvoiceDetail.getCarrierChargeUnitRateAmount().compareTo(parameterListingDTO.getMinNumberValue()) >= 0 &&
                        carrierInvoiceDetail.getCarrierChargeUnitRateAmount().compareTo(parameterListingDTO.getMaxNumberValue()) <= 0) {
                    checkEachChargeCodeThresholdRangeAgainstFinalInvoiceListForFTLVal = true;
                    return checkEachChargeCodeThresholdRangeAgainstFinalInvoiceListForFTLVal;
                } else {
                    checkEachChargeCodeThresholdRangeAgainstFinalInvoiceListForFTLVal = false;
                    return checkEachChargeCodeThresholdRangeAgainstFinalInvoiceListForFTLVal;
                }
            } else {
                checkEachChargeCodeThresholdRangeAgainstFinalInvoiceListForFTLVal = true;
            }
        }
        log.info(" Exiting --------  checkEachChargeCodeThresholdRangeAgainstFinalInvoiceListForFTL()");
        return checkEachChargeCodeThresholdRangeAgainstFinalInvoiceListForFTLVal;
    }

    private boolean checkEachChargeCodeThresholdRangeAgainstFinalTenderedListForFTL(Charge charge, List<ParameterListingDTO> parameterAutoAprChargeThresholdRange) {
        boolean checkEachChargeCodeThresholdRangeAgainstFinalTenderedListForFTLVal = false;
        log.info(" Inside --------  checkEachChargeCodeThresholdRangeAgainstFinalTenderedListForFTL()");
        for (ParameterListingDTO parameterListingDTO : parameterAutoAprChargeThresholdRange) {
            if (charge.getChargeCode().trim().equalsIgnoreCase(parameterListingDTO.getSpecificationSub().trim())) {
                if (charge.getTotalApprovedChargeAmount().compareTo(parameterListingDTO.getMinNumberValue()) >= 0 &&
                        charge.getTotalApprovedChargeAmount().compareTo(parameterListingDTO.getMaxNumberValue()) <= 0) {
                    checkEachChargeCodeThresholdRangeAgainstFinalTenderedListForFTLVal = true;
                    return checkEachChargeCodeThresholdRangeAgainstFinalTenderedListForFTLVal;
                } else {
                    checkEachChargeCodeThresholdRangeAgainstFinalTenderedListForFTLVal = false;
                    return checkEachChargeCodeThresholdRangeAgainstFinalTenderedListForFTLVal;
                }
            } else {
                checkEachChargeCodeThresholdRangeAgainstFinalTenderedListForFTLVal = true;
            }
        }
        return checkEachChargeCodeThresholdRangeAgainstFinalTenderedListForFTLVal;
    }

    public boolean checkEachChargeCodeThresholdRangeForLTL(List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList, List<ParameterListingDTO> parameterAutoAprChargeThresholdRange) {
        AtomicBoolean checkEachChargeCodeThresholdRangeValForLTL = new AtomicBoolean(false);
        AtomicBoolean stopAutoPay = new AtomicBoolean(false);
        log.info(" Inside --------  checkEachChargeCodeThresholdRangeForLTL()");
        finalInvoiceDetailMinusParameterList.stream().forEach(carrierInvoiceDetail -> {
            if (carrierInvoiceDetail.getCarrierChargeUnitRateAmount().compareTo(BigDecimal.ZERO) == 0) {
                checkEachChargeCodeThresholdRangeValForLTL.set(true);
                return;
            }
            var parameterList = parameterAutoAprChargeThresholdRange.stream().filter(a -> a.getSpecificationSub().trim().equalsIgnoreCase(carrierInvoiceDetail.getChargeCode().trim())).findFirst();
            if (parameterList.isPresent()) {
                Optional.ofNullable(carrierInvoiceDetail.getCarrierChargeUnitRateAmount())
                        .filter(eachChargeCodeInvoiceAmount -> AND.test(IS_GREATER.test(eachChargeCodeInvoiceAmount, parameterList.get().getMinNumberValue()),
                                IS_LESSER.test(eachChargeCodeInvoiceAmount, parameterList.get().getMaxNumberValue())))
                        .ifPresentOrElse(valid -> {
                            log.info(" Eligible for AutoPay, Charge Code " + parameterList.get().getSpecificationSub() +
                                    " Threshold Range Check Min Value " + parameterList.get().getMinNumberValue() +
                                    "Max " + parameterList.get().getMaxNumberValue());
                            checkEachChargeCodeThresholdRangeValForLTL.set(true);
                        },() -> {
                            log.info("Not within the tolerance " + parameterList.get().getSpecificationSub());
                            stopAutoPay.set(true);});

            } else {
                log.info(" Not Eligible for AutoPay after Fifth Check when the charge code is not present in parameter list  for charge code --- " + carrierInvoiceDetail.getChargeCode().trim());
                log.info(" Not Eligible for AutoPay after Fifth Check when the charge code is not present in parameter list  for charge Amount --- " + carrierInvoiceDetail.getCarrierChargeUnitRateAmount());
                stopAutoPay.set(true);
            }
        });
        log.info(" Exiting --------  checkEachChargeCodeThresholdRangeForLTL()");
        if(stopAutoPay.get()){
            return false;
        } else {
            return checkEachChargeCodeThresholdRangeValForLTL.get();
        }
    }

    private boolean checkMatchInInvoiceDetailAndParameterList(CarrierInvoiceDetail carrierInvoiceDetail, List<ParameterListingDTO> parameterAAprChgGrp) {
        for (ParameterListingDTO parameterListingDTO : parameterAAprChgGrp) {
            if (carrierInvoiceDetail.getChargeCode().trim().equalsIgnoreCase(parameterListingDTO.getSpecificationSub().trim())) {
                return false;
            }
        }
        return true;

    }

    private boolean checkMatchInTenderedChargeAndParameterList(Charge charge, List<ParameterListingDTO> parameterAAprChgGrp) {
        for (ParameterListingDTO parameterListingDTO : parameterAAprChgGrp) {
            if (charge.getChargeCode().trim().equalsIgnoreCase(parameterListingDTO.getSpecificationSub().trim())) {
                return false;
            }
        }
        return true;

    }

    // Auto Pay New WS Changes Start
    @Transactional
    public Boolean callAutoPayWS(Integer paymentId) {
        log.info(" Call to AutoPay Service Started ");
        TxnStatusDTO txnStatusDTO = new TxnStatusDTO();

        AtomicBoolean autoPayBatchStatus = new AtomicBoolean(false);
        if (null != paymentId) {
            log.info("Payment Id  is " + paymentId);
            List<Boolean> autoPayRecordsList = getPaymentObjectAndProcessAutoPay(paymentId);
            if (autoPayRecordsList.size() == 1)
                autoPayBatchStatus.set(true);
        }
        txnStatusDTO.setSuccess(autoPayBatchStatus.get());
        log.info(" Exiting ---- callAutoPayWS Method ");
        return txnStatusDTO.isSuccess();
    }

    private List<Boolean> getPaymentObjectAndProcessAutoPay(Integer paymentId) {
        log.info(" Inside -------------- getPaymentObjectAndProcessAutoPay method ");
        boolean autoPayFlag = false;
        List<Boolean> autoPayRecordsList = new ArrayList<>();
        log.info("Payment ID in getPaymentObjectAndProcessAutoPay method is " + paymentId);
        CarrierPaymentProcessEvent carrierPaymentProcessEvent = carrierPaymentProcessEventRepository.findPaymentByCarrierPaymentId(paymentId);
        CarrierInvoiceHeader carrierInvoiceHeader = null;
        Payment payment = paymentRepository.findPaymentByCarrierPaymentId(paymentId);
        if ((Objects.equals(payment.getStatusFlowTypeCode(), CarrierPaymentConstants.PENDING_LTL)
                || Objects.equals(payment.getStatusFlowTypeCode(), CarrierPaymentConstants.PENDING_AP)
                || Objects.equals(payment.getStatusFlowTypeCode(), CarrierPaymentConstants.REROUTE))) {

            List<ParameterListingDTO> parameterListings = carrierPaymentParameterDAO.getAllParametersForWorkFlowGroup(payment.getGroupFlowTypeCode());
            // Making Autopay false if the most recent invoice is rejected.
            List<CarrierInvoiceHeader> carrierInvoiceHeaderList = invoiceRepository.findByCarrierPaymentCarrierPaymentIdOrderByCarrierInvoiceHeaderIdDesc(payment.getCarrierPaymentId());
            if (!carrierInvoiceHeaderList.isEmpty() && !INVOICE_STATUS_REJECTED.equalsIgnoreCase(carrierInvoiceHeaderList.get(0).getInvoiceStatusCode())
                    && null != payment.getCurrencyCode() && payment.getCurrencyCode().equalsIgnoreCase(carrierInvoiceHeaderList.get(0).getCurrencyCode())) {
                carrierInvoiceHeader = invoiceRepository
                        .findTopByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                                payment.getCarrierPaymentId(), CarrierPaymentConstants.PAPER,
                                INVOICE_STATUS_REJECTED);
                autoPayFlag = checkAndPerformAutopay(payment, parameterListings, carrierInvoiceHeader);
                log.info(" AutoPay Flag after checkAndPerformAutopay method called is " + autoPayFlag);

            }
          /*  else if (null == payment.getDispatchDate()) {
                log.info(" AutoPay Flag after checkAndPerformAutopay method called is " + autoPayFlag);
                logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_NO_DISPATCH);
            } */
            else if (!carrierInvoiceHeaderList.isEmpty() && INVOICE_STATUS_REJECTED.equalsIgnoreCase(carrierInvoiceHeaderList.get(0).getInvoiceStatusCode())) {
                log.info(" AutoPay Flag after checkAndPerformAutopay method called is " + autoPayFlag);
                logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_IVCREJECT);
            } else if (null == payment.getCurrencyCode()) {
                log.info(" AutoPay Flag after checkAndPerformAutopay method called is " + autoPayFlag);
                logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_MIXED_CURRENCY);
                paymentHelperService.setPaymentStatusToReroute(payment);
            } else if (!payment.getCurrencyCode().equalsIgnoreCase(carrierInvoiceHeaderList.get(0).getCurrencyCode())) {
                log.info(" AutoPay Flag after checkAndPerformAutopay method called is " + autoPayFlag);
                logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_CURRENCY_MISMATCH);
                paymentHelperService.setPaymentStatusToReroute(payment);
            } else {
                log.info(" AutoPay Flag after checkAndPerformAutopay method called is " + autoPayFlag);
                logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_OTHER);
            }
            if (autoPayFlag) {
                updateActivityTable(payment);
                carrierInvoiceHeader.setAutoApprovalDate(LocalDateTime.now());
            }
        }
        autopayValidationService.validateToUpdateProcessEventTable(carrierPaymentProcessEvent, payment);
        autoPayRecordsList.add(autoPayFlag);
        log.info("Auto Pay Records List size is " + autoPayRecordsList.size());
        log.info(" Exiting -------------- getPaymentObjectAndProcessAutoPay method ");
        return autoPayRecordsList;
    }


    private void updateActivityTable(Payment payment) {
        log.info(" Inside -------------- updateActivityTable method when autopay is true ");
        ActivityUtil.saveActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY,
                CarrierPaymentConstants.ACT_AUTOPAY_PERFORM, CarrierPaymentConstants.ACT_APP,
                CarrierPaymentConstants.SYSTEM, activityService);
        elasticModificationService.updateElasticSearchForAutopay(payment);
        log.info(" Exiting -------------- updateActivityTable method when autopay is true ");
    }

    // AutoPay logging Reroute activity Changes Start
    public void logAutoRerouteActivity(Payment payment, String oldState) {
        log.info(" OriginalStatusFlowTypeCode in logAutoRerouteActivity method is " + oldState);
        Optional.ofNullable(payment).ifPresent(pay -> {
            if (oldState.equalsIgnoreCase(CarrierPaymentConstants.PENDING_AP)) {
                log.info(" Logging Auto Reroute Activity");
                ActivityUtil.saveActivity(payment, CarrierPaymentConstants.ACT_REROUTE_TYPE, CarrierPaymentConstants.ACT_AUTOPAY_REROUTE_PERFORM,
                        CarrierPaymentConstants.ACT_APP, CarrierPaymentConstants.SYSTEM, activityService);

            }
        });
    }

    // AutoPay logging Reroute activity Changes End
    public boolean checkAndPerformAutopay(Payment payment, List<ParameterListingDTO> parameterListings, CarrierInvoiceHeader carrierInvoiceHeader) {
        // LOCK THE PAYMENT WITH UPDATE ACTION FOR THE USER
        String systemUser = CarrierPaymentConstants.SYSTEM_USERID;
        lockUserService.lockCurrentUser(payment, systemUser, CarrierPaymentConstants.LOCK_UPDATE);
        var autoPayStatus = new AtomicBoolean(false);
        AtomicBoolean doAutoPay = new AtomicBoolean(false);
        log.info("checkAndPerformAutopay :: INSIDE LOGIC");
        // Validating Dispatch Status for cancelled loads
        if (payment.getDispatchStatus() != null && "Cancelled".equalsIgnoreCase(payment.getDispatchStatus().trim())) {
            logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_OTHER);
            return doAutoPay.get();
        }
        Optional.ofNullable(carrierInvoiceHeader).ifPresent(header -> {
            LocalDateTime scanTimestamp = carrierInvoiceHeader.getScanTimestamp();
            log.info("checkAndPerformAutopay :: Recent non Paper invoice ID "
                    + carrierInvoiceHeader.getCarrierInvoiceHeaderId() + carrierInvoiceHeader.getAutoApproveEligibleInd());
            boolean autoPayFlag = false;
            if (CarrierPaymentConstants.Y.equalsIgnoreCase(carrierInvoiceHeader.getAutoApproveEligibleInd())) {
                log.info("Before checkRulesForAutopay method invoke ");
                // JBI Autopay call starts
                if (CarrierPaymentConstants.Y.equalsIgnoreCase(carrierInvoiceHeader.getAutoApproveEligibleInd()) &&
                        (Objects.equals(payment.getStatusFlowTypeCode(), CarrierPaymentConstants.PENDING_AP)) &&
                        JBI.equalsIgnoreCase(payment.getGroupFlowTypeCode())) {
                    autoPayStatus.set(jbiAutoPayService.processJBILoadPayment(payment, parameterListings, scanTimestamp));
                    log.info("END JBI AUTOPAY ");
                }
                //JBI CALL ENDS
                else if (checkRulesForAutopay(payment, carrierInvoiceHeader.getCarrierInvoiceDetailList(), parameterListings, scanTimestamp)
                        && autoPayEditsService.validateChargeAmounstAgainsDb2(payment, carrierInvoiceHeader)) {
                    log.info("Before checkInvoiceRulesForAutopay method invoked autopayflag is " + autoPayFlag);
                    autoPayFlag = autoPayEditsService.checkInvoiceRulesForAutopay(payment, carrierInvoiceHeader, parameterListings);
                    log.info("After checkInvoiceRulesForAutopay method invoked autopayflag is " + autoPayFlag);

                    log.info("checkAndPerformAutopay :: autoPayFlag :: " + autoPayFlag);
                    doAutoPay.set(autoPayFlag);

                    Optional.ofNullable(carrierInvoiceHeader.getCarrierInvoiceDetailList()).filter(details -> doAutoPay.get())
                            .ifPresent(autoPay -> {
                                autoPayStatus.set(performAutopay(Optional.ofNullable(payment), carrierInvoiceHeader));
                                log.info("checkAndPerformAutopay :: Payment Status :: " + payment.getStatusFlowTypeCode());
                            });
                }
            }

        });
        // UNLOCK THE PAYMENT WITH UPDATE ACTION FOR THE USER
        lockUserService.unlockCurrentUser(payment, systemUser);
        return autoPayStatus.get();
    }

    public void postChargesStatusToTopic(Integer paymentId) {
        ChargeApprovalDTO chargeApprovalDTO = new ChargeApprovalDTO();
        Payment payment = paymentRepository.findByCarrierPaymentId(paymentId);
        chargeApprovalDTO.setBusinessUnit(payment.getGroupFlowTypeCode());
        chargeApprovalDTO.setLoadNumber(payment.getLoadNumber());
        chargeApprovalDTO.setFleetCode(payment.getFleetCode());
        chargeApprovalDTO.setBillingPartyCode(payment.getBillToPartyId());
        if (null != payment.getCarrierPaymentShipmentID() && null != payment.getCarrierPaymentShipmentID().getCarrierPaymentShipmentID()) {
            chargeApprovalDTO.setDivision(paymentShipmentRepository.findDivisionByCarrierPaymentShipmentID(payment.getCarrierPaymentShipmentID().getCarrierPaymentShipmentID()));
        }
        chargeApprovalDTO.setDispatchNumber(payment.getDispatchNumber());
        chargeApprovalDTO.setScacCode(payment.getScacCode());
        List<ChargesInfoDTO> chargesInfoDTOS = new ArrayList<>();
        payment.getCarrierPaymentChargeList().stream().filter(charge -> charge.getExpirationTimestamp() == null).forEach(charge -> {
            ChargesInfoDTO chargesInfoDTO = new ChargesInfoDTO();
            chargesInfoDTO.setChargeCode(charge.getChargeCode());
            chargesInfoDTO.setChargeDecisionDate(charge.getChargeDecisionDate());
            chargesInfoDTO.setApplyToCustomerChargeFlag(charge.getChargeApplyToCustomerIndicator());
            chargesInfoDTO.setChargeAmount(charge.getTotalApprovedChargeAmount());
            chargesInfoDTO.setChargeDecisionStatus(charge.getChargeDecisionCode());
            chargesInfoDTO.setStopNumber(charge.getStopNumber());
            chargesInfoDTOS.add(chargesInfoDTO);
        });
        chargeApprovalDTO.setChargesInfoDTOS(chargesInfoDTOS);
        postToChargesApprovalTopic(chargeApprovalDTO);

    }

    private void postToChargesApprovalTopic(ChargeApprovalDTO chargeApprovalDTO) {
        try {
            Map<String, Object> headers = new HashMap<>();
            headers.put("messageType ", "PAYMENT_APPROVAL");
            headers.put("JMSXGroupID", chargeApprovalDTO.getLoadNumber());
            producerTemplate.requestBodyAndHeaders("direct://postToChargesStatusTopic", chargeApprovalDTO, headers);
        } catch (Exception e) {
            log.error("Exception while posting Charges status Details" + e);
        }
    }

}

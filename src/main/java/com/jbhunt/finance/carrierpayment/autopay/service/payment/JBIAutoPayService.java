package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ParameterListingDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceDetail;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.repository.InvoiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.*;
import static com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargeCommonUtil.isNullOrEmpty;
import static com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargePredicateUtil.JBI_ZERO_CHARGES_CHECK;

@Slf4j
@Service
public class JBIAutoPayService extends AutoPayEditsService {
    @Autowired
    private AutoPayService autoPayService;

    @Autowired
    private ChargeDecisionService chargeDecisionService;

    @Autowired
    private PaymentStateService paymentStateService;
    @Autowired
    private InvoiceRepository invoiceRepository;

    public static List<Charge> extractInvoiceDuplicates(List<CarrierInvoiceDetail> invoiceList, final List<Charge> chargeList) {
        final List<Charge> finalChargeList = new ArrayList<>();
        List<String> zeroChargeList = new ArrayList<>();
        Set<String> setList = invoiceList.stream().map(x -> x.getChargeCode().trim()).collect(Collectors.toSet());
        setList.forEach(p -> {
            if (chargeList.stream().anyMatch(charge -> charge.getChargeCode().trim().equalsIgnoreCase(p))) {
                List<CarrierInvoiceDetail> duplicateList = invoiceList.stream().filter(x -> x.getChargeCode().equalsIgnoreCase(p)).collect(Collectors.toList());
                if (duplicateList.size() >= 2) { //&& !finalChargeList.contains(p)
                    BigDecimal totalAmount = duplicateList.stream().map(CarrierInvoiceDetail::getCarrierChargeUnitRateAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal chareAmount = chargeList.stream().filter(x -> x.getChargeCode().equalsIgnoreCase(p.trim())).map(Charge::getTotalApprovedChargeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (totalAmount.compareTo(chareAmount) == 0) {
                        finalChargeList.addAll(chargeList.stream().filter(x -> x.getChargeCode().equalsIgnoreCase(p.trim())).collect(Collectors.toList()));
                        zeroChargeList.add(p);
                    }
                } else if (duplicateList.size() == 1) {
                    zeroChargeListAdd(chargeList, p, duplicateList, finalChargeList, zeroChargeList);
                }
            } else if (JBI_ZERO_CHARGES_CHECK.test(p, invoiceList)) {
                zeroChargeList.add(p);
            }
        });
        if (!zeroChargeList.isEmpty() && zeroChargeList.size() == setList.size()) {
            return finalChargeList;
        } else {
            return new ArrayList<>();
        }
    }

    private static void zeroChargeListAdd(List<Charge> chargeList, String chargeCode, List<CarrierInvoiceDetail> duplicateList, List<Charge> finalChargeList, List<String> zeroChargeList) {
        BigDecimal chareAmount = chargeList.stream().filter(x -> x.getChargeCode().equalsIgnoreCase(chargeCode.trim())).map(Charge::getTotalApprovedChargeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (duplicateList.get(0).getCarrierChargeUnitRateAmount().compareTo(chareAmount) == 0) {
            finalChargeList.addAll(chargeList.stream().filter(x -> x.getChargeCode().equalsIgnoreCase(chargeCode.trim())).collect(Collectors.toList()));
            zeroChargeList.add(chargeCode);
        }
    }

    public boolean processJBILoadPayment(Payment payment, List<ParameterListingDTO> parameterListings, LocalDateTime scanTimestamp) {
        AtomicBoolean autoPayIsASuccess = new AtomicBoolean(false);
        AtomicBoolean autoPayFlag = new AtomicBoolean(false);
        // Fetching the most recent non processed invoice for the partial autopay.
        List<CarrierInvoiceHeader> carrierInvoiceHeaderList = invoiceRepository
                .findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                        payment.getCarrierPaymentId(), CarrierPaymentConstants.PAPER, PROCESSED);
        for (CarrierInvoiceHeader carrierInvoiceHeader : carrierInvoiceHeaderList) {
            if (carrierInvoiceHeader.getInvoiceStatusCode().equalsIgnoreCase(CarrierPaymentConstants.INVOICE_STATUS_REJECTED)) {
                log.info("Fail for Rejected invoice");
                log.info(" AutoPay Flag after checkAndPerformAutopay method called is " + autoPayFlag);
                autoPayService.logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_IVCREJECT);
                break;
            }
            if (autoPayService.checkRulesForAutopay(payment, carrierInvoiceHeader.getCarrierInvoiceDetailList(), parameterListings,scanTimestamp)
                    && validateChargeAmounstAgainsDb2(payment, carrierInvoiceHeader)) {
                log.info("After check Invoice Rules For Autopay method invoked autopay flag is " + autoPayFlag);
                List<Charge> chargeList = checkJBIRulesForAutopay(payment, carrierInvoiceHeader, parameterListings);
                if (chargeList.isEmpty()) {
                    break;
                }
                Optional.ofNullable(chargeList).filter(details -> !chargeList.isEmpty())
                        .ifPresent(autoPay -> {
                            // RETURNS lOAD STATUS
                            TxnStatusDTO txnStatusDTO = performJBIAutopay(payment, carrierInvoiceHeader, chargeList, parameterListings);
                            autoPayIsASuccess.set(txnStatusDTO.isSuccess());
                            autoPayFlag.set(txnStatusDTO.isAutoPayStatus());
                            log.info("Load status at the end of transaction :: " + payment.getStatusFlowTypeCode());
                        });
            } else {
                autoPayFlag.set(false);
                log.info("AutoApprove flag at the end of transaction :: " + autoPayFlag.get());
                break;
            }
            if (autoPayIsASuccess.get() && autoPayFlag.get()) {
                log.info("AutoPay Transaction flag :: " + autoPayIsASuccess.get()+
                        "AutoApprove flag at the end of transaction :: "+autoPayFlag.get());
                break;
            } else if (!autoPayIsASuccess.get()) {
                log.info("AutoPay Transaction flag at the end :: " + autoPayIsASuccess.get());
                break;
            }
        }
        return autoPayIsASuccess.get();
    }

    public void acknowledgeInvoicesForJBIAutoPay(Payment payment) {

        List<CarrierInvoiceHeader> invoices = payment.getCarrierInvoiceHeaderList().stream()
                .filter(invoice -> (!invoice.getInvoiceStatusCode()
                        .equalsIgnoreCase(PROCESSED)
                )).filter(invoice -> !invoice.getInvoiceStatusCode()
                        .equalsIgnoreCase(CarrierPaymentConstants.INVOICE_STATUS_REJECTED))
                .collect(Collectors.toList());
        Optional.ofNullable(invoices).filter(list -> !list.isEmpty())
                .ifPresent(paperInvoiceList -> {
                    log.info("check JBI Autopay For EDI :: Invoice List size :: "
                            + paperInvoiceList.size());
                    invoices.stream().forEach(paperInvoice -> paperInvoice
                            .setInvoiceStatusCode(CarrierPaymentConstants.INVOICE_STATUS_ACKLNGD));
                    log.info("check Autopay For EDI :: Invoice List Status :: "
                            + invoices.get(0).getInvoiceStatusCode());
                    invoiceRepository.saveAll(invoices);
                });

    }

    public List<Charge> checkJBIRulesForAutopay(Payment payment, CarrierInvoiceHeader ediInvoice, List<ParameterListingDTO> parameterListings) {
        List<CarrierInvoiceDetail> invoiceDetail = ediInvoice.getCarrierInvoiceDetailList();
        List<ParameterListingDTO> jbiParameterAutoApprv = autoPayService.getRequiredParameterList(parameterListings, CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGEGROUP);
        log.info("check JBI Rules For Autopay Started ");
        List<Charge> unActionedChargeLists = Optional.ofNullable(payment.getCarrierPaymentChargeList())
                .map(chargeList -> chargeList.stream()
                        .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                        .filter(decision -> decision.getChargeDecisionCode() == null).collect(Collectors.toList())).orElse(new ArrayList<>());
        ArrayList<CarrierInvoiceDetail> invoiceDetalgroupList = new ArrayList<>();
        ArrayList<CarrierInvoiceDetail> othrinvoiceDetalList = new ArrayList<>();
        ArrayList<Charge> groupchargeList = new ArrayList<>();
        ArrayList<Charge> otherchargeList = new ArrayList<>();
        unActionedChargeLists.forEach(charge ->
                getChargeList(jbiParameterAutoApprv, groupchargeList, otherchargeList, charge)
        );
        //Clubbing the Transportation group charge codes.
        ArrayList<Charge> approveGroupchargeList = new ArrayList<>();
        ArrayList<Charge> approveOtherchargeList = new ArrayList<>();
        payment.getCarrierPaymentChargeList().forEach(charge ->
                getChargeList(jbiParameterAutoApprv, approveGroupchargeList, approveOtherchargeList, charge)
        );
        Optional.ofNullable(invoiceDetail).filter(list -> !list.isEmpty())
                .ifPresent(invoiceDetailList1 ->
                        invoiceDetailList1.forEach(invoiceDetal -> {
                            getInvoiceDetailList(jbiParameterAutoApprv, invoiceDetalgroupList, othrinvoiceDetalList, invoiceDetal);
                        }));

        Optional<CarrierInvoiceDetail> transitInvCharge = invoiceDetalgroupList.stream().filter(val3 -> val3.getChargeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.TRANSIT)).findAny();
        Optional<CarrierInvoiceDetail> fuelInvCharge = invoiceDetalgroupList.stream().filter(val3 -> val3.getChargeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.FUELSURCHG)).findAny();

        BigDecimal invoicegroupamount = invoiceDetalgroupList.stream().map(CarrierInvoiceDetail::getCarrierChargeUnitRateAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal chrgegroupamount = groupchargeList.stream().map(Charge::getTotalApprovedChargeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal mulfuelInvChargeamt = invoiceDetalgroupList.stream().filter(val3 -> val3.getChargeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.FUELSURCHG)).map(CarrierInvoiceDetail::getCarrierChargeUnitRateAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal multransitInvChargeamt = invoiceDetalgroupList.stream().filter(val3 -> val3.getChargeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.TRANSIT)).map(CarrierInvoiceDetail::getCarrierChargeUnitRateAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!validateFuelSurChargeAmountAndInvoiceChargeAmount(fuelInvCharge, mulfuelInvChargeamt, transitInvCharge, multransitInvChargeamt, payment)) {
            return new ArrayList<>();
        }

        List<Charge> chargeList = new ArrayList<>();
        // Checking the current group is not empty for the group Zero Approvals along the other charge list.
        if (!invoiceDetalgroupList.isEmpty() && invoicegroupamount.compareTo(chrgegroupamount) == 0) {
            if (!approveGroupchargeList.isEmpty() && approveGroupchargeList.stream().anyMatch(z -> "Approve".equals(z.getChargeDecisionCode()))) {
                log.info("Already approved charge code on the Transportation group. ");
                autoPayService.logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_CHGLSTEXCP);
                return new ArrayList<>();
            } else {
                chargeList.addAll(groupchargeList);
            }
        }
        List<Charge> matchedChargeList = extractInvoiceDuplicates(othrinvoiceDetalList, otherchargeList);
        if (!matchedChargeList.isEmpty()) {

            chargeList.addAll(matchedChargeList);
        }
        if (chargeList.isEmpty()) {
            autoPayService.logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_CHGLSTEXCP);
        }
        return chargeList;
    }

// Auto Pay New WS Changes End
// Auto Pay Changes End

    public TxnStatusDTO saveJBIAutopayDetails(ChargeDTO chargeDTO, Payment payment) {
        TxnStatusDTO transactionStatus = chargeDecisionService.chargeDecision(chargeDTO, payment);
        List<Charge> allActiveCharges = chargeDecisionService.verifyChargeApprove(payment);
        Boolean flag = allActiveCharges.stream().allMatch(x -> APPROVED.equalsIgnoreCase(x.getChargeDecisionCode()) || PAID.equalsIgnoreCase(x.getChargeDecisionCode()));
        log.info("Inside perform JBI Autopay :: payment status :: " + payment.getStatusFlowTypeCode());

        if (transactionStatus.isSuccess() && Boolean.TRUE.equals(flag)) {
            transactionStatus.setAutoPayStatus(true);
            payment.setStatusFlowTypeCode(CarrierPaymentConstants.AUTO_APPROVED);
            paymentStateService.createNewPaymentStateAndSavePayment(payment);
            log.info("END performAutopay :: payment status :: " + payment.getStatusFlowTypeCode());
        }

        return transactionStatus;
    }

    private TxnStatusDTO performJBIAutopay(Payment payment, CarrierInvoiceHeader ediInvoice, List<Charge> chargeList, List<ParameterListingDTO> parameterListings) {
        AtomicBoolean autoPayStatus = new AtomicBoolean(false);
        TxnStatusDTO transaction = new TxnStatusDTO();
        Optional.of(payment).filter(pay -> Objects.equals(pay.getStatusFlowTypeCode(), CarrierPaymentConstants.PENDING_AP))
                .ifPresent(paymentForAutoApprove -> {
                            ChargeDTO chargeDTO = autoPayService.populateChargeDTO(Optional.of(payment), ediInvoice);
                            List<Integer> chargeIds = chargeList.stream()
                                    .filter(chargeDecision -> chargeDecision.getChargeDecisionCode() == null)
                                    .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null).map(Charge::getChargeId).collect(Collectors.toList());
                            List<Integer> zeroChargeIds = processZeroAmountCharge(chargeList, parameterListings, payment);
                            if (!zeroChargeIds.isEmpty()) {
                                List<Integer> chargeIdsWithOutZero = chargeList.stream().filter(chargeDecision -> chargeDecision.getChargeDecisionCode() == null)
                                        .filter(dec -> dec.getTotalApprovedChargeAmount().compareTo(BigDecimal.ZERO) != 0)
                                        .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null).map(Charge::getChargeId).collect(Collectors.toList());
                                boolean approvedChargeIds = payment.getCarrierPaymentChargeList().stream()
                                        .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                                        .filter(chargeId -> chargeIdsWithOutZero.stream().noneMatch(y -> chargeId.getChargeId().equals(y)))
                                        .filter(chargeDecision -> chargeDecision.getChargeDecisionCode() == null)
                                        .allMatch(dec -> dec.getTotalApprovedChargeAmount().compareTo(BigDecimal.ZERO) == 0);
                                if (approvedChargeIds) {

                                    chargeIds.addAll(zeroChargeIds);
                                }
                            }
                            chargeDTO.setChargeIdList(chargeIds);
                            TxnStatusDTO transactionStatus = saveJBIAutopayDetails(chargeDTO, payment);
                            autoPayStatus.set(transactionStatus.isSuccess());
                            transaction.setAutoPayStatus(transactionStatus.isAutoPayStatus());
                            transaction.setSuccess(transactionStatus.isSuccess());
                        }
                );
        if (transaction.isAutoPayStatus()) {
            acknowledgeInvoicesForJBIAutoPay(payment);
            log.info(" Invoices Acknowledged ");
        }
        return transaction;
    }

    private List<Integer> processZeroAmountCharge(List<Charge> tenderedChargeList, List<ParameterListingDTO> parameterAAprChgGrp, Payment payment) {
        return payment.getCarrierPaymentChargeList().stream()
                .filter(chargeDecision -> chargeDecision.getChargeDecisionCode() == null)
                .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                .filter(dec -> dec.getTotalApprovedChargeAmount().compareTo(BigDecimal.ZERO) == 0).map(Charge::getChargeId).collect(Collectors.toList());
    }

    public boolean jBIInvoiceAndTenderedChargeTransportationGroupAmountValidation(BigDecimal invoiceTransportationGroupAmount, BigDecimal tenderedChargeTransportationGroupAmount, List<CarrierInvoiceDetail> invoiceDetailTransportationGrpList, List<Charge> tenderedTransportationGrpChargeList) {
        boolean checkExactListMatchVal = false;
        if (((isNullOrEmpty(tenderedTransportationGrpChargeList) && isNullOrEmpty(invoiceDetailTransportationGrpList)) ||
                (tenderedTransportationGrpChargeList.isEmpty() &&
                        invoiceTransportationGroupAmount.compareTo(BigDecimal.ZERO) == 0) || (invoiceDetailTransportationGrpList.isEmpty() && tenderedChargeTransportationGroupAmount.compareTo(BigDecimal.ZERO) == 0) || (!isNullOrEmpty(tenderedTransportationGrpChargeList)
                && !isNullOrEmpty(invoiceDetailTransportationGrpList)))) {
            if (invoiceTransportationGroupAmount.compareTo(BigDecimal.ZERO) == 0) {
                checkExactListMatchVal = true;
            } else if (invoiceTransportationGroupAmount.compareTo(tenderedChargeTransportationGroupAmount) == 0) {
                checkExactListMatchVal = true;
            } else {
                checkExactListMatchVal = false;
            }
        }
        return checkExactListMatchVal;
    }


    public Boolean validateInvoiceAndTenderedList(List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList, List<Charge> finalTenderedChargeListMinusParameterList, boolean checkExactListMatchVal) {
        checkExactListMatchVal = false;
        for (CarrierInvoiceDetail carrierInvoiceDetail : finalInvoiceDetailMinusParameterList) {
            if (finalTenderedChargeListMinusParameterList.stream().filter(chrge -> chrge.getChargeCode().trim().equalsIgnoreCase(carrierInvoiceDetail.getChargeCode().trim())).filter(charge ->
                    charge.getTotalApprovedChargeAmount().compareTo(carrierInvoiceDetail.getCarrierChargeUnitRateAmount()) == 0).findAny().isPresent()) {
                checkExactListMatchVal = true;
            } else if (carrierInvoiceDetail.getCarrierChargeUnitRateAmount().compareTo(BigDecimal.ZERO) == 0) {
                checkExactListMatchVal = true;
            }
        }


        return checkExactListMatchVal;
    }


}

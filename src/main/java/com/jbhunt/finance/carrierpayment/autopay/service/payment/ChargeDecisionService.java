package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.ApprovalTransaction;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.ChargeOverride;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.repository.ApprovalTransactionRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.ChargeRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.SupplierRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.ChargeIntegrationService;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.CalcAmountUtil;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargeCommonUtil;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargeDecisionServiceUtil;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargeEntityUtil;
import com.jbhunt.infrastructure.exception.JBHValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentErrorConstants.SAVING_TO_DB_FAILED;

@Slf4j
@Service
public class ChargeDecisionService {

    @Autowired
    private ChargeRepository chargeRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ChargeValidationService chargeValidationService;
    @Autowired
    private PostChargeDecisionService postChargeDecisionService;
    @Autowired
    private PaymentHelperService paymentHelperService;
    @Autowired
    private ChargeIntegrationService chargeIntegrationService;
    @Autowired
    private LockUserService lockUserService;
    @Autowired
    private ChargeAuditDetailService chargeAuditDetailService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private ApprovalTransactionRepository approvalTransactionRepository;
    /**
     * Method to update charge decision action
     *
     * @param chargeDTO
     * @return
     */
    @Transactional
    public TxnStatusDTO chargeDecision(ChargeDTO chargeDTO, Payment payment) {
        log.info("Inside ChargeDecisionService : chargeDecision: " + chargeDTO.getLoadNumber());
        log.info("ChargeDecisionService :: chargeDTO.isAutoPay() :: " + chargeDTO.isAutoPay());
        TxnStatusDTO txnStatusDTO = new TxnStatusDTO();
        ActivityDTO activityDTO = new ActivityDTO();

        Payment existingPayment = paymentRepository.findByCarrierPaymentId(chargeDTO.getPaymentId());
        TxnStatusDTO notStale = lockUserService.checkForNotStale(existingPayment);
        chargeDTO.setEstimatedDueDate(existingPayment.getEstimatedDueDate());
        chargeDTO.setWorkFlowStatus(existingPayment.getStatusFlowTypeCode());
        chargeDTO.setCurrencyCode(existingPayment.getCurrencyCode());

        if (notStale.isSuccess() || chargeDTO.isAutoPay()) {

            // VALIDATIONS
            Map<String, Boolean> errorMap = new HashMap<>();
            errorMap= chargeValidationService.validateChargeCodes(chargeDTO,errorMap,payment);
            if(CollectionUtils.isNotEmpty(errorMap.keySet())) {
                ChargeCommonUtil.txnStatusErrorList(txnStatusDTO, errorMap);
                ChargeCommonUtil.logStatusDTO(txnStatusDTO);
                txnStatusDTO.setSuccess(false);
                return txnStatusDTO;
            }
            errorMap = chargeValidationService.approveRejectChargeValidations(chargeDTO);
            log.info("ERROR MAP" + errorMap);
            // Amount validation start
            chargeDTO = chargeValidationService.validateAmountforEachCharge(chargeDTO, errorMap);
            chargeValidationService.updateInvoiceDetails(chargeDTO, errorMap);

            Boolean validToUpdate = errorMap.values().stream().filter(Boolean.FALSE::equals).findAny().orElse(true);
            log.info("chargeDecision :: validToUpdate: " + validToUpdate);

            if (Boolean.TRUE.equals(validToUpdate)) {
                // Amount validation start
                // APPROVE/REJECT CHARGE
                log.info("CHARGE .ACTION :: " + chargeDTO.getChargeDecisionCode());
                log.info("QUICKPAY WAIVER INDICATOR :: " + chargeDTO.getQuickPayWaiverIndicator());
                Boolean isSaved = updateCharges(chargeDTO);
                txnStatusDTO.setSuccess(isSaved);
                // ON SAVE SUCCESS
                postChargeDecisionService.afterSave(chargeDTO, txnStatusDTO, activityDTO, existingPayment);

                // LOCK THE PAYMENT WITH UPDATE ACTION FOR THE USER
                String actionedUser = ChargeDecisionServiceUtil.getActionedUser(chargeDTO);
                lockUserService.lockCurrentUser(existingPayment, actionedUser, CarrierPaymentConstants.LOCK_UPDATE);

                // PROCESS & POST MESSAGE LOGIC
                if (!chargeDTO.isAutoPay()) {
                    log.info("CHARGE ACTION :: PROCESS");
                    postChargeDecisionService.processPayment(txnStatusDTO, existingPayment);
                    log.info("CHARGE DECISION CALL ENDS : PMNT STATUS :: " + existingPayment.getStatusFlowTypeCode());
                }

            }

            ChargeCommonUtil.txnStatusErrorList(txnStatusDTO, errorMap);
            ChargeCommonUtil.logStatusDTO(txnStatusDTO);
            //Added for Auto Pay Validation
            log.info("Auto pay exception CALL ENDS : PMNT Status:: " + chargeDTO.isAutoPay());
            if (chargeDTO.isAutoPay() && (!txnStatusDTO.getErrorList().isEmpty())) {
                txnStatusDTO.setSuccess(false);
            }

        } else {
            txnStatusDTO = ChargeCommonUtil.stalePayment(notStale);
        }

        return txnStatusDTO;
    }

    public Integer updateCarrierPaymentApprovalTransaction(ChargeDTO chargeDTO, List<Charge> chargeList) {
        ApprovalTransaction approvalTransaction = new ApprovalTransaction();
        approvalTransaction.setCarrierPayment(chargeDTO.getPaymentId());
        approvalTransaction.setCarrierInvoiceHeaderID(chargeDTO.getHeaderId());
        approvalTransaction.setCarrierPaymentWorkflowStatusTypeCode(chargeDTO.getWorkFlowStatus());
        approvalTransaction.setEstimatedDueDate(chargeDTO.getEstimatedDueDate());
        approvalTransaction.setRequirementCompletionTimestamp(chargeDTO.getInvoiceReceivedTermsDate());
        var supplier = supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(chargeDTO.getScacCode(),chargeDTO.getCurrencyCode());
        if(null!= supplier && null!= supplier.getParentSupplierID()) {
            var parentSupplier = supplierService.retrieveSupplierBySupplierId(supplier.getParentSupplierID());
            approvalTransaction.setFactoringSupplierID(parentSupplier.getSupplierID());
        }
        approvalTransaction.setCarrierInvoiceNumberExtended(chargeDTO.getInvoiceNumber());
        approvalTransaction.setDecisionDate(LocalDate.now());
        approvalTransaction.setDecisionUserID(CarrierPaymentConstants.SYSTEM);
        approvalTransaction.setCarrierPaymentChargeDecisionCode(chargeDTO.getChargeDecisionCode());
        approvalTransaction.setCarrierPaymentChargeDecisionReasonCode(chargeDTO.getReasonCode());
        approvalTransaction.setCurrencyCode(chargeDTO.getCurrencyCode());
        approvalTransaction.setTransactionAmount(CalcAmountUtil.calculateApprovedAmount(chargeList));
        approvalTransactionRepository.save(approvalTransaction);
        return approvalTransaction.getApprovalTransactionID();
    }

    private Boolean updateCharges(ChargeDTO chargeDTO) {

        log.info("Auto pay exception CALL ENDS : PMNT STATUS :: " + chargeDTO.isAutoPay());
        List<Charge> chargeList = chargeRepository.findAllById(chargeDTO.getChargeIdList());
        List<Charge> savedList = null;
        chargeList.forEach(charge -> ChargeEntityUtil.updateEachCharge(chargeDTO, charge));
        if(CollectionUtils.isNotEmpty(chargeList))
        {
            Integer transactionID = updateCarrierPaymentApprovalTransaction(chargeDTO,chargeList);
            chargeList.forEach(charge1 -> charge1.setCarrierApprovalTransaction(transactionID));
        }

        // FOR DB2
        List<Integer> externalChargeIDList = chargeList.stream().map(Charge::getExternalChargeID).collect(Collectors.toList());

        // DB2 - REJECT
        Optional.ofNullable(chargeDTO.getChargeDecisionCode())
                .filter(decision -> Objects.equals(decision, CarrierPaymentConstants.REJECT_STATUS))
                .ifPresent(reject -> {
                    List<Integer> externalChargeBillingIDList = chargeList.stream().filter(Objects::nonNull).map(Charge::getExternalChargeBillingID).collect(Collectors.toList());
                    if (null != externalChargeBillingIDList && !externalChargeBillingIDList.isEmpty()) {
                        while (externalChargeBillingIDList.remove(null)) ;
                        externalChargeIDList.addAll(externalChargeBillingIDList);
                    }
                });

        chargeDTO.setExternalChargeIDList(externalChargeIDList);

        try {
            savedList = chargeRepository.saveAll(chargeList);

            if(CollectionUtils.isNotEmpty(savedList)){
                savedList.forEach( s -> chargeAuditDetailService.saveCarrierPaymentChargeAuditDetail(s.getCarrierPayment(),s,s.getChargeDecisionCode()));
            }

        } catch (Exception e) {
            log.error("Update charge decision Exception ", e);
            throw new JBHValidationException(SAVING_TO_DB_FAILED);
        }

        return Optional.ofNullable(savedList).filter(list -> !list.isEmpty()).map(valSaved -> true).orElse(false);
    }

    @Transactional
    public Boolean autopaychargeDecision(Payment payment, List<ChargeDTO> chargeDTO) {
        try {
            List<Charge> savedList = null;
            log.info("autopaychargeDecision  : PMNT STATUS :: " + payment.getCarrierPaymentId());
            savedList =  chargeRepository.saveAll(payment.getCarrierPaymentChargeList());
            if(CollectionUtils.isNotEmpty(savedList)){
                savedList.forEach( s -> chargeAuditDetailService.saveCarrierPaymentChargeAuditDetail(s.getCarrierPayment(),s,s.getChargeDecisionCode()));
            }
            chargeDTO.forEach(charge -> charge.setWorkFlowGroupType(payment.getGroupFlowTypeCode()));
            return chargeIntegrationService.addAndeditcharge(chargeDTO);

        } catch (Exception e) {
            log.error("Update charge decision Exception ", e);
            throw new JBHValidationException(SAVING_TO_DB_FAILED);
        }
    }

    public ChargeOverride updateOvverideCharge(Charge existingCharge) {
        return paymentHelperService.updateOvverideCharge(existingCharge);

    }

    public boolean validateChargeAmount(ChargeDTO chargeDTO, Payment payment) {
        Map<String, Boolean> errorMap = chargeValidationService.approveRejectChargeValidations(chargeDTO);
        log.info(CarrierPaymentConstants.LOG_ERROR_MAP + "in validateChargeAmount() method " + errorMap);
        chargeValidationService.validateAmountforEachCharge(chargeDTO, errorMap);
        Boolean validToUpdate = errorMap.values().stream().filter(Boolean.FALSE::equals).findAny().orElse(true);

        // Total Tendered Amount vs Total DB2 Amount validation start
        if (Boolean.TRUE.equals(validToUpdate)) {
            Optional<BigDecimal> totalAmount = Optional.ofNullable(payment.getCarrierPaymentChargeList())
                    .map(chargeList -> chargeList.stream()
                            .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                            .filter(decision -> decision.getChargeDecisionCode() == null
                            ).map(Charge::getTotalApprovedChargeAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

            BigDecimal db2Amount = chargeValidationService.validateTotalAmount(chargeDTO);

            log.info("chargeDecision :: validToUpdate in validateChargeAmount() is : " + validToUpdate);
            log.info("Total Tendered Amount is  " + totalAmount);
            log.info("Db2 amount is  " + db2Amount);
            if (Objects.nonNull(db2Amount) && totalAmount.isPresent() && db2Amount.compareTo(totalAmount.get()) == 0) {
                log.info("chargeDecision :: validToUpdate: " + validToUpdate);
                validToUpdate = true;
            } else {
                validToUpdate = false;
            }
        }
        return validToUpdate;
    }

    public List<Charge> verifyChargeApprove(Payment payment) {
       return  chargeRepository
                .findByCarrierPaymentCarrierPaymentIdAndExpirationTimestampIsNull(
                        payment.getCarrierPaymentId());

    }

    // Auto Pay Archived load changes Start
    public int getCountOfRecords(String loadNumber) {
        return chargeIntegrationService.getCountOfRecords(loadNumber);
    }
    // Auto Pay Archived load changes End
}

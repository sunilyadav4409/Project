package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.*;
import com.jbhunt.finance.carrierpayment.autopay.repository.*;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ActivityUtil;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.CarrierPaymentUtil;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargeCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.jbhunt.finance.carrierpayment.autopay.util.payment.CarrierPaymentPredicateUtil.AND;

@Slf4j
@Service
public class PaymentActivityUpdateService {

    @Autowired
    private ChargeRepository chargeRepository;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private WorkflowGroupAssociationRepository wGroupAssociationRepository;
    @Autowired
    private PaymentStateService paymentStateService;
    @Autowired
    private PaymentStateLogRepository stateLogRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private ChargeUtilService chargeUtilService;
    @Autowired
    private EstimationDueDateService estimationDueDateService;

    public void updateInvoiceStatus(ChargeDTO chargeDTO) {
        Optional.ofNullable(AND.test(Optional.ofNullable(chargeDTO.getInvoiceNumber()).isPresent(),
                Optional.ofNullable(chargeDTO.getHeaderId()).isPresent())).filter(Boolean.TRUE::equals)
                .ifPresent(approve -> {
                    CarrierInvoiceHeader existingHeader = invoiceRepository.findById(chargeDTO.getHeaderId()).get();
                    if(CarrierPaymentConstants.ICS.equalsIgnoreCase(chargeDTO.getWorkFlowGroupType())) {
                        chargeDTO.setInvoiceReceivedTermsDate( estimationDueDateService.getTermStartDateForApproveScenarioByCurrentInvoicesScanDate(chargeDTO.getPaymentId(),
                                existingHeader.getScanTimestamp()));
                    }
                    Optional.ofNullable(existingHeader).ifPresent(invoice -> {
                        invoice.setInvoiceStatusCode(CarrierPaymentConstants.INVOICE_STATUS_PROCESSED);
                        invoiceRepository.save(invoice);
                    });
                });
    }

    // totalChargeAmount,totalApprovedInvoiceAmt,paymentActionTypeCode,statusFlowTypeCode
    public Payment updatePayment(Payment existingPayment, boolean noPaperworkStatus) {

        Optional.ofNullable(existingPayment).ifPresent(payment -> {

            List<Charge> allActiveCharges = chargeRepository
                    .findByCarrierPaymentCarrierPaymentIdAndExpirationTimestampIsNull(
                            existingPayment.getCarrierPaymentId());

            ChargeCommonUtil.setTotalApprovedInvoiceAmt(allActiveCharges, payment);

            setStatusFlowTypeCode(noPaperworkStatus, payment);

            setTotalChargeAmount(allActiveCharges, payment);

            payment.setPaymentActionTypeCode(null);
            paymentRepository.save(payment);
        });

        return existingPayment;
    }


    public void chargeApproveRejectActivityCreation(ChargeDTO chargeDTO, ActivityDTO activityDTO) {

        Optional.ofNullable(chargeDTO.getChargeDecisionCode())
                .filter(decision -> Objects.equals(decision, CarrierPaymentConstants.APPROVED))
                .ifPresent(isApprove -> CarrierPaymentUtil.setActivityPerformed(activityDTO,
                        CarrierPaymentConstants.CFP_APRV, CarrierPaymentConstants.CHRG_APRVD));

        Optional.ofNullable(chargeDTO.getChargeDecisionCode())
                .filter(decision -> Objects.equals(decision, CarrierPaymentConstants.REJECT_STATUS))
                .ifPresent(isReject -> CarrierPaymentUtil.setActivityPerformed(activityDTO,
                        CarrierPaymentConstants.CFP_APRV, CarrierPaymentConstants.CHRG_RJCTD));

        ActivityUtil.createActivityRecord(activityDTO, chargeDTO, activityService);
    }

    private void setStatusFlowTypeCode(boolean noPaperworkStatus, Payment payment) {
        if (!noPaperworkStatus) {
            paymentStateService.createNewPaymentState(payment);
            log.info("changedToPaperwork Due to Lumper/TONUCharge Add");
        } else {
            // i) CHANGE STATUS TO LTLAPPRV - NEW CHARGE ADDED
            // ii) STATE LOG CREATION - Status change from Queue to Queue
            // (Processed -> LTLApprv,Ignore to normal)
            statusChangeOnAddCharge(payment);
        }
    }

    private void setTotalChargeAmount(List<Charge> allActiveCharges, Payment payment) {
        BigDecimal totalAmount = chargeUtilService.getAmount(allActiveCharges);
        payment.setTotalChargeAmount(totalAmount);
        log.info("CHARGE SAVE/UPDATE :: updatePayment :: TotalChargeAmount " + totalAmount);
    }

    public void statusChangeOnAddCharge(Payment payment) {

        String newStatusFlow = updateWorkFlowStatus(payment);
        log.info("PAYMENT NEW STATUS : CHARGE/LINE ITEM ACTION : newStatusFlow: " + newStatusFlow);

        Optional.ofNullable(newStatusFlow).ifPresent(newStatus -> {
            payment.setStatusFlowTypeCode(newStatus);
            // PAYMENT STATE LOG
            log.info("CREATE PAYMENT STATE LOG FOR :: " + newStatusFlow);
            paymentStateService.createNewPaymentState(payment);
        });

        // PAYMENT STATE LOG FOR IGNORE FLOW
        Optional.ofNullable(payment.getPaymentActionTypeCode())
                .filter(actiontype -> Objects.equals(actiontype, CarrierPaymentConstants.IGNORE))
                .ifPresent(ignoreAction -> {
                    log.info("CREATE PAYMENT STATE LOG FOR IGNORE FLOW :: " + newStatusFlow);
                    paymentStateService.createNewPaymentState(payment);
                });

    }

    private String updateWorkFlowStatus(Payment existingPayment) {
        log.info("POST PROCESSED STATE - NEXT PAYMENT STATUS");
        log.info("CURRENT PAYMENT STATE :: " + existingPayment.getStatusFlowTypeCode());

        List<PaymentStateLog> paymentRerouted = stateLogRepository
                .findByCarrierPaymentIdCarrierPaymentIdAndStatusFlowTypeCode(existingPayment.getCarrierPaymentId(),
                        CarrierPaymentConstants.REROUTE);
        log.info("NEXT PAYMENT STATUS: paymentRerouted size: " + paymentRerouted.size());

        WorkflowGroupAssociation workflowGroupAssociation = null;
        if (CollectionUtils.isEmpty(paymentRerouted)
                && Objects.equals(existingPayment.getGroupFlowTypeCode(), CarrierPaymentConstants.ICS)
                && Objects.equals(existingPayment.getStatusFlowTypeCode(), CarrierPaymentConstants.CANCEL_JOB)) {
            log.info("NEXT PAYMENT STATUS: NON REROUTED ICS RcvCnclJob LOAD");
            workflowGroupAssociation = wGroupAssociationRepository
                    .findByWorkflowGroupTypeCodeAndWorkflowStatusEventCode(existingPayment.getGroupFlowTypeCode(),
                            CarrierPaymentConstants.EVENT_PRE_PRCS);
            log.info("NEXT STATUS: " + workflowGroupAssociation.getWorkflowStatusEventCode());
        } else {
            log.info("NEXT PAYMENT STATUS: ALL LOADS EXCEPT NON REROUTED ICS RcvCnclJob LOAD");
            workflowGroupAssociation = wGroupAssociationRepository
                    .findByWorkflowGroupTypeCodeAndWorkflowStatusEventCode(existingPayment.getGroupFlowTypeCode(),
                            CarrierPaymentConstants.EVENT_CHARGES_POST_APPVL);
            log.info("NEXT STATUS: " + workflowGroupAssociation.getWorkflowStatusEventCode());
        }

        return Optional.ofNullable(workflowGroupAssociation)
                .filter(inProcessedState -> Arrays.asList(CarrierPaymentConstants.PAID, CarrierPaymentConstants.APPROVE,
                        CarrierPaymentConstants.REJECT_CHARGE, CarrierPaymentConstants.AUTO_APPROVED,
                        CarrierPaymentConstants.CANCEL_JOB).contains(existingPayment.getStatusFlowTypeCode()))
                .map(WorkflowGroupAssociation::getWorkflowStatusTypeCode).orElse(null);
    }
}

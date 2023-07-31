package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.service.ChargeIntegrationService;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargeDecisionServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class PostChargeDecisionService {

    @Autowired
    private  ChargeAuditBuilder chargeAuditBuilder;
    @Autowired
    private  PaymentActivityUpdateService paymentActivityUpdateService;
    @Autowired
    private ElasticModificationService elasticModificationService;
    @Autowired
    private ProcessPaymentService processPaymentService;
    @Autowired
    private MessagePostService messagePostService;
    @Autowired
    private  ChargeIntegrationService chargeIntegrationService;
    @Autowired
    private  BillableChargeService billableChargeService;

    @Transactional
    public void afterSave(ChargeDTO chargeDTO, TxnStatusDTO txnStatusDTO, ActivityDTO activityDTO,
                          Payment existingPayment) {

        if (txnStatusDTO.isSuccess()) {

            // INVOICE STATUS UPDATED
            paymentActivityUpdateService.updateInvoiceStatus(chargeDTO);

            // PAYMENT UPDATE
            paymentActivityUpdateService.updatePayment(existingPayment, true);

            // DB2 BACKFILL APPROVE/REJECT
            Optional.ofNullable(chargeDTO.getChargeDecisionCode()).ifPresent(decision -> {
                chargeDTO.setProjectCode(existingPayment.getProjectCode());
                Optional.ofNullable(existingPayment.getDispatchTokenId()).map(Integer::valueOf)
                        .ifPresent(chargeDTO::setJobId);
                log.info("APPROVE CHARGE - START BACKFILL : chargeDTO :: " + chargeDTO);
                updateDB2Data(chargeDTO, txnStatusDTO);
            });

            if ("LTL".equalsIgnoreCase(existingPayment.getGroupFlowTypeCode())) {
                billableChargeService.checkApprovedChargesAreAccessorialsAndCreateCustomerCharge(chargeDTO);
            }

            // APPROVE/REJECT ACTIVITY
            paymentActivityUpdateService.chargeApproveRejectActivityCreation(chargeDTO, activityDTO);

            // ELASTIC UPDATE
            Optional.ofNullable(chargeDTO.getChargeDecisionCode())
                    .filter(decision -> Objects.equals(decision, CarrierPaymentConstants.APPROVED)
                            || Objects.equals(decision, CarrierPaymentConstants.REJECT_STATUS))
                    .ifPresent(updateInvoiceNbrAndTotal -> elasticModificationService.updatePaidStatusToElasticDocument(
                            existingPayment, false, chargeDTO.getInvoiceNumber()));

        }

    }

    public void processPayment(TxnStatusDTO txnStatusDTO, Payment existingPayment) {
        log.info("ChargeDecisionService: CALL TO processPaymentFromCharge");
        Optional.of(txnStatusDTO).filter(TxnStatusDTO::isSuccess).ifPresent(processCall -> {
            Boolean processedSuccessfully = processPaymentService.processPaymentFromCharge(existingPayment);
            log.info("ChargeDecisionService :processedSuccessfully: " + processedSuccessfully);
            Optional.ofNullable(processedSuccessfully).filter(isTrue -> isTrue)
                    .ifPresent(prcd -> messagePostService.postApprovedMessage(existingPayment));
        });
    }

    private void updateDB2Data(ChargeDTO chargeDTO, TxnStatusDTO txnStatusDTO) {
        TxnStatusDTO statusDTO = new TxnStatusDTO();
        log.info("ChargeDecisionService :: DB2 BACKFILLING");
        log.info("ChargeDecisionService :: saveCharge :: chargeDTO" + chargeDTO);

        Optional.ofNullable(txnStatusDTO.isSuccess()).filter(isTrue -> isTrue).ifPresent(executeSql -> {
            // DB2 - APPROVE
            Optional.ofNullable(chargeDTO.getChargeDecisionCode())
                    .filter(decision -> Objects.equals(decision, CarrierPaymentConstants.APPROVED))
                    .ifPresent(approve -> {
                        chargeAuditBuilder.buildChargeAuditDTO(chargeDTO);
                        statusDTO.setSuccess(chargeIntegrationService.approveCharges(chargeDTO));

                    });
                       ChargeDecisionServiceUtil.integrationStatus(txnStatusDTO, statusDTO);
        });

    }

}

package com.jbhunt.finance.carrierpayment.autopay.service.payment;


import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.repository.ChargeRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.RateIntegrationService;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargePredicateUtil;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j

@Service
public class ProcessPaymentService {

    @Autowired
    private ElasticModificationService elasticModificationService;
    private static final String LOG_STATUS = "Status: ";
    @Autowired
    private ChargeRepository chargeRepository;
    @Autowired
    private PaymentStateService paymentStateService;
    @Autowired
    private RateIntegrationService rateIntegrationService;

    public Boolean processPaymentFromCharge(Payment existingPayment) {
        log.info("ProcessPaymentService :: processPaymentFromCharge: CALL TO approvePayment");
        AtomicBoolean processedSuccessfully = new AtomicBoolean(false);

        List<Charge> chargeList = new ArrayList<>();
        if (null != existingPayment.getCarrierPaymentId()) {
            log.info("PaymentId in ProcessPaymentService is ::  " + existingPayment.getCarrierPaymentId());
            chargeList = chargeRepository.findByCarrierPaymentCarrierPaymentIdAndExpirationTimestampIsNull(
                    existingPayment.getCarrierPaymentId());
        }
        log.info("ProcessPaymentService :: chargeList.size(): " + chargeList.size());

        List<Charge> finalChargeList = chargeList;
        Optional.ofNullable(chargeList).filter(list -> !list.isEmpty()).ifPresent(charges -> {
            boolean allChargesActioned = finalChargeList.stream()
                    .allMatch(val -> Objects.nonNull(val.getChargeDecisionCode()));
            log.info("processPaymentFromCharge: allChargesActioned : " + allChargesActioned);

            log.info("Payment Status : " + existingPayment.getStatusFlowTypeCode());

            if (allChargesActioned
                    && CarrierPaymentConstants.PENDING_STATUS_LIST.contains(existingPayment.getStatusFlowTypeCode())) {
                TxnStatusDTO processStatus = approvePayment(existingPayment);
                processedSuccessfully.set(processStatus.isSuccess());
                log.info("processPaymentFromCharge: CALL TO approvePayment ENDS :: processStatus" + processStatus);
            }
        });
        return processedSuccessfully.get();
    }

    private TxnStatusDTO approvePayment(Payment payment) {
        log.info("Inside approvePayment method ");
        AtomicBoolean isFactored = new AtomicBoolean(false);
        TxnStatusDTO status = new TxnStatusDTO();

        String userId = UserUtil.getLoggedInUser();

        List<Charge> chargeList = payment.getCarrierPaymentChargeList().stream()
                .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null).collect(Collectors.toList());

        if (Objects.nonNull(chargeList)) {

            boolean allRejected = chargeList.stream().allMatch(
                    val -> val.getChargeDecisionCode().equalsIgnoreCase(CarrierPaymentConstants.REJECT_STATUS));

            boolean allApproved = chargeList.stream()
                    .allMatch(val -> val.getChargeDecisionCode().equalsIgnoreCase(CarrierPaymentConstants.APPROVE));

            boolean allPaid = chargeList.stream()
                    .allMatch(val -> val.getChargeDecisionCode().equalsIgnoreCase(CarrierPaymentConstants.PAID));

            if (allRejected) {
                log.info(LOG_STATUS + CarrierPaymentConstants.REJECT_CHARGE);
                status = paymentStateService.updatePaymentStatus(payment, CarrierPaymentConstants.REJECT_CHARGE);
                Optional.ofNullable(status.isSuccess()).filter(Boolean.TRUE::equals)
                        .ifPresent(pmt -> elasticModificationService.updateElasticSearchProcess(payment, "", "", isFactored.get()));
            } else if (allApproved || ChargePredicateUtil.IS_APPROVE_AND_REJECT.test(chargeList)
                    || (ChargePredicateUtil.IS_APPROVE_PAID_AND_REJECT.test(chargeList) && !allPaid)) {
                log.info(LOG_STATUS + CarrierPaymentConstants.APPROVE);
                status = paymentStateService.updatePaymentStatus(payment, CarrierPaymentConstants.APPROVE);
                Optional.ofNullable(status.isSuccess()).filter(Boolean.TRUE::equals)
                        .ifPresent(pmt -> elasticModificationService.updateElasticSearchProcess(payment, "", "", isFactored.get()));
            } else if (allPaid || ChargePredicateUtil.IS_PAID_AND_REJECT.test(chargeList)) {
                log.info(LOG_STATUS + CarrierPaymentConstants.PAID);
                status = paymentStateService.updatePaymentStatus(payment, CarrierPaymentConstants.PAID);
                Optional.ofNullable(status.isSuccess()).filter(Boolean.TRUE::equals)
                        .ifPresent(pmt -> elasticModificationService.updateElasticSearchProcess(payment, "", "", isFactored.get()));
            } else {
                log.info("INVALID CHARGE DECISION CODE");
            }
            // RTC056 Call
            log.info("RTC056 Call for LTL: WorkFlow: " + payment.getGroupFlowTypeCode());
            Optional.ofNullable(payment)
                    .filter(pmnt -> Objects.equals(CarrierPaymentConstants.LTL, pmnt.getGroupFlowTypeCode()))
                    .ifPresent(doCustomerRate -> rateIntegrationService.callRTC056(payment, userId));
        }
        return status;
    }


}

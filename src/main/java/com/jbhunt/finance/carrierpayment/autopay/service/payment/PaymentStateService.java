package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.entity.PaymentStateLog;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentStateLogRepository;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.InvoiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Service
@Slf4j
public class PaymentStateService {
    @Autowired
    PaymentStateLogRepository stateLogRepository;
    @Autowired
    PaymentRepository paymentRepository;

    public  void createNewPaymentState(Payment payment) {
        log.info("createNewPaymentState method starts!!!");
        List<PaymentStateLog> paymentStateLogs = new ArrayList<>();
        PaymentStateLog newState = new PaymentStateLog();

        PaymentStateLog oldState = stateLogRepository.findPaymentStateLog(payment.getCarrierPaymentId());
        if (Objects.isNull(oldState)) {
            // CREATING New State
            createNewState(payment, paymentStateLogs, newState);
        } else if (!Objects.equals(oldState.getStatusFlowTypeCode(), payment.getStatusFlowTypeCode())
                || Objects.equals(oldState.getStatusFlowTypeCode(), payment.getStatusFlowTypeCode())
                && Objects.nonNull(oldState.getPaymentActionTypeCode())) {
            // END & CREATE- StatusMismatch OR
            // StatusMatch with Actiontype Ignore/Snooze Differences
            oldState.setPaymentTaskEndDate(LocalDateTime.now());
            stateLogRepository.saveAndFlush(oldState);
            paymentStateLogs.add(oldState);
            log.info("ENDED OLD STATE LOG StatusFlowTypeCode :: " + oldState.getStatusFlowTypeCode());
            log.info("ENDED OLD STATE LOG PaymentActionTypeCode :: " + oldState.getPaymentActionTypeCode());
            log.info("NEW STATE LOG :: " + newState);
            createNewState(payment, paymentStateLogs, newState);
        }
        log.info("createNewPaymentState :: Payment Status :: " + payment.getStatusFlowTypeCode());
        log.info("createNewPaymentState method ends");
    }


    private  void createNewState(Payment payment, List<PaymentStateLog> paymentStateLogs,
                                       PaymentStateLog newState) {
        newState.setCarrierPaymentId(payment);
        newState.setPaymentTaskStartDate(LocalDateTime.now());
        newState.setStatusFlowTypeCode(payment.getStatusFlowTypeCode());
        if(null != payment.getCarrierPaymentId()){
            stateLogRepository.saveAndFlush(newState);
        }
        paymentStateLogs.add(newState);
        payment.setPaymentStateLog(paymentStateLogs);
        log.info("CREATED NEW STATE LOG :: " + newState.getStatusFlowTypeCode());
    }


    /**
     * @param payment
     * @param paymentStatus
     * @return This method will update the payment status
     */
    public  TxnStatusDTO updatePaymentStatus(Payment payment, String paymentStatus) {
        log.info("Inside updatePaymentStatus method start");
        TxnStatusDTO status = new TxnStatusDTO();
        List<PaymentStateLog> paymentStateLogs = new ArrayList<>();
        PaymentStateLog oldState = stateLogRepository.findPaymentStateLog(payment.getCarrierPaymentId());
        if (null != oldState) {
            PaymentStateLog newState = new PaymentStateLog();
            payment.setStatusFlowTypeCode(paymentStatus);
            if (paymentStatus.equalsIgnoreCase(CarrierPaymentConstants.APPROVE)
                    || paymentStatus.equalsIgnoreCase(CarrierPaymentConstants.REJECT_CHARGE)
                    || paymentStatus.equalsIgnoreCase(CarrierPaymentConstants.REROUTE)
                    || paymentStatus.equalsIgnoreCase(CarrierPaymentConstants.PAID)) {
                payment.setPaymentActionTypeCode(null);
            }
            oldState.setPaymentTaskEndDate(LocalDateTime.now());
            oldState.setCarrierPaymentId(payment);
            stateLogRepository.saveAndFlush(oldState);
            newState.setStatusFlowTypeCode(paymentStatus);
            newState.setCarrierPaymentId(payment);
            newState.setPaymentTaskStartDate(LocalDateTime.now());
            stateLogRepository.saveAndFlush(newState);
            paymentStateLogs.add(oldState);
            paymentStateLogs.add(newState);
            payment.setPaymentStateLog(paymentStateLogs);

            paymentRepository.save(payment);
            status.setSuccess(true);
        }
        log.info("Inside updatePaymentStatus method end");
        return status;
    }

    public  void createNewPaymentStateAndSavePayment(Payment payment) {
        createNewPaymentState(payment);
        log.info("Before Save :: PaymentStatus: " + payment.getStatusFlowTypeCode());
        InvoiceUtil.releaseFromIgnore(payment);
        paymentRepository.save(payment);
    }


}

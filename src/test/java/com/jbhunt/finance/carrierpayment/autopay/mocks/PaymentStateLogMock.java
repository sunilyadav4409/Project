package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.entity.PaymentStateLog;

import java.time.LocalDateTime;

public class PaymentStateLogMock {

    public static PaymentStateLog getPaymentStateLogEqualsPaymentMock(){
        PaymentStateLog paymentStateLog = new PaymentStateLog();
        paymentStateLog.setStatusFlowTypeCode(PaymentMocks.getPaymentMock().getStatusFlowTypeCode());
        paymentStateLog.setPaymentActionTypeCode("dfh");
        paymentStateLog.setPaymentTaskEndDate(LocalDateTime.now());
        paymentStateLog.setPaymentTaskStartDate(LocalDateTime.now());
        paymentStateLog.setReasonCode("hdsg");
        return  paymentStateLog;
    }

    public static PaymentStateLog getPaymentStateLogEqualsPaymentMock1(){
        PaymentStateLog paymentStateLog = new PaymentStateLog();
        paymentStateLog.setStatusFlowTypeCode("PendingAP");
        paymentStateLog.setPaymentActionTypeCode("dfh");
        paymentStateLog.setPaymentTaskEndDate(LocalDateTime.now());
        paymentStateLog.setPaymentTaskStartDate(LocalDateTime.now());
        paymentStateLog.setReasonCode("hdsg");
        return  paymentStateLog;
    }

}

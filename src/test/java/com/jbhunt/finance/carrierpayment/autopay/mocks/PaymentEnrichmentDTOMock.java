package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.dto.PaymentEnrichmentDTO;

public class PaymentEnrichmentDTOMock {
    public static PaymentEnrichmentDTO paymentEnrichmentMock(){
        PaymentEnrichmentDTO paymentEnrichmentDTO = new PaymentEnrichmentDTO();
        paymentEnrichmentDTO.setPaymentTerms("test");
        paymentEnrichmentDTO.setQuickPay(true);
        paymentEnrichmentDTO.setAutoRate("Y");

        return paymentEnrichmentDTO;
    }
}

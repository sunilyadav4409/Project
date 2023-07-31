package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.dto.PaymentEnrichmentRequestDTO;

public class PaymentEnrichmentRequestDTOMock {
    public static PaymentEnrichmentRequestDTO createMock(){
        PaymentEnrichmentRequestDTO paymentEnrichmentDTO = new PaymentEnrichmentRequestDTO();
        paymentEnrichmentDTO.setLoadNumber("1234");
        paymentEnrichmentDTO.setScacCode("432A");
        paymentEnrichmentDTO.setWarehouseFlag(true);
        return paymentEnrichmentDTO;
    }
}

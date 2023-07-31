package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.entity.PaymentShipment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentEntityMocks {

    private static final LocalDateTime NOW = LocalDateTime.now();

    public static Payment createPaymentEntityMocks(){
        Payment payment = new Payment();
        payment.setCarrierPaymentId(1);
        payment.setStatusFlowTypeCode("RcvCnclJob");
        payment.setGroupFlowTypeCode("ICS");
        payment.setScacCode("MSKD");
        payment.setLoadNumber("JW23456");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setVendorPaymentTerm(2);
        payment.setDispatchNumber("1");
        payment.setTotalChargeAmount(BigDecimal.ZERO);
        payment.setDispatchCompletionDate(LocalDate.now());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getListOfCarrierInvoiceHeaderMock());
        Charge charge = new Charge();
        charge.setExpirationTimestamp(LocalDateTime.now());
        payment.setCarrierPaymentChargeList(List.of(charge));
        PaymentShipment shipment = new PaymentShipment();
        shipment.setCarrierPaymentShipmentID(133456);
        payment.setCarrierPaymentShipmentID(shipment);
        return  payment;
    }
}

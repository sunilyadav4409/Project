package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.ChargeOverride;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ChargeEntityMocks {

    private static final LocalDateTime NOW = LocalDateTime.now();

    public static Charge createChargeEntityMocks(){
        Charge charge = new Charge();
        CarrierInvoiceHeader header = new CarrierInvoiceHeader();
        header.setCarrierInvoiceHeaderId(1);
        charge.setChargeId(1);

        charge.setChargeCode("LUMPLDPAY");
        ChargeOverride chargeOverride = new ChargeOverride();
        charge.setChargeOverride(chargeOverride);
        charge.setLstUpdS(NOW);

        charge.setCarrierInvoiceHeader(header);
        charge.setChargeDecisionCode("PAID");
        Payment payment = new Payment();
        payment.setCarrierPaymentId(1);

        payment.setStatusFlowTypeCode("Approve");
        charge.setCarrierPayment(payment);
        charge.setChargeApplyToCustomerIndicator('Y');
        charge.setTotalChargeAmount(BigDecimal.TEN);
        charge.setTotalApprovedChargeAmount(BigDecimal.TEN);
        charge.setStopNumber(1);
        return  charge;
    }
}

package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

@Slf4j
public class InvoiceUtil {

    private InvoiceUtil() {

    }

    public static void releaseFromIgnore(Payment payment) {
        log.info("Release Payment from Ignore");
        Optional.ofNullable(payment.getPaymentActionTypeCode())
                .filter(actiontype -> Objects.equals(actiontype, CarrierPaymentConstants.IGNORE))
                .ifPresent(ignoreAction -> {
                    log.info("ACTION TYPE IS :: " + payment.getPaymentActionTypeCode());
                    // Release Ignore
                    payment.setPaymentActionTypeCode(null);
                });
    }

}

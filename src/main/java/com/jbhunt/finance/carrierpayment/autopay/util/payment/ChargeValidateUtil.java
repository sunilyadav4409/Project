package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChargeValidateUtil {

    private ChargeValidateUtil() {

    }

    public static boolean validateInvoiceDate(String invoiceDate) {
        AtomicBoolean isValidDate = new AtomicBoolean(true);
        Optional.ofNullable(invoiceDate)
                .ifPresent(date -> Optional.ofNullable(LocalDateTime.parse(invoiceDate).toLocalDate().isAfter(LocalDateTime.now().toLocalDate()))
                        .filter(isTrue -> isTrue).ifPresent(validDate -> isValidDate.set(false)));
        return isValidDate.get();
    }

}

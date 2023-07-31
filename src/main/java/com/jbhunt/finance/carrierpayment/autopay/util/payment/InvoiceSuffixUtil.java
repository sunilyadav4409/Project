package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class InvoiceSuffixUtil {

    private InvoiceSuffixUtil() {

    }

    public static Boolean getSuffixValue(String headerInvoiceNumber, ChargeDTO chargeDTO) {

        String inputInvoiceNumber = chargeDTO.getInvoiceNumber();
        log.info("SUFFIX VALIDATIONS ::inputInvoiceNumber:" + inputInvoiceNumber + ":headerInvoiceNumber:"
                + headerInvoiceNumber);

        String[] words = inputInvoiceNumber.split(headerInvoiceNumber, CarrierPaymentConstants.LIMIT_2);
        Optional.ofNullable(words).ifPresent(wordArray -> {
            log.info(":words.length:" + words.length);
            for (int i = 0; i < words.length; i++) {
                log.info("print the words:" + words[i]);
            }
        });

        StringBuilder suffixBuilder = new StringBuilder();
        Optional.of(words).filter(array -> array.length == CarrierPaymentConstants.LIMIT_2).map(elements -> elements[1])
                .filter(suffix -> !suffix.isEmpty()).ifPresent(suffixBuilder::append);
        log.info("SUFFIX VALIDATIONS ::suffixBuilder:" + suffixBuilder);

        Optional.of(suffixBuilder).filter(builder -> builder.length() > 0)
                .ifPresent(builderVal -> chargeDTO.setInvoiceNumberSuffix(builderVal.toString()));

        return Optional.of(words).map(array -> array.length == CarrierPaymentConstants.LIMIT_2).orElse(Boolean.FALSE);
    }

}

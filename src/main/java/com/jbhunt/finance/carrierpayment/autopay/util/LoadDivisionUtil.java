package com.jbhunt.finance.carrierpayment.autopay.util;

import com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class LoadDivisionUtil {

    private LoadDivisionUtil() {

    }

    public static String getDivisionCode(String division) {

        String divisioncode = "";
        log.info("ChargeIntegrationService : validating chargecode");
        if (Optional.ofNullable(division).isPresent()) {

            if (division.equalsIgnoreCase(PaymentChargesConstants.DCS)) {
                divisioncode = "HJBT JBDCS";
            } else if (division.equalsIgnoreCase(PaymentChargesConstants.JBI)) {
                divisioncode = "HJBT JBVAN";
            } else if (division.equalsIgnoreCase(PaymentChargesConstants.ICS)
                    || division.equalsIgnoreCase(PaymentChargesConstants.LTL)) {
                divisioncode = "HJBT JBHA";
            }
        }
        return divisioncode;

    }

}
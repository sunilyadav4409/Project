package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import com.jbhunt.finance.carrierpayment.autopay.entity.ChargeOverride;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class ChargeOverrideUtil {

    private ChargeOverrideUtil() {

    }

    public static void endExistingOverride(ChargeOverride currentOverride) {
        currentOverride.setExpirationTimestamp(LocalDateTime.now());
        log.info("OVERRIDE HISTORY: EXPIRED ID: " + currentOverride.getChargeOverrideId());
    }

    public static ChargeOverride newChargeOverride(ChargeOverride existingOverride) {
        ChargeOverride newOverride = new ChargeOverride();
        newOverride.setVendorAmount(existingOverride.getVendorAmount());
        newOverride.setReasonComment(existingOverride.getReasonComment());
        newOverride.setChargeQuantity(existingOverride.getChargeQuantity());
        newOverride.setDeviationReasonCode(existingOverride.getDeviationReasonCode());
        newOverride.setOverrideReasonCode(existingOverride.getOverrideReasonCode());
        newOverride.setOverrideApproverId(existingOverride.getOverrideApproverId());
        return newOverride;
    }

}

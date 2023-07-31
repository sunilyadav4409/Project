package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CarrierPaymentUtil {

    private CarrierPaymentUtil() {

    }

    /**
     * To set activity types in charge, comments, line items
     *
     * @param activityDTO
     * @param typeCode
     * @param performType
     */
    public static void setActivityPerformed(ActivityDTO activityDTO, String typeCode, String performType) {
        activityDTO.setActivityTypeCode(typeCode);
        activityDTO.setActivityPerformTypeCode(performType);
    }

}

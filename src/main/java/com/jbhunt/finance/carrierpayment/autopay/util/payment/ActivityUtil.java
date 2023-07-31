package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ActivityService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class ActivityUtil {

    private ActivityUtil() {

    }

    /**
     * This method will save the Activity details
     */
    public static void saveActivity(Payment payment, String typeCode, String performType, String sourceType,
                                    String userId, ActivityService activityService) {
        log.info("Inside saveActivity method start: Type:" + typeCode);

        ActivityDTO activity = getActivityDTO( typeCode, performType, sourceType, userId );
        saveActivityDetail( payment, activityService, activity );
        log.info("Inside saveActivity method end");
    }

    /**
     * This method will save the Activity details for Charge Updates
     */
    public static TxnStatusDTO createActivityRecord(ActivityDTO activityDTO, ChargeDTO chargeDTO,
                                                    ActivityService activityService) {
        log.info("SAVE/UPDATE CHARGE :: ACTIVITY");
        log.info("ACTIVITY:: isAutoPay :: " + chargeDTO.isAutoPay());
        activityDTO.setCarrierPaymentId(chargeDTO.getPaymentId());
        activityDTO.setActivitySourceTypeCode(Optional.ofNullable(chargeDTO).filter(ChargeDTO::isAutoPay)
                .map(x -> CarrierPaymentConstants.ACT_APP).orElseGet(() -> CarrierPaymentConstants.ACT_SRC_PERSON));
        activityDTO.setActivityPerformerId(
                Optional.ofNullable(chargeDTO).filter(ChargeDTO::isAutoPay).map(x -> CarrierPaymentConstants.SYSTEM)
                        .orElseGet(() -> UserUtil.getLoggedInUser()));
        activityDTO.setActivityPerformedDate(LocalDateTime.now().toString());
        log.info("ACTIVITY:: ActivitySourceType :: " + activityDTO.getActivitySourceTypeCode());
        log.info("ACTIVITY:: ActivityPerformerId :: " + activityDTO.getActivityPerformerId());
        log.info("Charge Service :: Activity type :::" + activityDTO.getActivityPerformTypeCode());
        return activityService.saveActivityDetails(activityDTO);

    }

    public static ActivityDTO getActivityDTO(String actAutoPay, String actAutoPayPerform, String actApp, String system) {
        ActivityDTO activity = new ActivityDTO();
        activity.setActivityTypeCode( actAutoPay );
        activity.setActivityPerformTypeCode( actAutoPayPerform );
        activity.setActivitySourceTypeCode( actApp );
        activity.setActivityPerformerId( system );
        activity.setActivityPerformedDate( LocalDateTime.now().toString() );
        return activity;
    }

    private static void saveActivityDetail(Payment payment, ActivityService activityService, ActivityDTO activity) {
        Optional.ofNullable( payment ).ifPresent( act -> {
            activity.setCarrierPaymentId( payment.getCarrierPaymentId() );
            activityService.saveActivityDetails( activity );
        } );
    }


}

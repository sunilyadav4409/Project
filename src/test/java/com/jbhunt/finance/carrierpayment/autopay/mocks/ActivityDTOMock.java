package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityDTO;

public class ActivityDTOMock {
    public static ActivityDTO getActivityDtoMock(){
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setActivitySourceTypeCode("jhds");
        activityDTO.setActivityPerformerId("32");
        activityDTO.setCarrierPaymentId(243);
        return activityDTO;
    }
}

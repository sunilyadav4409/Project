package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityDTO;

public class ActivityDTOMocks {
    public static ActivityDTO createActivityMock(){
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setActivitySourceTypeCode("App");
        activityDTO.setActivityPerformTypeCode("BOL");
        return activityDTO;
    }
}

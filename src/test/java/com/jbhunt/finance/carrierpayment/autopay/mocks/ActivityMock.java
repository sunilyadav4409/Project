package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.entity.Activity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityMock {
    public static Activity getActivityMock(){
        Activity activity = new Activity();
        activity.setActivityDate(LocalDateTime.now());
        activity.setActivityDetailReferenceValue("dn");
        activity.setActivityDetailTypeCode("hsdgaj");
        activity.setActivityPerformTypeCode("hdgas");
        activity.setActivityTrackId(2324);
        activity.setPayment(PaymentMocks.getPaymentMock());
        return activity;
    }


    public static  List<Activity> getActivityMock1(){
        List<Activity> activityListList = new ArrayList<>();

        Activity activity = new Activity();
        activity.setActivitySourceTypeCode("jhds");
        activity.setActivityDetailTypeCode("FUTUREDATE");
        activity.setLstUpdS(LocalDateTime.now().minusDays(5));
        activity.setActivityPerformerId("32");
        activityListList.add(activity);

        Activity activity1 = new Activity();
        activity1.setActivitySourceTypeCode("jhds");
        activity1.setActivityDetailTypeCode("BypasBilTo");
        activity1.setLstUpdS(LocalDateTime.now().minusDays(3));
        activity1.setActivityPerformerId("32");
        activityListList.add(activity1);

        Activity activity2 = new Activity();
        activity2.setActivitySourceTypeCode("jhds");
        activity2.setActivityDetailTypeCode("ChgLstExcp");
        activity2.setLstUpdS(LocalDateTime.now().minusDays(4));
        activity2.setActivityPerformerId("32");
        activityListList.add(activity2);
        return activityListList;
    }
    public static  List<Activity> getActivityMock2(){
        List<Activity> activityListList = new ArrayList<>();

        Activity activity = new Activity();
        activity.setActivitySourceTypeCode("jhds");
        activity.setActivityDetailTypeCode("FUTUREDATE");
        activity.setLstUpdS(LocalDateTime.now().minusDays(1));
        activity.setActivityPerformerId("32");
        activityListList.add(activity);

        return activityListList;
    }
}

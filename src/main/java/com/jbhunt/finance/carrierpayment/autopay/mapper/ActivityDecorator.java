package com.jbhunt.finance.carrierpayment.autopay.mapper;

import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Activity;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public abstract class ActivityDecorator implements ActivityMapper {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public Activity activityDTOToActivity(ActivityDTO activityDto) {
        Activity activity = activityMapper.activityDTOToActivity(activityDto);
        LocalDateTime localDateTime = LocalDateTime.parse(activityDto.getActivityPerformedDate());
        activity.setActivityDate(localDateTime);
        Payment payment = new Payment();
        payment.setCarrierPaymentId(activityDto.getCarrierPaymentId());
        activity.setPayment(payment);
        return activity;
    }

}

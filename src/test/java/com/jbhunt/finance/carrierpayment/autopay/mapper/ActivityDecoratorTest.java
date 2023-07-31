package com.jbhunt.finance.carrierpayment.autopay.mapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Activity;

@RunWith(MockitoJUnitRunner.class)
public class ActivityDecoratorTest {
    @InjectMocks
    ActivityDecorator activityDecorator = Mockito.mock(ActivityDecorator.class, Mockito.CALLS_REAL_METHODS);

    @Mock
    private ActivityMapper activityMapper;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void activityDTOToActivityTest() {
        // --- ARRANGE ---
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setActivityPerformedDate("2017-05-22T12:35:30");
        Mockito.when(activityMapper.activityDTOToActivity(Mockito.any())).thenReturn(new Activity());

        // --- ACT ---
        Activity activity = activityDecorator.activityDTOToActivity(activityDTO);

        // --- ASSERT ---
        Assert.assertNotNull(activity);
    }
}

package com.jbhunt.finance.carrierpayment.autopay.service;
import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Activity;
import com.jbhunt.finance.carrierpayment.autopay.mapper.ActivityMapper;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ActivityDTOMock;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ActivityMock;
import com.jbhunt.finance.carrierpayment.autopay.repository.ActivityRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ActivityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActivityServiceTest {
   @InjectMocks
    ActivityService activityService;
   @Mock
    ActivityRepository activityRepository;
   @Mock
    ActivityMapper activityMapper;

   @Test
    public void saveActivityDetailsTest(){
       when(activityMapper.activityDTOToActivity(any())).thenReturn(ActivityMock.getActivityMock());
       when(activityRepository.save(any())).thenReturn(ActivityMock.getActivityMock());
       TxnStatusDTO status= activityService.saveActivityDetails(ActivityDTOMock.getActivityDtoMock());
       assertNotNull(status);
       assertEquals(true,status.isSuccess());
       verify(activityMapper,times(1)).activityDTOToActivity(any(ActivityDTO.class));
       verify(activityRepository,times(1)).save(any(Activity.class));

   }
    @Test(expected = Exception.class)
    public void saveActivityDetailsExceptionTest(){
        when(activityRepository.save(any())).thenThrow(new Exception());
        TxnStatusDTO status= activityService.saveActivityDetails(ActivityDTOMock.getActivityDtoMock());
        assertNotNull(status);
        assertEquals(false,status.isSuccess());
        verify(activityMapper,times(1)).activityDTOToActivity(any(ActivityDTO.class));
        verify(activityRepository,times(1)).save(any(Activity.class));

    }

}

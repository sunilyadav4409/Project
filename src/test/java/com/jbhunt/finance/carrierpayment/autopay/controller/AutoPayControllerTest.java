package com.jbhunt.finance.carrierpayment.autopay.controller;

import com.jbhunt.finance.carrierpayment.autopay.dto.AutopayProcessDTO;
import com.jbhunt.finance.carrierpayment.autopay.feature.FeatureFlags;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.AutoPayService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ProcessAutoPayService;
import io.rollout.flags.RoxFlag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AutoPayControllerTest {

    @InjectMocks
    AutoPayController autoPayController;

    @Mock
     private AutoPayService autoPayService;
    @Mock
    private ProcessAutoPayService processAutoPayService;
    @Mock
    private  FeatureFlags featureFlags;


    @Test
    public void testCallAutoPayWebService() throws Exception {
        autoPayController.callAutoPayWebService( 1);
        verify(autoPayService, times( 1 )).callAutoPayWS( 1);
    }

    @Test
    public void testCallAutoPayWebService1() throws Exception {
        ReflectionTestUtils.setField(featureFlags, "publishChargeUpdatesToBilling", new RoxFlag(true), RoxFlag.class);
        when(autoPayService.callAutoPayWS(any())).thenReturn(true);
        ResponseEntity response = autoPayController.callAutoPayWebService( 1);
        assertNotNull(response);
        assertEquals( HttpStatus.OK,response.getStatusCode());
        verify(autoPayService, times( 1 )).postChargesStatusToTopic(1);
    }

    @Test
    public void testProcessAutoPay() throws Exception {
       AutopayProcessDTO autopayProcessDTO = new AutopayProcessDTO();
       autopayProcessDTO.setPaymentIds(List.of(123456));
       String callingPoint = "TEST";
        when(processAutoPayService.postPaymentToProcessAutoPay(any())).thenReturn(true);
        ResponseEntity response = autoPayController.postPaymentToProcessAutoPay( autopayProcessDTO);
        assertNotNull(response);
        assertEquals( HttpStatus.OK,response.getStatusCode());
    }
}

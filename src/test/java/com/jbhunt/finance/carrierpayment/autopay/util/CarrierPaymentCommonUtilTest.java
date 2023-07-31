package com.jbhunt.finance.carrierpayment.autopay.util;

import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import com.jbhunt.finance.carrierpayment.autopay.util.payment.CarrierPaymentCommonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CarrierPaymentCommonUtilTest {

    @InjectMocks
    private CarrierPaymentCommonUtil carrierPaymentCommonUtil;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(carrierPaymentCommonUtil);
    }
    
    @Test
    public void testConvertLocalDateTimeToString(){
        
     // --- ARRANGE -- -
        
        LocalDateTime localDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        
     // --- ACT -- -
        
        String date = carrierPaymentCommonUtil.convertLocalDateTimeToString(localDate, formatter);
        
     // --- ASSERT -- -
        
        assertNotNull(date);
    }
    
    @Test
    public void testConvertLocalDateToString(){
        
     // --- ARRANGE -- -
        
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        
     // --- ACT -- -
        
        String date = carrierPaymentCommonUtil.convertLocalDateToString(localDate, formatter);
        
     // --- ASSERT -- -
        
        assertNotNull(date);
    }
    
}

package com.jbhunt.finance.carrierpayment.autopay.mapper;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChargeMapperDecoratorTest {

    @InjectMocks
    ChargeMapperDecorator chargeMapperDecorator = Mockito.mock(ChargeMapperDecorator.class, Mockito.CALLS_REAL_METHODS);

    @Mock
    private ChargeMapper chargeMapper;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(chargeMapperDecorator);
    }

    @Test
    public void chargeToChargeDTOTest() {
        // --- ARRANGE ---
        Charge charge = new Charge();
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceNumber("HW34565");
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        charge.setCarrierInvoiceHeader(carrierInvoiceHeader);
        Mockito.when(chargeMapper.chargeToChargeDTO(Mockito.any())).thenReturn(new ChargeDTO());
        
        // --- ACT ---
        ChargeDTO chargeDTO = chargeMapperDecorator.chargeToChargeDTO(charge);
        
        // --- ASSERT 
        Assert.assertNotNull(chargeDTO);
    }
}

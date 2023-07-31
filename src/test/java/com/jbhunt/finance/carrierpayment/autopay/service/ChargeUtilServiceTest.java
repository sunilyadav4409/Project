package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.mapper.ChargeMapper;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeDTOMocks;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ChargeUtilService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeMocks.getChargeList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChargeUtilServiceTest {

    @InjectMocks
    private ChargeUtilService chargeUtilService;

    private ChargeMapper chargeMapper = Mockito.mock(ChargeMapper.class);

    @Before
    public void init() {
        MockitoAnnotations.initMocks(chargeUtilService);
    }

    @Test
    public void testupdatePayment() {

        // --- ACT---
        when(chargeMapper.chargeListToChargeDTOList(getChargeList(null))).thenReturn(ChargeDTOMocks.getChargeDTOList("Approve"));
        BigDecimal amount =chargeUtilService.getAmount( getChargeList(null));
        assertEquals(BigDecimal.TEN,amount);
        Mockito.verify(chargeMapper,times(1)).chargeListToChargeDTOList(Mockito.any());
    }

    @Test
    public void testupdatePaymentOne() {
        // --- ACT---
        BigDecimal amount= chargeUtilService.getAmount(getChargeList(LocalDateTime.now()));
        assertEquals(BigDecimal.ZERO,amount);

    }

    @Test
    public void testupdatePaymentTwo() {
        // --- ACT---
        BigDecimal amount=chargeUtilService.getAmount( new ArrayList<>());
        assertEquals(BigDecimal.ZERO,amount);
    }

}

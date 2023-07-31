package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.dao.ChargeDAO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeTokenDTO;
import com.jbhunt.infrastructure.exception.JBHValidationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChargeIntegrationServiceTest {

    @InjectMocks
    private ChargeIntegrationService chargeIntegrationService;

    @Mock
    private ChargeDAO chargeRepo;

    @Test
    public void approveChargesTest() throws Exception {

        // --- ARRANGE ---

        when(chargeRepo.updateApproveDetails(Mockito.any(ChargeDTO.class))).thenReturn(true);

        // --- ACT ---

        Boolean response = chargeIntegrationService.approveCharges(new ChargeDTO());

        // --- ASSERT ---

        Assert.assertNotNull(response);
        Assert.assertEquals(true, response);
    }

    @Test
    public void test_addAndeditcharge(){

        ChargeDTO chargeDTO = new ChargeDTO();
        chargeDTO.setChargeId(1);
        chargeDTO.setExternalChargeID(1);
        chargeDTO.setExternalChargeBillingID(1);
        when(chargeRepo.getExistingChargeCount(chargeDTO)).thenReturn(1);
        when(chargeRepo.updateCustCharge(chargeDTO)).thenReturn(new ChargeTokenDTO());
        assertTrue(chargeIntegrationService.addAndeditcharge(Arrays.asList(chargeDTO)));

    }
    @Test(expected = JBHValidationException.class)
    public void test_addAndeditcharge_exception(){

        ChargeDTO chargeDTO = new ChargeDTO();
        chargeDTO.setChargeId(1);
        chargeDTO.setExternalChargeID(1);
        chargeDTO.setExternalChargeBillingID(1);
        when(chargeRepo.getExistingChargeCount(chargeDTO)).thenReturn(2);

        assertFalse(chargeIntegrationService.addAndeditcharge(Arrays.asList(chargeDTO)));

    }

    private ChargeTokenDTO getChargeTokenDTO(boolean status) {
        ChargeTokenDTO chargeTokenDTO = new ChargeTokenDTO();
        chargeTokenDTO.setIntegrationStatus(status);
        return chargeTokenDTO;
    }

    @Test
    public void getCountOfRecordsZeroTest() {
        when(chargeRepo.getCountFromTOrderByLoadNumber(any())).thenReturn(0);
        int count = chargeIntegrationService.getCountOfRecords("JW23456");
        assertEquals(0, count);
    }

    @Test
    public void getCountOfRecordsOneTest() {
        when(chargeRepo.getCountFromTOrderByLoadNumber(any())).thenReturn(1);
        int count = chargeIntegrationService.getCountOfRecords("V686424");
        assertEquals(1, count);
    }

}

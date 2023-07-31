package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.Parameter;
import com.jbhunt.finance.carrierpayment.autopay.entity.SpecificationAssociation;
import com.jbhunt.finance.carrierpayment.autopay.mapper.ChargeMapper;
import com.jbhunt.finance.carrierpayment.autopay.repository.ChargeRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.ParameterRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.SpecificationAssociationRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.AutoPayService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ChargeValidationService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.InvoiceValidationAndUpdateService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.*;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.APPROVE;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeDTOMocks.getChargeDTO;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeMocks.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChargeValidationServiceTest {

    @InjectMocks
    private ChargeValidationService chargeValidationService;

    private ParameterRepository parameterRepository=Mockito.mock(ParameterRepository.class);

    private ChargeMapper chargeMapper = Mockito.mock(ChargeMapper.class);
    private ChargeRepository chargeRepository = Mockito.mock(ChargeRepository.class);
    private com.jbhunt.finance.carrierpayment.autopay.service.payment.InvoiceValidationAndUpdateService InvoiceValidationAndUpdateService = Mockito
            .mock(InvoiceValidationAndUpdateService.class);
    private ChargeIntegrationService chargeIntegrationService=Mockito.mock(ChargeIntegrationService.class);

    private SpecificationAssociationRepository specificationAssociationRepository=Mockito.mock(SpecificationAssociationRepository.class);
    private AutoPayService autoPayService=Mockito.mock(AutoPayService.class);

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testapproveRejectChargeValidations() {

        // ---- ARRANGE ----

        ChargeDTO chargeDTO = new ChargeDTO();
        chargeDTO.setInvoiceDate(LocalDateTime.now().toString());
        chargeDTO.setHeaderId(1);
        chargeDTO.setInvoiceNumber("Inv12");

        // ---- ACT ----

        Map<String, Boolean> errorMap = chargeValidationService.approveRejectChargeValidations(chargeDTO);

        // ---- ASSERT ----

        assertNotNull(errorMap);
        assertEquals(1, errorMap.size());
    }


    @Test
    public void testvalidateAmountforEachCharge() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = getChargeDTO();
        chargeDTO.setOverrideWarningSave(true);
        chargeDTO.setChargeDecisionCode("Approve");
        List<Integer> list=new ArrayList<>();
        list.add(1);
        list.add(2);        chargeDTO.setChargeIdList(list);
        chargeDTO.setChargeIdList(list);

        when(chargeRepository.findAllById(chargeDTO.getChargeIdList())).thenReturn(getChargeList());
        //  Mockito.when(chargeIntegrationService.validateCharges(chargeDTO)).thenReturn();
        Map<String, Boolean> errorMap=new HashMap<>();
        chargeValidationService.validateAmountforEachCharge(chargeDTO,errorMap);
        assertEquals(1,errorMap.size());
    }


    @Test
    public void testvalidateAmountforEachCharge_reject() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = getChargeDTO();
        chargeDTO.setOverrideWarningSave(true);
        chargeDTO.setChargeDecisionCode("Reject");
        List<Integer> list=new ArrayList<>();
        list.add(1);
        list.add(2);
        chargeDTO.setChargeIdList(list);
        when(chargeRepository.findAllById(chargeDTO.getChargeIdList())).thenReturn(getChargeList());

        Map<String, Boolean> errorMap=new HashMap<>();
        chargeValidationService.validateAmountforEachCharge(chargeDTO,errorMap);
        assertEquals(1,errorMap.size());
    }
    @Test
    public void  test_updateInvoiceDetails(){
        ChargeDTO chargeDTO = getChargeDTO();
        chargeDTO.setOverrideWarningSave(true);
        chargeDTO.setChargeDecisionCode("Reject");
        List<Integer> list=new ArrayList<>();
        list.add(1);
        list.add(2);
        chargeDTO.setChargeIdList(list);
        Map<String, Boolean> errorMap=new HashMap<>();
        doNothing().when(InvoiceValidationAndUpdateService).checkOrUpdateInvoiceNumber(chargeDTO,errorMap);
        chargeValidationService.updateInvoiceDetails(chargeDTO,errorMap);
        assertNotNull(errorMap);
        assertEquals(0, errorMap.size());
    }

    @Test
    public void  test_validateChargeCodes_ICS(){
        ChargeDTO chargeDTO = getChargeDTO();
        chargeDTO.setOverrideWarningSave(true);
        chargeDTO.setWorkFlowGroupType("ICS");
        List<Integer> list=new ArrayList<>();
        list.add(1);
        list.add(2);
        chargeDTO.setChargeIdList(list);
        Map<String, Boolean> errorMap=new HashMap<>();
        SpecificationAssociation specificationAssociation = new SpecificationAssociation();
        specificationAssociation.setSpecificationAssociationID(123456);
        Parameter parameter=new Parameter();
        parameter.setSpecificationSub("TRANSIT");
        parameter.setEffectiveDate(LocalDateTime.now());
        parameter.setExpirationDate(LocalDateTime.of(9999,03,13,3,4));

        when(specificationAssociationRepository.findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(any(),any(),any(),any(),any())).thenReturn(specificationAssociation);
        when( chargeRepository.findAllById(chargeDTO.getChargeIdList())).thenReturn(getChargeCodeValidation(1));

        when(parameterRepository
                .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndWorkflowSpecificationAssociationWorkflowGroupTypeCodeAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                       any(), any(),any(),any())).thenReturn(Arrays.asList(parameter));
        chargeDTO.setChargeDecisionCode(APPROVE);
        chargeValidationService.validateChargeCodes(chargeDTO,errorMap, any());
        assertNotNull(errorMap);
        assertEquals(1, errorMap.size());
    }


    @Test
    public void  test_validateChargeCodes_JBI(){
        ChargeDTO chargeDTO = getChargeDTO();
        chargeDTO.setOverrideWarningSave(true);
        chargeDTO.setWorkFlowGroupType("JBI");
        List<Integer> list=new ArrayList<>();
        list.add(1);
        list.add(2);
        chargeDTO.setChargeIdList(list);
        Map<String, Boolean> errorMap=new HashMap<>();
        SpecificationAssociation specificationAssociation = new SpecificationAssociation();
        specificationAssociation.setSpecificationAssociationID(123456);
        Parameter parameter=new Parameter();
        Parameter parameter1=new Parameter();
        parameter.setSpecificationSub("TRANSIT");
        parameter.setEffectiveDate(LocalDateTime.now());
        parameter.setExpirationDate(LocalDateTime.of(9999,03,13,3,4));
        parameter1.setSpecificationSub("AMS");
        parameter1.setEffectiveDate(LocalDateTime.now());
        parameter1.setExpirationDate(LocalDateTime.of(9999,03,13,3,4));
        when(specificationAssociationRepository.findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(any(),any(),any(),any(),any())).thenReturn(specificationAssociation);
        when( chargeRepository.findAllById(chargeDTO.getChargeIdList())).thenReturn(getChargeCodeValidation(1));
        when(parameterRepository
                .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndWorkflowSpecificationAssociationWorkflowGroupTypeCodeAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                        any(), any(),any(),any())).thenReturn(Arrays.asList(parameter,parameter1));
        chargeDTO.setChargeDecisionCode(APPROVE);
        chargeValidationService.validateChargeCodes(chargeDTO,errorMap, any());
        assertNotNull(errorMap);
        assertEquals(0,errorMap.size());
    }
    @Test
    public void  test_validateChargeCodesNonAMS_JBI(){
        ChargeDTO chargeDTO = getChargeDTO();
        chargeDTO.setOverrideWarningSave(true);
        chargeDTO.setWorkFlowGroupType("JBI");
        List<Integer> list=new ArrayList<>();
        list.add(1);
        list.add(2);
        chargeDTO.setChargeIdList(list);
        Map<String, Boolean> errorMap=new HashMap<>();
        SpecificationAssociation specificationAssociation = new SpecificationAssociation();
        specificationAssociation.setSpecificationAssociationID(123456);
        Parameter parameter=new Parameter();
        Parameter parameter1=new Parameter();
        parameter1.setSpecificationSub("FUELSURCHG");
        parameter.setSpecificationSub("TRANSIT");
        parameter.setEffectiveDate(LocalDateTime.now());
        parameter.setExpirationDate(LocalDateTime.of(9999,03,13,3,4));
        parameter1.setEffectiveDate(LocalDateTime.now());
        parameter1.setExpirationDate(LocalDateTime.of(9999,03,13,3,4));
        when(specificationAssociationRepository.findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(any(),any(),any(),any(),any())).thenReturn(specificationAssociation);
        when( chargeRepository.findAllById(chargeDTO.getChargeIdList())).thenReturn(getChargeCodeValidation2(1));

        when(parameterRepository
                .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndWorkflowSpecificationAssociationWorkflowGroupTypeCodeAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                        any(), any(),any(),any())).thenReturn(Arrays.asList(parameter,parameter1));
        chargeDTO.setChargeDecisionCode(APPROVE);
        chargeValidationService.validateChargeCodes(chargeDTO,errorMap, any());
        assertEquals(0, errorMap.size());
    }
    @Test
    public void  test_validateChargeCodesNonAMS_ICS(){
        ChargeDTO chargeDTO = getChargeDTO();
        chargeDTO.setOverrideWarningSave(true);
        chargeDTO.setWorkFlowGroupType("ICS");
        List<Integer> list=new ArrayList<>();
        list.add(1);
        list.add(2);
        chargeDTO.setChargeIdList(list);
        Map<String, Boolean> errorMap=new HashMap<>();
        SpecificationAssociation specificationAssociation = new SpecificationAssociation();
        specificationAssociation.setSpecificationAssociationID(123456);
        Parameter parameter=new Parameter();
        Parameter parameter1=new Parameter();
        parameter1.setSpecificationSub("FUELSURCHG");
        parameter.setSpecificationSub("TRANSIT");
        parameter.setEffectiveDate(LocalDateTime.now());
        parameter.setExpirationDate(LocalDateTime.of(9999,03,13,3,4));
        parameter1.setEffectiveDate(LocalDateTime.now());
        parameter1.setExpirationDate(LocalDateTime.of(9999,03,13,3,4));
        when(specificationAssociationRepository.findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(any(),any(),any(),any(),any())).thenReturn(specificationAssociation);
        when( chargeRepository.findAllById(chargeDTO.getChargeIdList())).thenReturn(getChargeCodeValidation2(1));

        when(parameterRepository
                .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndWorkflowSpecificationAssociationWorkflowGroupTypeCodeAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                        any(), any(),any(),any())).thenReturn(Arrays.asList(parameter,parameter1));
        chargeDTO.setChargeDecisionCode(APPROVE);
        chargeValidationService.validateChargeCodes(chargeDTO,errorMap, any());
        assertEquals(0, errorMap.size());
    }

    @Test
    public void  test_validateChargeCodes_ICS_Params_null(){
        ChargeDTO chargeDTO = getChargeDTO();
        chargeDTO.setOverrideWarningSave(true);
        chargeDTO.setWorkFlowGroupType("ICS");
        List<Integer> list=new ArrayList<>();
        list.add(1);
        list.add(2);
        chargeDTO.setChargeIdList(list);
        Map<String, Boolean> errorMap=new HashMap<>();
        Parameter parameter=new Parameter();

        when(specificationAssociationRepository.findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(any(),any(),any(),any(),any())).thenReturn(new SpecificationAssociation());
        when( chargeRepository.findAllById(chargeDTO.getChargeIdList())).thenReturn(getChargeCodeValidation(1));
        chargeDTO.setChargeDecisionCode(APPROVE);
        chargeValidationService.validateChargeCodes(chargeDTO,errorMap, any());
        assertNotNull(errorMap);
        assertEquals(1, errorMap.size());
    }

    @Test
    public void  test_validateChargeCodes_ICS_Params_null1(){
        ChargeDTO chargeDTO = getChargeDTO();
        chargeDTO.setOverrideWarningSave(true);
        chargeDTO.setWorkFlowGroupType("ICS");
        List<Integer> list=new ArrayList<>();
        list.add(1);
        list.add(2);
        chargeDTO.setChargeIdList(list);
        Map<String, Boolean> errorMap=new HashMap<>();
        SpecificationAssociation specificationAssociation = new SpecificationAssociation();
        specificationAssociation.setSpecificationAssociationID(123456);
        Parameter parameter=new Parameter();

        when(specificationAssociationRepository.findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(any(),any(),any(),any(),any())).thenReturn(specificationAssociation);
        when( chargeRepository.findAllById(chargeDTO.getChargeIdList())).thenReturn(getChargeCodeValidation(1));

        when(parameterRepository
                .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndWorkflowSpecificationAssociationWorkflowGroupTypeCodeAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                        any(), any(),any(),any())).thenReturn(new ArrayList<>());
        chargeDTO.setChargeDecisionCode(APPROVE);
        chargeValidationService.validateChargeCodes(chargeDTO,errorMap, any());
        assertNotNull(errorMap);
        assertEquals(1, errorMap.size());
    }

}

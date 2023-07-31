package com.jbhunt.finance.carrierpayment.autopay.service;


import com.jbhunt.finance.carrierpayment.autopay.dao.ChargeDAO;
import com.jbhunt.finance.carrierpayment.autopay.dao.CustomerChargeDAO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.CustomerChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeDTOMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeEntityMocks;
import com.jbhunt.finance.carrierpayment.autopay.repository.ChargeRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.ParameterRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.SpecificationAssociationRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.BillableChargeService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BillableChargeServiceTest {

    @Mock
    ChargeDAO chargeRepo;
    @Mock
    ChargeRepository chargeRepository;
    @Mock
    ParameterRepository parameterRepository;
    @Mock
    SpecificationAssociationRepository specificationAssociationRepository;
    @Mock
    ChargeIntegrationHelper chargeIntegrationHelper;
    @InjectMocks
    BillableChargeService billableChargeService;
    @Mock
    CustomerChargeDAO customerChargeDAO;

    @Test
    public void checkApprovedChargesAreAccessorialsAndCreateCustomerChargeTest() {
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        List<Charge> charges = new ArrayList<>();
        charges.add(ChargeEntityMocks.createChargeEntityMocks());
        charges.add(ChargeEntityMocks.createChargeEntityMocks());
        Mockito.when(chargeRepository.findAllById(Mockito.any())).thenReturn(charges);
        List<CustomerChargeDTO> customerChargeDTOS = new ArrayList<>();
        CustomerChargeDTO customerChargeDTO = new CustomerChargeDTO();
        customerChargeDTO.setChargeCode("LUMPLDPAY");
        customerChargeDTO.setStopNumber(1);
        customerChargeDTOS.add(customerChargeDTO);

        customerChargeDTO = new CustomerChargeDTO();
        customerChargeDTO.setChargeCode("LUMPLDPAY");
        customerChargeDTO.setStopNumber(1);
        customerChargeDTOS.add(customerChargeDTO);

        Mockito.when(customerChargeDAO.getCustomerChargesWithExpenseRowAsN(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(customerChargeDTOS);
        billableChargeService.checkApprovedChargesAreAccessorialsAndCreateCustomerCharge(chargeDTO);
        Mockito.verify(customerChargeDAO).getCustomerChargesWithExpenseRowAsN(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void inactivateExistingAndAddNewCustomerCharge() {
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        List<CustomerChargeDTO> customerChargeDTOS = new ArrayList<>();
        CustomerChargeDTO customerChargeDTO = new CustomerChargeDTO();
        customerChargeDTO.setChargeCode("LUMPLDPAY");
        customerChargeDTO.setStopNumber(1000);
        customerChargeDTO.setTotalChargeAmount(BigDecimal.valueOf(2000));
        customerChargeDTOS.add(customerChargeDTO);

        customerChargeDTOS.add(customerChargeDTO);
        Charge charge = ChargeEntityMocks.createChargeEntityMocks();
        charge.setTotalApprovedChargeAmount(BigDecimal.valueOf(5000));
        billableChargeService.inactivateExistingAndAddNewCustomerCharge(chargeDTO,customerChargeDTOS, charge);
        Mockito.verify(chargeRepo).updateExpenceRowN(Mockito.any());
    }

    @Test
    public void addNewCustomerChargeWhenExistingCustomerStopNumberIsNotPresentInNewCharge() {
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        List<CustomerChargeDTO> customerChargeDTOS = new ArrayList<>();
        CustomerChargeDTO customerChargeDTO = new CustomerChargeDTO();
        customerChargeDTO.setChargeCode("LUMPLDPAY");
        customerChargeDTO.setStopNumber(1);
        customerChargeDTO.setTotalChargeAmount(BigDecimal.valueOf(2000));
        customerChargeDTOS.add(customerChargeDTO);

        customerChargeDTO = new CustomerChargeDTO();
        customerChargeDTO.setChargeCode("LUMPLDPAY");
        customerChargeDTO.setStopNumber(1);
        customerChargeDTOS.add(customerChargeDTO);
        Charge charge = ChargeEntityMocks.createChargeEntityMocks();
        charge.setStopNumber(5000);
        billableChargeService.inactivateExistingAndAddNewCustomerCharge(chargeDTO,customerChargeDTOS, charge);

    }

    @Test
    public void addCustChargeTest() {
        Charge charge = ChargeEntityMocks.createChargeEntityMocks();
        Mockito.when(customerChargeDAO.insertCustomerChargekeyHolder(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        boolean result = billableChargeService.addCustCharge(charge, "TEST", 123543,555555);
        Assertions.assertEquals(true, result);
    }
    @Test
    public void addCustChargeTestForException() {
        Charge charge = ChargeEntityMocks.createChargeEntityMocks();
        Mockito.when(customerChargeDAO.insertCustomerChargekeyHolder(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(NullPointerException.class);
        boolean result = billableChargeService.addCustCharge(charge, "TEST", 123543,555555);
        Assertions.assertEquals(false, result);
    }

    @Test
    public void checkApprovedChargesAreAccessorialsAndCreateCustomerChargeWhenSingleChargeIsApproved() {
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        List<Charge> charges = new ArrayList<>();
        charges.add(ChargeEntityMocks.createChargeEntityMocks());
        Mockito.when(chargeRepository.findAllById(Mockito.any())).thenReturn(charges);
        List<CustomerChargeDTO> customerChargeDTOS = new ArrayList<>();
        CustomerChargeDTO customerChargeDTO = new CustomerChargeDTO();
        customerChargeDTO.setChargeCode("LUMPLDPAY");
        customerChargeDTO.setStopNumber(1);
        customerChargeDTOS.add(customerChargeDTO);

        customerChargeDTO = new CustomerChargeDTO();
        customerChargeDTO.setChargeCode("LUMPLDPAY");
        customerChargeDTO.setStopNumber(1);
        customerChargeDTOS.add(customerChargeDTO);

        Mockito.when(customerChargeDAO.getCustomerChargesWithExpenseRowAsN(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(customerChargeDTOS);
        billableChargeService.checkApprovedChargesAreAccessorialsAndCreateCustomerCharge(chargeDTO);
        Mockito.verify(customerChargeDAO).getCustomerChargesWithExpenseRowAsN(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void checkApprovedChargesAreAccessorialsAndCreateCustomerChargeForUniqueChargeCodeScenario() {
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        List<Charge> charges = new ArrayList<>();
        Charge charge = ChargeEntityMocks.createChargeEntityMocks();
        charge.setChargeCode("Test132");
        charges.add(charge);
        charges.add(ChargeEntityMocks.createChargeEntityMocks());
        Mockito.when(chargeRepository.findAllById(Mockito.any())).thenReturn(charges);
        List<CustomerChargeDTO> customerChargeDTOS = new ArrayList<>();
        CustomerChargeDTO customerChargeDTO = new CustomerChargeDTO();
        customerChargeDTO.setChargeCode("LUMPLDPAY");
        customerChargeDTO.setStopNumber(1);
        customerChargeDTO.setTotalChargeAmount(BigDecimal.valueOf(100));
        customerChargeDTOS.add(customerChargeDTO);

        Mockito.when(customerChargeDAO.getCustomerChargesWithExpenseRowAsN(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(customerChargeDTOS);
        billableChargeService.checkApprovedChargesAreAccessorialsAndCreateCustomerCharge(chargeDTO);
        Mockito.verify(customerChargeDAO, Mockito.times(4)).getCustomerChargesWithExpenseRowAsN(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void handleWhenSingleChangeIsApprovedTest() {
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        List<Charge> charges = new ArrayList<>();

        List<CustomerChargeDTO> customerChargeDTOS = new ArrayList<>();
        CustomerChargeDTO customerChargeDTO = new CustomerChargeDTO();
        customerChargeDTO.setChargeCode("LUMPLDPAY");
        customerChargeDTO.setStopNumber(1);
        customerChargeDTOS.add(customerChargeDTO);

        charges.add(ChargeEntityMocks.createChargeEntityMocks());
        Mockito.when(customerChargeDAO.getCustomerChargesWithExpenseRowAsN(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(customerChargeDTOS);
        Mockito.when(parameterRepository.fetchPaymentParameterCount(Mockito.any(), Mockito.any())).thenReturn(2);
        billableChargeService.handleWhenSingleChangeIsApproved(chargeDTO, charges);
        Mockito.verify(specificationAssociationRepository).findSpecificationAssociationIDForSpecificationChargeLevel(Mockito.any(),Mockito.any(),Mockito.any());
    }

    @Test
    public void handleWhenSingleChangeIsApprovedTestWhenCustomerChargesNotPresent() {
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        List<Charge> charges = new ArrayList<>();
        charges.add(ChargeEntityMocks.createChargeEntityMocks());
        Mockito.when(customerChargeDAO.getCustomerChargesWithExpenseRowAsN(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(parameterRepository.fetchPaymentParameterCount(Mockito.any(), Mockito.any())).thenReturn(2);
        billableChargeService.handleWhenSingleChangeIsApproved(chargeDTO, charges);
        Mockito.verify(specificationAssociationRepository).findSpecificationAssociationIDForSpecificationChargeLevel(Mockito.any(),Mockito.any(),Mockito.any());
    }

    @Test
    public void addCustomerChargeWhenNoPaymentParametersPresent() {
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        Charge charge = ChargeEntityMocks.createChargeEntityMocks();
        Mockito.when(parameterRepository.fetchPaymentParameterCount(Mockito.any(), Mockito.any())).thenReturn(2);
        billableChargeService.addCustomerChargeWhenNoPaymentParametersPresent(chargeDTO, charge);
        Mockito.verify(specificationAssociationRepository).findSpecificationAssociationIDForSpecificationChargeLevel(Mockito.any(),Mockito.any(),Mockito.any());
    }

    @Test
    public void addCustomerChargeWhenNoPaymentParametersPresentAndStopNumberIsZero() {
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        Charge charge = ChargeEntityMocks.createChargeEntityMocks();
        charge.setStopNumber(0);
        Mockito.when(parameterRepository.fetchPaymentParameterCount(Mockito.any(), Mockito.any())).thenReturn(0);
        billableChargeService.addCustomerChargeWhenNoPaymentParametersPresent(chargeDTO, charge);
        Mockito.verify(specificationAssociationRepository).findSpecificationAssociationIDForSpecificationChargeLevel(Mockito.any(),Mockito.any(),Mockito.any());
    }

    @Test
    public void addCustomerChargeWhenNoPaymentParametersPresentAndStopNumberIsNotZero() {
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        Charge charge = ChargeEntityMocks.createChargeEntityMocks();
        charge.setStopNumber(1);
        Mockito.when(parameterRepository.fetchPaymentParameterCount(Mockito.any(), Mockito.any())).thenReturn(0);
        billableChargeService.addCustomerChargeWhenNoPaymentParametersPresent(chargeDTO, charge);
        Mockito.verify(specificationAssociationRepository).findSpecificationAssociationIDForSpecificationChargeLevel(Mockito.any(),Mockito.any(),Mockito.any());
    }

    @Test
    public void addCustomerChargeWhenOnlyOneChargePresentInDBTest() {

        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        List<CustomerChargeDTO> customerChargeDTOS = new ArrayList<>();
        CustomerChargeDTO customerChargeDTO = new CustomerChargeDTO();
        customerChargeDTO.setChargeCode("LUMPLDPAY");
        customerChargeDTO.setStopNumber(1);
        customerChargeDTO.setTotalChargeAmount(BigDecimal.TEN);
        customerChargeDTOS.add(customerChargeDTO);
        Mockito.when(parameterRepository.fetchPaymentParameterCount(Mockito.any(), Mockito.any())).thenReturn(1);
        Charge charge = ChargeEntityMocks.createChargeEntityMocks();
        billableChargeService.addCustomerChargeWhenOnlyOneChargePresentInDB(chargeDTO, charge, customerChargeDTOS);
        Mockito.verify(specificationAssociationRepository).findSpecificationAssociationIDForSpecificationChargeLevel(Mockito.any(),Mockito.any(),Mockito.any());
    }

    @Test
    public void handleWhenMoreChargesPresentInDBForSameChargeCode() {
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        List<CustomerChargeDTO> customerChargeDTOS = new ArrayList<>();
        CustomerChargeDTO customerChargeDTO = new CustomerChargeDTO();
        customerChargeDTO.setChargeCode("LUMPLDPAY");
        customerChargeDTO.setStopNumber(0);
        customerChargeDTO.setTotalChargeAmount(BigDecimal.TEN);
        customerChargeDTOS.add(customerChargeDTO);
        Charge charge = ChargeEntityMocks.createChargeEntityMocks();
        billableChargeService.handleWhenMoreChargesPresentInDBForSameChargeCode(chargeDTO, charge, customerChargeDTOS);
        Mockito.verify(chargeRepo).updateExpenceRowN(Mockito.any());
    }

    @Test
    public void handleWhenMoreChargesPresentInDBForSameChargeCodeAndStopNumberIsNotZero() {
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        List<CustomerChargeDTO> customerChargeDTOS = new ArrayList<>();
        CustomerChargeDTO customerChargeDTO = new CustomerChargeDTO();
        customerChargeDTO.setChargeCode("LUMPLDPAY");
        customerChargeDTO.setStopNumber(1000);
        customerChargeDTO.setTotalChargeAmount(BigDecimal.valueOf(3000));
        customerChargeDTOS.add(customerChargeDTO);
        Charge charge = ChargeEntityMocks.createChargeEntityMocks();
        billableChargeService.handleWhenMoreChargesPresentInDBForSameChargeCode(chargeDTO, charge, customerChargeDTOS);
        Mockito.verify(chargeRepo).updateExpenceRowN(Mockito.any());
    }

    @Test
    public void inactivateExistingAndAddNewCustomerChargesWhenAmountsNotEqualTest() {

        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO("ICS");
        List<CustomerChargeDTO> customerChargeDTOS = new ArrayList<>();
        CustomerChargeDTO customerChargeDTO = new CustomerChargeDTO();
        customerChargeDTO.setChargeCode("LUMPLDPAY");
        customerChargeDTO.setStopNumber(0);
        customerChargeDTO.setTotalChargeAmount(BigDecimal.valueOf(2000));
        customerChargeDTOS.add(customerChargeDTO);

        Charge charge = ChargeEntityMocks.createChargeEntityMocks();
        charge.setStopNumber(0);
        billableChargeService.inactivateExistingAndAddNewCustomerChargesWhenStopNumberZeroEligible(chargeDTO, charge, customerChargeDTOS);
        Mockito.verify(chargeRepo).updateExpenceRowN(Mockito.any());
    }
}

package com.jbhunt.finance.carrierpayment.autopay.service;


import com.jbhunt.finance.carrierpayment.autopay.entity.*;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ActivityMock;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentMocks;
import com.jbhunt.finance.carrierpayment.autopay.repository.CarrierPaymentProcessEventRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.ParameterRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.SpecificationAssociationRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class AutopayValidationServiceTest {

    @InjectMocks
    private AutopayValidationService autopayValidationService;
    @Mock
    private ParameterRepository parameterRepository;
    @Mock
    private SpecificationAssociationRepository specificationAssociationRepository;
    @Mock
    private AutoPayService autoPayService;
    @Mock
    private ProcessAutoPayService processAutoPayService;
    @Mock
    private EstimationDueDateService estimationDueDateService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CarrierPaymentProcessEventRepository carrierPaymentProcessEventRepository;
    @Mock
    private PaymentStateService paymentStateService;
    @Mock
    private ElasticModificationService elasticModificationService;



    @Test
    public void fetchBillToCodesMatchParametersTest() {
        SpecificationAssociation specificationAssociation = new SpecificationAssociation();
        specificationAssociation.setSpecificationAssociationID(123456);
        Parameter parameter = new Parameter();
        parameter.setSpecificationSub("ABCDE");
        parameter.setEffectiveDate(LocalDateTime.now());
        parameter.setExpirationDate(LocalDateTime.of(9999, 03, 13, 3, 4));
        when(specificationAssociationRepository.findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(any(), any(), any(), any(), any())).thenReturn(specificationAssociation);
        when(parameterRepository
                .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                        any(), any(), any())).thenReturn(Arrays.asList(parameter));
        boolean autoPayStatus = autopayValidationService.fetchBillToCodes(PaymentMocks.getPayment());
        assertTrue(autoPayStatus);
    }

    @Test
    public void fetchBillToCodesNotMatchParametersTest() {

        SpecificationAssociation specificationAssociation = new SpecificationAssociation();
        specificationAssociation.setSpecificationAssociationID(123456);
        Parameter parameter = new Parameter();
        parameter.setSpecificationSub("WEDFR");
        parameter.setEffectiveDate(LocalDateTime.now());
        parameter.setExpirationDate(LocalDateTime.of(9999, 03, 13, 3, 4));
        when(specificationAssociationRepository.findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(any(), any(), any(), any(), any())).thenReturn(specificationAssociation);
        when(parameterRepository
                .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                        any(), any(), any())).thenReturn(Arrays.asList(parameter));
        boolean autoPayStatus = autopayValidationService.fetchBillToCodes(PaymentMocks.getPayment());
        assertFalse(autoPayStatus);

    }

    @Test
    public void fetchBillToCodesParametersEmptyTest() {
        SpecificationAssociation specificationAssociation = new SpecificationAssociation();
        specificationAssociation.setSpecificationAssociationID(123456);
        Parameter parameter = new Parameter();
        when(specificationAssociationRepository.findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(any(), any(), any(), any(), any())).thenReturn(specificationAssociation);
        when(parameterRepository
                .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                        any(), any(), any())).thenReturn(null);
        boolean autoPayStatus = autopayValidationService.fetchBillToCodes(PaymentMocks.getPayment());
        assertFalse(autoPayStatus);

    }

    @Test
    public void validateCalculatedDueDateTest() {
        Payment payment = PaymentMocks.getPayment();
        LocalDateTime ScanTimeStamp = LocalDateTime.now().minusDays(10);
        LocalDateTime calculatedDate = LocalDateTime.now().plusDays(1);
        when(estimationDueDateService.getTermStartDateForApproveScenarioByCurrentInvoicesScanDate(any(), any())).thenReturn(calculatedDate);
        boolean autoPayStatus = autopayValidationService.validateCalculatedInvoiceDate(payment, ScanTimeStamp);
        assertFalse(autoPayStatus);

    }

    @Test
    public void validateCalculatedDueDateAfterDueDateTest() {
        Payment payment = PaymentMocks.getPayment();
        LocalDateTime ScanTimeStamp = LocalDateTime.now().minusDays(30);
        LocalDateTime calculatedDate = LocalDateTime.now().minusDays(1);
        when(estimationDueDateService.getTermStartDateForApproveScenarioByCurrentInvoicesScanDate(any(), any())).thenReturn(calculatedDate);
        boolean autoPayStatus = autopayValidationService.validateCalculatedInvoiceDate(payment, ScanTimeStamp);
        assertTrue(autoPayStatus);

    }

    @Test
    public void validateCalculatedDateCurrentDueDateTest() {
        Payment payment = PaymentMocks.getPayment();
        LocalDateTime ScanTimeStamp = LocalDateTime.now().minusDays(30);
        LocalDateTime calculatedDate = LocalDateTime.now();
        when(estimationDueDateService.getTermStartDateForApproveScenarioByCurrentInvoicesScanDate(any(), any())).thenReturn(calculatedDate);
        boolean autoPayStatus = autopayValidationService.validateCalculatedInvoiceDate(payment, ScanTimeStamp);
        assertTrue(autoPayStatus);

    }

    @Test
    public void validateCalculatedDueDateTestNull() {
        Payment payment = PaymentMocks.getPayment();
        LocalDateTime ScanTimeStamp = LocalDateTime.now().minusDays(10);
        LocalDateTime calculatedDate = null;
        when(estimationDueDateService.getTermStartDateForApproveScenarioByCurrentInvoicesScanDate(any(), any())).thenReturn(calculatedDate);
        boolean autoPayStatus = autopayValidationService.validateCalculatedInvoiceDate(payment, ScanTimeStamp);
        assertFalse(autoPayStatus);
    }

    //validateToUpdateProcessEventTable

    @Test
    public void validateToUpdateProcessEventTableFalse() {
        Payment payment = PaymentMocks.getPayment();
        payment.setActivities( ActivityMock.getActivityMock1());
        boolean updateFlag = autopayValidationService.validateToUpdateProcessEventTable(new CarrierPaymentProcessEvent(), payment);
        verify(processAutoPayService,times(1)).updateCarrierPaymentProcessTable(any());
        assertFalse(updateFlag);
    }

    @Test
    public void validateToUpdateProcessEventTableTrue() {
        Payment payment = PaymentMocks.getPayment();
        payment.setActivities( ActivityMock.getActivityMock2());
        boolean updateFlag =  autopayValidationService.validateToUpdateProcessEventTable(new CarrierPaymentProcessEvent(), payment);
        assertTrue(updateFlag);
    }


    @Test
    public void validateZeroMileDispatchValidation() {
        Payment payment = new Payment();
        payment.setDispatchMiles(0);
        payment.setGroupFlowTypeCode("ICS");
        payment.setActivities( ActivityMock.getActivityMock2());
        SpecificationAssociation specificationAssociation = new SpecificationAssociation();
        specificationAssociation.setSpecificationAssociationID(123456);
        WorkflowSpecificationAssociation workSpecAss = new WorkflowSpecificationAssociation();
        workSpecAss.setWorkflowGroupTypeCode("ICS");
        Parameter parameter = new Parameter();
        parameter.setSpecificationSub("false");
        parameter.setMaxNumberValue(BigDecimal.valueOf(0.00));
        parameter.setMinNumberValue(BigDecimal.valueOf(0.00));
        parameter.setEffectiveDate(LocalDateTime.now());
        parameter.setExpirationDate(LocalDateTime.of(9999, 03, 13, 3, 4));
        parameter.setWorkflowSpecificationAssociation(workSpecAss);
        when(specificationAssociationRepository.findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(any(), any(), any(), any(), any())).thenReturn(specificationAssociation);
        when(parameterRepository
                .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                        any(), any(), any())).thenReturn(Arrays.asList(parameter));
        boolean updateFlag =  autopayValidationService.zeroMileDispatchValidation(payment);
        assertFalse(updateFlag);
    }
    @Test
    public void validateZeroMileDispatchValidation2() {
        Payment payment = PaymentMocks.getPayment();
        payment.setDispatchMiles(null);
        payment.setGroupFlowTypeCode("ICS");
        payment.setActivities( ActivityMock.getActivityMock2());
        SpecificationAssociation specificationAssociation = new SpecificationAssociation();
        specificationAssociation.setSpecificationAssociationID(123456);
        WorkflowSpecificationAssociation workSpecAss = new WorkflowSpecificationAssociation();
        workSpecAss.setWorkflowGroupTypeCode("ICS");
        Parameter parameter = new Parameter();
        parameter.setSpecificationSub("false");
        parameter.setMaxNumberValue(BigDecimal.valueOf(0.00));
        parameter.setMinNumberValue(BigDecimal.valueOf(0.00));
        parameter.setEffectiveDate(LocalDateTime.now());
        parameter.setExpirationDate(LocalDateTime.of(9999, 03, 13, 3, 4));
        parameter.setWorkflowSpecificationAssociation(workSpecAss);
        when(specificationAssociationRepository.findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(any(), any(), any(), any(), any())).thenReturn(specificationAssociation);
        when(parameterRepository
                .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                        any(), any(), any())).thenReturn(Arrays.asList(parameter));
        boolean updateFlag =  autopayValidationService.zeroMileDispatchValidation(payment);
        assertFalse(updateFlag);
    }
    @Test
    public void validateZeroMileDispatchValidationJBI() {
        Payment payment = PaymentMocks.getPayment();
        payment.setDispatchMiles(20);
        payment.setGroupFlowTypeCode("JBI");
        payment.setActivities( ActivityMock.getActivityMock2());
        SpecificationAssociation specificationAssociation = new SpecificationAssociation();
        specificationAssociation.setSpecificationAssociationID(123456);
        WorkflowSpecificationAssociation workSpecAss = new WorkflowSpecificationAssociation();
        workSpecAss.setWorkflowGroupTypeCode("JBI");
        Parameter parameter = new Parameter();
        parameter.setSpecificationSub("true");
        parameter.setMaxNumberValue(BigDecimal.valueOf(100.00));
        parameter.setMinNumberValue(BigDecimal.valueOf(10.00));
        parameter.setEffectiveDate(LocalDateTime.now());
        parameter.setExpirationDate(LocalDateTime.of(9999, 03, 13, 3, 4));
        parameter.setWorkflowSpecificationAssociation(workSpecAss);
        when(specificationAssociationRepository.findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(any(), any(), any(), any(), any())).thenReturn(specificationAssociation);
        when(parameterRepository
                .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                        any(), any(), any())).thenReturn(Arrays.asList(parameter));
        boolean updateFlag =  autopayValidationService.zeroMileDispatchValidation(payment);
        assertFalse(updateFlag);
    }
    @Test
    public void validateZeroMileDispatchValidationICS() {
        Payment payment = PaymentMocks.getPayment();
        payment.setDispatchMiles(20);
        payment.setGroupFlowTypeCode("ICS");
        payment.setActivities( ActivityMock.getActivityMock2());
        SpecificationAssociation specificationAssociation = new SpecificationAssociation();
        specificationAssociation.setSpecificationAssociationID(123456);
        WorkflowSpecificationAssociation workSpecAss = new WorkflowSpecificationAssociation();
        workSpecAss.setWorkflowGroupTypeCode("ICS");
        Parameter parameter = new Parameter();
        parameter.setSpecificationSub("false");
        parameter.setMaxNumberValue(BigDecimal.valueOf(100.00));
        parameter.setMinNumberValue(BigDecimal.valueOf(10.00));
        parameter.setEffectiveDate(LocalDateTime.now());
        parameter.setExpirationDate(LocalDateTime.of(9999, 03, 13, 3, 4));
        parameter.setWorkflowSpecificationAssociation(workSpecAss);
        when(specificationAssociationRepository.findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(any(), any(), any(), any(), any())).thenReturn(specificationAssociation);
        when(parameterRepository
                .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                        any(), any(), any())).thenReturn(Arrays.asList(parameter));
        boolean updateFlag =  autopayValidationService.zeroMileDispatchValidation(payment);
        assertFalse(updateFlag);
    }
    @Test
    public void validateZeroMileDispatchValidation3() {
        Payment payment = PaymentMocks.getPayment();
        payment.setDispatchMiles(1);
        payment.setActivities( ActivityMock.getActivityMock2());
        boolean updateFlag =  autopayValidationService.zeroMileDispatchValidation(payment);
        assertFalse(updateFlag);
    }
    @Test
    public void validateZeroMileDispatchValidation1() {
        Payment payment = PaymentMocks.getPayment();
        payment.setDispatchMiles(500);
        boolean updateFlag =  autopayValidationService.zeroMileDispatchValidation(payment);
        assertFalse(updateFlag);
    }

}

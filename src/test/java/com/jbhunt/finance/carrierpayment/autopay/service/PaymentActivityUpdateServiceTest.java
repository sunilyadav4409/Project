

package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.*;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ActivityDTOMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeDTOMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeEntityMocks;
import com.jbhunt.finance.carrierpayment.autopay.repository.*;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ActivityService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ChargeUtilService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.PaymentActivityUpdateService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.PaymentStateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PaymentActivityUpdateServiceTest {

    @InjectMocks
    private PaymentActivityUpdateService paymentActivityUpdateService;

    @Mock
    private ChargeRepository chargeRepository;

    @Mock
    private ActivityService activityService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private WorkflowGroupAssociationRepository wGroupAssociationRepository;

    @Mock
    private PaymentStateLogRepository stateLogRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private ChargeUtilService chargeUtilService;

    @Mock
    private PaymentStateService paymentStateService;

    @Test
    public void testUpdateInvoiceStatus() {

        ChargeDTO chargeDTO = getChargeDTO("Approve");
        Mockito.when(invoiceRepository.findById(Mockito.anyInt())).thenReturn(getCarrierInvoiceHeader());

        paymentActivityUpdateService.updateInvoiceStatus(chargeDTO);

        verify(invoiceRepository, times(1)).save(Mockito.any());
    }

    @Test
    public void testUpdatePayment() {

        Payment existingPayment = getPayment("AutoApprv", "");

        List<Charge> charges = new ArrayList<>();
        charges.add(ChargeEntityMocks.createChargeEntityMocks());

        Mockito.when(chargeUtilService.getAmount(Mockito.any())).thenReturn(BigDecimal.TEN);

        Mockito.when(chargeRepository
                .findByCarrierPaymentCarrierPaymentIdAndExpirationTimestampIsNull(Mockito.any())).thenReturn(charges);

        Payment updatePayment = paymentActivityUpdateService.updatePayment(existingPayment, false);

        assertNotNull(updatePayment);

        assertEquals("AutoApprv", updatePayment.getStatusFlowTypeCode());

        assertEquals(BigDecimal.TEN, updatePayment.getTotalChargeAmount());

        verify(paymentRepository, times(1)).save(Mockito.any());

        verify(paymentStateService, times(1)).createNewPaymentState(Mockito.any());
    }

    @Test
    public void testUpdatePayment_One() {

        Payment existingPayment = getPayment("AutoApprv", "");
        Mockito.when(wGroupAssociationRepository
                .findByWorkflowGroupTypeCodeAndWorkflowStatusEventCode(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(getWorkFlowGroupAssn());
        Mockito.when(chargeUtilService.getAmount(Mockito.any())).thenReturn(BigDecimal.TEN);

        Mockito.when(stateLogRepository
                .findByCarrierPaymentIdCarrierPaymentIdAndStatusFlowTypeCode(Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());
        Payment updatePayment = paymentActivityUpdateService.updatePayment(existingPayment, true);

        assertNotNull(updatePayment);

        assertEquals("LTLApprv", updatePayment.getStatusFlowTypeCode());

        assertEquals(BigDecimal.TEN, updatePayment.getTotalChargeAmount());

        verify(paymentStateService, times(2)).createNewPaymentState(Mockito.any());
    }

    @Test
    public void testUpdatePayment_CancelJob() {

        Payment existingPayment = getPayment("RcvCnclJob", "ICS");

        Mockito.when(wGroupAssociationRepository.findByWorkflowGroupTypeCodeAndWorkflowStatusEventCode(
                Mockito.any(), Mockito.any()))
                .thenReturn(getWorkFlowGroupAssn());

        Mockito.when(chargeUtilService.getAmount(Mockito.any())).thenReturn(BigDecimal.TEN);

        Mockito.when(stateLogRepository
                .findByCarrierPaymentIdCarrierPaymentIdAndStatusFlowTypeCode(Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());

        Payment updatePayment = paymentActivityUpdateService.updatePayment(existingPayment, true);

        assertNotNull(updatePayment);

        assertEquals("LTLApprv", updatePayment.getStatusFlowTypeCode());

        assertEquals("ICS", updatePayment.getGroupFlowTypeCode());

        assertEquals(BigDecimal.TEN, updatePayment.getTotalChargeAmount());

        verify(paymentStateService, times(2)).createNewPaymentState(Mockito.any());
    }

    @Test
    public void testUpdatePayment_StateLog() {

        Payment existingPayment = getPayment("RcvCnclJob", "ICS");
        Mockito.when(wGroupAssociationRepository.findByWorkflowGroupTypeCodeAndWorkflowStatusEventCode(
                Mockito.any(),  Mockito.any()))
                .thenReturn(getWorkFlowGroupAssn());
        Mockito.when(wGroupAssociationRepository.findByWorkflowGroupTypeCodeAndWorkflowStatusEventCode(existingPayment.getGroupFlowTypeCode(),
                CarrierPaymentConstants.EVENT_PRE_PRCS)).thenReturn(getWorkFlowGroupAssn());

        Mockito.when(chargeUtilService.getAmount(Mockito.any())).thenReturn(BigDecimal.TEN);

        Mockito.when(stateLogRepository
                .findByCarrierPaymentIdCarrierPaymentIdAndStatusFlowTypeCode(Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());

        Payment updatePayment = paymentActivityUpdateService.updatePayment(existingPayment, true);

        assertNotNull(updatePayment);

        assertEquals("LTLApprv", updatePayment.getStatusFlowTypeCode());

        assertEquals("ICS", updatePayment.getGroupFlowTypeCode());

        assertEquals(BigDecimal.TEN, updatePayment.getTotalChargeAmount());

        verify(paymentStateService, times(2)).createNewPaymentState(Mockito.any());
    }

    @Test
    public void testUpdatePayment_StatusFlowTypeCodeNull() {

        Payment existingPayment = getPayment("Paperwork", "ICS");
        Mockito.when(wGroupAssociationRepository
                .findByWorkflowGroupTypeCodeAndWorkflowStatusEventCode(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(getWorkFlowGroupAssn());

        Mockito.when(chargeUtilService.getAmount(Mockito.any())).thenReturn(BigDecimal.TEN);

        Mockito.when(stateLogRepository
                .findByCarrierPaymentIdCarrierPaymentIdAndStatusFlowTypeCode(Mockito.any(),Mockito.any())).thenReturn(new ArrayList<>());

        Payment updatePayment = paymentActivityUpdateService.updatePayment(existingPayment, true);

        assertNotNull(updatePayment);

        assertEquals("Paperwork", updatePayment.getStatusFlowTypeCode());

        assertEquals("ICS", updatePayment.getGroupFlowTypeCode());

        assertEquals(BigDecimal.TEN, updatePayment.getTotalChargeAmount());

        verify(paymentStateService, times(1)).createNewPaymentState(Mockito.any());
    }

    @Test
    public void testUpdatePayment_ChargeListEmpty() {

        Payment existingPayment = getPayment1();

        Payment updatePayment = paymentActivityUpdateService.updatePayment(existingPayment, false);

        assertNotNull(updatePayment);

        assertEquals("AutoApprv", updatePayment.getStatusFlowTypeCode());

        assertEquals(null, updatePayment.getGroupFlowTypeCode());

        verify(paymentStateService, times(1)).createNewPaymentState(Mockito.any());

    }

    @Test
    public void testUpdatePayment_ChargeListNotEmpty() {

        Payment existingPayment = getPayment1();

        Payment updatePayment = paymentActivityUpdateService.updatePayment(existingPayment, false);

        assertNotNull(updatePayment);

        assertEquals("AutoApprv", updatePayment.getStatusFlowTypeCode());

        assertEquals(null, updatePayment.getGroupFlowTypeCode());

        verify(paymentStateService, times(1)).createNewPaymentState(Mockito.any());
    }


    @Test
    public void testChargeApproveRejectActivityCreation_Approve() {

        paymentActivityUpdateService.chargeApproveRejectActivityCreation(getChargeDTO("Approve"), ActivityDTOMocks.createActivityMock());

        verify(activityService, times(1)).saveActivityDetails(Mockito.any());
    }

    @Test
    public void testChargeApproveRejectActivityCreation_Reject() {

        paymentActivityUpdateService.chargeApproveRejectActivityCreation(getChargeDTO("Reject"), ActivityDTOMocks.createActivityMock());

        verify(activityService, times(1)).saveActivityDetails(Mockito.any());
    }


    private Payment getPayment1() {
        Payment payment = new Payment();
        List<Charge> carrierPaymentChargeList = new ArrayList<>();
        payment.setCarrierPaymentChargeList(carrierPaymentChargeList);
        payment.setStatusFlowTypeCode("AutoApprv");
        payment.setPaymentActionTypeCode("Ignore");
        return payment;
    }

    private WorkflowGroupAssociation getWorkFlowGroupAssn() {
        WorkflowGroupAssociation groupAssociation = new WorkflowGroupAssociation();
        groupAssociation.setWorkflowStatusTypeCode("LTLApprv");
        groupAssociation.setWorkflowStatusEventCode("LTLApprv");

        return groupAssociation;
    }

    private Payment getPayment(String code, String groupType) {
        Payment payment = new Payment();
        List<Charge> carrierPaymentChargeList = new ArrayList<>();
        Charge charge = new Charge();
        charge.setChargeCode("CC01");
        charge.setChargeDecisionCode("Approve");
        charge.setExpirationTimestamp(LocalDateTime.now());
        ChargeOverride chargeOverride = new ChargeOverride();
        chargeOverride.setOverrideAmount(new BigDecimal("100"));
        charge.setChargeOverride(chargeOverride);
        carrierPaymentChargeList.add(charge);
        payment.setCarrierPaymentChargeList(carrierPaymentChargeList);
        payment.setStatusFlowTypeCode(code);
        payment.setGroupFlowTypeCode(groupType);
        payment.setPaymentActionTypeCode("Ignore");
        return payment;
    }

    private Optional<CarrierInvoiceHeader> getCarrierInvoiceHeader() {
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierDocumentNumber("Doc1");
        return Optional.of(carrierInvoiceHeader);
    }

    private ChargeDTO getChargeDTO(String decisionCode) {
        ChargeDTO chargeDTO = ChargeDTOMocks.createChargeDTOMOcks();
        chargeDTO.setHeaderId(1);
        chargeDTO.setInvoiceNumber("Inv12");
        chargeDTO.setInvoiceDate("2018/05/02");
        chargeDTO.setPaymentId(2);
        chargeDTO.setScacCode("MGAS");
        chargeDTO.setAutoPay(true);
        chargeDTO.setChargeDecisionCode(decisionCode);
        return chargeDTO;
    }
}

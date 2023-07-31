package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierPaymentChargeAuditDetail;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.mocks.AuditReasonTypeMock;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentMocks;
import com.jbhunt.finance.carrierpayment.autopay.repository.AuditReasonTypeRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.CarrierPaymentChargeAuditDetailRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ChargeAuditDetailService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChargeAuditDetailServiceTest {

    @Mock
    CarrierPaymentChargeAuditDetailRepository carrierPaymentChargeAuditDetailRepository;

    @Mock
    AuditReasonTypeRepository auditReasonTypeRepository;

    @InjectMocks
    ChargeAuditDetailService chargeAuditDetailService;

    @Test
    public void saveCarrierPaymentChargeAuditDetailTestWithAuditReason(){
        Mockito.when(auditReasonTypeRepository.findByAuditReasonTypeName(PaymentChargesConstants.UPDATE)).thenReturn(AuditReasonTypeMock.getAuditReasonType("update"));
        chargeAuditDetailService.saveCarrierPaymentChargeAuditDetail(PaymentMocks.getPayment(), ChargeMocks.getCharge(),PaymentChargesConstants.UPDATE);
        verify(carrierPaymentChargeAuditDetailRepository,times(1)).save(any(CarrierPaymentChargeAuditDetail.class));
        verify(auditReasonTypeRepository,times(1)).findByAuditReasonTypeName(PaymentChargesConstants.UPDATE);
    }
    @Test
    public void saveCarrierPaymentChargeAuditDetailTestWithoutAuditReason(){
        Mockito.when(auditReasonTypeRepository.findByAuditReasonTypeName(PaymentChargesConstants.UPDATE)).thenReturn(AuditReasonTypeMock.getAuditReasonType("update"));
        chargeAuditDetailService.saveCarrierPaymentChargeAuditDetail(PaymentMocks.getPayment(), ChargeMocks.getCharge(),"");
        verify(carrierPaymentChargeAuditDetailRepository,times(1)).save(any(CarrierPaymentChargeAuditDetail.class));
        verify(auditReasonTypeRepository,times(1)).findByAuditReasonTypeName(PaymentChargesConstants.UPDATE);
    }
    @Test
    public void saveCarrierPaymentChargeAuditDetailTestWithoutAuditReason1(){
        Charge charge = ChargeMocks.getCharge();
        charge.setChargeDecisionCode("REJECT");
        Mockito.when(auditReasonTypeRepository.findByAuditReasonTypeName("REJECT")).thenReturn(AuditReasonTypeMock.getAuditReasonType("REJECT"));
        chargeAuditDetailService.saveCarrierPaymentChargeAuditDetail(PaymentMocks.getPayment(), charge,"");
        verify(carrierPaymentChargeAuditDetailRepository,times(1)).save(any(CarrierPaymentChargeAuditDetail.class));
        verify(auditReasonTypeRepository,times(1)).findByAuditReasonTypeName("REJECT");
    }

    @Test
    public void saveCarrierPaymentChargeAuditDetailTestWithoutAuditReason2(){
        Charge charge = ChargeMocks.getCharge();
        charge.setChargeDecisionCode("REJECT1");
        Mockito.when(auditReasonTypeRepository.findByAuditReasonTypeName(PaymentChargesConstants.UPDATE)).thenReturn(AuditReasonTypeMock.getAuditReasonType("update"));
        CarrierPaymentChargeAuditDetail chargeAuditDetail = chargeAuditDetailService.saveCarrierPaymentChargeAuditDetail(PaymentMocks.getPayment(), charge,"");
        verify(carrierPaymentChargeAuditDetailRepository,times(1)).save(any(CarrierPaymentChargeAuditDetail.class));
        verify(auditReasonTypeRepository,times(2)).findByAuditReasonTypeName(anyString());
        Assert.assertEquals("N", chargeAuditDetail.getOriginalChargeIndicator().toString());
    }

    @Test
    public void saveCarrierPaymentChargeAuditDetailTestWithAuditReasonADD(){
        Mockito.when(auditReasonTypeRepository.findByAuditReasonTypeName(PaymentChargesConstants.ADD)).thenReturn(AuditReasonTypeMock.getAuditReasonType("add"));
        CarrierPaymentChargeAuditDetail chargeAuditDetail =  chargeAuditDetailService.saveCarrierPaymentChargeAuditDetail(PaymentMocks.getPayment(), ChargeMocks.getCharge(),PaymentChargesConstants.ADD);
        verify(carrierPaymentChargeAuditDetailRepository,times(1)).save(any(CarrierPaymentChargeAuditDetail.class));
        verify(auditReasonTypeRepository,times(1)).findByAuditReasonTypeName(PaymentChargesConstants.ADD);
        Assert.assertEquals("Y", chargeAuditDetail.getOriginalChargeIndicator().toString());
    }

    @Test
    public void saveCarrierPaymentChargeAuditDetailTestWithChargeTypeCode(){
        Mockito.when(auditReasonTypeRepository.findByAuditReasonTypeName(PaymentChargesConstants.ADD)).thenReturn(AuditReasonTypeMock.getAuditReasonType("add"));
        CarrierPaymentChargeAuditDetail chargeAuditDetail =  chargeAuditDetailService.saveCarrierPaymentChargeAuditDetail(PaymentMocks.getPaymentRequiredDocumentProcessed(), ChargeMocks.getCharge(),PaymentChargesConstants.ADD);
        String chargeCreateTypeCode =  chargeAuditDetailService.chargeCreationTypeCodeValidation(PaymentMocks.getPaymentRequiredDocumentProcessed());
        verify(carrierPaymentChargeAuditDetailRepository,times(1)).save(any(CarrierPaymentChargeAuditDetail.class));
        verify(auditReasonTypeRepository,times(1)).findByAuditReasonTypeName(PaymentChargesConstants.ADD);
        Assert.assertEquals("Y", chargeAuditDetail.getOriginalChargeIndicator().toString());
        Assert.assertEquals("POSTAPROVE", chargeCreateTypeCode);
    }
    @Test
    public void saveCarrierPaymentChargeAuditDetailTestWithChargeTypeCode1(){
        Mockito.when(auditReasonTypeRepository.findByAuditReasonTypeName(PaymentChargesConstants.ADD)).thenReturn(AuditReasonTypeMock.getAuditReasonType("add"));
        CarrierPaymentChargeAuditDetail chargeAuditDetail =  chargeAuditDetailService.saveCarrierPaymentChargeAuditDetail(PaymentMocks.getPayment(), ChargeMocks.getCharge(),PaymentChargesConstants.ADD);
        String chargeCreateTypeCode =  chargeAuditDetailService.chargeCreationTypeCodeValidation(PaymentMocks.getPayment());
        verify(carrierPaymentChargeAuditDetailRepository,times(1)).save(any(CarrierPaymentChargeAuditDetail.class));
        verify(auditReasonTypeRepository,times(1)).findByAuditReasonTypeName(PaymentChargesConstants.ADD);
        Assert.assertEquals("Y", chargeAuditDetail.getOriginalChargeIndicator().toString());
        Assert.assertEquals("POSTINVOIC", chargeCreateTypeCode);

    }
}


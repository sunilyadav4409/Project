package com.jbhunt.finance.carrierpayment.autopay.service;


import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.entity.*;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeEntityMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentEntityMocks;
import com.jbhunt.finance.carrierpayment.autopay.repository.CarrierPaymentChargeAuditDetailRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.AutoPayService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.AutopayChargeStatusRules;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.AutopayValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.APPROVED;

@RunWith(MockitoJUnitRunner.class)
public class AutopayChargeStatusRulesTest {

    @InjectMocks
    AutopayChargeStatusRules autopayChargeStatusRules;

    @Mock
    CarrierPaymentChargeAuditDetailRepository carrierPaymentChargeAuditDetailRepository;
    @Mock
    AutopayValidationService autopayValidationService;
    @Mock
    AutoPayService autoPayService;


    @Test
    public void isChargeTotalAmountIsTenPercentOrHundredDollars() {
        List<CarrierPaymentChargeAuditDetail> carrierPaymentChargeAuditDetails = getCarrierPaymentChargeAuditDetails();
        Mockito.when(carrierPaymentChargeAuditDetailRepository.findByChargeIdIn(Mockito.anyList())).thenReturn(carrierPaymentChargeAuditDetails);
        List<Parameter> parameters = getParameters();
        Mockito.when(autopayValidationService.fetchParams(Mockito.any(), Mockito.any())).thenReturn(parameters);

        boolean result = autopayChargeStatusRules.isChargeTotalAmountIsTenPercentOrHundredDollars(new ArrayList<>());
        Assertions.assertTrue(result);

    }

    private List<Parameter> getParameters() {
        List<Parameter> parameters = new ArrayList<>();
        Parameter parameter = new Parameter();
        parameter.setParameterCharacterValue("PERCENT");
        parameter.setMinNumberValue(BigDecimal.valueOf(10));
        parameters.add(parameter);

        parameter = new Parameter();
        parameter.setParameterCharacterValue("AMOUNT");
        parameter.setMinNumberValue(BigDecimal.valueOf(100));
        parameters.add(parameter);
        return parameters;
    }

    private List<CarrierPaymentChargeAuditDetail> getCarrierPaymentChargeAuditDetails() {
        List<CarrierPaymentChargeAuditDetail> carrierPaymentChargeAuditDetails = new ArrayList<>();
        CarrierPaymentChargeAuditDetail carrierPaymentChargeAuditDetail = new CarrierPaymentChargeAuditDetail();
        carrierPaymentChargeAuditDetail.setChargeCreationTimeTypeCode("INVOICE");
        carrierPaymentChargeAuditDetail.setTotalChargeAmount(BigDecimal.valueOf(100));
        carrierPaymentChargeAuditDetail.setCrtS(LocalDateTime.now());
        AuditReasonType auditReasonType = new AuditReasonType();
        auditReasonType.setAuditReasonTypeName("ADD");
        carrierPaymentChargeAuditDetail.setAuditReason(auditReasonType);
        carrierPaymentChargeAuditDetails.add(carrierPaymentChargeAuditDetail);

        carrierPaymentChargeAuditDetail = new CarrierPaymentChargeAuditDetail();
        carrierPaymentChargeAuditDetail.setChargeCreationTimeTypeCode("INVOICE");
        carrierPaymentChargeAuditDetail.setTotalChargeAmount(BigDecimal.valueOf(500));
        carrierPaymentChargeAuditDetail.setCrtS(LocalDateTime.now());
        auditReasonType = new AuditReasonType();
        auditReasonType.setAuditReasonTypeName("UPDATE");
        carrierPaymentChargeAuditDetail.setAuditReason(auditReasonType);
        carrierPaymentChargeAuditDetails.add(carrierPaymentChargeAuditDetail);

        carrierPaymentChargeAuditDetail = new CarrierPaymentChargeAuditDetail();
        carrierPaymentChargeAuditDetail.setChargeCreationTimeTypeCode("INVOICE");
        carrierPaymentChargeAuditDetail.setTotalChargeAmount(BigDecimal.valueOf(1));
        carrierPaymentChargeAuditDetail.setCrtS(LocalDateTime.now());
        auditReasonType = new AuditReasonType();
        auditReasonType.setAuditReasonTypeName("DELETE");
        carrierPaymentChargeAuditDetail.setAuditReason(auditReasonType);
        carrierPaymentChargeAuditDetails.add(carrierPaymentChargeAuditDetail);

        carrierPaymentChargeAuditDetail = new CarrierPaymentChargeAuditDetail();
        carrierPaymentChargeAuditDetail.setChargeCreationTimeTypeCode("TEST-INVOICE");
        carrierPaymentChargeAuditDetail.setTotalChargeAmount(BigDecimal.valueOf(1));
        carrierPaymentChargeAuditDetail.setCrtS(LocalDateTime.now());
        auditReasonType = new AuditReasonType();
        auditReasonType.setAuditReasonTypeName("UPDATE");
        carrierPaymentChargeAuditDetail.setAuditReason(auditReasonType);
        carrierPaymentChargeAuditDetails.add(carrierPaymentChargeAuditDetail);

        carrierPaymentChargeAuditDetail = new CarrierPaymentChargeAuditDetail();
        carrierPaymentChargeAuditDetail.setChargeCreationTimeTypeCode("ASSIGN");
        carrierPaymentChargeAuditDetail.setTotalChargeAmount(BigDecimal.valueOf(100));
        carrierPaymentChargeAuditDetail.setCrtS(LocalDateTime.now());
        auditReasonType = new AuditReasonType();
        auditReasonType.setAuditReasonTypeName("UPDATE");
        carrierPaymentChargeAuditDetail.setAuditReason(auditReasonType);
        carrierPaymentChargeAuditDetails.add(carrierPaymentChargeAuditDetail);

        carrierPaymentChargeAuditDetail = new CarrierPaymentChargeAuditDetail();
        carrierPaymentChargeAuditDetail.setChargeCreationTimeTypeCode("Test-ASSIGN");
        carrierPaymentChargeAuditDetail.setTotalChargeAmount(BigDecimal.valueOf(100));
        carrierPaymentChargeAuditDetail.setCrtS(LocalDateTime.now());
        auditReasonType = new AuditReasonType();
        auditReasonType.setAuditReasonTypeName("UPDATE");
        carrierPaymentChargeAuditDetail.setAuditReason(auditReasonType);
        carrierPaymentChargeAuditDetails.add(carrierPaymentChargeAuditDetail);
        return carrierPaymentChargeAuditDetails;
    }

    @Test
    public void validateBookedAndInvoiceAmountsVarianceForLTL() {
        Payment payment = new Payment();
        payment.setGroupFlowTypeCode(CarrierPaymentConstants.LTL);
        List<Charge> charges = new ArrayList<>();
        payment.setCarrierPaymentChargeList(charges);
        List<CarrierPaymentChargeAuditDetail> carrierPaymentChargeAuditDetails = getCarrierPaymentChargeAuditDetails();
        Mockito.when(carrierPaymentChargeAuditDetailRepository.findByChargeIdIn(Mockito.anyList())).thenReturn(carrierPaymentChargeAuditDetails);
        List<Parameter> parameters = getParameters();
        Mockito.when(autopayValidationService.fetchParams(Mockito.any(), Mockito.any())).thenReturn(parameters);
        boolean result = autopayChargeStatusRules.validateBookedAndInvoiceAmountsVarianceForLTL(payment);
        Assertions.assertTrue(result);
        Mockito.verify(autoPayService).logAutoPayFailureActivity(Mockito.any(), Mockito.any());
    }
}



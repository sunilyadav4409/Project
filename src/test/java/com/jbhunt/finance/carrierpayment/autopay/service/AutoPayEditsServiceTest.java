package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ParameterMocks;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import static com.jbhunt.finance.carrierpayment.autopay.mocks.CarrierInvoiceHeaderMocks.*;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeDTOMocks.createChargeDTOMOcks;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeMocks.*;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentMocks.getConsolidatedPaymentLTLFuel;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentMocks.getPaymentAPFail1;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.SpecificationAssociationEntityMocks.getParameterList5;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class AutoPayEditsServiceTest {

    @InjectMocks
    private AutoPayEditsService autoPayEditsService;

    @Mock
    private ChargeDecisionService chargeDecisionService;

    @Mock
    private AutoPayService autoPayService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AutopayValidationService autopayValidationService;

    @Mock
    private PaymentHelperService paymentHelperService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(autoPayEditsService);
    }

    @Test
    public void testCheckInvoiceRulesForAutoPayICSFuelReturnsTrue() {

        Payment payment = getConsolidatedPaymentLTLFuel("Reroute");
        payment.setGroupFlowTypeCode("LTL");
        when(chargeDecisionService.autopaychargeDecision( any(),any() )).thenReturn( true );
        boolean autoPayStatus = autoPayEditsService.checkInvoiceRulesForAutopay(payment, getInvoiceHeader_2(), ParameterMocks.getParameterListings1() );
        assertTrue(autoPayStatus);
        verify(chargeDecisionService,times(1)).autopaychargeDecision( any(),any() );

    }

    @Test
    public void testCheckInvoiceRulesForAutoPayICSSameChargesReturnsTrue() {

        Payment payment = getConsolidatedPaymentLTLFuel("Reroute");
        payment.setGroupFlowTypeCode("LLTL");
        boolean autoPayStatus = autoPayEditsService.checkInvoiceRulesForAutopay(payment, getInvoiceHeader_2(), ParameterMocks.getParameterListings1() );
        assertFalse(autoPayStatus);
        verify(autoPayService,times(1)).logAutoPayFailureActivity(any(),any());
    }

    @Test
    public void testCheckInvoiceRulesForAutoPayICSNonLTLFuelReturnsTrue() {

        Payment payment = getConsolidatedPaymentLTLFuel("Reroute");
        payment.setGroupFlowTypeCode("LLTL");
        payment.setCarrierPaymentChargeList(getChargeList10());
        when(autoPayService.getRequiredParameterList(any(),any())).thenReturn(getParameterList5());
        when(chargeDecisionService.autopaychargeDecision( any(),any() )).thenReturn( true );
        boolean autoPayStatus = autoPayEditsService.checkInvoiceRulesForAutopay(payment, getInvoiceHeader_3(),  ParameterMocks.getParameterListings1() );
        verify(autoPayService,times(1)).getRequiredParameterList(any(),anyString());
        verify(chargeDecisionService,times(1)).autopaychargeDecision(any(),any());
        assertTrue(autoPayStatus);
    }

    @Test
    public void testCheckInvoiceRulesForAutoPayICSNonLTLTransitReturnsTrue() {

        Payment payment = getConsolidatedPaymentLTLFuel("Reroute");
        payment.setGroupFlowTypeCode("LLTL");
        payment.setCarrierPaymentChargeList(getChargeList11());
        when( chargeDecisionService.updateOvverideCharge( any()) ).thenReturn(getChargeOverride());
        when(chargeDecisionService.autopaychargeDecision( any(),any() )).thenReturn( true );
        boolean autoPayStatus = autoPayEditsService.checkInvoiceRulesForAutopay(payment, getInvoiceHeader_3(), ParameterMocks.getParameterListings1() );
        verify(chargeDecisionService,times(1)).updateOvverideCharge(any());
        verify(chargeDecisionService,times(1)).autopaychargeDecision(any(),any());
        assertTrue(autoPayStatus);
    }


    @Test
    public void testCheckInvoiceRulesForAutoPayICSLTLTransitReturnsTrue() {

        Payment payment = getConsolidatedPaymentLTLFuel("Reroute");
        payment.setGroupFlowTypeCode("LTL");
        payment.setCarrierPaymentChargeList(getChargeList10());
        when(autoPayService.getRequiredParameterList(any(),any())).thenReturn(getParameterList5());
        when( chargeDecisionService.updateOvverideCharge( any()) ).thenReturn(getChargeOverride());
        when(chargeDecisionService.autopaychargeDecision( any(),any() )).thenReturn( true );
        boolean autoPayStatus = autoPayEditsService.checkInvoiceRulesForAutopay(payment, getInvoiceHeader_3(), ParameterMocks.getParameterListings1() );
        assertTrue(autoPayStatus);
        verify(chargeDecisionService,times(2)).updateOvverideCharge(any());
        verify(chargeDecisionService,times(1)).autopaychargeDecision(any(),any());

    }


    @Test
    public void testCheckInvoiceRulesForAutoPayICSLTLTransitFuelReturnsTrue() {

        Payment payment = getConsolidatedPaymentLTLFuel("Reroute");
        payment.setGroupFlowTypeCode("LTL");
        payment.setCarrierPaymentChargeList(getChargeList10());
        when(autoPayService.getRequiredParameterList(any(),any())).thenReturn(getParameterList5());
        when( chargeDecisionService.updateOvverideCharge( any()) ).thenReturn(getChargeOverride());
        when(chargeDecisionService.autopaychargeDecision( any(),any() )).thenReturn( true );
        CarrierInvoiceHeader carrierInvoiceHeader=getInvoiceHeader_3();
        carrierInvoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail5());
        boolean autoPayStatus = autoPayEditsService.checkInvoiceRulesForAutopay(payment, carrierInvoiceHeader, ParameterMocks.getParameterListings1() );
        assertTrue(autoPayStatus);
        verify(chargeDecisionService,times(1)).autopaychargeDecision(any(),any());

    }

    @Test
    public void testCheckInvoiceRulesForAutoPayLTLReturnsTrue12() {
        Payment payment = getPaymentAPFail1();
        when(autoPayService.populateChargeDTO( any(),any() )).thenReturn( createChargeDTOMOcks() );
        boolean autoPayStatus = autoPayEditsService.validateChargeAmounstAgainsDb2(payment, getInvoiceHeader_1() );
        assertFalse(autoPayStatus);
        verify(chargeDecisionService,times(0)).autopaychargeDecision(any(),any());
    }
}

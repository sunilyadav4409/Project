package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceDetail;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ParameterMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.TxnStatusDTOMocks;
import com.jbhunt.finance.carrierpayment.autopay.repository.InvoiceRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentStateLogRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.jbhunt.finance.carrierpayment.autopay.mocks.CarrierInvoiceHeaderMocks.*;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class JBIAutoPayServiceTest {

    @InjectMocks
    JBIAutoPayService jbiAutoPayService;

    @Mock
    private ChargeDecisionService chargeDecisionService;

    @Mock
    private PaymentStateService paymentStateService;
    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private AutoPayService autoPayService;
    @Mock
    private PaymentStateLogRepository stateLogRepository;
    @Mock
    private AutopayValidationService autopayValidationService;
    @Mock
    private PaymentHelperService paymentHelperService;

    @Test
    public void failAutopayWithRejectedInvoiceTest() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionNullMock(), ParameterMocks.getParameterListings2(), LocalDateTime.now());
        assertFalse(autoPayStatus);
    }
    @Test
    public void failAutopayWithRejectedInvoiceInvalieChrgeTest() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock21());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionNullMock21(), ParameterMocks.getParameterListings2(), LocalDateTime.now());
        assertFalse(autoPayStatus);
    }

    @Test
    public void failAutopayWithRejectedInvoiceTest1() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.FALSE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionNullMock(), ParameterMocks.getParameterListings2(), LocalDateTime.now());
        assertFalse(autoPayStatus);
    }

    @Test
    public void passAutoPayWhenLoadIsMatchedTest() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock1());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        when(chargeDecisionService.chargeDecision(any(),any())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionMock(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertTrue(autoPayStatus);
    }
    @Test
    public void passAutoPayWhenLoadIsMatchedTest12() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock12());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        when(chargeDecisionService.chargeDecision(any(),any())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionMock12(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertTrue(autoPayStatus);
    }

    @Test
    public void passAutoPayWhenLoadIsMatchedTest13() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock12());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        when(chargeDecisionService.chargeDecision(any(),any())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionMock12(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertTrue(autoPayStatus);
    }
    @Test
    public void passSAveAutoPayWhenLoadIsMatchedTest() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock1());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        when(chargeDecisionService.chargeDecision(any(),any())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks(false));
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionMock(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertFalse(autoPayStatus);
    }

    @Test
    public void passAutoPayWhenLoadDuplicatesTest() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock2());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        when(chargeDecisionService.chargeDecision(any(),any())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithDuplicatesMock(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertTrue(autoPayStatus);
    }
    @Test
    public void passAutoPayWhenLoadNotDuplicatesTest() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock3());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        when(chargeDecisionService.chargeDecision(any(),any())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithDuplicatesMock1(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertTrue(autoPayStatus);
    }
    @Test
    public void passAutoPayWhenLoadNotDuplicatesTest1() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock3());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        when(chargeDecisionService.chargeDecision(any(),any())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithDuplicatesMock2(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertTrue(autoPayStatus);
    }
    @Test
    public void passAutoPayWhenLoadNotDuplicatesTest2() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock4());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        when(chargeDecisionService.chargeDecision(any(),any())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithDuplicatesMock4(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertTrue(autoPayStatus);
    }
    @Test
    public void passAutoPayWhenLoadRejectedTest() {
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiRejectedCreateMock1());
       boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionMock(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertFalse(autoPayStatus);
    }
    @Test
    public void failAutoPayWhenLoadIsMatchedTest() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionMock(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertFalse(autoPayStatus);
    }
    @Test
    public void failAutoPayWhenLoadIsMatchedTest1() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock123());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionMock5(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertFalse(autoPayStatus);
    }
    @Test
    public void passAutoPayTenderChargesTest() {
        boolean autoPayStatus = jbiAutoPayService.jBIInvoiceAndTenderedChargeTransportationGroupAmountValidation(BigDecimal.TEN,BigDecimal.TEN,jbiCreateMock().get(0).getCarrierInvoiceDetailList(),PaymentMocks.getPaymentJBIWithChargeDecionMock().getCarrierPaymentChargeList());
        assertTrue(autoPayStatus);
    }
    @Test
    public void passAutoPayTenderChargesTest1() {
        boolean autoPayStatus = jbiAutoPayService.jBIInvoiceAndTenderedChargeTransportationGroupAmountValidation(BigDecimal.ZERO,BigDecimal.ZERO,createjbiMock6().getCarrierInvoiceDetailList(),PaymentMocks.getPaymentJBIWithChargeDecionMock().getCarrierPaymentChargeList());
        assertTrue(autoPayStatus);
    }
    @Test
    public void passAutoPayTenderChargesTest2() {
        boolean autoPayStatus = jbiAutoPayService.jBIInvoiceAndTenderedChargeTransportationGroupAmountValidation(BigDecimal.ONE,BigDecimal.TEN,createjbiMock6().getCarrierInvoiceDetailList(),PaymentMocks.getPaymentJBIWithChargeDecionMock().getCarrierPaymentChargeList());
        assertFalse(autoPayStatus);
    }
    @Test
    public void passAutoPayWhenLoadIsMatchedTest20() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock12());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        when(chargeDecisionService.chargeDecision(any(),any())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionMock12(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertTrue(autoPayStatus);
    }

    @Test
    public void approveZeroChargespassAutoPayWhenLoadIsMatchedTest20() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock20());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        when(chargeDecisionService.chargeDecision(any(),any())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionMock20(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertTrue(autoPayStatus);
    }
    @Test
    public void approveZeroChargespassAutoPayWhenLoadIsMatchedTest22() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock22());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        when(chargeDecisionService.chargeDecision(any(),any())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionMock22(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertTrue(autoPayStatus);
    }
    @Test
    public void approveZeroChargespassAutoPayWhenLoadIsMatchedTest25() {
        when(autoPayService.checkRulesForAutopay(any(), any(), any(), any())).thenReturn(Boolean.TRUE);
        when(invoiceRepository.findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(), anyString(), anyString())).thenReturn(jbiCreateMock25());
        when(autoPayService.populateChargeDTO(any(), any())).thenReturn(new ChargeDTO());
        when(chargeDecisionService.validateChargeAmount(any(), any())).thenReturn(Boolean.TRUE);
        when(autoPayService.getRequiredParameterList(any(), anyString())).thenReturn(ParameterMocks.getParameterListings2());
        when(chargeDecisionService.chargeDecision(any(),any())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        boolean autoPayStatus = jbiAutoPayService.processJBILoadPayment(PaymentMocks.getPaymentJBIWithChargeDecionMock25(), ParameterMocks.getParameterListings3(), LocalDateTime.now());
        assertTrue(autoPayStatus);
    }
    @Test
    public void autopayJBIChargeListMatch() {
        List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList = getCarrierInvoiceDetail5() ;
        List<Charge> finalTenderedChargeListMinusParameterList = ChargeMocks.getJbiChargeTransportationGroupChargeApprovedMock();
        Boolean flag = jbiAutoPayService.validateInvoiceAndTenderedList(finalInvoiceDetailMinusParameterList, finalTenderedChargeListMinusParameterList, false);
        assertFalse(flag);
    }

    @Test
    public void autopayJBIChargeListMatchNotAmount() {
        List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList = getCarrierInvoiceDetail01() ;
        List<Charge> finalTenderedChargeListMinusParameterList = ChargeMocks.getJbiChargeTransportationGroupChargeApprovedMockAMS();
        Boolean flag = jbiAutoPayService.validateInvoiceAndTenderedList(finalInvoiceDetailMinusParameterList, finalTenderedChargeListMinusParameterList, false);
        assertFalse(flag);
    }

    @Test
    public void autopayJBIChargeListMatchAmountMatch() {
        List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList = getCarrierInvoiceDetail01() ;
        List<Charge> finalTenderedChargeListMinusParameterList = ChargeMocks.getJbiChargeTransportationGroupChargeApprovedMockAMSMatch();
        Boolean flag = jbiAutoPayService.validateInvoiceAndTenderedList(finalInvoiceDetailMinusParameterList, finalTenderedChargeListMinusParameterList, false);
        assertTrue(flag);
    }

    @Test
    public void autopayJBIChargeListMatchZero() {
        List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList = getCarrierInvoiceDetail15() ;
        List<Charge> finalTenderedChargeListMinusParameterList = ChargeMocks.getJbiChargeTransportationGroupChargeApprovedMock();
        Boolean flag = jbiAutoPayService.validateInvoiceAndTenderedList(finalInvoiceDetailMinusParameterList, finalTenderedChargeListMinusParameterList, false);
        assertTrue(flag);
    }

    }

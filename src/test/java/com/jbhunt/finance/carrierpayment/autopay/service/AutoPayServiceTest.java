
package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dao.CarrierPaymentParameterDAO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ParameterListingDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceDetail;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.entity.Supplier;
import com.jbhunt.finance.carrierpayment.autopay.mocks.*;
import com.jbhunt.finance.carrierpayment.autopay.repository.*;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.*;
import org.apache.camel.ProducerTemplate;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jbhunt.finance.carrierpayment.autopay.mocks.CarrierInvoiceDetailMock.getDuplicateCarrierInvoiceDetail1234;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.CarrierInvoiceHeaderMocks.*;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.ParameterMocks.*;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentMocks.CheckRulesForAutoPayChargeCode;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentMocks.getConsolidatedPaymentLTLFuel;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class AutoPayServiceTest {

    @InjectMocks
    AutoPayService autoPayService;

    @Mock
    ActivityService activityService;

    @Mock
    ChargeDecisionService chargeDecisionService;


    @Mock
    InvoiceRepository invoiceRepository;

    @Mock
    AutoPayEditsService autoPayEditsService;

    @Mock
    PaymentStateService paymentStateService;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    CarrierPaymentProcessEventRepository carrierPaymentProcessEventRepository;

    @Mock
    PaymentStateLogRepository stateLogRepository;
    @Mock
    CarrierPaymentParameterDAO carrierPaymentParameterDAO;

    @Mock
    private SupplierService supplierService;

    @Mock
    private ElasticModificationService elasticModificationService;

    @Mock
    private JBIAutoPayService jbiAutoPayService;
    @Mock
    private PaymentHelperService paymentHelperService;
    @Mock
    private AutopayValidationService autopayValidationService;
    @Mock
    private AutopayChargeStatusRules autopayChargeStatusRules;
    @Mock
    private PaymentShipmentRepository paymentShipmentRepository;
    private ProducerTemplate producerTemplate = mock(ProducerTemplate.class);

    private LockUserService lockUserService = mock(LockUserService.class);



    @Test
    public void failAutoPayWhenNoParamsPresentTest()  {
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPayment(), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), new ArrayList<>(  ) , LocalDateTime.now());
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_LDARCHIVED));

        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPayWhenLoadIsNullTest()  {
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPayment(), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), new ArrayList<>(  ), LocalDateTime.now() );
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_LDARCHIVED));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPayWhenLoadIsArchivedTest()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(0);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay( getConsolidatedPaymentLTLFuel("Reroute"), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), new ArrayList<>(  ), LocalDateTime.now() );
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_LDARCHIVED));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void passAutoPayWhenLoadIsNotArchivedTest()   {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentLTLWithChargeDecionNullMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay3(), ParameterMocks.getParameterListings1(), LocalDateTime.now() );
        assertTrue(autoPayStatus);
    }
    @Test
    public void passAutoPayWhenLoadIsNotArchivedTes()   {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentLTLWithChargeDecionNullMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay3(), ParameterMocks.getParameterListings1(), LocalDateTime.now() );
        assertTrue(autoPayStatus);
    }

    @Test
    public void failAutoPayWhenSupplierIDIsNull()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
       when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(new Supplier());      boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPayment(), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), new ArrayList<>(  ), LocalDateTime.now() );
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_SUPPNOTWD));

        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPayWhenSupplierIDIsNul()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
       when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(new Supplier());      boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPayment(), CarrierInvoiceDetailMock.getCarrierInvoiceDetail1(), new ArrayList<>(  ) , LocalDateTime.now());
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_SUPPNOTWD));

        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPayWhenSupplierIDIThrowsException()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
       when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(null);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPayment(), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), new ArrayList<>(  ) , LocalDateTime.now());
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_SUPPNOTWD));

        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPayWhenNoSupplierID() {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPayment(), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), new ArrayList<>(  ), LocalDateTime.now() );
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_SUPPNOTWD));

        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPayWhenChargeListHasARejectedChargeForNonLTL()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentWithFOrNonLTLRejectedChargeCode(), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), ParameterMocks.getParameterMocksForAutoApprove(), LocalDateTime.now() );
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_PREVDECSN));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPayWhenChargeListHasANotNullDecisionRejectedOrApprovedCodeLTL()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentLTLMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), ParameterMocks.getParameterMocksForAutoApprove(), LocalDateTime.now() );

        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_PREVDECSN));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPayWhenValidateDuplicateChargeList()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(autopayValidationService.validateCalculatedInvoiceDate(any(Payment.class), any())).thenReturn(true);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentICSDuplicateChargeMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), ParameterMocks.getParameterListings1() , LocalDateTime.now());
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_DUPECHGECD));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void validateCalculatedDueDateTest()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());     // when(autopayValidationService.validateCalculatedInvoiceDate(any(Payment.class),any())).thenReturn(false);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentICSDuplicateChargeMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), ParameterMocks.getParameterListings1(), LocalDateTime.now() );
        assertFalse(autoPayStatus);

    }

    @Test
    public void failAutoPayWhenValidateDuplicateTenderedChargeList()  {
        LocalDateTime current = LocalDateTime.now();
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(autopayValidationService.validateCalculatedInvoiceDate(any(Payment.class), any())).thenReturn(true);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentICSMock(), CarrierInvoiceDetailMock.getDuplicateCarrierInvoiceDetail(), ParameterMocks.getParameterListings1(), LocalDateTime.now() );

        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_DUPECHGECD));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }



    @Test
    public void failAutoPayValidateDuplicateTenderedChargeListForLTL()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentLTLWithDuplicateChargeMock(), CarrierInvoiceDetailMock.getDuplicateCarrierInvoiceDetail(), ParameterMocks.getParameterListings1() , LocalDateTime.now());

        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_DUPECHGECD));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPayValidateToleranceAmountWhenTotalApproveChargeAmountIsZero() {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentLTLWithZeroTotalCharge(), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), ParameterMocks.getParameterListings1() , LocalDateTime.now());

        assertFalse(autoPayStatus);
        verify(activityService,times(0)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPayValidateIfInvoiceAmountIsNullOrLessThenThreshold() {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentLTLWithChargeDecionNullMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), ParameterMocks.getParameterListings() , LocalDateTime.now());

        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_EXEDTHRHLD));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPayValidateChargeAmountsGreaterAndLesserWhenInvoiceAmountIsLessThanOrGreaterThanThreshold()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentLTLWithChargeDecionNullMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), ParameterMocks.getParameterListings1() , LocalDateTime.now());
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_EXEDTOLNCE));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPayCheckPercentageThresholdExceeds()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentLTLWithChargeDecionNullMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay(), ParameterMocks.getParameterListings1() , LocalDateTime.now());
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_EXEDPCNTT));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }
    @Test
    public void failAutoPayCheckPercentageJbi()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentJBIWithChargeDecionNullMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay(), ParameterMocks.getParameterListings1() , LocalDateTime.now());
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_EXEDPCNTT));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPaycheckExactListMatchForTenderedAndChargeList()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentLTLWithChargeDecionNullMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay1(), ParameterMocks.getParameterListings1(), LocalDateTime.now() );
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_CHGLSTEXCP));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPaycheckExactListMatchForTenderedAndInvoiceChargeList()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentLTLWithChargeDecionNullMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay4(), ParameterMocks.getParameterListings1() , LocalDateTime.now());
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_CHGLSTEXCP));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void failAutoPayCheckChargeThresholdWhenMoreThanOneParametersReturned() {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentLTLWithChargeDecionNullMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay2(), ParameterMocks.getParameterListings1() , LocalDateTime.now());
        assertFalse(autoPayStatus);
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(Payment.class),eq(CarrierPaymentConstants.ACT_AUTOPAY),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL),
                eq(CarrierPaymentConstants.ACT_APP),eq(CarrierPaymentConstants.SYSTEM),eq(CarrierPaymentConstants.ACT_AUTOPAY_FAIL_CHGEXEDTOL));
        verify(activityService,times(1)).saveActivityAutoPayFailure(any(),anyString(),anyString(),anyString(),anyString(),anyString());
    }
    @Test
    public void successAutoPayCheckChargeThresholdWhenMoreThanOneParametersReturned() {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentLTLWithChargeDecionNullMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay5(), ParameterMocks.getParameterListings5() , LocalDateTime.now());
        assertTrue(autoPayStatus);
    }
    @Test
    public void checkRulesForAutopay()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentLTLWithChargeDecionNullMock(), CarrierInvoiceDetailMock.getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay3(), ParameterMocks.getParameterListings1() , LocalDateTime.now());
        assertTrue(autoPayStatus);
    }
    @Test
    public void checkRulesForAutopayExactListMatch()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(jbiAutoPayService.jBIInvoiceAndTenderedChargeTransportationGroupAmountValidation(any(),any(),any(),any())).thenReturn(true);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentJBIWithDuplicatesMock25(), CarrierInvoiceDetailMock.getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPayListMatch(), ParameterMocks.getParameterMocksForAutoApproveListMatch(), LocalDateTime.now() );
        assertTrue(autoPayStatus);
    }
    @Test
    public void checkRulesForAutopayJbi()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(jbiAutoPayService.validateInvoiceAndTenderedList(any(), any(),anyBoolean())).thenReturn(true);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay(PaymentMocks.getPaymentJBIWithDuplicatesMock(), createjbiMock2().getCarrierInvoiceDetailList(), ParameterMocks.getParameterListings1(), LocalDateTime.now() );
        assertTrue(autoPayStatus);
    }
    @Test
    public void testCheckAndPerformAutoPayJbiFalse()  {
        Payment payment= PaymentMocks.getPaymentJBIWithJbisMock();
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setGroupFlowTypeCode("JBI");
        CarrierInvoiceHeader ch = getInvoiceHeader();
        TxnStatusDTO txnStatusDTO = new TxnStatusDTO();
        txnStatusDTO.setSuccess(true);
        ch.setCarrierInvoiceDetailList(createjbiMock1().getCarrierInvoiceDetailList());
        lenient().when(chargeDecisionService.chargeDecision( Mockito.any(),Mockito.any())).thenReturn(txnStatusDTO);
        autoPayService.checkAndPerformAutopay(payment, ParameterMocks.getParameterMocksForAutoApprove(),ch);

    }

    @Test
    public void checkPaperInvoicesAutoAcknowledgeAutoPay()  {
        Payment payment= CheckRulesForAutoPayChargeCode(new BigDecimal(50.00), new BigDecimal(100.00));
        payment.setStatusFlowTypeCode("LTLApprv");
        payment.setDispatchStatus("Active");
        CarrierInvoiceHeader ch = getInvoiceHeader();
        TxnStatusDTO txnStatusDTO = new TxnStatusDTO();
        txnStatusDTO.setSuccess(true);
        ch.setCarrierInvoiceDetailList(CarrierInvoiceHeaderMocks.getCarrierInvoiceDetail6());

        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(autoPayEditsService.validateChargeAmounstAgainsDb2(any(),any())).thenReturn(true);
        when(autoPayEditsService.checkInvoiceRulesForAutopay(any(),any(), any() )).thenReturn(true);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        lenient().when(chargeDecisionService.chargeDecision( Mockito.any(),Mockito.any())).thenReturn(txnStatusDTO);

        assertTrue(autoPayService.checkAndPerformAutopay(payment, ParameterMocks.getParameterListings1(),ch));

        verify(autoPayEditsService,times(1)).checkInvoiceRulesForAutopay(any(),any(), any() );
        verify(invoiceRepository,times(1)).saveAll(any());

    }
    @Test
    public void checkDispatchStatusCancelled()  {
        Payment payment= CheckRulesForAutoPayChargeCode(new BigDecimal(50.00), new BigDecimal(100.00));
        payment.setStatusFlowTypeCode("LTLApprv");
        payment.setDispatchStatus("Cancelled");
        CarrierInvoiceHeader ch = getInvoiceHeader();
        TxnStatusDTO txnStatusDTO = new TxnStatusDTO();
        txnStatusDTO.setSuccess(true);
        ch.setCarrierInvoiceDetailList(CarrierInvoiceHeaderMocks.getCarrierInvoiceDetail6());
        lenient().when(chargeDecisionService.chargeDecision( Mockito.any(),Mockito.any())).thenReturn(txnStatusDTO);
        assertFalse(autoPayService.checkAndPerformAutopay(payment, ParameterMocks.getParameterListings1(),ch));
    }

    @Test
    public void checkPaperInvoicesAutoAcknowledgeAutoPay1()  {
        Payment payment= CheckRulesForAutoPayChargeCode(new BigDecimal(50.00), new BigDecimal(100.00));
        payment.setStatusFlowTypeCode("LTLApprv");
        payment.setDispatchStatus("Active");
        CarrierInvoiceHeader ch = getInvoiceHeader();
        TxnStatusDTO txnStatusDTO = new TxnStatusDTO();
        txnStatusDTO.setSuccess(false);
        ch.setCarrierInvoiceDetailList(CarrierInvoiceHeaderMocks.getCarrierInvoiceDetail6());

        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(autoPayEditsService.validateChargeAmounstAgainsDb2(any(),any())).thenReturn(true);
        when(autoPayEditsService.checkInvoiceRulesForAutopay(any(),any(), any() )).thenReturn(true);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        lenient().when(chargeDecisionService.chargeDecision( Mockito.any(),Mockito.any())).thenReturn(txnStatusDTO);

        assertFalse(autoPayService.checkAndPerformAutopay(payment, ParameterMocks.getParameterListings1(),ch));

        verify(autoPayEditsService,times(1)).checkInvoiceRulesForAutopay(any(),any(), any() );

    }

    @Test
    public void testCheckAndPerformAutoPay()  {
        Payment payment= CheckRulesForAutoPayChargeCode(new BigDecimal(50.00), new BigDecimal(100.00));
        payment.setStatusFlowTypeCode("LTLApprv");
        payment.setDispatchStatus("Active");
        CarrierInvoiceHeader ch = getInvoiceHeader();
        TxnStatusDTO txnStatusDTO = new TxnStatusDTO();
        txnStatusDTO.setSuccess(true);
        ch.setCarrierInvoiceDetailList(CarrierInvoiceHeaderMocks.getCarrierInvoiceDetail6());

        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(autoPayEditsService.validateChargeAmounstAgainsDb2(any(),any())).thenReturn(true);
        when(autoPayEditsService.checkInvoiceRulesForAutopay(any(),any(), any() )).thenReturn(true);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        lenient().when(chargeDecisionService.chargeDecision( Mockito.any(),Mockito.any())).thenReturn(txnStatusDTO);
        assertTrue(autoPayService.checkAndPerformAutopay(payment, ParameterMocks.getParameterListings1(),ch));
        verify(autoPayEditsService,times(1)).checkInvoiceRulesForAutopay(any(),any(), any() );

    }
    @Test
    public void testCheckEachChargeCodeThresholdRangeForLTLTrue()  {
        List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList = getDuplicateCarrierInvoiceDetail1234();
        List<ParameterListingDTO> parameterAutoAprChargeThresholdRange = getParameterListingsMock();
        boolean flag = autoPayService.checkEachChargeCodeThresholdRangeForLTL(finalInvoiceDetailMinusParameterList,parameterAutoAprChargeThresholdRange );
        assertTrue(autoPayService.checkEachChargeCodeThresholdRangeForLTL(finalInvoiceDetailMinusParameterList,parameterAutoAprChargeThresholdRange));
        Assertions.assertThat(flag).isTrue();
    }
    @Test
    public void testCheckEachChargeCodeThresholdRangeForLTLFalse()  {
        List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList = getDuplicateCarrierInvoiceDetail1234();
        List<ParameterListingDTO> parameterAutoAprChargeThresholdRange = getParameterListings4();
        boolean flag = autoPayService.checkEachChargeCodeThresholdRangeForLTL(finalInvoiceDetailMinusParameterList,parameterAutoAprChargeThresholdRange );
        assertFalse(autoPayService.checkEachChargeCodeThresholdRangeForLTL(finalInvoiceDetailMinusParameterList,parameterAutoAprChargeThresholdRange));
        Assertions.assertThat(flag).isFalse();
    }
    @Test
    public void testCheckEachChargeCodeThresholdRangeForLTLFalseNoMatchingParams()  {
        List<CarrierInvoiceDetail> finalInvoiceDetailMinusParameterList = getDuplicateCarrierInvoiceDetail1234();
        List<ParameterListingDTO> parameterAutoAprChargeThresholdRange = getParameterListings3();
        boolean flag = autoPayService.checkEachChargeCodeThresholdRangeForLTL(finalInvoiceDetailMinusParameterList,parameterAutoAprChargeThresholdRange );
        assertFalse(autoPayService.checkEachChargeCodeThresholdRangeForLTL(finalInvoiceDetailMinusParameterList,parameterAutoAprChargeThresholdRange));
        Assertions.assertThat(flag).isFalse();

    }

    @Test
    public void testCheckAndPerformAutoPayReroute()  {
        Payment payment= CheckRulesForAutoPayChargeCode(new BigDecimal(50.00), new BigDecimal(100.00));
        payment.setStatusFlowTypeCode("Reroute");
        payment.setDispatchStatus("Active");
        CarrierInvoiceHeader ch = getInvoiceHeader();
        TxnStatusDTO txnStatusDTO = new TxnStatusDTO();
        txnStatusDTO.setSuccess(true);
        ch.setCarrierInvoiceDetailList(CarrierInvoiceHeaderMocks.getCarrierInvoiceDetail6());

        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(SupplierMock.getSupplier());      when(autoPayEditsService.validateChargeAmounstAgainsDb2(any(),any())).thenReturn(true);
        when(autoPayEditsService.checkInvoiceRulesForAutopay(any(),any(), any() )).thenReturn(true);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        lenient().when(chargeDecisionService.chargeDecision( Mockito.any(),Mockito.any())).thenReturn(txnStatusDTO);
        assertTrue(autoPayService.checkAndPerformAutopay(payment, ParameterMocks.getParameterListings1(),ch));
        verify(autoPayEditsService,times(1)).checkInvoiceRulesForAutopay(any(),any(), any() );

    }


    @Test
    public void testCheckRulesForAutoPay_ThresholdValue()  {
        var supplier = new Supplier();
        supplier.setSupplierID(1);
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(autopayValidationService.zeroMileDispatchValidation(any())).thenReturn(false);
        when(supplierService.retrieveSupplierByScacAndCurrencyOrJustScac(any(),any())).thenReturn(supplier);
        Payment payment = CheckRulesForAutoPayChargeCode(new BigDecimal(50.00), new BigDecimal(100.00));
        Boolean flag=autoPayService.checkRulesForAutopay(payment, CarrierInvoiceHeaderMocks.getCarrierInvoiceDetail6(), ParameterMocks.getParameterListings1() , LocalDateTime.now());
        assertTrue(flag);
    }

    @Test
    public void callAutoPayWSTest() {

        when(carrierPaymentProcessEventRepository.findPaymentByCarrierPaymentId(any())).thenReturn(null);
        when(paymentRepository.findPaymentByCarrierPaymentId(any())).thenReturn(PaymentEntityMocks.createPaymentEntityMocks());
        Boolean result  = autoPayService.callAutoPayWS(123443);
        Assert.assertNotNull(result);
        Assertions.assertThat(result).isTrue();
    }
    @Test
    public void callAutoPayWSTestRejected() {
        when(carrierPaymentProcessEventRepository.findPaymentByCarrierPaymentId(any())).thenReturn(null);
        when(paymentRepository.findPaymentByCarrierPaymentId(any())).thenReturn(PaymentEntityMocks.createPaymentEntityMocks());
        Boolean result  = autoPayService.callAutoPayWS(123443);
        Assert.assertNotNull(result);
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void callAutoPayWSCarrierEventPresent() {

        when(carrierPaymentProcessEventRepository.findPaymentByCarrierPaymentId(any())).thenReturn(CarrierPaymentEventProcessMocks.createCarrierPaymentrocessMocks());
        when(paymentRepository.findPaymentByCarrierPaymentId(any())).thenReturn(PaymentEntityMocks.createPaymentEntityMocks());
        Boolean result  = autoPayService.callAutoPayWS(123443);
        Assert.assertNotNull(result);
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void failAutoPayWhenBillToNotMatchTest()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(autopayValidationService.fetchBillToCodes(any())).thenReturn(false);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay( getConsolidatedPaymentLTLFuel("Reroute"), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), new ArrayList<>(  ), LocalDateTime.now() );
        assertFalse(autoPayStatus);
    }

    @Test
    public void failAutoPayWhenBillToMatchTest()  {
        when(chargeDecisionService.getCountOfRecords(any())).thenReturn(1);
        when(autopayValidationService.fetchBillToCodes(any())).thenReturn(true);
        boolean autoPayStatus = autoPayService.checkRulesForAutopay( getConsolidatedPaymentLTLFuel("Reroute"), CarrierInvoiceDetailMock.getCarrierInvoiceDetail(), new ArrayList<>(  ), LocalDateTime.now() );
        assertFalse(autoPayStatus);
    }

    @Test
    public void postChargesStatusToTopic() {
        Mockito.when(paymentRepository.findByCarrierPaymentId(Mockito.any())).thenReturn(PaymentEntityMocks.createPaymentEntityMocks());
        autoPayService.postChargesStatusToTopic(1235);
        Mockito.verify(producerTemplate).requestBodyAndHeaders(Mockito.anyString(), Mockito.any(),Mockito.any());
    }

    @Test
    public void postChargesStatusToTopicForException() {
        Mockito.when(paymentRepository.findByCarrierPaymentId(Mockito.any())).thenReturn(PaymentEntityMocks.createPaymentEntityMocks());
        Mockito.when(producerTemplate.requestBodyAndHeaders(Mockito.anyString(), Mockito.any(),Mockito.any())).thenThrow(NullPointerException.class);
        autoPayService.postChargesStatusToTopic(1234);
        Mockito.verify(producerTemplate).requestBodyAndHeaders(Mockito.anyString(), Mockito.any(),Mockito.any());
    }
}



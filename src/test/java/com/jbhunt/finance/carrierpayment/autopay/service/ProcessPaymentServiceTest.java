package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeEntityMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentEntityMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.TxnStatusDTOMocks;
import com.jbhunt.finance.carrierpayment.autopay.repository.ChargeRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ElasticModificationService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.PaymentStateService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ProcessPaymentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessPaymentServiceTest {

    @InjectMocks
    private ProcessPaymentService processPaymentService;


    @Mock
    private ChargeRepository chargeRepository;

    @Mock
    private AbstractAuthenticationToken abstractAuthenticationToken;

    @Mock
    private RateIntegrationService rateIntegrationService;

    @Mock
    private PaymentStateService paymentStateService;

    @Mock
    private ElasticModificationService elasticModificationService;

    @Before
    public void setup() {
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .subject("subject")
                .claim("preferred_username",  "jisaad0")
                .claim("azp", "clientId")
                .build());
        setup(jwtAuthenticationToken);
    }
    @Test
    public void testProcessPaymentFromCharge_EmptyChargeList() {

        TxnStatusDTO txnStatusDTO = Mockito.mock(TxnStatusDTO.class);
        txnStatusDTO.setSuccess(true);

        Boolean paymentFromCharge = processPaymentService.processPaymentFromCharge(getPayment());

        assertFalse(paymentFromCharge);
    }

    @Test
    public void testProcessPaymentFromCharge_ActionFalse() {


        TxnStatusDTO txnStatusDTO = TxnStatusDTOMocks.createTxnStatusDTOMocks();

        Mockito.when(
                chargeRepository.findByCarrierPaymentCarrierPaymentIdAndExpirationTimestampIsNull(Mockito.anyInt()))
                .thenReturn(getCharge());

        Mockito.when(paymentStateService.updatePaymentStatus(Mockito.any(), Mockito.any())).thenReturn(txnStatusDTO);

        Mockito.when(rateIntegrationService.callRTC056(Mockito.any(), Mockito.any())).thenReturn(txnStatusDTO);


        Boolean paymentFromCharge = processPaymentService.processPaymentFromCharge(getPayment());
        verify(elasticModificationService, times(1)).updateElasticSearchProcess(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
        assertTrue(paymentFromCharge);
    }

    @Test
    public void testProcessPaymentFromCharge_PendingList() {
        TxnStatusDTO txnStatusDTO = Mockito.mock(TxnStatusDTO.class);
        txnStatusDTO.setSuccess(true);
        Payment payment = getPayment();
        payment.setStatusFlowTypeCode("Pending");
        Mockito.when(
                chargeRepository.findByCarrierPaymentCarrierPaymentIdAndExpirationTimestampIsNull(Mockito.anyInt()))
                .thenReturn(getCharge_Paid());

        Boolean paymentFromCharge = processPaymentService.processPaymentFromCharge(payment);

        assertFalse(paymentFromCharge);
    }

    @Test
    public void testProcessPaymentFromCharge_allRejected() {

        TxnStatusDTO txnStatusDTO = Mockito.mock(TxnStatusDTO.class);
        txnStatusDTO.setSuccess(true);
        Payment existingPayment = getPayment();

        Mockito.when(
                chargeRepository.findByCarrierPaymentCarrierPaymentIdAndExpirationTimestampIsNull(Mockito.anyInt()))
                .thenReturn(getCharge_Reject());

        Mockito.when(paymentStateService.updatePaymentStatus(Mockito.any(), Mockito.any())).thenReturn(txnStatusDTO);


        Mockito.when(rateIntegrationService.callRTC056(Mockito.any(), Mockito.any())).thenReturn(txnStatusDTO);


        Boolean paymentFromCharge = processPaymentService.processPaymentFromCharge(existingPayment);

        assertFalse(paymentFromCharge);
    }

    @Test
    public void testProcessPaymentFromCharge_allPaid() {

        TxnStatusDTO txnStatusDTO = Mockito.mock(TxnStatusDTO.class);
        txnStatusDTO.setSuccess(true);
        Payment existingPayment = getPayment_Paid();

        Mockito.when(
                chargeRepository.findByCarrierPaymentCarrierPaymentIdAndExpirationTimestampIsNull(Mockito.anyInt()))
                .thenReturn(getCharge_Paid());

        Mockito.when(paymentStateService.updatePaymentStatus(Mockito.any(), Mockito.any())).thenReturn(txnStatusDTO);

        Mockito.when(rateIntegrationService.callRTC056(Mockito.any(), Mockito.any())).thenReturn(txnStatusDTO);


        Boolean paymentFromCharge = processPaymentService.processPaymentFromCharge(existingPayment);

        assertFalse(paymentFromCharge);
    }

    @Test
    public void testProcessPaymentFromCharge_else() {

        TxnStatusDTO txnStatusDTO = Mockito.mock(TxnStatusDTO.class);
        txnStatusDTO.setSuccess(true);
        Payment existingPayment = getPayment_AutoApprove();
        Mockito.when(
                chargeRepository.findByCarrierPaymentCarrierPaymentIdAndExpirationTimestampIsNull(Mockito.anyInt()))
                .thenReturn(getCharge_AutoApprove());


        Mockito.when(rateIntegrationService.callRTC056(Mockito.any(), Mockito.any())).thenReturn(txnStatusDTO);

        Boolean paymentFromCharge = processPaymentService.processPaymentFromCharge(existingPayment);

        assertFalse(paymentFromCharge);
    }

    @Test
    public void testProcessPaymentFromCharge_allApproved() {

        TxnStatusDTO txnStatusDTO = Mockito.mock(TxnStatusDTO.class);
        txnStatusDTO.setSuccess(true);
        Payment existingPayment = getPayment_Paid();
        existingPayment.setCarrierPaymentChargeList(getCharge_Approved());
        Mockito.when(
                chargeRepository.findByCarrierPaymentCarrierPaymentIdAndExpirationTimestampIsNull(Mockito.anyInt()))
                .thenReturn(getCharge_Approved());
        Mockito.when(paymentStateService.updatePaymentStatus(Mockito.any(), Mockito.any())).thenReturn(txnStatusDTO);

        Mockito.when(rateIntegrationService.callRTC056(Mockito.any(), Mockito.any())).thenReturn(txnStatusDTO);

        Boolean paymentFromCharge = processPaymentService.processPaymentFromCharge(existingPayment);

        assertFalse(paymentFromCharge);
    }


    private List<Charge> getCharge() {
        List<Charge> chargeList = new ArrayList<>();
        Charge charge =  ChargeEntityMocks.createChargeEntityMocks();
        charge.setExpirationTimestamp(null);
        chargeList.add(charge);
        return chargeList;
    }

    private List<Charge> getCharge_Reject() {
        List<Charge> chargeList = new ArrayList<>();
        Charge charge =  ChargeEntityMocks.createChargeEntityMocks();
        charge.setChargeDecisionCode("Reject");
        charge.setExpirationTimestamp(null);
        chargeList.add(charge);
        return chargeList;
    }

    private List<Charge> getCharge_Paid() {
        List<Charge> chargeList = new ArrayList<>();
        Charge charge =  ChargeEntityMocks.createChargeEntityMocks();
        charge.setChargeDecisionCode("PAID");
        charge.setExpirationTimestamp(null);
        chargeList.add(charge);
        return chargeList;
    }

    private List<Charge> getCharge_AutoApprove() {
        List<Charge> chargeList = new ArrayList<>();
        Charge charge = new Charge();
        charge.setChargeDecisionCode("AutoApprove");
        charge.setExpirationTimestamp(null);
        chargeList.add(charge);
        return chargeList;
    }

    private List<Charge> getCharge_TimeStampNotNull() {
        List<Charge> chargeList = new ArrayList<>();
        Charge charge = ChargeEntityMocks.createChargeEntityMocks();
        charge.setChargeDecisionCode("Approve");
        charge.setExpirationTimestamp(LocalDateTime.now());
        chargeList.add(charge);
        return chargeList;
    }

    private Payment getPayment() {
        Payment payment =  PaymentEntityMocks.createPaymentEntityMocks();
        payment.setCarrierPaymentId(1);
        payment.setScacCode("T-E0");
        payment.setStatusFlowTypeCode("Reroute");
        payment.setTotalChargeAmount(new BigDecimal("100"));
        payment.setDispatchNumber("1");
        payment.setGroupFlowTypeCode("LTL");
        payment.setLoadNumber("L01");
        payment.setCarrierPaymentChargeList(getCharge_Reject());
        return payment;
    }

    private List<Charge> getCharge_Approved() {
        List<Charge> chargeList = new ArrayList<>();
        Charge charge =  ChargeEntityMocks.createChargeEntityMocks();
        charge.setChargeDecisionCode("Approve");
        charge.setExpirationTimestamp(null);
        chargeList.add(charge);
        return chargeList;
    }

    private Payment getPayment_Paid() {
        Payment payment =  PaymentEntityMocks.createPaymentEntityMocks();
        payment.setCarrierPaymentId(1);
        payment.setScacCode("R.02");
        payment.setStatusFlowTypeCode("Reroute");
        payment.setTotalChargeAmount(new BigDecimal("100"));
        payment.setDispatchNumber("1");
        payment.setGroupFlowTypeCode("LTL");
        payment.setLoadNumber("L01");
        payment.setCarrierPaymentChargeList(getCharge_Paid());
        return payment;
    }

    private Payment getPayment_AutoApprove() {
        Payment payment =  PaymentEntityMocks.createPaymentEntityMocks();
        payment.setCarrierPaymentId(1);
        payment.setScacCode("J&L6");
        payment.setStatusFlowTypeCode("Reroute");
        payment.setTotalChargeAmount(new BigDecimal("100"));
        payment.setDispatchNumber("1");
        payment.setGroupFlowTypeCode("LTL");
        payment.setLoadNumber("L01");
        payment.setCarrierPaymentChargeList(getCharge_AutoApprove());
        return payment;
    }

    private Payment getPayment_TimeStampNotNull() {
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        payment.setCarrierPaymentId(1);
        payment.setScacCode("R/00");
        payment.setStatusFlowTypeCode("Reroute");
        payment.setTotalChargeAmount(new BigDecimal("100"));
        payment.setDispatchNumber("1");
        payment.setGroupFlowTypeCode("LTL");
        payment.setLoadNumber("L01");
        payment.setCarrierPaymentChargeList(getCharge_TimeStampNotNull());
        return payment;
    }

    private void setup(JwtAuthenticationToken jwtAuthenticationToken) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
    }


}

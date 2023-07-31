package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.biz.securepid.PIDCredentials;
import com.jbhunt.finance.carrierpayment.autopay.TestSuiteConfig;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.ChargeOverride;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.TxnStatusDTOMocks;
import com.jbhunt.finance.carrierpayment.autopay.properties.PaymentApplicationProperties;
import com.jbhunt.finance.carrierpayment.autopay.repository.ApprovalTransactionRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.ChargeRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.OptimisticLockRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeDTOMocks.*;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeMocks.getCharge;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeMocks.getChargeList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = { TestSuiteConfig.class })
@SpringBootTest
public class ChargeDecisionServiceTest {



    @InjectMocks
    private ChargeDecisionService chargeDecisionService;

    @Mock
    private PaymentApplicationProperties paymentApplicationProperties;
    private ChargeRepository chargeRepository = Mockito.mock(ChargeRepository.class);
    private RestTemplate chargesRestTemplate = Mockito.mock(RestTemplate.class);
    private PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
    private OptimisticLockRepository optimisticLockRepository = Mockito.mock(OptimisticLockRepository.class);
    private ChargeValidationService chargeValidationService = Mockito.mock(ChargeValidationService.class);
    private ChargeIntegrationService  chargeIntegrationService=Mockito.mock(ChargeIntegrationService.class);
    private  PaymentHelperService paymentHelperService=Mockito.mock(PaymentHelperService.class);
    private ChargeAuditDetailService chargeAuditDetailService =  Mockito.mock(ChargeAuditDetailService.class);
    @Mock
    private AbstractAuthenticationToken abstractAuthenticationToken;
    private PostChargeDecisionService postChargeDecisionService = Mockito.mock(PostChargeDecisionService.class);
    private PIDCredentials pidCredentials;
    @Mock
    private LockUserService lockUserService;
    @Mock
    private SupplierService supplierService;
    @Mock
    private ApprovalTransactionRepository approvalTransactionRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        pidCredentials = new PIDCredentials("rcon849", "jb4143");
        paymentApplicationProperties = new PaymentApplicationProperties();
        paymentApplicationProperties.setElasticSearchBaseUrl("http://finance-elastic-dev.jbhunt.com:9200/");
        paymentApplicationProperties.setElasticSearchWorkFlowIndex("finance-payment-carrierpayment-workflow");
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .subject("subject")
                .claim("preferred_username", "service-account" + "jisaad0")
                .claim("azp", "clientId")
                .build());
        setup(jwtAuthenticationToken);
    }


    @Test
    public void testchargeDecisionTrue_Approve() throws IOException {
        // --- ARRANGE ---
        ChargeDTO chargeDTO = getChargeDTOApp("Approve");
        chargeDTO.setAutoPay(true);
        Mockito.when(chargeRepository.findAllById(Mockito.anyList()))
                .thenReturn(Arrays.asList(getCharge(1)));
        Mockito.when(paymentRepository.findByCarrierPaymentId(Mockito.any())).thenReturn(PaymentMocks.chargeDecision().get());
        Mockito.when(chargeRepository.saveAll(Mockito.anyList())).thenReturn(getChargeList());
        Mockito.when(chargeValidationService.validateAmountforEachCharge(chargeDTO,Map.of())).thenReturn(chargeDTO);
        // --- ACT ---
        Mockito.when(lockUserService.checkForNotStale(PaymentMocks.chargeDecision().get())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        TxnStatusDTO response = chargeDecisionService.chargeDecision(chargeDTO,any());
        verify(paymentRepository,times(1)).findByCarrierPaymentId(Mockito.any());
        verify(chargeRepository,times(1)).saveAll(Mockito.anyList());
        verify(paymentRepository,times(1)).findByCarrierPaymentId(Mockito.any());
        verify(chargeValidationService,times(1)).validateAmountforEachCharge(chargeDTO,Map.of());
        verify(chargeAuditDetailService,times(1)).saveCarrierPaymentChargeAuditDetail(any(),any(),any());
        // --- ASSERT ---
        assertTrue(response.isSuccess());
    }

    @Test
    public void testchargeDecisionTrue_Reject() throws IOException {
        // --- ARRANGE ---
        ChargeDTO chargeDTO = getChargeDTO("Reject");
        chargeDTO.setAutoPay(true);
        Charge charge=getCharge(1);
        charge.setChargeDecisionCode("Reject");
        Mockito.when(chargeRepository.findAllById(Mockito.anyList()))
                .thenReturn(Arrays.asList(charge));
        Mockito.when(paymentRepository.findByCarrierPaymentId(Mockito.any())).thenReturn(PaymentMocks.chargeDecision().get());
        Mockito.when(chargeRepository.saveAll(Mockito.anyList())).thenReturn(getChargeList());
        Mockito.when(chargeValidationService.validateAmountforEachCharge(chargeDTO,Map.of())).thenReturn(chargeDTO);
        Mockito.when(lockUserService.checkForNotStale(PaymentMocks.chargeDecision().get())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());

        // --- ACT ---
        TxnStatusDTO response = chargeDecisionService.chargeDecision(chargeDTO,any());
        // --- ASSERT ---
        assertTrue(response.isSuccess());
        verify(chargeAuditDetailService,times(1)).saveCarrierPaymentChargeAuditDetail(any(),any(),any());
        verify(paymentRepository,times(1)).findByCarrierPaymentId(Mockito.any());
        verify(chargeRepository,times(1)).saveAll(Mockito.anyList());
        verify(paymentRepository,times(1)).findByCarrierPaymentId(Mockito.any());
        verify(chargeValidationService,times(1)).validateAmountforEachCharge(chargeDTO,Map.of());

    }

    @Test
    public void testchargeDecision_EmptyDecisionCode() throws IOException {
        ChargeDTO chargeDTO = getChargeDTO("");
        chargeDTO.setJobId(1);
        chargeDTO.setProjectCode("ABCD");
        Mockito.when(chargeRepository.findAllById(Mockito.anyList()))
                .thenReturn(Arrays.asList(getCharge(1)));
        Mockito.when(paymentRepository.findByCarrierPaymentId(Mockito.any())).thenReturn(PaymentMocks.chargeDecision().get());
        Mockito.when(chargeRepository.saveAll(Mockito.anyList())).thenReturn(getChargeList());
        Mockito.when(chargeValidationService.validateAmountforEachCharge(chargeDTO,Map.of())).thenReturn(chargeDTO);
        Mockito.when(lockUserService.checkForNotStale(PaymentMocks.chargeDecision().get())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());

        // --- ACT ---
        TxnStatusDTO response = chargeDecisionService.chargeDecision(chargeDTO,any());
        verify(chargeAuditDetailService,times(1)).saveCarrierPaymentChargeAuditDetail(any(),any(),any());
        verify(paymentRepository,times(1)).findByCarrierPaymentId(Mockito.any());
        verify(chargeRepository,times(1)).saveAll(Mockito.anyList());
        verify(paymentRepository,times(1)).findByCarrierPaymentId(Mockito.any());
        verify(chargeValidationService,times(1)).validateAmountforEachCharge(chargeDTO,Map.of());

        // --- ASSERT ---
        assertTrue(response.isSuccess());
    }

    @Test
    public void testchargeDecision_validateChargeAmount_sucess() throws IOException {
        ChargeDTO chargeDTO = getChargeDTO("");
        chargeDTO.setJobId(1);
        chargeDTO.setProjectCode("ABCD");
        Mockito.when(chargeValidationService.validateAmountforEachCharge(chargeDTO,Map.of())).thenReturn(chargeDTO);
        Mockito.when(chargeValidationService.validateTotalAmount(chargeDTO)).thenReturn(BigDecimal.ZERO);
        // --- ACT ---ChargeDTO chargeDTO,Payment payment
        Boolean response = chargeDecisionService.validateChargeAmount(chargeDTO,PaymentMocks.chargeDecision().get());
        // --- ASSERT ---
        verify(chargeValidationService,times(1)).validateTotalAmount(Mockito.any());
        verify(chargeValidationService,times(1)).validateAmountforEachCharge(chargeDTO,Map.of());

        assertTrue(response);
    }

    @Test
    public void testchargeDecision_validateChargeAmount_false() throws IOException {
        ChargeDTO chargeDTO = getChargeDTO("");
        chargeDTO.setJobId(1);
        chargeDTO.setProjectCode("ABCD");

        Mockito.when(chargeValidationService.validateAmountforEachCharge(chargeDTO,Map.of())).thenReturn(chargeDTO);
        // --- ACT ---ChargeDTO chargeDTO,Payment payment
        Boolean response = chargeDecisionService.validateChargeAmount(chargeDTO,PaymentMocks.chargeDecision().get());
        // --- ASSERT ---
        assertFalse(response);
        verify(chargeValidationService,times(1)).validateAmountforEachCharge(chargeDTO,Map.of());
        verify(chargeValidationService,times(1)).validateTotalAmount(Mockito.any());

    }
    @Test
    public void testchargeDecision_validateChargeCode_false() throws IOException {
        ChargeDTO chargeDTO = getChargeDTO("");
        chargeDTO.setJobId(1);
        chargeDTO.setProjectCode("ABCD");
        chargeDTO.setChargeCode("AMS");
        Mockito.when(chargeValidationService.validateAmountforEachCharge(chargeDTO,Map.of())).thenReturn(chargeDTO);

        // --- ACT ---ChargeDTO chargeDTO,Payment payment
        Boolean response = chargeDecisionService.validateChargeAmount(chargeDTO,PaymentMocks.chargeDecision().get());
        // --- ASSERT ---
        assertFalse(response);
        verify(chargeValidationService,times(1)).validateAmountforEachCharge(chargeDTO,Map.of());
        verify(chargeValidationService,times(1)).validateTotalAmount(Mockito.any());

    }


    @Test
    public void testchargeDecision_autopaychargeDecision_true() throws IOException {
        Mockito.when(chargeRepository.saveAll(PaymentMocks.chargeDecision().get().getCarrierPaymentChargeList()))
                .thenReturn(Arrays.asList(getCharge(1)));
        Mockito.when(chargeIntegrationService.addAndeditcharge(getChargeDTOList("Approve"))).thenReturn(Boolean.TRUE);
        // --- ACT ---ChargeDTO chargeDTO,Payment payment
        Boolean response = chargeDecisionService.autopaychargeDecision(PaymentMocks.chargeDecision().get(),getChargeDTOList("Approve"));
        // --- ASSERT ---
        assertTrue(response);
        verify(chargeAuditDetailService,times(1)).saveCarrierPaymentChargeAuditDetail(any(),any(),any());
        verify(chargeRepository,times(1)).saveAll(Mockito.anyList());
        verify(chargeIntegrationService,times(1)).addAndeditcharge(getChargeDTOList("Approve"));
        verify(postChargeDecisionService,times(0)).processPayment(Mockito.any(),Mockito.any());
    }

    @Test(expected = Exception.class)
    public void testchargeDecision_autopaychargeDecision_Exception() throws IOException {
        Mockito.when(chargeIntegrationService.addAndeditcharge(getChargeDTOList("Approve"))).thenThrow(Exception.class);
        // --- ACT ---ChargeDTO chargeDTO,Payment payment
        Boolean response = chargeDecisionService.autopaychargeDecision(PaymentMocks.chargeDecision().get(),getChargeDTOList("Approve"));
        // --- ASSERT ---
        assertFalse(response);
        verify(chargeIntegrationService,times(1)).addAndeditcharge(getChargeDTOList("Approve"));
    }


    @Test
    public void testchargeDecisionFalse() throws IOException {
        // --- ARRANGE ---
        ChargeDTO chargeDTO = getChargeDTO("Reject");
        chargeDTO.setAutoPay(false);
        Mockito.when(paymentRepository.findByCarrierPaymentId(Mockito.any())).thenReturn(PaymentMocks.chargeDecision().get());

        Mockito.when(lockUserService.checkForNotStale(PaymentMocks.chargeDecision().get())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks(false));

        // --- ACT ---
        TxnStatusDTO response = chargeDecisionService.chargeDecision(chargeDTO,any());
        // --- ASSERT ---
        assertFalse(response.isSuccess());
        verify(postChargeDecisionService,times(0)).processPayment(Mockito.any(),Mockito.any());
    }

    @Test
    public void testchargeDecision_updateOvverideCharge() throws IOException {

        // --- ACT ---
        ChargeOverride chargeOverride=chargeDecisionService.updateOvverideCharge(getCharge(Integer.valueOf(1)));
        // --- ASSERT ---
        assertNull(chargeOverride);
    }

    @Test
    public void testchargeDecision_StatusFalse() throws IOException {
        // --- ARRANGE ---
        ChargeDTO chargeDTO = getChargeDTO("Approve");
        chargeDTO.setAutoPay(false);
        Mockito.when(paymentRepository.findByCarrierPaymentId(Mockito.any())).thenReturn(PaymentMocks.chargeDecision().get());

        Mockito.when(lockUserService.checkForNotStale(PaymentMocks.chargeDecision().get())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        Mockito.when(chargeValidationService.validateAmountforEachCharge(chargeDTO,Map.of())).thenReturn(chargeDTO);
        // --- ACT ---
        TxnStatusDTO response = chargeDecisionService.chargeDecision(chargeDTO,any());
        // --- ASSERT ---
        assertFalse(response.isSuccess());
        verify(postChargeDecisionService,times(1)).processPayment(Mockito.any(),Mockito.any());
    }

    @Test
    public void chargeDecisionGetCountOfRecordsZeroTest()  {
        when(chargeIntegrationService.getCountOfRecords(any())).thenReturn(0);
        int count= chargeDecisionService.getCountOfRecords("JW23456");
        assertEquals(0,count);
    }

    @Test
    public void chargeDecisionGetCountOfRecordsOneTest()  {
        when(chargeIntegrationService.getCountOfRecords(any())).thenReturn(1);
        int count= chargeDecisionService.getCountOfRecords("V686424");
        assertEquals(1,count);
    }

    @Test
    public void testchargeDecisionverify_StatusFalse() throws IOException {
        List<Charge> response = chargeDecisionService.verifyChargeApprove(PaymentMocks.chargeDecision().get());
        // --- ASSERT ---
        assertNotNull(response);
    }


    @Test
    public void testchargeDecision_validateChargeAmount_false1() throws IOException {
        ChargeDTO chargeDTO = getChargeDTO("Approve");
        chargeDTO.setAutoPay(false);
        Mockito.when(paymentRepository.findByCarrierPaymentId(Mockito.any())).thenReturn(PaymentMocks.chargeDecision().get());

        Mockito.when(lockUserService.checkForNotStale(PaymentMocks.chargeDecision().get())).thenReturn(TxnStatusDTOMocks.createTxnStatusDTOMocks());
        // VALIDATIONS
        Map<String, Boolean> errorMap = new HashMap<>();
        errorMap.put("validation",true);
        when(chargeValidationService.validateChargeCodes(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(errorMap);
        // --- ACT ---
        TxnStatusDTO response = chargeDecisionService.chargeDecision(chargeDTO,any());
        // --- ASSERT ---
        assertFalse(response.isSuccess());
    }

    private void setup(JwtAuthenticationToken jwtAuthenticationToken) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
    }
}

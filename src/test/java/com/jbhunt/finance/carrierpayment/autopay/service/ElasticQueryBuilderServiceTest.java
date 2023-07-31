package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.PaymentSearchDTO;
import com.jbhunt.finance.carrierpayment.autopay.mapper.ChargeMapper;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentMocks;
import com.jbhunt.finance.carrierpayment.autopay.properties.PaymentApplicationProperties;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ElasticQueryBuilderService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ElasticQueryBuilderServiceTest {

    @InjectMocks
    ElasticQueryBuilderService elasticQueryBuilderService;

    @Mock
    PaymentApplicationProperties paymentApplicationProperties;

    @Mock
    ChargeMapper chargeMapper;

    @Mock
    private RestHighLevelClient restClient;


    @Before
    public void init() {

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
    public void updateElasticSearchForAutoPayTest() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        UpdateRequest result = this.elasticQueryBuilderService.updateElasticSearchForAutoPay( PaymentMocks.getPaymentMock());
        assertEquals("finance-payment-carrierpayment-workflow", result.index());
        assertNotNull(result.doc());
    }

    @Test
    public void updateElasticSearchForAutoPayNotNullTest() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        UpdateRequest result = this.elasticQueryBuilderService.updateElasticSearchForAutoPay( PaymentMocks.getPaymentMockTotalApprovedInvAmt());
        assertEquals("finance-payment-carrierpayment-workflow", result.index());
        assertNotNull(result.doc());
    }

    @Test
    public void updatePaidStatusToElasticDocumentIsPaidTrueTest() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        UpdateRequest result = this.elasticQueryBuilderService.updatePaidStatusToElasticDocument( PaymentMocks.getPaymentMock(), true, "12345", null );
        assertEquals("finance-payment-carrierpayment-workflow", result.index());
        assertNotNull(result.doc());
    }

    @Test
    public void updatePaidStatusToElasticDocumentIsPaidFalseTest() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        UpdateRequest result = this.elasticQueryBuilderService.updatePaidStatusToElasticDocument( PaymentMocks.getPaymentMock(), false, "12345", null );
        assertEquals("finance-payment-carrierpayment-workflow", result.index());
        assertNotNull(result.doc());
    }

    @Test
    public void updatePaidStatusToElasticDocumentIsPaidTrueNullTest() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        UpdateRequest result = this.elasticQueryBuilderService.updatePaidStatusToElasticDocument( PaymentMocks.getPaymentMock(), true, "12345", new PaymentSearchDTO() );
        assertEquals("finance-payment-carrierpayment-workflow", result.index());
        assertNotNull(result.doc());
    }

    @Test
    public void updatePaidStatusToElasticDocumentIsPaidFalseNullTest() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        UpdateRequest result = this.elasticQueryBuilderService.updatePaidStatusToElasticDocument( PaymentMocks.getPaymentMock(), false, "12345", new PaymentSearchDTO() );
        assertEquals("finance-payment-carrierpayment-workflow", result.index());
        assertNotNull(result.doc());
    }

    @Test
    public void updatePaidStatusToElasticDocumentEmptyChargeListTest() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        UpdateRequest result = this.elasticQueryBuilderService.updatePaidStatusToElasticDocument( PaymentMocks.getPaymentMockEmptyChargeList(), false, "12345", new PaymentSearchDTO() );
        assertEquals("finance-payment-carrierpayment-workflow", result.index());
        assertNotNull(result.doc());
    }

    @Test
    public void updateElasticSearchProcessIgnoreTest() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        UpdateRequest result = this.elasticQueryBuilderService.updateElasticSearchProcess( PaymentMocks.getPaymentMock(), CarrierPaymentConstants.IGNORE, "" );
        assertEquals("finance-payment-carrierpayment-workflow", result.index());
        assertNotNull(result.doc());
    }

    @Test
    public void updateElasticSearchProcessSnoozeTest() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        UpdateRequest result = this.elasticQueryBuilderService.updateElasticSearchProcess( PaymentMocks.getPaymentMock(), CarrierPaymentConstants.SNOOZE, "" );
        assertEquals("finance-payment-carrierpayment-workflow", result.index());
        assertNotNull(result.doc());
    }
    @Test
    public void updateElasticSearchProcessQuickPayTest() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        UpdateRequest result = this.elasticQueryBuilderService.updateElasticSearchProcess( PaymentMocks.getPaymentMock(), CarrierPaymentConstants.SNOOZE, "" );
        assertEquals("finance-payment-carrierpayment-workflow", result.index());
        assertNotNull(result.doc());
    }

    @Test
    public void updateElasticSearchProcessFreePayTest() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        UpdateRequest result = this.elasticQueryBuilderService.updateElasticSearchProcess( PaymentMocks.getPaymentMockFreePay(), CarrierPaymentConstants.SNOOZE, "" );
        assertEquals("finance-payment-carrierpayment-workflow", result.index());
        assertNotNull(result.doc());
    }

    @Test
    public void updateElasticSearchProcessApproveStatusTest() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        UpdateRequest result = this.elasticQueryBuilderService.updateElasticSearchProcess( PaymentMocks.getPaymentMockApproveStatus(), CarrierPaymentConstants.SNOOZE, "" );
        assertEquals("finance-payment-carrierpayment-workflow", result.index());
        assertNotNull(result.doc());
    }

    @Test
    public void getPaymentSearchObjTest() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        SearchRequest searchRequest = elasticQueryBuilderService.getPaymentSearchObj(12345);
        assertNotNull(searchRequest);
    }

    @Test
    public void updateElasticSearchProcessApproveStatusTest1() {
        when(paymentApplicationProperties.getElasticSearchWorkFlowIndex()).thenReturn("finance-payment-carrierpayment-workflow");
        UpdateRequest result = this.elasticQueryBuilderService.updateElasticSearchForAutoPayFailure( PaymentMocks.getPaymentMockApproveStatus());
        assertEquals("finance-payment-carrierpayment-workflow", result.index());
        assertNotNull(result.doc());
    }

    private void setup(JwtAuthenticationToken jwtAuthenticationToken) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
    }

}

package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.mapper.ChargeMapper;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentMocks;
import com.jbhunt.finance.carrierpayment.autopay.properties.PaymentApplicationProperties;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ElasticClientService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ElasticModificationService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ElasticQueryBuilderService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ElasticUpdateService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchResponseSections;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ElasticModificationServiceTest {
    @InjectMocks
    ElasticModificationService elasticModificationService;
    @Mock
    ElasticQueryBuilderService elasticQueryBuilderService;
    @Mock
    RestHighLevelClient restHighLevelClient;
    @Mock
    ChargeMapper chargeMapper;
    @Mock
    ElasticUpdateService elasticUpdateService;
    @Mock
    ElasticClientService elasticClientService;

    @Mock
    PaymentApplicationProperties paymentApplicationProperties;

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
    public void updateElasticSearchForAutopayTest(){
        elasticModificationService.updateElasticSearchForAutopay(PaymentMocks.getPaymentMock());
        verify(elasticQueryBuilderService,times(1)).updateElasticSearchForAutoPay(any());
        verify(elasticUpdateService,times(1)).updateElasticSearch(any());
    }
    @Test
    public void updateElasticSearchForAutoPayFailureTest(){
        elasticModificationService.updateElasticSearchForAutoPayFailure(PaymentMocks.getPaymentAPFail());
        verify(elasticQueryBuilderService,times(1)).updateElasticSearchForAutoPayFailure(any());
        verify(elasticUpdateService,times(1)).updateElasticSearch(any());
    }
    @Test
    public void updateElasticSearchForAutoPayFailureTest1(){
        elasticModificationService.updateElasticSearchForAutoPayFailure(PaymentMocks.getPaymentMock());
        verify(elasticQueryBuilderService,times(1)).updateElasticSearchForAutoPayFailure(any());
        verify(elasticUpdateService,times(1)).updateElasticSearch(any());
    }
    @Test
    public void updateElasticSearchProcessTest(){
        elasticModificationService.updateElasticSearchProcess(PaymentMocks.getPaymentMock(), CarrierPaymentConstants.IGNORE, "", false );
        verify(elasticQueryBuilderService,times(1)).updateElasticSearchProcess(any(), anyString(), anyString());
        verify(elasticUpdateService,times(1)).updateElasticSearch(any());
    }

    @Test
    public void updatePaidStatusToElasticDocumentTest(){
        String jsonString ="{\n" +
                "          \"lastReasonCode\" : null,\n" +
                "          \"nmfcScacCode\" : \"FXFE\",\n" +
                "          \"shipperCity\" : \"BRAZIL\",\n" +
                "          \"receiverCity\" : \"PAWTUCKET\",\n" +
                "          \"dispatchNumber\" : null,\n" +
                "          \"workFlowGroup\" : \"LTL\",\n" +
                "          \"shipperPostalCode\" : \"47834\",\n" +
                "          \"shipperCountryCode\" : \"USA\",\n" +
                "          \"unitOfWeightMeasurementCode\" : null,\n" +
                "          \"carrierName\" : \"FEDEX FREIGHT PRIORITY\",\n" +
                "          \"freightClass\" : [\n" +
                "            \"50   \"\n" +
                "          ],\n" +
                "          \"paymentId\" : 1234,\n" +
                "          \"receiverPostalCode\" : \"02861\",\n" +
                "          \"totalWeightQuantity\" : 12432,\n" +
                "          \"workInProgressInd\" : false,\n" +
                "          \"scanTimestamp\" : \"2020-04-14\",\n" +
                "          \"carrierDocumentNumber\" : \"096871224\",\n" +
                "          \"lastComment\" : null,\n" +
                "          \"totalInvoiceAmount\" : 1914.01,\n" +
                "          \"deliveryDate\" : \"2020-01-16\",\n" +
                "          \"proNumber\" : \"4310997725\",\n" +
                "          \"shipperState\" : \"IN\",\n" +
                "          \"snoozeReasonCode\" : null,\n" +
                "          \"receiverAddress1\" : \"505 CENTRAL AVE\",\n" +
                "          \"receiverAddress2\" : \"PO BOX 682\",\n" +
                "          \"receivedTimestamp\" : \"2020-04-14\",\n" +
                "          \"receiverCountryCode\" : \"USA\",\n" +
                "          \"shipperAddress1\" : \"10665 N  STATE ROAD 59\",\n" +
                "          \"receiverName\" : \"TEKNOR APEX GROUP INC\",\n" +
                "          \"shipperAddress2\" : \"PO BOX 682\",\n" +
                "          \"carrierInvoiceNumber\" : \"4310997725\",\n" +
                "          \"loadNumber\" : \"TD72185\",\n" +
                "          \"shipperName\" : \"METALS N ADDITIVES LLC\",\n" +
                "          \"rejectReasonCode\" : null,\n" +
                "          \"invoiceSourceTypeCode\" : \"EDI\",\n" +
                "          \"scacCode\" : null,\n" +
                "          \"invoiceTypeCode\" : \"ORPHAN\",\n" +
                "          \"pickupDate\" : null,\n" +
                "          \"receiverState\" : null,\n" +
                "          \"orphanInvoiceStatusCode\" : \"ORPHAN\",\n" +
                "          \"invoiceHeaderID\" : 4946215,\n" +
                "          \"invoiceDate\" : [\n" +
                "            \"2020-01-14   \"\n" +
                "          ],\n" +
                "          \"invoiceNumber\" : [\n" +
                "            \"12345  \"\n" +
                "          ]\n" +
                "}";
        ShardSearchFailure[] shardSearchFailures = null;
        SearchResponse.Clusters clusters = null;
        BytesReference source = new BytesArray(jsonString );
        SearchHit hit = new SearchHit( 1 );
        hit.sourceRef( source );SearchHits hits = new SearchHits( new SearchHit[] { hit }, null, 10 );
        SearchResponseSections searchResponseSections = new SearchResponseSections( hits, null, null, false, null, null, 5 );
        SearchResponse searchResponse = new SearchResponse( searchResponseSections,"1",1,1,1,1,shardSearchFailures,clusters);
        SearchRequest searchRequest = new SearchRequest(  );
        when(elasticQueryBuilderService.getPaymentSearchObj(any())).thenReturn( searchRequest );
        when(elasticClientService.executeSearch( any() )).thenReturn( searchResponse );
        assertNotNull( searchResponse );
        elasticModificationService.updatePaidStatusToElasticDocument(PaymentMocks.getPaymentMock(), true, "12345");
        verify(elasticQueryBuilderService,times(1)).updatePaidStatusToElasticDocument(any(), anyBoolean(), anyString(), any());
        verify(elasticUpdateService,times(1)).updateElasticSearch(any());
    }
    private void setup(JwtAuthenticationToken jwtAuthenticationToken) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
    }


}

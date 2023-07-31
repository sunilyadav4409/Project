package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentMocks;
import com.jbhunt.finance.carrierpayment.autopay.properties.PaymentApplicationProperties;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.MessagePostService;
import org.apache.camel.ProducerTemplate;
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

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessagePostServiceTest {

    @InjectMocks
    private MessagePostService messagePostService;
    @Mock
    ProducerTemplate producerTemplate;
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
    public void upostApprovedMessageTest()   {
        messagePostService.postApprovedMessage(PaymentMocks.getPaymentMock());
    }
//    @Test
//    public void postToElasticTopicTest() throws JsonProcessingException {
//        when(paymentApplicationProperties.getAmqElasticTopicName()).thenReturn("test");
//        messagePostService.postToElasticTopic(1233, ElasticMocks.getPaymentSearchDTO());
//        verify(producerTemplate, times(1)).sendBodyAndHeaders(anyString(),any(),anyMap());
//    }

    private void setup(JwtAuthenticationToken jwtAuthenticationToken) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
    }
}

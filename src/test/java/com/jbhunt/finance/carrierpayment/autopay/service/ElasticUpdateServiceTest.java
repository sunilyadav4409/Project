package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.properties.PaymentApplicationProperties;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ElasticUpdateService;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;

@RunWith(MockitoJUnitRunner.class)
public class ElasticUpdateServiceTest {

    @InjectMocks
    ElasticUpdateService elasticUpdateService;

    @Mock
    PaymentApplicationProperties paymentApplicationProperties;

    @Mock
    RestHighLevelClient restHighLevelClient;


    @Test
    public void updateElasticSearchTest() {
        elasticUpdateService.updateElasticSearch(new UpdateRequest());
        assertNotNull(new UpdateRequest(  ));
    }

    @Test(expected = Exception.class)
    public void updateElasticSearchTestException() throws Exception{
        RequestOptions requestOptions = Mockito.mock(RequestOptions.class);
        Mockito.when(restHighLevelClient.update(any(), requestOptions)).thenThrow(Exception.class);
        elasticUpdateService.updateElasticSearch(new UpdateRequest());
    }
}

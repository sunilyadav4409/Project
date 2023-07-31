package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.service.payment.ElasticClientService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ElasticClientServiceTest {
    @InjectMocks
    ElasticClientService elasticClientService;
    @Mock
    RestHighLevelClient restClient;

    @Test(expected = Exception.class)
    public void executeSearchExceptionTest() throws IOException {
        SearchRequest searchRequest = null;
        elasticClientService.executeSearch(searchRequest);
        verify(restClient,times(1)).search(any(), RequestOptions.DEFAULT);

    }


    @Test(expected = Exception.class)
    public void executeSearchExceptionTest1() throws IOException {
        SearchRequest searchRequest = null;
        when(restClient.search( searchRequest, RequestOptions.DEFAULT )).thenThrow( new IOException(  ) );
        verify(restClient,times(1)).search(any(), RequestOptions.DEFAULT);

    }

    @Test(expected = Exception.class)
    public void executeSearchTest() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        elasticClientService.executeSearch(searchRequest);
        verify(restClient,times(1)).search(any(), RequestOptions.DEFAULT);

    }
}

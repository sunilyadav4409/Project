package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticClientService {
    @Autowired
    private RestHighLevelClient restClient;


    public SearchResponse executeSearch(SearchRequest searchRequest){
        SearchResponse searchResponse = null;
        try {
            searchResponse   =  restClient.search(searchRequest,RequestOptions.DEFAULT);
            return searchResponse;
        } catch (IOException e) {
           log.error("Error while fetching results from Elastic" + e);
           throw new RestClientException("Error while fetching results from Elastic");
        }
    }
}

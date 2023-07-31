package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.properties.PaymentApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
public class ElasticUpdateService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void updateElasticSearch(UpdateRequest updateRequest){
        try {
            updateRequest.retryOnConflict(3);
            updateRequest.setRefreshPolicy( WriteRequest.RefreshPolicy.NONE);
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (Exception ex){
            log.error("Exception while trying to update Elastic Search" +ex);
        }
    }

}

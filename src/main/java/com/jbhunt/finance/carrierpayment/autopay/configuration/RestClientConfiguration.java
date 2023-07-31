package com.jbhunt.finance.carrierpayment.autopay.configuration;

import com.jbhunt.finance.carrierpayment.autopay.properties.PaymentApplicationProperties;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfiguration {
    @Autowired
    private PaymentApplicationProperties paymentApplicationProperties;

    private CredentialsProvider credentialProvider(String userId, String password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY, new UsernamePasswordCredentials(userId, password));
        return credentialsProvider;
    }

    @Bean
    public RestHighLevelClient configRuleESRestClient(
            @Value("${DOMAIN_PID}") String userId, @Value("${DOMAIN_PASSWORD}") String password) {

        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(
                                paymentApplicationProperties.getElasticSearchBaseUrl(), paymentApplicationProperties.getElasticPort(),"https"))
                        .setHttpClientConfigCallback(
                                httpAsyncClientBuilder ->
                                        httpAsyncClientBuilder.setDefaultCredentialsProvider(
                                                credentialProvider(userId, password))));
    }
}

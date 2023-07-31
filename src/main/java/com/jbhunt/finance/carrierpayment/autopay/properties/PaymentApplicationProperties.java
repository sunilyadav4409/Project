package com.jbhunt.finance.carrierpayment.autopay.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@RefreshScope
@Component
@ConfigurationProperties
public class PaymentApplicationProperties {

    private String eoibaseurl;

    private String supplierServiceBaseUrl;


    private List<String> queueNames;

    private List<String> lookUpCriteria;

    private String url;

    private List<String> agencyType;

    private List<String> minority;

    private List<String> identificationType;

    private List<String> businessType;

    private String fileNetSchemaURL;

    private String elasticSearchBaseUrl;

    private Integer connectionMaxTotal;

    private String pidName;

    private String pidDirectory;

    private List<String> pcsCode;

    private String amqTopicName;

    private String elasticSearchActivityType;

    private String elasticSearchPaymentType;

    private String elasticSearchVendorType;

    private String elasticSearchParameterType;
    private String elasticSearchWorkFlowIndex;


    private Integer jestReadTimeout;

    private Integer jestDfltMaxTotalConnPerRoute;

    private Integer jestMaxTotalConnection;

    private Integer defaultMaxConnPerRoute;

    private List<String> autoAckWorkFlowGroupCodes;
    //Elastic Update posting..Appending refresh and timeout
    private String refreshParamElasticUpdate;
    private String timeoutParamElasticUpdate;
    private String incentiveURL;
    private String intBaseUrl;
    // Post to ErrorQueue (Elastic Update)
    private String amqElasticTopicName;

    private Integer elasticPort;

    private String elasticDocType;

    private String paymentsTopicName;
    private String chargesDecisionStatusTopicName;
}

package com.jbhunt.finance.carrierpayment.autopay.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeApprovalDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.PaymentAndProcessDTO;
import com.jbhunt.finance.carrierpayment.autopay.properties.PaymentApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.ACTIV_MQ_TOPIC;

@Slf4j
@Component
public class DynamicRouteBuilder extends RouteBuilder {

    @Autowired
    PaymentApplicationProperties paymentApplicationProperties;
    public void configure() {

String uriTopic = ACTIV_MQ_TOPIC+paymentApplicationProperties.getAmqElasticTopicName();
        String paymentsTopic= "activemq:topic:"+paymentApplicationProperties.getPaymentsTopicName();
        String chargesDecisionStatusTopic = "activemq:topic:"+paymentApplicationProperties.getChargesDecisionStatusTopicName();

        from("direct://postToPaymentTopic")
                .marshal().json(JsonLibrary.Jackson, String.class)
                .convertBodyTo(String.class)
                .log("Posting to Payment Topic = ${body}").to(ExchangePattern.InOnly,uriTopic)
        .end();

        from("direct://postToAutopayPaymentsTopic")
                .marshal(new JacksonDataFormat(this.objectMapper(), PaymentAndProcessDTO.class))
                .log("Posting to AutoPayPaymentEvent Topic = ${body}")
                .to(ExchangePattern.InOnly, paymentsTopic)
                .end();

        from("direct://postToChargesStatusTopic")
                .marshal(new JacksonDataFormat(this.objectMapper(), ChargeApprovalDTO.class))
                .log("Posting to Charges Approved Topic = ${body}")
                .to(ExchangePattern.InOnly,chargesDecisionStatusTopic)
                .end();

    }

    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        return objectMapper;
    }
}

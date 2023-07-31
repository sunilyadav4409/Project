package com.jbhunt.finance.carrierpayment.autopay.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.properties.ClientProperties;
import com.jbhunt.finance.carrierpayment.autopay.properties.PaymentApplicationProperties;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.SimpleDateFormat;

@Configuration
@AllArgsConstructor
@EnableConfigurationProperties({
        ClientProperties.class,
        PaymentApplicationProperties.class})
public class WebClientConfiguration {
    private final WebClientServletFactory webclientServletFactory;
    private final PaymentApplicationProperties paymentApplicationProperties;
    private final ClientProperties clientProperties;

    @Bean("supplierServiceClient")
    WebClient supplierServiceClient(@Qualifier("servletClientExchangeStrategies") ExchangeStrategies exchangeStrategies) {
        return webclientServletFactoryHelper(
                exchangeStrategies,
                CarrierPaymentConstants.CLIENT_REGISTRATION,
                paymentApplicationProperties.getSupplierServiceBaseUrl());
    }

    private WebClient webclientServletFactoryHelper(ExchangeStrategies exchangeStrategies, String registration, String baseUrl) {
        WebClient webClient = webclientServletFactory.createWebClient(
                registration,
                clientProperties.getTokenUri(),
                clientProperties.getClientId(),
                clientProperties.getClientSecret(),
                exchangeStrategies
        );
        return webClient.mutate().baseUrl(baseUrl).build();
    }
    @Bean("servletClientExchangeStrategies")
    public ExchangeStrategies clientExchangeStrategies(
            @Qualifier("servletObjectMapper") ObjectMapper objectMapper) {
        return ExchangeStrategies
                .builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();
    }

    @Bean("servletObjectMapper")
    public ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}


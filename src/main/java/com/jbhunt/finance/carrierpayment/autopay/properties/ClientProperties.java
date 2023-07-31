package com.jbhunt.finance.carrierpayment.autopay.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@RefreshScope
@Component
@ConfigurationProperties("spring.security.oauth2.client")
public class ClientProperties {
    @Value("${spring.security.oauth2.client.registration.serviceAccount.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.serviceAccount.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.serviceAccount.token-uri}")
    private String tokenUri;

}

package com.jbhunt.finance.carrierpayment.autopay.configuration;

import com.jbhunt.biz.securepid.PIDCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PIDConfiguration {

//    @Bean
//    public OAuth2ClientProperties oAuth2ClientProperties() {
//        return new OAuth2ClientProperties();
//    }

    @Bean
    public PIDCredentials pidCredentials(
            @Value("${DOMAIN_PID}") String userid, @Value("${DOMAIN_PASSWORD}") String password) {
        return new PIDCredentials(userid, password);
    }

}

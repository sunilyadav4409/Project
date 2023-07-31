package com.jbhunt.finance.carrierpayment.autopay.configuration;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfiguration {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.cors().and()
				.authorizeHttpRequests()
				.requestMatchers( EndpointRequest.to("health", "info", "metrics", "prometheus")).permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.oauth2ResourceServer()
				.jwt().and().and().build();
	}
}



package com.jbhunt.finance.carrierpayment.autopay;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import com.jbhunt.biz.securepid.PIDCredentials;


@Profile("test")
@Configuration
@EnableWebSecurity
@EnableJpaRepositories(basePackages = "com.jbhunt.finance.carrierpayment.autopay.repository")
@EntityScan(value = "com.jbhunt.finance.carrierpayment.autopay.entity")
@ComponentScan(basePackages = {"com.jbhunt.finance.carrierpayment.autopay", "com.jbhunt.finance.carrierpayment.autopay.mapper"})
public class TestSuiteConfig {
	@Bean
	public PIDCredentials pidCredentials() {
		return new PIDCredentials("username", "password");
	}
	
	@Bean
	public PIDCredentials filenetCredentials() {
		return new PIDCredentials("username", "password");
	}
}

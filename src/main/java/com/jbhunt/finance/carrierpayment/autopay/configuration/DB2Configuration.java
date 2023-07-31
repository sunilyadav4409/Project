package com.jbhunt.finance.carrierpayment.autopay.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration

public class DB2Configuration {

    @Primary
    @Bean(name = { "db2DataSource", "datasource" })
    @ConfigurationProperties(prefix = "jbhunt.general.datasource.com.jbhunt.datasource.jbhdb2p")
    public DataSource db2DataSource(@Value("${DOMAIN_PID}") String userId, @Value("${DOMAIN_PASSWORD}") String password) {
        log.info(
                "Configuring DB2 DataSource... Property Used: jbhunt.general.datasource.com.jbhunt.datasource.JBHDB2P and the username is:"
                        + userId);
        return DataSourceBuilder.create().username(userId).password(password)
                .build();
    }

    @Bean  (name = { "txManager", "transactionManager" })
    public DataSourceTransactionManager txManager(DataSource db2DataSource) {
        return new DataSourceTransactionManager(db2DataSource);
    }


    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource db2DataSource) {
        return new NamedParameterJdbcTemplate(db2DataSource);
    }


}


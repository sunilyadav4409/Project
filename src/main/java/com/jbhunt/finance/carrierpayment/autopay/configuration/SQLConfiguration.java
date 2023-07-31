package com.jbhunt.finance.carrierpayment.autopay.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories( entityManagerFactoryRef = "bookEntityManagerRef",
        transactionManagerRef = "transactionManager",basePackages = {
        "com.jbhunt.finance.carrierpayment.autopay.repository"})
@DependsOn("txManager")
public class SQLConfiguration {


    @Bean(name = "datasource1")
    @ConfigurationProperties(prefix = "spring.datasource1")
    public DataSource datasource1(@Value("${DOMAIN_PID}") String username,
                                  @Value("${DOMAIN_PASSWORD}") String password
    ) {
        return DataSourceBuilder.create()
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name = "paymentsDbJdbcTemplate")
    public NamedParameterJdbcTemplate paymentsDbJdbcTemplate( @Qualifier("datasource1") DataSource datasource1) {
        return new NamedParameterJdbcTemplate(datasource1);
    }

    @Primary
    @Bean  (name = { "sqtxManager", "sqtransactionManager" })
    public DataSourceTransactionManager txManager(DataSource datasource1) {
        return new DataSourceTransactionManager(datasource1);
    }


   @Primary
    @Bean(name = "bookEntityManagerRef")
    @DependsOn("transactionManager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            @Qualifier("datasource1") DataSource dataSource) {
        return entityManagerFactoryBuilder.dataSource(dataSource)
                .packages("com.jbhunt.finance.carrierpayment.autopay.entity").persistenceUnit("Book").build();
    }


    @Bean(name = "transactionManager")
    public PlatformTransactionManager secondTransactionManager(@Qualifier("bookEntityManagerRef")
                                                                       EntityManagerFactory secondEntityManagerFactory) {
        return new JpaTransactionManager(secondEntityManagerFactory);
    }
}




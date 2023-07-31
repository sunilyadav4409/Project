package com.jbhunt.finance.carrierpayment.autopay.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@RefreshScope
@Component
@ConfigurationProperties
public class PaymentIntegrationProperties {

	private String supplierServiceUrl;


	private List<String> unitValues;

	private List<String> stackableItemValues;

	private List<String> commentTypes;

	private List<String> loadCommentTypes;

	private List<String> customerCodesForRec;

	private List<String> customerCodesForBill;

	private String webServiceUserid;

	private String webServicePassword;

	private String ftlRatesUrl;

	//@Value("${spring.cloud.config.uri}")
	private String configserveruri;

	private Integer connectionMaxTotal;

	private Integer defaultMaxConnPerRoute;

	private String pidName;

	private String pidDirectory;

	private String fileNetSchemaURL;
}

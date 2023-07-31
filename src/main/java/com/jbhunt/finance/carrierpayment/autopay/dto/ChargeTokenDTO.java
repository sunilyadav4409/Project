package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChargeTokenDTO {

	private Boolean integrationStatus;
	private Integer tokenId;
	private Boolean updateExternalChargeIdSuccess;
	private Boolean insertNewChargeSuccess;
	private Integer externalChargeBillingID;
	private BigDecimal totalApprovedChargeAmount;

}

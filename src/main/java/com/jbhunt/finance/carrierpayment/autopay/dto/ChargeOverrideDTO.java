package com.jbhunt.finance.carrierpayment.autopay.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChargeOverrideDTO {

	private Integer chargeOverrideId;
	private String overrideApproverId;
	private String personName;
	private String overrideReasonCode;
	private String deviationReasonCode;
	private BigDecimal overrideAmount;
	private BigDecimal vendorAmount;
	private String reasonComment;

	private String overrideReasonCodeDescription;
	private String deviationReasonCodeDescription;
}

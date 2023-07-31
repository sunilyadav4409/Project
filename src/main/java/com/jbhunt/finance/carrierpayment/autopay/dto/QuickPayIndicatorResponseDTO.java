package com.jbhunt.finance.carrierpayment.autopay.dto;


import lombok.Data;

@Data
public class QuickPayIndicatorResponseDTO {

	private Integer stopNumber;
	private String chargeCode;
	private Character quickPayWaiverIndicator;
}

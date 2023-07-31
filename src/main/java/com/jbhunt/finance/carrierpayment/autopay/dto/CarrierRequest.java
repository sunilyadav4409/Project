package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

@Data
public class CarrierRequest {
	private String carrierCode;
	private Integer pubId;
	private Integer sctId;
	private String divisionCode;

}

package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

@Data
public class PaymentEnrichmentRequestDTO {

	private String loadNumber;
	private String dispatchNumber;
	private String scacCode;
	private boolean warehouseFlag;
	private String orderId;
}

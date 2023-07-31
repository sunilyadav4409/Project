package com.jbhunt.finance.carrierpayment.autopay.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class TOrderChargeMessageDTO {

	private String loadNumber;
	private String jobId;
	private String dispatchNumber;
	private Integer dispatchYear;
	private String division;
	private String transitMode;
	private String projectCode;
	private String scacCode;
	private String carrierName;
	private String deliveryDate;
	private String destinationCity;
	private String destinationState;
	private String originCity;
	private String originState;
	private Boolean bolReqInd;
	private Boolean deliverReceiptReqInd;
	private BigDecimal totalCharges;
	private Boolean lumperReceiptReqInd;
	private String shipmentType;
	private Integer tokenId;
	private String loadTypeCode;
}

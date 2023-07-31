package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

@Data
public class ActivityResponseDTO {
	private Integer activityTrackId;
	private String activityPerformedDate;
	private String activityPerformerId;
	private String activityTypeCode;
	private String activityTypeDesc;
	private String activityPerformTypeCode;
	private String activityPerformTypeDesc;
	private Integer dispatchNumber;
	private String scac;
	private String powerFleetCode;
	private String powerFleetCodeDesc;
	private String loadNumber;
	private Integer carrierPaymentId;
	private String activityDetailTypeCode;
	private String activityDetailTypeDescription;
}

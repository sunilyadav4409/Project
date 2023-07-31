package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

@Data
public class ActivityDTO {
	private Integer activityTrackId;
	private String activityTypeCode;
	private String activityPerformTypeCode;
	private Integer carrierPaymentId;
	private String activitySourceTypeCode;
	private String powerFleetCode;
	private String activityPerformerId;
	private String activityPerformedDate;
	private String activityDetailTypeCode;
	private String activityDetailReferenceValue;
}

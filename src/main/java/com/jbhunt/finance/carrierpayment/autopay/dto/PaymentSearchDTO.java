package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PaymentSearchDTO {

	private Integer paymentId;

	private String loadNumber;
	private String jobId;
	private Integer dispatchNumber;
	private String dispatchYear;
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
	private BigDecimal totalApprovedCharges;
	private Boolean lumperReceiptReqInd;
	private String shipmentType;
	private Integer tokenId;
	private Boolean quickPayInd;
	private String ignoreReasonCode;
	private String snoozeReasonCode;
	private List<String> invoiceNumber;
	private String bolNumber;
	private String proNumber;
	private String equipmentType;
	private String billToCode;
	private Boolean factored;
	private String daysApproaching;
	private String lastModifiedBy;
	private String modifiedDate;
	private String modifiedTime;
	private String lastProcessedBy;
	private String workFlowGroup;
	private String workFlowStatus;
	private List<String> invoiceDate;
	private String invoiceTotal;
	private String shipmentId;
	private String mcNumber;
	private String fleetCode;
	private String origPostalCode;
	private String destPostalCode;
	private String shipperLocation;
	private String referenceNumber;
	private Boolean workInProgressInd = false;
	private String lastProcessedAction;
	private String lastProcessedActionPerformedBy;
	private String lastProcessedTimeStamp;
	private String paymentMethodTypeCode;
	private Integer paymentMethodTypeSortValue;
}

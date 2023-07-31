package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChargeAuditDTO {

    private String loadNumber;
    private String chargeCode;
    private String dispatchNumber;
    private Integer jobId;
    private String projectCode;
    private String scacCode;

    private String invoiceNumber;
    private BigDecimal vendorChargeAmount;
    private String invoiceDate;

    private BigDecimal chargeAmount;

    private String chargeReasonCode;
    private String chargeReasonComment;
    private String directorOverrideId;

}

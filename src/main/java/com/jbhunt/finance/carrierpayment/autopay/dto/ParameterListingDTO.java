package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ParameterListingDTO {

    private String parameterSpecificationTypeCode;
    private String parameterSpecificationTypeDescription;
    private String carrierPaymentWorkflowGroupTypeCode;
    private Integer parameterID;
    private Integer workflowSpecificationAssociation;
    private String operatorCode;
    private String specificationSub;
    private BigDecimal minNumberValue;
    private BigDecimal maxNumberValue;
    private String effectiveDate;
    private String expirationDate;

}

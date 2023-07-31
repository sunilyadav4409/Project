package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerChargeDTO {

    private Integer stopNumber;
    private Integer externalChargeId;
    private String chargeCode;
    private BigDecimal totalChargeAmount;
}

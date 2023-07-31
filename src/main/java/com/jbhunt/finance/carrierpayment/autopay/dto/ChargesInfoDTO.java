package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ChargesInfoDTO {
    private String chargeCode;
    private BigDecimal chargeAmount;
    private Integer stopNumber;
    private Character applyToCustomerChargeFlag;
    private String chargeDecisionStatus;
    private LocalDateTime chargeDecisionDate;
    
}

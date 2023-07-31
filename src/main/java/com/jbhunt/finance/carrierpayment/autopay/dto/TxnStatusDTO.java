package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class TxnStatusDTO {
    private boolean success = false;
    private String error;
    private String warning;
    private String name;
    private String id;
    private List<String> errorList = new ArrayList<>();

    private boolean isAmountWarning = false;
    private boolean changedToPaperworkAndChargeAdded = false;
    private boolean lumpDocMissWarning = false;
    private BigDecimal totalApprovedChargeAmount;

    private boolean isFactored = false;
   private boolean autoPayStatus=false;
}

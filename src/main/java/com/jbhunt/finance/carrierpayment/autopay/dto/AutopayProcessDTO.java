package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

import java.util.List;

@Data
public class AutopayProcessDTO {

    private List<Integer> paymentIds;
    private String callingPoint;
    private String invoiceSourceType;
}

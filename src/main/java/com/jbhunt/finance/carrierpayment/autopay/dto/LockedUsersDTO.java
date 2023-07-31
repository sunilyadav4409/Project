package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

@Data
public class LockedUsersDTO {

    private Integer paymentId;
    private String action;
    private String message;
}

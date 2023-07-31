package com.jbhunt.finance.carrierpayment.autopay.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChargeApprovalDTO {

    private String businessUnit;
    private String fleetCode;
    private String division;
    private String loadNumber;
    private String dispatchNumber;
    private String scacCode;
    private String billingPartyCode;
    private List<ChargesInfoDTO> chargesInfoDTOS;


}

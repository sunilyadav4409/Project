package com.jbhunt.finance.carrierpayment.autopay.dto;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RerateChargeResponseDTO {

    private boolean status = false;
    private Map<Integer, Integer> chargeTokenIdMap = new HashMap<>();
}

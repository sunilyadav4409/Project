package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.dto.OrderTableLocationDataDTO;

public class OrderTableLocationDataDTOMocks {

    public static OrderTableLocationDataDTO orderTableLocationDataDTOMock(){
        OrderTableLocationDataDTO orderTableLocationDataDTO = new OrderTableLocationDataDTO();
        orderTableLocationDataDTO.setOrderId("1234");
        return orderTableLocationDataDTO;
    }

}

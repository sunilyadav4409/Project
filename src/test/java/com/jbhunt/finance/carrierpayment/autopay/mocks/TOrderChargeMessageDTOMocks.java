package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.dto.TOrderChargeMessageDTO;

public class TOrderChargeMessageDTOMocks {
    public static TOrderChargeMessageDTO createMocks(){
        TOrderChargeMessageDTO tOrderChargeMessageDTO = new TOrderChargeMessageDTO();
        tOrderChargeMessageDTO.setLoadNumber("12343");
        tOrderChargeMessageDTO.setDispatchYear(2019);
        tOrderChargeMessageDTO.setScacCode("jhgsad");
        tOrderChargeMessageDTO.setDispatchNumber("11");
        tOrderChargeMessageDTO.setJobId("43786");
        tOrderChargeMessageDTO.setTokenId(1);
        tOrderChargeMessageDTO.setShipmentType("LTL");
        tOrderChargeMessageDTO.setTransitMode("Train");
        tOrderChargeMessageDTO.setDivision("div");
        tOrderChargeMessageDTO.setDispatchYear(2019);
        tOrderChargeMessageDTO.setScacCode("jhgsad");
        tOrderChargeMessageDTO.setDispatchNumber("11");
        tOrderChargeMessageDTO.setJobId("43786");
        return tOrderChargeMessageDTO;
    }
    public static TOrderChargeMessageDTO sameDispatchNumberMocks(){
        TOrderChargeMessageDTO tOrderChargeMessageDTO = new TOrderChargeMessageDTO();
        tOrderChargeMessageDTO.setLoadNumber("12343");
        tOrderChargeMessageDTO.setDispatchYear(2019);
        tOrderChargeMessageDTO.setScacCode("jhgsad");
        tOrderChargeMessageDTO.setDispatchNumber(PaymentMocks.getPaymentMock().getDispatchNumber());
        tOrderChargeMessageDTO.setJobId("43786");
        return tOrderChargeMessageDTO;
    }

}

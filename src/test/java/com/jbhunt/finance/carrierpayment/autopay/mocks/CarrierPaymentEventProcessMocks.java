package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierPaymentProcessEvent;

public class CarrierPaymentEventProcessMocks {
    public static CarrierPaymentProcessEvent createCarrierPaymentrocessMocks(){
        CarrierPaymentProcessEvent carrierPaymentProcessEvent = new CarrierPaymentProcessEvent();
        carrierPaymentProcessEvent.setCarrierPaymentWorkflowGroupTypeCode( "ICS" );
        carrierPaymentProcessEvent.setCrtPgmC( "CarrierPaymentInvoice");
        return carrierPaymentProcessEvent;
    }

}

package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.entity.PaymentShipment;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeEntityMocks;
import com.jbhunt.finance.carrierpayment.autopay.repository.OverrideRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.AutoPayService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ElasticModificationService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.PaymentHelperService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.PaymentStateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PaymentHelperServiceTest {

    @InjectMocks
    private PaymentHelperService paymentHelperService;
    @Mock
    private OverrideRepository overrideRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private AutoPayService autoPayService;
    @Mock
    private PaymentStateService paymentStateService;
    @Mock
    private ElasticModificationService elasticModificationService;


    @Test
    public void  tstUpdateOverrideCharge(){

        paymentHelperService.updateOvverideCharge(ChargeEntityMocks.createChargeEntityMocks());

        verify(overrideRepository, times(1)).save(Mockito.any());
    }

    @Test
    public void setPaymentStatusToRerouteTest() {
        Payment payment = new Payment();
        payment.setGroupFlowTypeCode("ICS");
        payment.setStatusFlowTypeCode("PendingAP");
        paymentHelperService.setPaymentStatusToReroute(payment);
        verify(paymentRepository,times(1)).save(any());
    }
    @Test
    public void setPaymentStatusToRerouteTest1() {
        Payment payment = new Payment();
        payment.setGroupFlowTypeCode("LTL");
        payment.setStatusFlowTypeCode("LTLApprove");
        paymentHelperService.setPaymentStatusToReroute(payment);
        verify(paymentRepository,times(0)).save(any());
    }
    @Test
    public void getOrderIDTest1() {
        Payment payment = new Payment();
        PaymentShipment shipment = new PaymentShipment();
        shipment.setLoadToken(123456);
        shipment.setCarrierPaymentShipmentID(12345);
        payment.setCarrierPaymentShipmentID(shipment);
        Integer orderID = paymentHelperService.getOrderID(payment);
        assertNotNull(orderID);
    }
    @Test
    public void getOrderIDTest2() {
        Payment payment = new Payment();
        PaymentShipment shipment = new PaymentShipment();
        shipment.setCarrierPaymentShipmentID(12345);
        payment.setCarrierPaymentShipmentID(shipment);
        Integer orderID = paymentHelperService.getOrderID(payment);
        assertNull(orderID);
    }
    @Test
    public void getOrderIDTest3() {
        Payment payment = new Payment();
        payment.setCarrierPaymentShipmentID(null);
        Integer orderID = paymentHelperService.getOrderID(payment);
        assertNull(orderID);
    }
}

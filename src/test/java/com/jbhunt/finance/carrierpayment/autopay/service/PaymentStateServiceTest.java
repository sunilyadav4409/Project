package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentStateLogMock;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentStateLogRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.PaymentStateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PaymentStateServiceTest {

    @InjectMocks
    PaymentStateService paymentStateService;
    @Mock
    PaymentStateLogRepository paymentStateLogRepository;
    @Mock
    PaymentRepository paymentRepository;

    @Test
    public void createNewStateWhenOldsatetIsNullTest(){
        when(paymentStateLogRepository.findPaymentStateLog(any())).thenReturn(null);
        paymentStateService.createNewPaymentState(PaymentMocks.getPaymentMock());
        verify(paymentStateLogRepository,times(1)).findPaymentStateLog(anyInt());
    }
    @Test
    public void createNewStateWhenOldsatetIsNotNullTest(){
        when(paymentStateLogRepository.findPaymentStateLog(any())).thenReturn(PaymentStateLogMock.getPaymentStateLogEqualsPaymentMock());
        paymentStateService.createNewPaymentState(PaymentMocks.getPaymentMock());
        verify(paymentStateLogRepository,times(1)).findPaymentStateLog(anyInt());
    }
    @Test
    public void updatePaymentStatusTest(){
        when(paymentStateLogRepository.findPaymentStateLog(any())).thenReturn(PaymentStateLogMock.getPaymentStateLogEqualsPaymentMock());
        paymentStateService.updatePaymentStatus(PaymentMocks.getPaymentMock(),"Approve");
        verify(paymentStateLogRepository,times(1)).findPaymentStateLog(anyInt());
        verify(paymentRepository,times(1)).save(any(Payment.class));
    }

    @Test
    public void createNewPaymentStateAndSavePaymentTest(){
        when(paymentStateLogRepository.findPaymentStateLog(any())).thenReturn(PaymentStateLogMock.getPaymentStateLogEqualsPaymentMock());
        paymentStateService.createNewPaymentStateAndSavePayment(PaymentMocks.getPaymentMock());
        verify(paymentStateLogRepository,times(1)).findPaymentStateLog(anyInt());
        verify(paymentRepository,times(1)).save(any(Payment.class));
    }


}

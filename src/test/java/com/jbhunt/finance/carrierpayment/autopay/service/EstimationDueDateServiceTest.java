package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.entity.RequiredDocument;
import com.jbhunt.finance.carrierpayment.autopay.mocks.CarrierInvoiceHeaderMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentEntityMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.RequiredDocumentMock;
import com.jbhunt.finance.carrierpayment.autopay.repository.DocumentRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.InvoiceRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.EstimationDueDateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EstimationDueDateServiceTest {
    @InjectMocks
    EstimationDueDateService estimationDueDateService;
    @Mock
    InvoiceRepository invoiceRepository;
    @Mock
    DocumentRepository documentRepository;
    @Mock
    PaymentRepository paymentRepository;

    @Test
    public void getTermStartDateForApproveScenarioByCurrentInvoicesScanDateTest(){
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        payment.setAppointmentDate(LocalDate.of(2018, 12, 7));
        when(documentRepository.findByPaymentIdCarrierPaymentId(anyInt())).thenReturn(RequiredDocumentMock.getRequiredDocumentListScanDateNullMock());
        when(paymentRepository.findByCarrierPaymentId(anyInt())).thenReturn(payment);
        LocalDateTime result =  estimationDueDateService.getTermStartDateForApproveScenarioByCurrentInvoicesScanDate(1234, LocalDateTime.now());
        verify(documentRepository,times(1)).findByPaymentIdCarrierPaymentId(anyInt());
        assertNotNull(result);
    }

}


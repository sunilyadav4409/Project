package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.mocks.CarrierInvoiceHeaderMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeDTOMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeMocks;
import com.jbhunt.finance.carrierpayment.autopay.repository.ChargeRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.InvoiceRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.InvoiceValidationAndUpdateService;
import com.jbhunt.infrastructure.exception.JBHValidationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceValidationAndUpdateServiceTest {
@InjectMocks
InvoiceValidationAndUpdateService invoiceValidationAndUpdateService;
@Mock
    ChargeRepository  chargeRepository;
@Mock
    InvoiceRepository invoiceRepository;

    @Test
    public void checkOrUpdateInvoiceNumberWithoutInvoiceNumberinInvoiceHeaderTest() {
        Map<String, Boolean> flagMap = new HashMap<>();
        invoiceValidationAndUpdateService.checkOrUpdateInvoiceNumber(ChargeDTOMocks.createChargeDTOMOcks(),flagMap);
        verify(invoiceRepository, times(0)).findByCarrierInvoiceHeaderId(anyInt());

    }

    @Test (expected= JBHValidationException.class)
    public void checkOrUpdateInvoiceNumberExceptionTest() {
        Map<String, Boolean> flagMap = new HashMap<>();
        when(invoiceRepository.findByCarrierInvoiceHeaderId(anyInt())).thenReturn(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithPaymentMock());
        invoiceValidationAndUpdateService.checkOrUpdateInvoiceNumber(ChargeDTOMocks.createAuditRecordOnApproveMocks(),flagMap);
        verify(invoiceRepository, times(0)).findByCarrierInvoiceHeaderId(anyInt());

    }
    @Test
    public void checkOrUpdateInvoiceNumberTest() {
        Map<String, Boolean> flagMap = new HashMap<>();
        when(invoiceRepository.findByCarrierInvoiceHeaderId(anyInt())).thenReturn(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithPaymentMock());
        when(chargeRepository.findByHeaderId(anyInt())).thenReturn(ChargeMocks.getChaargeListMock());
        invoiceValidationAndUpdateService.checkOrUpdateInvoiceNumber(ChargeDTOMocks.createAuditWithPaymentIdMocks(),flagMap);
        verify(invoiceRepository, times(1)).findByCarrierInvoiceHeaderId(anyInt());
        verify(chargeRepository, times(1)).findByHeaderId(anyInt());

    }
    @Test
    public void checkOrUpdateInvoiceNumberWithoutInvoiceNumberinInviceRepostoryTest() {
        Map<String, Boolean> flagMap = new HashMap<>();
        when(invoiceRepository.findByCarrierInvoiceHeaderId(anyInt())).thenReturn(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithoutInvoiceNumberMock());
        when(chargeRepository.findByHeaderId(anyInt())).thenReturn(ChargeMocks.getChaargeListMock());
        invoiceValidationAndUpdateService.checkOrUpdateInvoiceNumber(ChargeDTOMocks.createAuditWithPaymentIdMocks(),flagMap);
        verify(invoiceRepository, times(1)).findByCarrierInvoiceHeaderId(anyInt());
        verify(chargeRepository, times(1)).findByHeaderId(anyInt());
        verify(invoiceRepository, times(1)).save(any());

    }
    @Test(expected = Exception.class)
    public void checkOrUpdateInvoiceNumberWithoutInvoiceNumberinInviceRepostoryExceptionTest() {
        Map<String, Boolean> flagMap = new HashMap<>();
        when(invoiceRepository.save(any())).thenThrow(new Exception());
        invoiceValidationAndUpdateService.checkOrUpdateInvoiceNumber(ChargeDTOMocks.createAuditWithPaymentIdMocks(),flagMap);
        verify(invoiceRepository, times(1)).findByCarrierInvoiceHeaderId(anyInt());
        verify(chargeRepository, times(1)).findByHeaderId(anyInt());
        verify(invoiceRepository, times(1)).save(any());

    }

}

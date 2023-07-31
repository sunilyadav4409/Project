package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.dao.CarrierPaymentParameterDAO;
import com.jbhunt.finance.carrierpayment.autopay.dto.AutopayProcessDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ParameterListingDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierPaymentProcessEvent;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.feature.FeatureFlags;
import com.jbhunt.finance.carrierpayment.autopay.mocks.CarrierInvoiceHeaderMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentEntityMocks;
import com.jbhunt.finance.carrierpayment.autopay.repository.CarrierPaymentProcessEventRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.InvoiceRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ProcessAutoPayService;
import com.jbhunt.infrastructure.exception.JBHValidationException;
import io.rollout.flags.RoxFlag;
import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.AUTOPAY_CALLINGPOINT_INVOICE;
import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.PAPER;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessAutoPayServiceTest {

    @InjectMocks
    private ProcessAutoPayService processAutoPayService;
    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private CarrierPaymentProcessEventRepository carrierPaymentProcessEventRepository;
    @Mock
    private CarrierPaymentParameterDAO carrierPaymentParameterDAO;
    @Mock
    private ProducerTemplate producerTemplate;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private FeatureFlags featureFlags;

    @Test
    public void insertRecordInCarrierPaymentProcessEventTableTest() {

        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(BigDecimal.ONE);
        when(invoiceRepository.findTopByCarrierPaymentCarrierPaymentIdAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(),
                any())).thenReturn(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithPaymentMock());
        when(carrierPaymentProcessEventRepository.findCountOfCarrierPaymentProcessEvent(any(), any())).thenReturn(0);
        when(carrierPaymentParameterDAO.getParametersForWorkFlowGroup(any())).thenReturn(param);
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        processAutoPayService.insertRecordInCarrierPaymentProcessEventTable(payment, "UnitTest", true, LocalDateTime.now());

        verify(carrierPaymentProcessEventRepository, times(1)).save(any());
    }
    @Test
    public void insertRecordInCarrierPaymentProcessEventTablePaperInvoiceTest() {
        var carrierInvoiceHeader=  CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithPaymentMock();
        carrierInvoiceHeader.setInvoiceSourceTypeCode(PAPER);
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(BigDecimal.ONE);
        when(invoiceRepository.findTopByCarrierPaymentCarrierPaymentIdAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(),
                any())).thenReturn(carrierInvoiceHeader);
        when(carrierPaymentProcessEventRepository.findCountOfCarrierPaymentProcessEvent(any(), any())).thenReturn(0);
        when(carrierPaymentParameterDAO.getParametersForWorkFlowGroup(any())).thenReturn(param);
        ReflectionTestUtils.setField(featureFlags, "paperInvcAutoPayProcess", new RoxFlag(true), RoxFlag.class);
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        processAutoPayService.insertRecordInCarrierPaymentProcessEventTable(payment, "UnitTest", true, LocalDateTime.now());

        verify(carrierPaymentProcessEventRepository, times(1)).save(any());
    }
    @Test
    public void insertRecordInCarrierPaymentProcessEventTablePaperInvoiceWithFeatureFlagOffTest() {
        var carrierInvoiceHeader=  CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithPaymentMock();
        carrierInvoiceHeader.setInvoiceSourceTypeCode(PAPER);
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(BigDecimal.ONE);
        when(invoiceRepository.findTopByCarrierPaymentCarrierPaymentIdAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(),
                any())).thenReturn(carrierInvoiceHeader);
        when(carrierPaymentParameterDAO.getParametersForWorkFlowGroup(any())).thenReturn(param);
        ReflectionTestUtils.setField(featureFlags, "paperInvcAutoPayProcess", new RoxFlag(false), RoxFlag.class);
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        processAutoPayService.insertRecordInCarrierPaymentProcessEventTable(payment, "UnitTest", true, LocalDateTime.now());

        verify(carrierPaymentProcessEventRepository, times(0)).save(any());
    }
    @Test
    public void insertRecordInCarrierPaymentProcessEventTablePaperInvoiceWithLoadStatusRejectedTest() {
        var carrierInvoiceHeader=  CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithPaymentMock();
        carrierInvoiceHeader.setInvoiceSourceTypeCode(PAPER);
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(BigDecimal.ONE);
        when(invoiceRepository.findTopByCarrierPaymentCarrierPaymentIdAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(),
                any())).thenReturn(carrierInvoiceHeader);
        when(carrierPaymentParameterDAO.getParametersForWorkFlowGroup(any())).thenReturn(param);
        ReflectionTestUtils.setField(featureFlags, "paperInvcAutoPayProcess", new RoxFlag(false), RoxFlag.class);
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        payment.setStatusFlowTypeCode("Rejected");
        processAutoPayService.insertRecordInCarrierPaymentProcessEventTable(payment, "UnitTest", true, LocalDateTime.now());

        verify(carrierPaymentProcessEventRepository, times(0)).save(any());
    }
    @Test
    public void insertRecordInCarrierPaymentProcessEventTableForPaperTest() {
        var carrierInvoiceHeader=  CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithPaymentMock();
        carrierInvoiceHeader.setInvoiceSourceTypeCode(PAPER);
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(BigDecimal.ZERO);
        when(invoiceRepository.findTopByCarrierPaymentCarrierPaymentIdAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(),
                any())).thenReturn(carrierInvoiceHeader);
        when(carrierPaymentParameterDAO.getParametersForWorkFlowGroup(any())).thenReturn(param);
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        ReflectionTestUtils.setField(featureFlags, "paperInvcAutoPayProcess", new RoxFlag(true), RoxFlag.class);
        processAutoPayService.insertRecordInCarrierPaymentProcessEventTable(payment, "UnitTest", false, LocalDateTime.now());

        Mockito.verify(producerTemplate).requestBodyAndHeaders(Mockito.anyString(), Mockito.any(),Mockito.any());
    }
    @Test
    public void insertRecordInCarrierPaymentProcessEventTableForEDITest() {
        var carrierInvoiceHeader=  CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithPaymentMock();
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(BigDecimal.ZERO);
        when(invoiceRepository.findTopByCarrierPaymentCarrierPaymentIdAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(),
                any())).thenReturn(carrierInvoiceHeader);
        when(carrierPaymentParameterDAO.getParametersForWorkFlowGroup(any())).thenReturn(param);
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        processAutoPayService.insertRecordInCarrierPaymentProcessEventTable(payment, "UnitTest", false, LocalDateTime.now());

        Mockito.verify(producerTemplate).requestBodyAndHeaders(Mockito.anyString(), Mockito.any(),Mockito.any());
    }

    @Test
    public void insertRecordInCarrierPaymentProcessEventTableWhenEventRecordCountGreaterThanZeroTest() {
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(BigDecimal.ONE);
        when(invoiceRepository.findTopByCarrierPaymentCarrierPaymentIdAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(),
                any())).thenReturn(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithPaymentMock());
        when(carrierPaymentProcessEventRepository.findCountOfCarrierPaymentProcessEvent(any(), any())).thenReturn(1);
        when(carrierPaymentProcessEventRepository.findByCarrierPaymentIDAndCarrierPaymentWorkflowGroupTypeCodeAndProcessedTimestampIsNull(
                any(), any())).thenReturn(new CarrierPaymentProcessEvent());
        when(carrierPaymentParameterDAO.getParametersForWorkFlowGroup(any())).thenReturn(param);
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        processAutoPayService.insertRecordInCarrierPaymentProcessEventTable(payment, AUTOPAY_CALLINGPOINT_INVOICE, true, LocalDateTime.now());

        Mockito.verify(carrierPaymentProcessEventRepository, times(1)).save(any());
    }

    @Test
    public void postToQueueWhereThereIsNoDelayTest() {
        when(invoiceRepository.findTopByCarrierPaymentCarrierPaymentIdAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(),
                any())).thenReturn(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithPaymentMock());
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(BigDecimal.ZERO);
        when(carrierPaymentParameterDAO.getParametersForWorkFlowGroup(any())).thenReturn(param);
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        processAutoPayService.insertRecordInCarrierPaymentProcessEventTable(payment, "UnitTest", true, LocalDateTime.now());

    }


    @Test
    public void postPaymentToProcessAutoPayTest() {
        AutopayProcessDTO autopayProcessDTO = new AutopayProcessDTO();
        autopayProcessDTO.setPaymentIds(List.of(12345));
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        when(paymentRepository.findByCarrierPaymentId(any())).thenReturn(payment);
        processAutoPayService.insertRecordInCarrierPaymentProcessEventTable(payment, "UnitTest", false, LocalDateTime.now());
        boolean status = processAutoPayService.postPaymentToProcessAutoPay(autopayProcessDTO);
        Assertions.assertEquals(true, status);
    }

    @Test
    public void insertRecordInCarrierPaymentProcessEventTableTestFalse() {
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(BigDecimal.ONE);
        when(invoiceRepository.findTopByCarrierPaymentCarrierPaymentIdAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                any(),
                any())).thenReturn(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithPaymentMock());
        when(carrierPaymentProcessEventRepository.findCountOfCarrierPaymentProcessEvent(any(), any())).thenReturn(0);
        when(carrierPaymentParameterDAO.getParametersForWorkFlowGroup(any())).thenReturn(param);
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        processAutoPayService.insertRecordInCarrierPaymentProcessEventTable(payment, "UnitTest", false, LocalDateTime.now());

        verify(carrierPaymentProcessEventRepository, times(1)).save(any());
    }

    @Test
    public void updateRecordInCarrierPaymentProcessEventTableTestFalse() throws JBHValidationException {
        when(carrierPaymentProcessEventRepository.updateCarrierPaymentProcessEvent(any(), any())).thenReturn(1);
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        processAutoPayService.updateCarrierPaymentProcessTable(payment);
        verify(carrierPaymentProcessEventRepository, times(0)).save(any());
    }

    @Test
    public void updateRecordInCarrierPaymentProcessEventTableTestTrue() throws JBHValidationException {
        Payment payment = PaymentEntityMocks.createPaymentEntityMocks();
        processAutoPayService.updateCarrierPaymentProcessTable(payment);
        verify(carrierPaymentProcessEventRepository, times(0)).save(any());
    }
}

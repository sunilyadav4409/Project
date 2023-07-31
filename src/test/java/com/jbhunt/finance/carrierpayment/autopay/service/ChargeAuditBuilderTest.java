package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.mocks.CarrierInvoiceHeaderMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeDTOMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeMocks;
import com.jbhunt.finance.carrierpayment.autopay.repository.ChargeRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.InvoiceRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ChargeAuditBuilder;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ChargeDTOAuditSetter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChargeAuditBuilderTest {

    @InjectMocks
    ChargeAuditBuilder chargeAuditBuilder;
    @Mock
    ChargeRepository chargeRepository;
    @Mock
    InvoiceRepository invoiceRepository;
    @Mock
    ChargeDTOAuditSetter chargeDTOAuditSetter;

    @Test
    public void buildChargeAuditDTONotAutopayandNullChargeIdANdNotPaperTest(){
        when(invoiceRepository.findByCarrierInvoiceHeaderId(any())).thenReturn(CarrierInvoiceHeaderMocks.createMock());
        ChargeDTO chargeDTO = ChargeDTOMocks.createAuditRecordOnApproveMocks();
        chargeAuditBuilder.buildChargeAuditDTO(chargeDTO);
        verify(invoiceRepository,times(1)).findByCarrierInvoiceHeaderId(anyInt());
        verify(chargeDTOAuditSetter,times(1)).poplateChargeForAddApproveWithEDI(any(ChargeDTO.class),anyList(),any());
        assertNull(chargeDTO.getChargeOverride());
        assertNotEquals(ChargeDTOMocks.createAuditRecordOnApproveMocks().getChargeAuditDTOs().size(),chargeDTO.getChargeAuditDTOs().size());

    }
    @Test
    public void buildChargeAuditDTONotAutopayandNullChargeIdANdPaperTest(){
        when(invoiceRepository.findByCarrierInvoiceHeaderId(any())).thenReturn(CarrierInvoiceHeaderMocks.createPaperInvoiceMock());
        ChargeDTO chargeDTO = ChargeDTOMocks.createAuditRecordOnApproveMocks();
        chargeAuditBuilder.buildChargeAuditDTO(chargeDTO);
        verify(invoiceRepository,times(1)).findByCarrierInvoiceHeaderId(anyInt());
        verify(chargeDTOAuditSetter,times(0)).poplateChargeForAddApproveWithEDI(any(ChargeDTO.class),anyList(),any());
        assertEquals(ChargeDTOMocks.createAuditRecordOnApproveMocks().getChargeAuditDTOs().size(),chargeDTO.getChargeAuditDTOs().size());

    }
    @Test
    public void buildChargeAuditDTONotAutopayandChargeIdANdPaperTest(){
        when(invoiceRepository.findByCarrierInvoiceHeaderId(any())).thenReturn(CarrierInvoiceHeaderMocks.createPaperInvoiceMock());
        when(chargeRepository.findAllById(anyList())).thenReturn(ChargeMocks.getChaargeListMock());
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO(1, false);
        chargeAuditBuilder.buildChargeAuditDTO(chargeDTO);
        verify(invoiceRepository,times(1)).findByCarrierInvoiceHeaderId(anyInt());
        verify(chargeDTOAuditSetter,times(2)).populateChargeForManualApproveWithPaper(any(ChargeDTO.class),anyList(),any());
        verify(chargeRepository,times(1)).findAllById(anyList());

    }
    @Test
    public void buildChargeAuditDTONotAutopayandChargeIdANdNotPaperTest(){
        when(invoiceRepository.findByCarrierInvoiceHeaderId(any())).thenReturn(CarrierInvoiceHeaderMocks.createMock());
        when(chargeRepository.findAllById(anyList())).thenReturn(ChargeMocks.getChaargeListMock());
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO(1, false);
        chargeAuditBuilder.buildChargeAuditDTO(chargeDTO);
        verify(invoiceRepository,times(1)).findByCarrierInvoiceHeaderId(anyInt());
        verify(chargeDTOAuditSetter,times(2)).populateChargeForManualApproveWithEDI(any(ChargeDTO.class),anyList(),any(),any());
        verify(chargeRepository,times(1)).findAllById(anyList());

    }
    @Test
    public void buildChargeAuditDTOAutopayandChargeIdANdTotalChargeAmountGreaterThanZeroTest(){
        when(invoiceRepository.findByCarrierInvoiceHeaderId(any())).thenReturn(CarrierInvoiceHeaderMocks.createMock());
        when(chargeRepository.findAllById(anyList())).thenReturn(ChargeMocks.getChaargeListMock());
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO(1, true);
        chargeAuditBuilder.buildChargeAuditDTO(chargeDTO);
        verify(invoiceRepository,times(1)).findByCarrierInvoiceHeaderId(anyInt());
        verify(chargeRepository,times(1)).findAllById(anyList());
        verify(chargeDTOAuditSetter,times(0)).chargeInBothCFPAndEDI(any(),any(),anyList(),any());
        verify(chargeDTOAuditSetter,times(2)).chargeInCFPButNotInEDI(any(),anyList(),any());

    }
    @Test
    public void buildChargeAuditDTOAutopayandChargeIdANdTotalChargeAmountANDInvoiceAmountNotSameANDDifferentChargeCOdeThanInvoiceTest(){
        when(invoiceRepository.findByCarrierInvoiceHeaderId(any())).thenReturn(CarrierInvoiceHeaderMocks.createMock());
        when(chargeRepository.findAllById(anyList())).thenReturn(ChargeMocks.getChaargeListWithTotalChargeASZeroMock());
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO(1, true);
        chargeAuditBuilder.buildChargeAuditDTO(chargeDTO);
        verify(invoiceRepository,times(1)).findByCarrierInvoiceHeaderId(anyInt());
        verify(chargeRepository,times(1)).findAllById(anyList());
        verify(chargeDTOAuditSetter,times(0)).chargeInBothCFPAndEDI(any(),any(),anyList(),any());
        verify(chargeDTOAuditSetter,times(2)).chargeInCFPButNotInEDI(any(),anyList(),any());

    }
    @Test
    public void buildChargeAuditDTOAutopayandChargeIdANdTotalChargeAmountANDInvoiceAmountSameANDDifferentChargeCOdeThanInvoiceTest(){
        when(invoiceRepository.findByCarrierInvoiceHeaderId(any())).thenReturn(CarrierInvoiceHeaderMocks.createMock());
        when(chargeRepository.findAllById(anyList())).thenReturn(ChargeMocks.getChaargeListWithTotalChargeASZeroAndSameChargeCodeAsInvoiceMock());
        ChargeDTO chargeDTO = ChargeDTOMocks.getChargeDTO(1, true);
        chargeAuditBuilder.buildChargeAuditDTO(chargeDTO);
        verify(invoiceRepository,times(1)).findByCarrierInvoiceHeaderId(anyInt());
        verify(chargeRepository,times(1)).findAllById(anyList());
        verify(chargeDTOAuditSetter,times(2)).chargeInBothCFPAndEDI(any(),any(),anyList(),any());
        verify(chargeDTOAuditSetter,times(0)).chargeInCFPButNotInEDI(any(),anyList(),any());

    }
}

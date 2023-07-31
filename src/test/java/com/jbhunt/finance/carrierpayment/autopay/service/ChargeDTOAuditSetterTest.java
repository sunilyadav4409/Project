package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.TestSuiteConfig;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeAuditDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceDetail;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.ChargeOverride;
import com.jbhunt.finance.carrierpayment.autopay.mapper.ChargeMapper;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeDTOAuditSetterMocks;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ChargeDTOAuditSetter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = { TestSuiteConfig.class })
@SpringBootTest
public class ChargeDTOAuditSetterTest {

    private static final String EDI = "EDI";

    @InjectMocks
    private ChargeDTOAuditSetter chargeDTOAuditSetter;

    private ChargeMapper chargeMapper = Mockito.mock(ChargeMapper.class);

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void chargeInBothCFPAndEDITest() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = ChargeDTOAuditSetterMocks.getChargeDTO(1, true);
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail d1 = ChargeDTOAuditSetterMocks.getDetail("TRANSIT", new BigDecimal(100));
        CarrierInvoiceDetail d2 = ChargeDTOAuditSetterMocks.getDetail("CALL", new BigDecimal(100));
        CarrierInvoiceDetail d3 = ChargeDTOAuditSetterMocks.getDetail("MISC", new BigDecimal(100));
        detailList.add(d1);
        detailList.add(d2);
        detailList.add(d3);
        Charge c1 = ChargeDTOAuditSetterMocks.getCharge(2, "TRANSIT", new BigDecimal(200));
        c1.setChargeOverride(ChargeDTOAuditSetterMocks.getChargeOverride());
        Mockito.when(chargeMapper.chargeDTOToChargeAuditDTO(Mockito.any())).thenReturn(new ChargeAuditDTO());
        List<ChargeAuditDTO> auditList = new ArrayList<>();

        // --- ACT ---
        chargeDTOAuditSetter.chargeInBothCFPAndEDI(chargeDTO, ChargeDTOAuditSetterMocks.getHeader(detailList, EDI), auditList, c1);
        Mockito.verify(chargeMapper,times(1)).chargeDTOToChargeAuditDTO(Mockito.any());
    }

    @Test
    public void chargeInBothCFPAndEDITestAmountMatch() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = ChargeDTOAuditSetterMocks.getChargeDTO(1, true);
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail d1 = ChargeDTOAuditSetterMocks.getDetail("TRANSIT", new BigDecimal(100));
        CarrierInvoiceDetail d2 = ChargeDTOAuditSetterMocks.getDetail("CALL", new BigDecimal(100));
        detailList.add(d1);
        detailList.add(d2);
        Charge c1 = ChargeDTOAuditSetterMocks.getCharge(2, "TRANSIT", new BigDecimal(100));
        c1.setChargeOverride(ChargeDTOAuditSetterMocks.getChargeOverride());
     //   Mockito.when(chargeMapper.chargeDTOToChargeAuditDTO(Mockito.any())).thenReturn(new ChargeAuditDTO());
        List<ChargeAuditDTO> auditList = new ArrayList<>();

        // --- ACT ---

        chargeDTOAuditSetter.chargeInBothCFPAndEDI(chargeDTO, ChargeDTOAuditSetterMocks.getHeader(detailList, EDI), auditList, c1);
        Mockito.verify(chargeMapper,times(0)).chargeDTOToChargeAuditDTO(Mockito.any());
    }

    @Test
    public void chargeInCFPButNotInEDITest() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = ChargeDTOAuditSetterMocks.getChargeDTO(1, true);
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail d1 = ChargeDTOAuditSetterMocks.getDetail("TRANSIT", new BigDecimal(100));
        CarrierInvoiceDetail d2 = ChargeDTOAuditSetterMocks.getDetail("CALL", new BigDecimal(100));
        detailList.add(d1);
        detailList.add(d2);
        Charge c1 = ChargeDTOAuditSetterMocks.getCharge(2, "TRANSIT", new BigDecimal(200));
        c1.setChargeOverride(ChargeDTOAuditSetterMocks.getChargeOverride());
        Mockito.when(chargeMapper.chargeDTOToChargeAuditDTO(Mockito.any())).thenReturn(new ChargeAuditDTO());
        List<ChargeAuditDTO> auditList = new ArrayList<>();
        // --- ACT ---

        chargeDTOAuditSetter.chargeInCFPButNotInEDI(chargeDTO, auditList, c1);
        Mockito.verify(chargeMapper,times(1)).chargeDTOToChargeAuditDTO(Mockito.any());

    }

    @Test
    public void chargeNotInCFPButInEDITest() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = ChargeDTOAuditSetterMocks.getChargeDTO(null, false);
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail d1 = ChargeDTOAuditSetterMocks.getDetail("FUEL", new BigDecimal(100));
        detailList.add(d1);
        List<Charge> chargeList = new ArrayList<>();
        Charge c1 = ChargeDTOAuditSetterMocks.getCharge(2, "TRANSIT", new BigDecimal(200));
        chargeList.add(c1);
        Mockito.when(chargeMapper.chargeDTOToChargeAuditDTO(Mockito.any())).thenReturn(new ChargeAuditDTO());
        List<ChargeAuditDTO> auditList = new ArrayList<>();

        // --- ACT ---

        chargeDTOAuditSetter.chargeNotInCFPButInEDI(chargeDTO, ChargeDTOAuditSetterMocks.getHeader(detailList, EDI), chargeList, auditList);
        Mockito.verify(chargeMapper,times(1)).chargeDTOToChargeAuditDTO(Mockito.any());
    }

    @Test
    public void chargeNotInCFPButInEDITestChargeMatch() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = ChargeDTOAuditSetterMocks.getChargeDTO(null, false);
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail d1 = ChargeDTOAuditSetterMocks.getDetail("TRANSIT", new BigDecimal(100));
        detailList.add(d1);
        List<Charge> chargeList = new ArrayList<>();
        Charge c1 = ChargeDTOAuditSetterMocks.getCharge(2, "TRANSIT", new BigDecimal(200));
        chargeList.add(c1);
//        Mockito.when(chargeMapper.chargeDTOToChargeAuditDTO(Mockito.any())).thenReturn(new ChargeAuditDTO());
        List<ChargeAuditDTO> auditList = new ArrayList<>();
        // --- ACT ---

        chargeDTOAuditSetter.chargeNotInCFPButInEDI(chargeDTO, ChargeDTOAuditSetterMocks.getHeader(detailList, EDI), chargeList, auditList);
        Mockito.verify(chargeMapper,times(0)).chargeDTOToChargeAuditDTO(Mockito.any());
    }

    @Test
    public void poplateChargeForAddApproveWithEDITest() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = ChargeDTOAuditSetterMocks.getChargeDTO(null, false);
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail d1 = ChargeDTOAuditSetterMocks.getDetail("TRANSIT", new BigDecimal(200));
        detailList.add(d1);
        Mockito.when(chargeMapper.chargeDTOToChargeAuditDTO(Mockito.any())).thenReturn(new ChargeAuditDTO());
        List<ChargeAuditDTO> auditList = new ArrayList<>();

        // --- ACT ---

        chargeDTOAuditSetter.poplateChargeForAddApproveWithEDI(chargeDTO, auditList, Optional.ofNullable(d1));
        Mockito.verify(chargeMapper,times(1)).chargeDTOToChargeAuditDTO(Mockito.any());
    }

    @Test
    public void poplateChargeForAddApproveWithEDITestAmountMatch() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = ChargeDTOAuditSetterMocks.getChargeDTO(null, false);
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail d1 = ChargeDTOAuditSetterMocks.getDetail("TRANSIT", new BigDecimal(100));
        detailList.add(d1);
//        Mockito.when(chargeMapper.chargeDTOToChargeAuditDTO(Mockito.any())).thenReturn(new ChargeAuditDTO());
        List<ChargeAuditDTO> auditList = new ArrayList<>();

        // --- ACT ---

        chargeDTOAuditSetter.poplateChargeForAddApproveWithEDI(chargeDTO, auditList, Optional.ofNullable(d1));
        Mockito.verify(chargeMapper,times(0)).chargeDTOToChargeAuditDTO(Mockito.any());
    }

    @Test
    public void poplateChargeForManualApproveWithEDITest() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = ChargeDTOAuditSetterMocks.getChargeDTO(null, false);
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail d1 = ChargeDTOAuditSetterMocks.getDetail("TRANSIT", new BigDecimal(200));
        detailList.add(d1);
        Charge c1 = ChargeDTOAuditSetterMocks.getCharge(2, "TRANSIT", new BigDecimal(100));
        c1.setChargeOverride(ChargeDTOAuditSetterMocks.getChargeOverride());
        Mockito.when(chargeMapper.chargeDTOToChargeAuditDTO(Mockito.any())).thenReturn(new ChargeAuditDTO());
        List<ChargeAuditDTO> auditList = new ArrayList<>();

        // --- ACT ---

        chargeDTOAuditSetter.populateChargeForManualApproveWithEDI(chargeDTO, auditList, c1, Optional.ofNullable(d1));
        Mockito.verify(chargeMapper,times(1)).chargeDTOToChargeAuditDTO(Mockito.any());
    }

    @Test
    public void poplateChargeForManualApproveWithEDITestAmountMatch() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = ChargeDTOAuditSetterMocks.getChargeDTO(null, false);
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail d1 = ChargeDTOAuditSetterMocks.getDetail("TRANSIT", new BigDecimal(100));
        detailList.add(d1);
        Charge c1 = ChargeDTOAuditSetterMocks.getCharge(2, "TRANSIT", new BigDecimal(100));
        c1.setChargeOverride(ChargeDTOAuditSetterMocks.getChargeOverride());
//        Mockito.when(chargeMapper.chargeDTOToChargeAuditDTO(Mockito.any())).thenReturn(new ChargeAuditDTO());
        List<ChargeAuditDTO> auditList = new ArrayList<>();

        // --- ACT ---

        chargeDTOAuditSetter.populateChargeForManualApproveWithEDI(chargeDTO, auditList, c1, Optional.ofNullable(d1));
        Mockito.verify(chargeMapper,times(0)).chargeDTOToChargeAuditDTO(Mockito.any());
    }

    @Test
    public void poplateChargeForManualApproveWithEDITestOvrdIdNull() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = ChargeDTOAuditSetterMocks.getChargeDTO(null, false);
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail d1 = ChargeDTOAuditSetterMocks.getDetail("TRANSIT", new BigDecimal(200));
        detailList.add(d1);
        Charge c1 = ChargeDTOAuditSetterMocks.getCharge(2, "TRANSIT", new BigDecimal(100));
        ChargeOverride ov = ChargeDTOAuditSetterMocks.getChargeOverride();
        ov.setOverrideApproverId("");
        c1.setChargeOverride(ov);
        Mockito.when(chargeMapper.chargeDTOToChargeAuditDTO(Mockito.any())).thenReturn(new ChargeAuditDTO());
        List<ChargeAuditDTO> auditList = new ArrayList<>();

        // --- ACT ---
        chargeDTOAuditSetter.populateChargeForManualApproveWithEDI(chargeDTO, auditList, c1, Optional.ofNullable(d1));
        Mockito.verify(chargeMapper,times(1)).chargeDTOToChargeAuditDTO(Mockito.any());
    }

    @Test
    public void populateChargeForManualApproveWithPaperTest() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = ChargeDTOAuditSetterMocks.getChargeDTO(1, false);
        Charge c1 = ChargeDTOAuditSetterMocks.getCharge(2, "TRANSIT", new BigDecimal(100));
        c1.setChargeOverride(ChargeDTOAuditSetterMocks.getChargeOverride());
        Mockito.when(chargeMapper.chargeDTOToChargeAuditDTO(Mockito.any())).thenReturn(new ChargeAuditDTO());
        List<ChargeAuditDTO> auditList = new ArrayList<>();

        // --- ACT ---

        chargeDTOAuditSetter.populateChargeForManualApproveWithPaper(chargeDTO, auditList, c1);
        Mockito.verify(chargeMapper,times(1)).chargeDTOToChargeAuditDTO(Mockito.any());

    }

    @Test
    public void populateChargeForManualApproveWithPaperTestAmountMatch() {

        // --- ARRANGE ---

        ChargeDTO chargeDTO = ChargeDTOAuditSetterMocks.getChargeDTO(1, false);
        Charge c1 = ChargeDTOAuditSetterMocks.getCharge(2, "TRANSIT", new BigDecimal(100));
        ChargeOverride ov = ChargeDTOAuditSetterMocks.getChargeOverride();
        ov.setVendorAmount(new BigDecimal(100));
        c1.setChargeOverride(ov);
//        Mockito.when(chargeMapper.chargeDTOToChargeAuditDTO(Mockito.any())).thenReturn(new ChargeAuditDTO());
        List<ChargeAuditDTO> auditList = new ArrayList<>();

        // --- ACT ---
        chargeDTOAuditSetter.populateChargeForManualApproveWithPaper(chargeDTO, auditList, c1);
        Mockito.verify(chargeMapper,times(0)).chargeDTOToChargeAuditDTO(Mockito.any());
    }


}

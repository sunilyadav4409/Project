package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeAuditDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeOverrideDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Supplier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.ICS;

public class ChargeDTOMocks {
    public static ChargeDTO createChargeDTOMOcks(){
        ChargeDTO chargeDTO = new ChargeDTO();
        chargeDTO.setTotalChargeAmount(new BigDecimal("121.00"));
        chargeDTO.setChargeCode("LUMPLOAD");
        return  chargeDTO;
    }
    public static ChargeDTO createAuditRecordOnApproveMocks(){
        ChargeDTO chargeDTO = new ChargeDTO();
        List<ChargeAuditDTO> chargeAuditDTOList = new ArrayList<>();
        ChargeAuditDTO chargeAuditDTO1 = new ChargeAuditDTO();
        chargeAuditDTO1.setChargeAmount(new BigDecimal("100.00"));
        chargeAuditDTO1.setLoadNumber("test");
        chargeAuditDTO1.setChargeCode("test");
        chargeAuditDTO1.setDispatchNumber("01");
        chargeAuditDTOList.add(chargeAuditDTO1);
        ChargeAuditDTO chargeAuditDTO2 = new ChargeAuditDTO();
        chargeAuditDTO2.setChargeAmount(new BigDecimal("110.00"));
        chargeAuditDTO2.setLoadNumber("test");
        chargeAuditDTO2.setChargeCode("test");
        chargeAuditDTO2.setDispatchNumber("01");
        chargeAuditDTOList.add(chargeAuditDTO2);
        chargeDTO.setTotalChargeAmount(new BigDecimal("121.00"));
        chargeDTO.setDispatchNumber("1");
        chargeDTO.setOrderID(132156);
        chargeDTO.setChargeAuditDTOs(chargeAuditDTOList);
        chargeDTO.setInvoiceNumber("ghsf");
        chargeDTO.setHeaderId(12324);
        return  chargeDTO;
    }
    public static ChargeDTO createAuditWithPaymentIdMocks(){
        ChargeDTO chargeDTO = new ChargeDTO();
        List<ChargeAuditDTO> chargeAuditDTOList = new ArrayList<>();
        ChargeAuditDTO chargeAuditDTO1 = new ChargeAuditDTO();
        chargeAuditDTO1.setChargeAmount(new BigDecimal("100.00"));
        chargeAuditDTO1.setLoadNumber("test");
        chargeAuditDTO1.setChargeCode("test");
        chargeAuditDTO1.setDispatchNumber("01");
        chargeAuditDTOList.add(chargeAuditDTO1);
        ChargeAuditDTO chargeAuditDTO2 = new ChargeAuditDTO();
        chargeAuditDTO2.setChargeAmount(new BigDecimal("110.00"));
        chargeAuditDTO2.setLoadNumber("test");
        chargeAuditDTO2.setChargeCode("test");
        chargeAuditDTO2.setDispatchNumber("01");
        chargeAuditDTOList.add(chargeAuditDTO2);
        chargeDTO.setTotalChargeAmount(new BigDecimal("121.00"));
        chargeDTO.setDispatchNumber("1");
        chargeDTO.setChargeAuditDTOs(chargeAuditDTOList);
        chargeDTO.setInvoiceNumber("ghsf");
        chargeDTO.setHeaderId(12324);
        chargeDTO.setPaymentId(PaymentMocks.getPaymentMock().getCarrierPaymentId());
        return  chargeDTO;
    }
    public static ChargeDTO createChargeOverrideMocks(){
        LocalDateTime dateTime = LocalDateTime.now();
        ChargeDTO chargeDTO = new ChargeDTO();
        ChargeOverrideDTO chargeOverride = new ChargeOverrideDTO();
        List<ChargeAuditDTO> chargeAuditDTOList = new ArrayList<>();
        ChargeAuditDTO chargeAuditDTO1 = new ChargeAuditDTO();
        chargeAuditDTO1.setChargeAmount(new BigDecimal("100.00"));
        chargeAuditDTO1.setLoadNumber("test");
        chargeAuditDTO1.setChargeCode("test");
        chargeAuditDTO1.setDispatchNumber("01");
        chargeAuditDTOList.add(chargeAuditDTO1);
        ChargeAuditDTO chargeAuditDTO2 = new ChargeAuditDTO();
        chargeAuditDTO2.setChargeAmount(new BigDecimal("110.00"));
        chargeAuditDTO2.setLoadNumber("test");
        chargeAuditDTO2.setChargeCode("test");
        chargeAuditDTO2.setDispatchNumber("01");
        chargeAuditDTOList.add(chargeAuditDTO2);
        chargeDTO.setTotalChargeAmount(new BigDecimal("121.00"));
        chargeDTO.setDispatchNumber("1");
        chargeOverride.setOverrideAmount(new BigDecimal("120.00"));
        chargeDTO.setInvoiceDate(dateTime.toString());
        chargeDTO.setInvoiceReceivedDate(dateTime);
        chargeDTO.setChargeOverride(chargeOverride);
        chargeDTO.setChargeAuditDTOs(chargeAuditDTOList);
        return  chargeDTO;
    }
    public static ChargeDTO createChargeOverrideWithExternalChargeIdListMocks(){
        LocalDateTime dateTime = LocalDateTime.now();
        ChargeDTO chargeDTO = new ChargeDTO();
        ChargeOverrideDTO chargeOverride = new ChargeOverrideDTO();
        List<ChargeAuditDTO> chargeAuditDTOList = new ArrayList<>();
        ChargeAuditDTO chargeAuditDTO1 = new ChargeAuditDTO();
        chargeAuditDTO1.setChargeAmount(new BigDecimal("100.00"));
        chargeAuditDTO1.setLoadNumber("test");
        chargeAuditDTO1.setChargeCode("test");
        chargeAuditDTO1.setDispatchNumber("01");
        chargeAuditDTOList.add(chargeAuditDTO1);
        ChargeAuditDTO chargeAuditDTO2 = new ChargeAuditDTO();
        chargeAuditDTO2.setChargeAmount(new BigDecimal("110.00"));
        chargeAuditDTO2.setLoadNumber("test");
        chargeAuditDTO2.setChargeCode("test");
        chargeAuditDTO2.setDispatchNumber("01");
        chargeAuditDTOList.add(chargeAuditDTO2);
        chargeDTO.setTotalChargeAmount(new BigDecimal("121.00"));
        chargeDTO.setDispatchNumber("1");
        chargeOverride.setOverrideAmount(new BigDecimal("120.00"));
        chargeDTO.setInvoiceDate(dateTime.toString());
        chargeDTO.setInvoiceReceivedDate(dateTime);
        chargeDTO.setChargeOverride(chargeOverride);
        chargeDTO.setOrderID(123456);
        chargeDTO.setChargeDecisionCode("Reject");
        List<Integer> externalChargeIDList = new ArrayList<>();
        externalChargeIDList.add(1);
        externalChargeIDList.add(2);
        Map map=new HashMap<Integer,String>();
        map.put(1,"fuelcharge");
        map.put(2,"transit");
        chargeDTO.setChargeCodeList(map);
        chargeDTO.setExternalChargeIDList(externalChargeIDList);
        chargeDTO.setChargeAuditDTOs(chargeAuditDTOList);
        return  chargeDTO;
    }

    public static ChargeDTO getChargeDTO(Integer id, boolean autoPayFlag) {
        ChargeDTO chargeDTO = new ChargeDTO();
        chargeDTO.setChargeId(id);
        chargeDTO.setChargeCode("LUMPUNLD");
        chargeDTO.setHeaderId(1);
        chargeDTO.setAutoPay(autoPayFlag);
        return chargeDTO;
    }


    public static ChargeDTO getChargeDTO() {
        ChargeDTO chargeDTO = new ChargeDTO();
        chargeDTO.setChargeId(1);
        chargeDTO.setChargeCode("LUMPLDPAY");
        chargeDTO.setStopNumber(10);
        return chargeDTO;
    }
    public static ChargeDTO getChargeDTO(String code) {
        ChargeDTO chargeDTO = new ChargeDTO();
        chargeDTO.setChargeIdList(Arrays.asList(1));
        chargeDTO.setChargeDecisionCode(code);
        chargeDTO.setUpdatedTimeList(Arrays.asList(NOW));
        chargeDTO.setInvoiceNumber("234fg548");
        chargeDTO.setHeaderId(1);
        chargeDTO.setPaymentId(1);
        chargeDTO.setWorkFlowGroupType(ICS);
        chargeDTO.setInvoiceReceivedTermsDate(NOW);
        chargeDTO.setInvoiceNumberSuffix("ABC");
        chargeDTO.setLoadNumber("J016999");
        chargeDTO.setQuickPayWaiverIndicator('Y');
        chargeDTO.setStopNumber(1);
        chargeDTO.setTotalChargeAmount(BigDecimal.TEN);
        return chargeDTO;
    }
    public static ChargeDTO getChargeDTOApp(String code) {
        ChargeDTO chargeDTO = new ChargeDTO();
        Supplier supplier = new Supplier();
        chargeDTO.setChargeIdList(Arrays.asList(1));
        chargeDTO.setChargeDecisionCode(code);
        chargeDTO.setUpdatedTimeList(Arrays.asList(NOW));
        chargeDTO.setInvoiceNumber("234fg548");
        chargeDTO.setHeaderId(1);
        chargeDTO.setPaymentId(1);
        chargeDTO.setWorkFlowGroupType(ICS);
        chargeDTO.setInvoiceReceivedTermsDate(NOW);
        chargeDTO.setInvoiceNumberSuffix("ABC");
        chargeDTO.setLoadNumber("J016999");
        chargeDTO.setQuickPayWaiverIndicator('Y');
        chargeDTO.setStopNumber(1);
        chargeDTO.setTotalChargeAmount(BigDecimal.TEN);
        chargeDTO.setEstimatedDueDate(LocalDate.now());
        chargeDTO.setScacCode("ABCD");
        chargeDTO.setChargeDecisionCode("PendingAP");
        chargeDTO.setChargeDecisionDate(NOW);
        chargeDTO.setChargeDecisionPersonId("jhhifiy");
        chargeDTO.setReasonCode("Rejecting");
        return chargeDTO;
    }
    private static final LocalDateTime NOW = LocalDateTime.now();

    public static List<ChargeDTO> getChargeDTOList(String code) {
        List<ChargeDTO> dtos=new ArrayList<>();
        ChargeDTO chargeDTO = new ChargeDTO();
        chargeDTO.setChargeIdList(Arrays.asList(1));
        chargeDTO.setChargeDecisionCode(code);
        chargeDTO.setUpdatedTimeList(Arrays.asList(NOW));
        chargeDTO.setInvoiceNumber("234fg548");
        chargeDTO.setHeaderId(1);
        chargeDTO.setPaymentId(1);
        chargeDTO.setInvoiceNumberSuffix("ABC");
        chargeDTO.setLoadNumber("J016999");
        chargeDTO.setQuickPayWaiverIndicator('Y');
        chargeDTO.setTotalApprovedChargeAmount(BigDecimal.TEN);
        dtos.add(chargeDTO);
        return dtos;
    }

    public static ChargeDTO getChargeDTO(Integer id, Integer ovId) {
        ChargeDTO chargeDTO = new ChargeDTO();
        chargeDTO.setChargeId(id);
        chargeDTO.setChargeCode("LUMPLDPAY");
        chargeDTO.setChargeOverride(getChargeOverrideDTO(ovId));
        chargeDTO.setTotalChargeAmount(new BigDecimal("100.00"));
        chargeDTO.setInvoiceNumber("IN12345");
        chargeDTO.setInvoiceDate("2017-05-19T01:22:30.704846900");
        chargeDTO.setChargeApplyToCustomerIndicator('Y');
        chargeDTO.setHeaderId(1);
        chargeDTO.setPaymentId(1);
        return chargeDTO;
    }


    public static ChargeOverrideDTO getChargeOverrideDTO(Integer ovId) {
        ChargeOverrideDTO chargeOverrideDTO = new ChargeOverrideDTO();
        chargeOverrideDTO.setChargeOverrideId(ovId);
        chargeOverrideDTO.setOverrideAmount(new BigDecimal("100.00"));
        return chargeOverrideDTO;
    }



}

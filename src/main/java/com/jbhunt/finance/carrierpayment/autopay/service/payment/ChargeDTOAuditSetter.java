package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeAuditDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceDetail;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.mapper.ChargeMapper;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargeAuditUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChargeDTOAuditSetter {

    private final ChargeMapper chargeMapper;

    public ChargeDTOAuditSetter(ChargeMapper chargeMapper) {
        this.chargeMapper = chargeMapper;
    }

    public void chargeInBothCFPAndEDI(ChargeDTO chargeDTO, CarrierInvoiceHeader approvedInvoice,
                                      List<ChargeAuditDTO> auditList, Charge charge) {
        Optional<CarrierInvoiceDetail> detail = ChargeAuditUtil.fetchMatchingInvoiceDetail(approvedInvoice,
                charge.getChargeCode());
        detail.filter(nonNullChargeAmt -> Objects.nonNull(charge.getTotalApprovedChargeAmount()))
                .filter(ediAmt -> detail.get().getCarrierChargeUnitRateAmount()
                        .compareTo(charge.getTotalApprovedChargeAmount()) != 0)
                .ifPresent(x -> {
                    ChargeAuditDTO chargeAuditDTO = chargeMapper.chargeDTOToChargeAuditDTO(chargeDTO);
                    chargeAuditDTO.setChargeCode(charge.getChargeCode());
                    chargeAuditDTO.setVendorChargeAmount(detail.get().getCarrierChargeUnitRateAmount());
                    log.info("AUD_BFILL AUTOPAY :: VENDOR AMOUNT :: " + chargeAuditDTO.getVendorChargeAmount());
                    chargeAuditDTO.setChargeAmount(charge.getTotalApprovedChargeAmount());
                    log.info("AUD_BFILL AUTOPAY :: CHARGE AMOUNT :: " + chargeAuditDTO.getChargeAmount());
                    chargeAuditDTO.setChargeReasonCode(CarrierPaymentConstants.DB2_AUDIT_AUTOPAY_REASON_CODE);
                    chargeAuditDTO.setChargeReasonComment(CarrierPaymentConstants.DB2_AUDIT_AUTOPAY_REASON_COMMENT);
                    auditList.add(chargeAuditDTO);
                });
    }

    public void chargeInCFPButNotInEDI(ChargeDTO chargeDTO, List<ChargeAuditDTO> auditList, Charge charge) {
        ChargeAuditDTO chargeAuditDTO = chargeMapper.chargeDTOToChargeAuditDTO(chargeDTO);
        chargeAuditDTO.setChargeCode(charge.getChargeCode());
        chargeAuditDTO.setVendorChargeAmount(null);
        log.info("AUD_BFILL::CHARGE NOT IN EDI :: VENDOR AMOUNT WILL BE NULL.");
        chargeAuditDTO.setChargeAmount(charge.getTotalApprovedChargeAmount());
        log.info("AUD_BFILL::CHARGE NOT IN EDI :: CHARGE AMOUNT:: " + chargeAuditDTO.getChargeAmount());
        chargeAuditDTO.setChargeReasonCode(CarrierPaymentConstants.DB2_AUDIT_AUTOPAY_REASON_CODE);
        chargeAuditDTO.setChargeReasonComment(CarrierPaymentConstants.DB2_AUDIT_AUTOPAY_REASON_COMMENT);
        auditList.add(chargeAuditDTO);
    }

    public void chargeNotInCFPButInEDI(ChargeDTO chargeDTO, CarrierInvoiceHeader approvedInvoice,
                                       List<Charge> chargeList, List<ChargeAuditDTO> auditList) {
        List<String> cFPChargeCodes = chargeList.stream().map(Charge::getChargeCode).collect(Collectors.toList());
        approvedInvoice.getCarrierInvoiceDetailList().forEach(ediDetail -> {
            if (!cFPChargeCodes.contains(ediDetail.getChargeCode().trim())) {
                log.info("AUD_BFILL::CHARGE NOT IN CFP :: " + ediDetail.getChargeCode().trim());
                ChargeAuditDTO chargeAuditDTO = chargeMapper.chargeDTOToChargeAuditDTO(chargeDTO);
                chargeAuditDTO.setChargeCode(ediDetail.getChargeCode().trim());
                chargeAuditDTO.setVendorChargeAmount(ediDetail.getCarrierChargeUnitRateAmount());
                log.info("AUD_BFILL::CHARGE NOT IN CFP ::VENDOR AMOUNT :: " + chargeAuditDTO.getVendorChargeAmount());
                chargeAuditDTO.setChargeAmount(BigDecimal.ZERO);
                log.info("AUD_BFILL AUTOPAY :: CHARGE AMOUNT WILL BE ZERO. ");
                chargeAuditDTO.setChargeReasonCode(CarrierPaymentConstants.DB2_AUDIT_AUTOPAY_REASON_CODE);
                chargeAuditDTO.setChargeReasonComment(CarrierPaymentConstants.DB2_AUDIT_AUTOPAY_REASON_COMMENT);
                auditList.add(chargeAuditDTO);
            }
        });
    }

    public void poplateChargeForAddApproveWithEDI(ChargeDTO chargeDTO, List<ChargeAuditDTO> auditList,
            Optional<CarrierInvoiceDetail> detail) {
        detail.filter(ediAmt -> detail.get().getCarrierChargeUnitRateAmount()
                .compareTo(chargeDTO.getTotalChargeAmount()) != 0).ifPresent(amtMismatch -> {
                    ChargeAuditDTO chargeAuditDTO = chargeMapper.chargeDTOToChargeAuditDTO(chargeDTO);
                    log.info("AUD_BFILL::ADD EDI:: CHARGE CODE:: " + chargeAuditDTO.getChargeCode());
                    chargeAuditDTO.setVendorChargeAmount(detail.get().getCarrierChargeUnitRateAmount());
                    log.info("AUD_BFILL::ADD EDI::VENDOR AMOUNT:: " + chargeAuditDTO.getVendorChargeAmount());
                    chargeAuditDTO.setChargeAmount(chargeDTO.getTotalChargeAmount());
                    log.info("AUD_BFILL::ADD EDI::CHARGE AMOUNT:: " + chargeAuditDTO.getChargeAmount());
                    log.info("AUD_BFILL::ADD EDI:: NO OVERRIDE DETAILS");
                    auditList.add(chargeAuditDTO);
                });
    }

    public void populateChargeForManualApproveWithEDI(ChargeDTO chargeDTO, List<ChargeAuditDTO> auditList, Charge charge,
                                                      Optional<CarrierInvoiceDetail> detail) {
        detail.filter(nonNullChargeAmt -> Objects.nonNull(charge.getTotalApprovedChargeAmount()))
                .filter(ediAmt -> detail.get().getCarrierChargeUnitRateAmount()
                        .compareTo(charge.getTotalApprovedChargeAmount()) != 0)
                .ifPresent(amtMismatch -> {
                    ChargeAuditDTO chargeAuditDTO = chargeMapper.chargeDTOToChargeAuditDTO(chargeDTO);
                    chargeAuditDTO.setChargeCode(charge.getChargeCode());
                    log.info("AUD_BFILL::MAN EDI:: CHARGE CODE:: " + chargeAuditDTO.getChargeCode());
                    chargeAuditDTO.setVendorChargeAmount(detail.get().getCarrierChargeUnitRateAmount());
                    log.info("AUD_BFILL::MAN EDI::VENDOR AMOUNT:: " + chargeAuditDTO.getVendorChargeAmount());
                    chargeAuditDTO.setChargeAmount(charge.getTotalApprovedChargeAmount());
                    log.info("AUD_BFILL::MAN EDI::CHARGE AMOUNT:: " + chargeAuditDTO.getChargeAmount());
                    ChargeAuditUtil.overrideDetailsPopulation(charge, chargeAuditDTO);
                    auditList.add(chargeAuditDTO);
                });
    }

    public void populateChargeForManualApproveWithPaper(ChargeDTO chargeDTO, List<ChargeAuditDTO> auditList,
            Charge charge) {
        Optional.ofNullable(charge.getChargeOverride())
                .filter(nonNullChargeAmt -> Objects.nonNull(charge.getTotalApprovedChargeAmount()))
                .filter(amtMismatch -> charge.getChargeOverride().getVendorAmount()
                        .compareTo(charge.getTotalApprovedChargeAmount()) != 0)
                .ifPresent(override -> {
                    ChargeAuditDTO chargeAuditDTO = chargeMapper.chargeDTOToChargeAuditDTO(chargeDTO);
                    chargeAuditDTO.setChargeCode(charge.getChargeCode());
                    log.info("AUD_BFILL::MAN PAPER::CHARGE CODE:: " + chargeAuditDTO.getChargeCode());
                    chargeAuditDTO.setVendorChargeAmount(override.getVendorAmount());
                    log.info("AUD_BFILL::MAN PAPER::VENDOR AMOUNT:: " + chargeAuditDTO.getVendorChargeAmount());
                    chargeAuditDTO.setChargeAmount(charge.getTotalApprovedChargeAmount());
                    log.info("AUD_BFILL::MAN PAPER::CHARGE AMOUNT:: " + chargeAuditDTO.getChargeAmount());
                    ChargeAuditUtil.overrideDetailsPopulation(charge, chargeAuditDTO);
                    auditList.add(chargeAuditDTO);
                });
    }
}

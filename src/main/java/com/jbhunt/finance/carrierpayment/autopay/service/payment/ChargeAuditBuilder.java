package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeAuditDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceDetail;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.repository.InvoiceRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.ChargeRepository;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargeAuditUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ChargeAuditBuilder {

    @Autowired
    private  ChargeRepository chargeRepository;
    @Autowired
    private  InvoiceRepository invoiceRepository;
    @Autowired
    private  ChargeDTOAuditSetter chargeDTOAuditSetter;

    public void buildChargeAuditDTO(ChargeDTO chargeDTO) {
       log.info("DB2 Insert/Update with Approve for AUDIT ");
        log.info("buildChargeAuditDTO : chargeDTO :: " + chargeDTO);

        CarrierInvoiceHeader approvedInvoice = invoiceRepository.findByCarrierInvoiceHeaderId(chargeDTO.getHeaderId());

        if (chargeDTO.isAutoPay()) {
            BigDecimal totalInvoiceAmount = ChargeAuditUtil.getTotalInvoiceAmount(approvedInvoice);
            log.info("AUD_BFILL AUTOPAY :: TOTAL INVOICE AMOUNT IS :: " + totalInvoiceAmount);

            List<Charge> chargeList = chargeRepository.findAllById(chargeDTO.getChargeIdList());
            BigDecimal totalChargeAmount = ChargeAuditUtil.getTotalChargeAmount(chargeList);
            log.info("AUD_BFILL AUTOPAY :: TOTAL CHARGE AMOUNT IS :: " + totalChargeAmount);

            if (totalInvoiceAmount.compareTo(totalChargeAmount) != 0) {
                List<ChargeAuditDTO> auditList = new ArrayList<>();
                List<String> invoiceChargeCodes = approvedInvoice.getCarrierInvoiceDetailList().stream()
                        .map(detail -> detail.getChargeCode().trim()).collect(Collectors.toList());
                chargeList.forEach(charge -> {
                    log.info("AUD_BFILL AUTOPAY :: CFP CHARGE AMOUNT :: " + charge.getTotalApprovedChargeAmount());
                    if (invoiceChargeCodes.contains(charge.getChargeCode())) {
                        // CHARGE IS IN BOTH EDI AND CFP
                        log.info("AUD_BFILL AUTOPAY :: CHARGE PRESENT :: " + charge.getChargeCode());
                        chargeDTOAuditSetter.chargeInBothCFPAndEDI(chargeDTO, approvedInvoice, auditList, charge);
                    } else {
                        // CHARGE NOT IN EDI BUT IN CFP
                        log.info("AUD_BFILL::CHARGE NOT IN EDI :: " + charge.getChargeCode());
                        chargeDTOAuditSetter.chargeInCFPButNotInEDI(chargeDTO, auditList, charge);
                    }
                });
                // CHARGE NOT IN CFP BUT IN EDI
                chargeDTOAuditSetter.chargeNotInCFPButInEDI(chargeDTO, approvedInvoice, chargeList, auditList);
                chargeDTO.setChargeAuditDTOs(auditList);
            }
        } else if (!chargeDTO.isAutoPay() && Objects.isNull(chargeDTO.getChargeId())
                && CollectionUtils.isEmpty(chargeDTO.getChargeIdList())) {
            // USER ADD CHARGE AND APPROVE WITH EDI INVOICE
            Optional.ofNullable(approvedInvoice).filter(
                    edi -> !Objects.equals(approvedInvoice.getInvoiceSourceTypeCode(), CarrierPaymentConstants.PAPER))
                    .ifPresent(auditPopulate -> {
                        log.info("AUD_BFILL::ADD EDI::HEADER ID::" + approvedInvoice.getCarrierInvoiceHeaderId());
                        List<ChargeAuditDTO> auditList = new ArrayList<>();
                        Optional<CarrierInvoiceDetail> detail = ChargeAuditUtil
                                .fetchMatchingInvoiceDetail(approvedInvoice, chargeDTO.getChargeCode());
                        chargeDTOAuditSetter.poplateChargeForAddApproveWithEDI(chargeDTO, auditList, detail);
                        chargeDTO.setChargeAuditDTOs(auditList);
                    });

            // USER ADD CHARGE AND APPROVE WITH PAPER INVOICE
            Optional.ofNullable(approvedInvoice).filter(
                    paper -> Objects.equals(approvedInvoice.getInvoiceSourceTypeCode(), CarrierPaymentConstants.PAPER))
                    .ifPresent(auditPopulate -> {
                        log.info("AUD_BFILL::ADD PAPER::HEADER ID::" + approvedInvoice.getCarrierInvoiceHeaderId());
                        log.info("AUD_BFILL::ADD PAPER:: NO AUDIT AS NO OVERRIDE PRESENT ON ADD CHARGE");
                    });
        } else {
            // MANUAL APPROVE/EDIT CHARGE AND APPROVE WITH EDI INVOICE
            Optional.ofNullable(approvedInvoice).filter(
                    edi -> !Objects.equals(approvedInvoice.getInvoiceSourceTypeCode(), CarrierPaymentConstants.PAPER))
                    .ifPresent(auditPopulate -> {
                        log.info("AUD_BFILL::MAN EDI::HEADER ID::" + approvedInvoice.getCarrierInvoiceHeaderId());
                        List<ChargeAuditDTO> auditList = new ArrayList<>();
                        List<Charge> chargeList = chargeRepository.findAllById(chargeDTO.getChargeIdList());
                        chargeList.forEach(charge -> {
                            Optional<CarrierInvoiceDetail> detail = ChargeAuditUtil
                                    .fetchMatchingInvoiceDetail(approvedInvoice, charge.getChargeCode());
                            log.info("MAN EDI:: CFP CHARGE AMOUNT :: " + charge.getTotalApprovedChargeAmount());
                            chargeDTOAuditSetter.populateChargeForManualApproveWithEDI(chargeDTO, auditList, charge,
                                    detail);
                        });
                        chargeDTO.setChargeAuditDTOs(auditList);
                    });

            // MANUAL APPROVE/EDIT CHARGE AND APPROVE WITH PAPER INVOICE
            Optional.ofNullable(approvedInvoice).filter(
                    paper -> Objects.equals(approvedInvoice.getInvoiceSourceTypeCode(), CarrierPaymentConstants.PAPER))
                    .ifPresent(auditPopulate -> {
                        log.info("AUD_BFILL::MAN PAPER::HEADER ID::" + approvedInvoice.getCarrierInvoiceHeaderId());
                        List<ChargeAuditDTO> auditList = new ArrayList<>();
                        List<Charge> chargeList = chargeRepository.findAllById(chargeDTO.getChargeIdList());
                        chargeList.forEach(charge -> {
                            log.info("MAN PAPER:: CFP CHARGE AMOUNT ::" + charge.getTotalApprovedChargeAmount());
                            chargeDTOAuditSetter.populateChargeForManualApproveWithPaper(chargeDTO, auditList, charge);
                        });
                        chargeDTO.setChargeAuditDTOs(auditList);
                    });
        }

        log.info("buildChargeAuditDTO : AUDIT DTO FORMED:: " + chargeDTO);
    }

}

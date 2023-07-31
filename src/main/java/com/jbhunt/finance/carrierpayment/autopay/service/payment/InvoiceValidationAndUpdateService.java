package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.repository.ChargeRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.InvoiceRepository;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.InvoiceSuffixUtil;
import com.jbhunt.infrastructure.exception.JBHValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentErrorConstants.INCORRECT_HEADERID_TAGGED;

@Slf4j
@Service
public class InvoiceValidationAndUpdateService {

    @Autowired
    private  ChargeRepository chargeRepository;
    @Autowired
    private  InvoiceRepository invoiceRepository;

    public void checkOrUpdateInvoiceNumber(ChargeDTO chargeDTO, Map<String, Boolean> flagMap) {
        log.info("CHARGE SERVICE :: Inside checkOrUpdateInvoiceNumber ");
        Optional.ofNullable(chargeDTO).filter(dto -> Objects.nonNull(dto.getInvoiceNumber()))
                .filter(dto -> Objects.nonNull(dto.getHeaderId())).ifPresent(approve -> {
                    log.info("checkOrUpdateInvoiceNumber :: chargeDTO.getHeaderId():: " + chargeDTO.getHeaderId());
                    CarrierInvoiceHeader existingHeader = invoiceRepository.findByCarrierInvoiceHeaderId(chargeDTO.getHeaderId());
                    Optional.ofNullable(existingHeader)
                            .filter(paymentIdMatches -> Objects.equals(
                                    existingHeader.getCarrierPayment().getCarrierPaymentId(), chargeDTO.getPaymentId()))
                            .orElseThrow(() -> new JBHValidationException(INCORRECT_HEADERID_TAGGED));
                    modifyInvoiceHeader(existingHeader, chargeDTO, flagMap);
                });
    }

    private void modifyInvoiceHeader(CarrierInvoiceHeader existingHeader, ChargeDTO chargeDTO,
            Map<String, Boolean> flagMap) {
        Optional.ofNullable(existingHeader).filter(paymentIdMatches -> Objects
                .equals(existingHeader.getCarrierPayment().getCarrierPaymentId(), chargeDTO.getPaymentId()))
                .ifPresent(header -> {
                    String existingInvoiceNumber = header.getCarrierInvoiceNumber();
                    log.info("checkOrUpdateInvoiceNumber :: existingInvoiceNumber :: " + existingInvoiceNumber);
                    // INVOICE HEADER UPDATE
                    if (StringUtils.isBlank(existingInvoiceNumber)) {
                        updateInvoiceHeader(chargeDTO, header, flagMap);
                    }
                    String headerInvoiceNumber = header.getCarrierInvoiceNumber();
                    // SUFFIX VALIATIONS.If valid invoice#, then get suffix
                    flagMap.put(
                            CarrierPaymentConstants.INVALID_INV_NUM.concat(": ").concat(chargeDTO.getInvoiceNumber()),
                            InvoiceSuffixUtil.getSuffixValue(headerInvoiceNumber, chargeDTO));
                    // Check duplicate invoice number suffix
                    flagMap.put("The Invoice Number " + chargeDTO.getInvoiceNumber() + " already exists",
                            checkDuplicateSuffix(chargeDTO));
                });
    }

    private void updateInvoiceHeader(ChargeDTO chargeDTO, CarrierInvoiceHeader existingHeader,
            Map<String, Boolean> flagMap) {

        existingHeader.setCarrierInvoiceNumber(chargeDTO.getInvoiceNumber());
        Optional.ofNullable(chargeDTO.getInvoiceDate()).map(LocalDateTime::parse)
                .ifPresent(existingHeader::setInvoiceDate);
        try {
            log.info("save header record with updated invoicenumber:");
            invoiceRepository.save(existingHeader);
        } catch (Exception e) {
            log.error("Update Invoice Header Exception ", e);
            flagMap.put(CarrierPaymentConstants.ERR_UPDATING_INVOICE, false);
        }
    }

    private Boolean checkDuplicateSuffix(ChargeDTO chargeDTO) {
        List<Charge> chargeList = chargeRepository.findByHeaderId(chargeDTO.getHeaderId());
        Set<String> suffixes = chargeList.stream().map(Charge::getInvoiceNumberSuffix).collect(Collectors.toSet());
        log.info("SUFFIX VALIDATIONS ::suffixes:" + suffixes);
        log.info("SUFFIX VALIDATIONS ::getInvoiceNumberSuffix:" + chargeDTO.getInvoiceNumberSuffix());
        return Optional.ofNullable(suffixes).
                map(list -> !list.contains(chargeDTO.getInvoiceNumberSuffix())).orElse(Boolean.FALSE);
    }

}

package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeAuditDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceDetail;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class ChargeAuditUtil {

    private ChargeAuditUtil() {

    }

    public static BigDecimal getTotalInvoiceAmount(CarrierInvoiceHeader approvedInvoice) {
        return approvedInvoice.getCarrierInvoiceDetailList().stream()
                .map(CarrierInvoiceDetail::getCarrierChargeUnitRateAmount).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal getTotalChargeAmount(List<Charge> chargeList) {
        return chargeList.stream().map(Charge::getTotalApprovedChargeAmount).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static Optional<CarrierInvoiceDetail> fetchMatchingInvoiceDetail(CarrierInvoiceHeader approvedInvoice,
                                                                            String cfpChargeCode) {
        return approvedInvoice.getCarrierInvoiceDetailList().stream()
                .filter(ediDetail -> Objects.equals(cfpChargeCode, ediDetail.getChargeCode().trim())).findFirst();
    }

    public static void overrideDetailsPopulation(Charge charge, ChargeAuditDTO chargeAuditDTO) {
        Optional.ofNullable(charge.getChargeOverride()).ifPresent(override -> {
            Optional.ofNullable(override.getOverrideApproverId()).filter(overrideId -> !overrideId.isEmpty())
                    .ifPresent(ovrd -> {
                        chargeAuditDTO.setChargeReasonCode(override.getOverrideReasonCode());
                        log.info("AUD_BFILL :: OVERRIDE :: " + chargeAuditDTO.getChargeReasonCode());
                        chargeAuditDTO.setDirectorOverrideId(override.getOverrideApproverId());
                    });
            chargeAuditDTO.setChargeReasonComment(override.getReasonComment());
            Optional.ofNullable(override.getDeviationReasonCode()).ifPresent(deviationCode -> {
                chargeAuditDTO.setChargeReasonCode(deviationCode);
                log.info("AUD_BFILL :: DEVIATION :: " + chargeAuditDTO.getChargeReasonCode());
            });
        });
    }

}

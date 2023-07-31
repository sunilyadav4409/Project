package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class CalcAmountUtil {

    private CalcAmountUtil() {

    }

    public static BigDecimal calculateTotal(List<ChargeDTO> chargeDTOList) {
        List<ChargeDTO> unRejectedCharges = chargeDTOList.stream()
                .filter(d -> !Objects.equals(d.getChargeDecisionCode(), CarrierPaymentConstants.REJECT_STATUS))
                .collect(Collectors.toList());
        // To calculate total based on TotalApprovedChargeAmount.
        BigDecimal totalAmounts = unRejectedCharges.stream()
                .filter(chargeDTO -> Objects.nonNull(chargeDTO.getTotalApprovedChargeAmount())).map(chargeDTO -> {
                    log.info("TOTAL AMOUNT :: " + chargeDTO.getTotalApprovedChargeAmount());
                    return chargeDTO.getTotalApprovedChargeAmount();
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("TOTAL AMOUNT : total charge amount :: " + totalAmounts);
        return totalAmounts;
    }

    public static BigDecimal calculateChargeAmountForAutoPay(Payment payment) {
        log.info("TOTAL AMOUNT : Before :: " + payment.getTotalChargeAmount());
        List<Charge> unActionedChargeList = Optional.ofNullable(payment.getCarrierPaymentChargeList())
                .map(chargeList -> chargeList.stream()
                        .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                        .filter(decision -> decision.getChargeDecisionCode() == null).collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        // To calculate total based on TotalApprovedChargeAmount.
        BigDecimal totalAmount = unActionedChargeList.stream()
                .filter(charge -> Objects.nonNull(charge.getTotalApprovedChargeAmount()))
                .map(charge -> charge.getTotalApprovedChargeAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("TOTAL AMOUNT : total charge amount for Autopay :: " + totalAmount);
        return totalAmount;
    }

    public static BigDecimal calculateApprovedAmount(List<Charge> chargeList) {
        log.info("calculateApprovedAmount: chargeList.size(): " + chargeList.size());
        List<Charge> approvedCharges = chargeList.stream()
                .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                .filter(charge -> (Objects.equals(charge.getChargeDecisionCode(), CarrierPaymentConstants.APPROVED)
                        || Objects.equals(charge.getChargeDecisionCode(), CarrierPaymentConstants.PAID)))
                .collect(Collectors.toList());
        log.info("Number of Approved And Paid charges : "+approvedCharges.size());
        // To calculate total based on TotalApprovedChargeAmount.
        BigDecimal totalAmount = approvedCharges.stream()
                .filter(charge -> Objects.nonNull(charge.getTotalApprovedChargeAmount()))
                .map(charge -> charge.getTotalApprovedChargeAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("TOTAL APPROVED AMOUNT : Calculated Approved Amount " + totalAmount);
        return totalAmount;
    }
}

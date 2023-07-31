package com.jbhunt.finance.carrierpayment.autopay.service.payment;


import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierPaymentChargeAuditDetail;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.Parameter;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.repository.CarrierPaymentChargeAuditDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.APPROVED;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutopayChargeStatusRules {

    private final CarrierPaymentChargeAuditDetailRepository carrierPaymentChargeAuditDetailRepository;
    private final AutopayValidationService autopayValidationService;
    private final AutoPayService autoPayService;

    public boolean isChargeTotalAmountIsTenPercentOrHundredDollars(List<Charge> charges) {

        List<CarrierPaymentChargeAuditDetail> carrierPaymentChargeAuditDetails = carrierPaymentChargeAuditDetailRepository.findByChargeIdIn(charges.stream().filter(charge -> "TRANSIT".equalsIgnoreCase(charge.getChargeCode())).map(Charge::getChargeId).collect(Collectors.toList()));

        Optional<CarrierPaymentChargeAuditDetail> assignedCarrierPaymentChargeAuditDetail = carrierPaymentChargeAuditDetails.stream().
                filter(auditCharge -> "ASSIGN".equalsIgnoreCase(auditCharge.getChargeCreationTimeTypeCode())
                && ("ADD".equalsIgnoreCase(auditCharge.getAuditReason().getAuditReasonTypeName()) || "UPDATE".equalsIgnoreCase(auditCharge.getAuditReason().getAuditReasonTypeName())))
                .sorted(Comparator.comparing(CarrierPaymentChargeAuditDetail::getCrtS).reversed()).findFirst();

        Optional<CarrierPaymentChargeAuditDetail> invoiceCarrierPaymentChargeAuditDetail = carrierPaymentChargeAuditDetails.stream().
                filter(auditCharge -> "INVOICE".equalsIgnoreCase(auditCharge.getChargeCreationTimeTypeCode())
                && ("ADD".equalsIgnoreCase(auditCharge.getAuditReason().getAuditReasonTypeName()) || "UPDATE".equalsIgnoreCase(auditCharge.getAuditReason().getAuditReasonTypeName())))
                .sorted(Comparator.comparing(CarrierPaymentChargeAuditDetail::getCrtS).reversed()).findFirst();


        if (assignedCarrierPaymentChargeAuditDetail.isPresent() && invoiceCarrierPaymentChargeAuditDetail.isPresent()) {
            BigDecimal latestAssignedCodeAmount = assignedCarrierPaymentChargeAuditDetail.get().getTotalChargeAmount();

            BigDecimal latestInvoiceCodeAmount = invoiceCarrierPaymentChargeAuditDetail.get().getTotalChargeAmount();

            if (latestAssignedCodeAmount != null && latestInvoiceCodeAmount != null) {
                List<Parameter> parameters = autopayValidationService.fetchParams("AAORGVSIVC", null);
                BigDecimal subtractedAmount = latestInvoiceCodeAmount.subtract(latestAssignedCodeAmount);
                if(CollectionUtils.isNotEmpty(parameters)) {
                    Parameter percentParameter = parameters.stream().filter(parameter -> "PERCENT".equalsIgnoreCase(parameter.getParameterCharacterValue())).findFirst().orElse(null);
                    Parameter amountParameter = parameters.stream().filter(parameter -> "AMOUNT".equalsIgnoreCase(parameter.getParameterCharacterValue())).findFirst().orElse(null);
                    if ((amountParameter != null && subtractedAmount.compareTo(amountParameter.getMinNumberValue()) >= 0) ||
                            (percentParameter != null && (subtractedAmount.multiply(BigDecimal.valueOf(100))).divide(latestAssignedCodeAmount).compareTo(percentParameter.getMinNumberValue()) >= 0)) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public boolean validateBookedAndInvoiceAmountsVarianceForLTL(Payment payment) {
        if (CarrierPaymentConstants.LTL.equalsIgnoreCase(payment.getGroupFlowTypeCode())) {
            List<Charge> actionedChargeList = payment.getCarrierPaymentChargeList().stream()
                            .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                            .filter(decision -> decision.getChargeDecisionCode() != null &&
                                    APPROVED.equalsIgnoreCase(decision.getChargeDecisionCode())).collect(Collectors.toList());

            List<Charge> unActionedChargeList = Optional.ofNullable(payment.getCarrierPaymentChargeList())
                    .map(chargeList -> chargeList.stream()
                            .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                            .filter(decision -> decision.getChargeDecisionCode() == null).collect(Collectors.toList()))
                    .orElse(new ArrayList<>());

            List<Charge> inActiveTransitList = Optional.ofNullable(payment.getCarrierPaymentChargeList())
                    .map(chargeList -> chargeList.stream()
                            .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() != null)
                            .filter(chargeCode -> "TRANSIT".equalsIgnoreCase(chargeCode.getChargeCode())).collect(Collectors.toList()))
                    .orElse(new ArrayList<>());
            unActionedChargeList.addAll(inActiveTransitList);

            if (actionedChargeList.isEmpty() && isChargeTotalAmountIsTenPercentOrHundredDollars(unActionedChargeList)) {
                autoPayService.logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_AMOUNT_VARIANCE);
                return true;
            }
        }
        return false;
    }
}



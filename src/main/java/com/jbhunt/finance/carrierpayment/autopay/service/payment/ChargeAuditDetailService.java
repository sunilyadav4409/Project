package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants;
import com.jbhunt.finance.carrierpayment.autopay.entity.AuditReasonType;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierPaymentChargeAuditDetail;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.repository.AuditReasonTypeRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.CarrierPaymentChargeAuditDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.*;
import static com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants.JBI;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargeAuditDetailService {
    private final CarrierPaymentChargeAuditDetailRepository carrierPaymentChargeAuditDetailRepository;
    private final AuditReasonTypeRepository auditReasonTypeRepository;


    public CarrierPaymentChargeAuditDetail saveCarrierPaymentChargeAuditDetail(Payment payment, Charge charge, String auditReason){
        AuditReasonType auditReasonType = null;
        if (StringUtils.isNotEmpty(auditReason)) {
            auditReasonType = getAuditReasonType(auditReason);
        } else if(StringUtils.isNotEmpty(charge.getChargeDecisionCode())){
            auditReasonType = getAuditReasonType(charge.getChargeDecisionCode());
        }
        if(null == auditReasonType || StringUtils.isEmpty(auditReasonType.getAuditReasonTypeName())){
            auditReasonType = getAuditReasonType(PaymentChargesConstants.UPDATE);
        }
        var carrierPaymentChargeAuditDetail = new CarrierPaymentChargeAuditDetail();
        carrierPaymentChargeAuditDetail.setCarrierPayment(payment);
        carrierPaymentChargeAuditDetail.setChargeCode(charge.getChargeCode());
        carrierPaymentChargeAuditDetail.setTotalChargeAmount(charge.getTotalApprovedChargeAmount());
        if(PaymentChargesConstants.ADD.equalsIgnoreCase(auditReasonType.getAuditReasonTypeName())){
            carrierPaymentChargeAuditDetail.setOriginalChargeIndicator('Y');
        } else {
            carrierPaymentChargeAuditDetail.setOriginalChargeIndicator('N');
        }

        carrierPaymentChargeAuditDetail.setStopNumber(charge.getStopNumber());
        carrierPaymentChargeAuditDetail.setAuditReason(auditReasonType);
        carrierPaymentChargeAuditDetail.setChargeCreationTimeTypeCode(chargeCreationTypeCodeValidation(payment));
        carrierPaymentChargeAuditDetail.setChargeId(charge.getChargeId());
        carrierPaymentChargeAuditDetailRepository.save(carrierPaymentChargeAuditDetail);
        return carrierPaymentChargeAuditDetail;
    }


    private AuditReasonType getAuditReasonType(String auditReason){
        return auditReasonTypeRepository.findByAuditReasonTypeName(auditReason);
    }

    public String chargeCreationTypeCodeValidation(Payment payment){
        String chargeType = null;
        var processedInvoice = false;
        if(payment.getGroupFlowTypeCode().equalsIgnoreCase(JBI)){
            //invoices validating if any processed
            if (Objects.nonNull(payment.getCarrierInvoiceHeaderList()) && payment.getCarrierInvoiceHeaderList().stream().anyMatch(u -> INVOICE_STATUS_PROCESSED.equalsIgnoreCase(u.getInvoiceStatusCode()))) {
                processedInvoice = true;
            }
        }
        if (processedInvoice) {
            chargeType = AFTER_APPROVE;
        } else {
            chargeType = AFTER_INVOICE;
        }

        return chargeType;
    }
}

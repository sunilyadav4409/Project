package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class ChargeEntityUtil {

    private ChargeEntityUtil() {

    }

    public static void updateEachCharge(ChargeDTO chargeDTO, Charge charge) {
        String actionedUser = Optional.ofNullable(chargeDTO).filter(ChargeDTO::isAutoPay)
                .map(x -> CarrierPaymentConstants.SYSTEM)
                .orElseGet(() -> UserUtil.getLoggedInUser());
        log.info("updateEachCharge: isAutoPay :: " + chargeDTO.isAutoPay());
        log.info("updateEachCharge: ACTIONED USER :: " + actionedUser);
        charge.setChargeDecisionPersonId(actionedUser);
        charge.setChargeDecisionCode(chargeDTO.getChargeDecisionCode());
        charge.setChargeDecisionDate(LocalDateTime.now());
        log.info("QUICKPAY WAIVER INDICATOR in ChargeEntitiyUtil File from Database is :: " + charge.getQuickPayWaiverIndicator());
        if(null!=chargeDTO.getQuickPayWaiverIndicator() && chargeDTO.getQuickPayWaiverIndicator().equals( 'Y' )) {
            log.info("QUICKPAY WAIVER INDICATOR in ChargeEntitiyUtil File from Payments UI is :: " + chargeDTO.getQuickPayWaiverIndicator());
            charge.setQuickPayWaiverIndicator( chargeDTO.getQuickPayWaiverIndicator() );
        }
        else{
            charge.setQuickPayWaiverIndicator(charge.getQuickPayWaiverIndicator());
        }


        // Approve
        Optional.ofNullable(chargeDTO.getChargeDecisionCode()).filter(CarrierPaymentConstants.APPROVED::equals)
                .ifPresent(approve -> setApproveDetails(chargeDTO, charge));
        // Reject
        Optional.ofNullable(chargeDTO.getChargeDecisionCode()).filter(CarrierPaymentConstants.REJECT_STATUS::equals)
                .ifPresent(reject -> setRejectDetails(chargeDTO, charge));
    }

    private static void setApproveDetails(ChargeDTO chargeDTO, Charge charge) {
        charge.setHeaderId(chargeDTO.getHeaderId());
        charge.setInvoiceNumberSuffix(chargeDTO.getInvoiceNumberSuffix());
        // Saving the Full Invoice Number Along with Suffix if exists
        Optional.ofNullable(chargeDTO.getInvoiceNumber()).ifPresent(invNum -> {
            charge.setCarrierInvoiceNumberExtended(invNum);
            charge.setEstimatedDueDate(chargeDTO.getEstimatedDueDate());
            charge.setCarrierPaymentWorkflowStatusTypeCode(chargeDTO.getWorkFlowStatus());
            log.info("ChargeDecisionService: INVOICE NUMBER EXTENDED :: " + charge.getCarrierInvoiceNumberExtended());
        });
    }

    private static void setRejectDetails(ChargeDTO chargeDTO, Charge charge) {
        charge.setReasonCode(chargeDTO.getReasonCode());
        charge.setRejectReasonComment(chargeDTO.getRejectReasonComment());
    }

}

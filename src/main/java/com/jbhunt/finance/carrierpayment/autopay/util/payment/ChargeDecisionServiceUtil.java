package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class ChargeDecisionServiceUtil {

    private ChargeDecisionServiceUtil() {

    }

    public static String getActionedUser(ChargeDTO chargeDTO) {
        String actionedUser = Optional.ofNullable(chargeDTO).filter(ChargeDTO::isAutoPay)
                .map(x -> CarrierPaymentConstants.SYSTEM).orElse(UserUtil.getLoggedInUser());
        log.info("LOCK: isAutoPay :: " + chargeDTO.isAutoPay());
        log.info("LOCK: ACTIONED USER :: " + actionedUser);
        return actionedUser;
    }

    public static void integrationStatus(TxnStatusDTO txnStatusDTO, TxnStatusDTO statusDTO) {
        Optional.ofNullable(statusDTO.isSuccess()).filter(isTrue -> isTrue)
                .ifPresent(updateSuccess -> txnStatusDTO.setSuccess(true));
        Optional.ofNullable(statusDTO.isSuccess()).filter(isTrue -> !isTrue).ifPresent(updateFail -> {
            txnStatusDTO.setSuccess(false);
            log.error("DB2 - Approve Update Fail");
        });
    }

}

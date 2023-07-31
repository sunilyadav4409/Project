package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants;
import com.jbhunt.finance.carrierpayment.autopay.dao.ChargeDAO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeTokenDTO;
import com.jbhunt.infrastructure.exception.JBHValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
@Service
public class ChargeIntegrationHelper {

    protected AtomicBoolean updateCharge(ChargeDTO chargeDTO, ChargeTokenDTO responseChargeTokenDTO, ChargeDAO chargeRepo) {
        log.info("INSIDE UPDATE CHARGE: TOKEN: " + chargeDTO.getExternalChargeID());

        Integer count = chargeRepo.getExistingChargeCount(chargeDTO);
        AtomicBoolean status = new AtomicBoolean(false);
        log.info("EXISTING CHARGE COUNT :: " + count);

        if (Objects.equals(count, 1)) {
            log.info("UPDATING CHARGE: " + chargeDTO.getChargeCode());
            ChargeTokenDTO updateTokenDTO=null;
            if(chargeDTO.getExternalChargeBillingID()!=null && !"LTL".equalsIgnoreCase(chargeDTO.getWorkFlowGroupType())){
                updateTokenDTO =chargeRepo.updateCustCharge(chargeDTO);
            }else {
                responseChargeTokenDTO.setExternalChargeBillingID(chargeDTO.getExternalChargeBillingID());
                updateTokenDTO = chargeRepo.updateCharge(chargeDTO);
            }

            responseChargeTokenDTO.setTokenId(updateTokenDTO.getTokenId());
            log.info("updateCharge updateTokenDTO.getTokenId()" + updateTokenDTO.getTokenId());
            responseChargeTokenDTO.setIntegrationStatus(true);
            status.set(true);
            return status;
        } else {
            log.info("DB2 - Update Charge Exception ");
            log.error("Exception occured at ChargeIntegrationHelper::updateCharge.");
            responseChargeTokenDTO.setUpdateExternalChargeIdSuccess(false);
            throw new JBHValidationException(PaymentChargesConstants.UNABLE_TO_UPDATE);
        }

    }

    public String fetchCustomerChargeCode(String chargeCode) {
        return Optional.ofNullable(PaymentChargesConstants.CHARGE_LUMPLOAD)
                .filter(x -> Objects.equals(chargeCode, PaymentChargesConstants.CHARGE_LUMPLDPAY))
                .orElseGet(() -> Optional.ofNullable(PaymentChargesConstants.CHARGE_LUMPUNLD)
                        .filter(y -> Objects.equals(chargeCode, PaymentChargesConstants.CHARGE_LUMPULPAY))
                        .orElseGet(() -> Optional.ofNullable(PaymentChargesConstants.CHARGE_DRVRLOAD)
                                .filter(z -> Objects.equals(chargeCode, PaymentChargesConstants.CHARGE_DRVRLDPAY))
                                .orElseGet(() -> Optional
                                        .ofNullable(PaymentChargesConstants.CHARGE_DRVRUNLD).filter(z -> Objects
                                                .equals(chargeCode, PaymentChargesConstants.CHARGE_DRVRULPAY))
                                        .orElse(chargeCode))));
    }

}

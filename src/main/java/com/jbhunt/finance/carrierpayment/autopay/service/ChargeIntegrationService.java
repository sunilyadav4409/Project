package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.dao.ChargeDAO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeTokenDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
@Slf4j
@Service
public class ChargeIntegrationService extends ChargeIntegrationHelper {

    @Autowired
    private  ChargeDAO chargeRepo;

    /**
     * Method to approve charges
     *
     * @param chargeDTO
     * @return
     */
    @Transactional
    public Boolean approveCharges(ChargeDTO chargeDTO) {
        log.info("ChargeIntegrationService : approve the charges");
        return chargeRepo.updateApproveDetails(chargeDTO);
    }

    @Transactional
    public Boolean addAndeditcharge(List<ChargeDTO> chargeDTOs) {
        ChargeTokenDTO responseChargeTokenDTO = new ChargeTokenDTO();
        log.info("ChargeIntegrationService : add and update charge");

        AtomicBoolean count =new AtomicBoolean(false);

        chargeDTOs.forEach(chargeDTO-> {
            // update existing charge
            Optional.ofNullable(chargeDTO).filter(dto -> Objects.nonNull(dto.getExternalChargeID()))
                    .filter(dto -> Objects.nonNull(dto.getExternalChargeID())).ifPresent(updateCharge -> {
                log.info("UPDATE TO DB2");
                updateCharge(chargeDTO, responseChargeTokenDTO, chargeRepo);
            });
        });
        count.set(true);
        return count.get();
    }

    public  Map<Integer,BigDecimal> validateCharges(ChargeDTO chargeDTO) {
        log.info("ChargeIntegrationService : Validating the charges");

        return chargeRepo.validatDetails(chargeDTO);
    }
    public BigDecimal validateTotalAmount(ChargeDTO chargedto) {
        log.info("ChargeIntegrationService : Validate the charges");
        return   chargeRepo.validateTotalAmount(chargedto);

    }

    // Auto Pay Archived load changes Start
    public int getCountOfRecords(String loadNumber) {
        return chargeRepo.getCountFromTOrderByLoadNumber(loadNumber);
    }
    // Auto Pay Archived load changes End
}

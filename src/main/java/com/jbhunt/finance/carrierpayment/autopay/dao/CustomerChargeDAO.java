package com.jbhunt.finance.carrierpayment.autopay.dao;

import com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeTokenDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.CustomerChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.util.PaymentIntegrationPredicateUtil;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class CustomerChargeDAO {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    @Qualifier("sqlConfigMapper")
    private Properties sqlProperties;


    public List<CustomerChargeDTO> getCustomerChargesWithExpenseRowAsN(String loadNumber, String chargeCode, Integer orderID) {
        String sql = sqlProperties.getProperty("GetExpNRowsForBillableCharges");
        log.info("QUERY FOR getting Exp_F = N IS :" + sql);
        Map<String, Object> paramMap = customerChargeValidation(loadNumber, chargeCode);
        paramMap.put(PaymentChargesConstants.PARAM_ORDER_ID, orderID);

        log.info("Charge Amount in loadNumber  :" + loadNumber + ":: chargeCode ::" + chargeCode);
        return namedParameterJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<>(CustomerChargeDTO.class));
    }

    private Map<String, Object> customerChargeValidation(String loadNumber, String chargeCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(PaymentChargesConstants.PARAM_LOAD_NUMBER, loadNumber);
        if (PaymentChargesConstants.CHARGE_LUMPLDPAY.equalsIgnoreCase(chargeCode)) {
            paramMap.put(PaymentChargesConstants.PARAM_CHARGE_CODE, PaymentChargesConstants.CHARGE_LUMPLOAD);
        } else if (PaymentChargesConstants.CHARGE_LUMPULPAY.equalsIgnoreCase(chargeCode)) {
            paramMap.put(PaymentChargesConstants.PARAM_CHARGE_CODE, PaymentChargesConstants.CHARGE_LUMPUNLD);
        } else if (PaymentChargesConstants.CHARGE_DRVRLDPAY.equalsIgnoreCase(chargeCode)) {
            paramMap.put(PaymentChargesConstants.PARAM_CHARGE_CODE, PaymentChargesConstants.CHARGE_DRVRLOAD);
        } else if (PaymentChargesConstants.CHARGE_DRVRULPAY.equalsIgnoreCase(chargeCode)) {
            paramMap.put(PaymentChargesConstants.PARAM_CHARGE_CODE, PaymentChargesConstants.CHARGE_DRVRUNLD);
        } else {
            paramMap.put(PaymentChargesConstants.PARAM_CHARGE_CODE, chargeCode);
        }

        return paramMap;
    }

    public Boolean insertCustomerChargekeyHolder(Charge charge, Integer orderId, String customerChargeCode, String scacCode, Integer stopNumber) {
        ChargeTokenDTO chargeTokenDTO = new ChargeTokenDTO();
        AtomicBoolean status = new AtomicBoolean(false);
        String sql = sqlProperties.getProperty("addChargeKey");
        log.info("QUERY FOR addCharge IS insertCustomerChargekeyHolder:::--" + sql);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        buildCommonParamMapWithToken(charge, orderId, customerChargeCode, namedParameters, stopNumber);
        namedParameters.addValue(PaymentChargesConstants.PARAM_CHARGE_QUANTITY, charge.getChargeQuantity());
        namedParameters.addValue(PaymentChargesConstants.PARAM_REF_NUMBER,
                PaymentIntegrationPredicateUtil.STRING_DATA.apply(charge.getReferenceNumberValue()));
        namedParameters.addValue(PaymentChargesConstants.PARAM_AMOUNT, charge.getTotalApprovedChargeAmount());
        namedParameters.addValue(PaymentChargesConstants.PARAM_SCAC_CODE, scacCode);

        int insertedCount = namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder,
                new String[]{PaymentChargesConstants.ORD_CHR_I});
        log.info("addCharge insertedCount :" + insertedCount);

        Optional.of(insertedCount).filter(count -> PaymentIntegrationPredicateUtil.IS_EQUAL.test(1, count))
                .ifPresent(inseted -> {
                    status.set(true);
                    Optional.ofNullable(keyHolder).filter(holder -> Objects.nonNull(holder.getKey()))
                            .map(holder -> Integer.valueOf(holder.getKey().intValue())).ifPresent(tokenId -> {
                        chargeTokenDTO.setExternalChargeBillingID(tokenId);
                        charge.setExternalChargeBillingID(tokenId);
                        log.info("insertCustomerChargekeyHolder INSERTED TOKEN ID :" + tokenId);
                    });
                });

        chargeTokenDTO.setIntegrationStatus(status.get());
        return chargeTokenDTO.getIntegrationStatus();
    }

    private void buildCommonParamMapWithToken(Charge charge, Integer orderId, String chargeCode,
                                              MapSqlParameterSource namedParameters, Integer stopNumber) {
        namedParameters.addValue(PaymentChargesConstants.PARAM_ORDER_ID, orderId);
        namedParameters.addValue(PaymentChargesConstants.PARAM_CHARGE_CODE, StringUtils.trim(chargeCode));
        namedParameters.addValue(PaymentChargesConstants.PARAM_STOP_NUMBER, PaymentIntegrationPredicateUtil.STOP_VALUE.apply(charge.getStopNumber()));
        if(stopNumber != null) {
            namedParameters.addValue(PaymentChargesConstants.PARAM_STOP_NUMBER, PaymentIntegrationPredicateUtil.STOP_VALUE.apply(stopNumber));
        }
        namedParameters.addValue(PaymentChargesConstants.PARAM_USER_ID,
                UserUtil.getLoggedInUser());
    }

}

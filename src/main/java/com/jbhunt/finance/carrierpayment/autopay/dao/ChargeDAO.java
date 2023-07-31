package com.jbhunt.finance.carrierpayment.autopay.dao;

import com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeTokenDTO;
import com.jbhunt.finance.carrierpayment.autopay.util.PaymentIntegrationPredicateUtil;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ChargeDAO {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    @Qualifier("sqlConfigMapper")
    private Properties sqlProperties;
    @Autowired
    private ChargeAuditDAO chargeAuditRepo;

    public Boolean insertCustomerChargekeyHolder(ChargeDTO chargeDTO) {
        ChargeTokenDTO chargeTokenDTO = new ChargeTokenDTO();
        AtomicBoolean status = new AtomicBoolean(false);
        String sql = sqlProperties.getProperty("addChargeKey");
        log.info("QUERY FOR addCharge IS insertCustomerChargekeyHolder:::--" + sql);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        buildCommonParamMapWithToken(chargeDTO, chargeDTO.getCustomerChargeCode(), namedParameters);
        namedParameters.addValue(PaymentChargesConstants.PARAM_CHARGE_QUANTITY, chargeDTO.getChargeQuantity());
        namedParameters.addValue(PaymentChargesConstants.PARAM_REF_NUMBER,
                PaymentIntegrationPredicateUtil.STRING_DATA.apply(chargeDTO.getReferenceNumberValue()));
        namedParameters.addValue(PaymentChargesConstants.PARAM_AMOUNT, chargeDTO.getTotalChargeAmount());
        namedParameters.addValue(PaymentChargesConstants.PARAM_SCAC_CODE, chargeDTO.getScacCode());

        int insertedCount = namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder,
                new String[]{PaymentChargesConstants.ORD_CHR_I});
        log.info("addCharge insertedCount :" + insertedCount);

        Optional.of(insertedCount).filter(count -> PaymentIntegrationPredicateUtil.IS_EQUAL.test(1, count))
                .ifPresent(inseted -> {
                    status.set(true);
                    Optional.ofNullable(keyHolder).filter(holder -> Objects.nonNull(holder.getKey()))
                            .map(holder -> Integer.valueOf(holder.getKey().intValue())).ifPresent(tokenId -> {
                                chargeTokenDTO.setExternalChargeBillingID(tokenId);
                                chargeDTO.setExternalChargeBillingID(tokenId);
                                log.info("insertCustomerChargekeyHolder INSERTED TOKEN ID :" + tokenId);
                            });
                });

        chargeTokenDTO.setIntegrationStatus(status.get());
        return chargeTokenDTO.getIntegrationStatus();
    }


    /**
     * Update customer charges
     *
     * @param chargeDTO
     * @param
     * @return
     */
    public ChargeTokenDTO updateCustCharge(ChargeDTO chargeDTO) {
        ChargeTokenDTO chargeTokenDTO = new ChargeTokenDTO();
        AtomicBoolean status = new AtomicBoolean(false);
        String sql = sqlProperties.getProperty("updateCustCharge");
        log.info("QUERY FOR updateCharge IS :" + sql);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(PaymentChargesConstants.PARAM_USER_ID,
                UserUtil.getLoggedInUser());
        paramMap.put(PaymentChargesConstants.PARAM_CHARGE_QUANTITY, chargeDTO.getChargeQuantity());
        paramMap.put(PaymentChargesConstants.PARAM_REFERENCE_TYPE,
                PaymentIntegrationPredicateUtil.STRING_DATA.apply(chargeDTO.getReferenceNumberTypeCode()));
        paramMap.put(PaymentChargesConstants.PARAM_REF_NUMBER,
                PaymentIntegrationPredicateUtil.STRING_DATA.apply(chargeDTO.getReferenceNumberValue()));
        ArrayList externalids = new ArrayList();
        externalids.add(chargeDTO.getExternalChargeID());
        externalids.add(chargeDTO.getExternalChargeBillingID());
        paramMap.put(PaymentChargesConstants.PARAM_EXT_CHARGE_ID,
                externalids);
        if (chargeDTO.getChargeOverride() != null) {
            paramMap.put(PaymentChargesConstants.PARAM_AMOUNT, chargeDTO.getChargeOverride().getOverrideAmount());
        } else {
            paramMap.put(PaymentChargesConstants.PARAM_AMOUNT,
                    PaymentIntegrationPredicateUtil.DECIMAL_DATA.apply(chargeDTO.getTotalChargeAmount()));
        }
        int updatedCount = namedParameterJdbcTemplate.update(sql, paramMap);
        log.info("updateCharge updatedCount :" + updatedCount);

        Optional.of(updatedCount).filter(count -> count > 0).ifPresent(updated -> {
            status.set(true);
            chargeTokenDTO.setTokenId(chargeDTO.getExternalChargeID());
            log.info("updateCharge UPDATED TOKEN ID :" + chargeDTO.getExternalChargeID());
        });

        chargeTokenDTO.setIntegrationStatus(status.get());
        return chargeTokenDTO;
    }

    /**
     * Update carrier charges
     *
     * @param chargeDTO
     * @param
     * @return
     */
    public ChargeTokenDTO updateCharge(ChargeDTO chargeDTO) {
        ChargeTokenDTO chargeTokenDTO = new ChargeTokenDTO();
        AtomicBoolean status = new AtomicBoolean(false);
        String sql = sqlProperties.getProperty("updateCharge");
        log.info("QUERY FOR updateCharge IS :" + sql);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(PaymentChargesConstants.PARAM_USER_ID,
                UserUtil.getLoggedInUser());
        paramMap.put(PaymentChargesConstants.PARAM_CHARGE_QUANTITY, chargeDTO.getChargeQuantity());
        paramMap.put(PaymentChargesConstants.PARAM_REFERENCE_TYPE,
                PaymentIntegrationPredicateUtil.STRING_DATA.apply(chargeDTO.getReferenceNumberTypeCode()));
        paramMap.put(PaymentChargesConstants.PARAM_REF_NUMBER,
                PaymentIntegrationPredicateUtil.STRING_DATA.apply(chargeDTO.getReferenceNumberValue()));
        paramMap.put(PaymentChargesConstants.PARAM_EXT_CHARGE_ID, chargeDTO.getExternalChargeID());

        if (chargeDTO.getChargeOverride() != null) {
            paramMap.put(PaymentChargesConstants.PARAM_AMOUNT, chargeDTO.getChargeOverride().getOverrideAmount());
        } else {
            paramMap.put(PaymentChargesConstants.PARAM_AMOUNT,
                    PaymentIntegrationPredicateUtil.DECIMAL_DATA.apply(chargeDTO.getTotalChargeAmount()));
        }
        int updatedCount = namedParameterJdbcTemplate.update(sql, paramMap);
        log.info("updateCharge updatedCount :" + updatedCount);

        Optional.of(updatedCount).filter(count -> count > 0).ifPresent(updated -> {
            status.set(true);
            chargeTokenDTO.setTokenId(chargeDTO.getExternalChargeID());
            log.info("updateCharge UPDATED TOKEN ID :" + chargeDTO.getExternalChargeID());
        });

        chargeTokenDTO.setIntegrationStatus(status.get());
        return chargeTokenDTO;
    }


    /**
     * Approve only carrier charges
     *
     * @param chargeDTO
     * @return
     */
    public Boolean updateApproveDetails(ChargeDTO chargeDTO) {
        log.info("updateApproveDetails");
        AtomicBoolean status = new AtomicBoolean(false);
        String sql = sqlProperties.getProperty("approveCharges");
        log.info("QUERY FOR approveCharges IS :" + sql);

        LocalDateTime invoiceDate = Optional.ofNullable(chargeDTO.getInvoiceDate()).map(LocalDateTime::parse)
                .orElse(null);

        LocalDateTime invoiceRecieveDate = chargeDTO.getInvoiceReceivedTermsDate();

        if (invoiceRecieveDate == null) {
            invoiceRecieveDate = invoiceDate;
        }

        Date date = Optional.ofNullable(invoiceDate)
                .map(toDate -> Date.from(toDate.atZone(ZoneId.systemDefault()).toInstant())).orElse(null);

        Date recieveDate = Optional.ofNullable(invoiceRecieveDate)
                .map(toDate -> Date.from(toDate.atZone(ZoneId.systemDefault()).toInstant())).orElse(null);

        AtomicInteger updatedCount = new AtomicInteger(0);
        chargeDTO.getExternalChargeIDList().forEach(eachApprovedTokenId -> {
            log.info("ApprovedToken:: " + eachApprovedTokenId);
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(PaymentChargesConstants.PARAM_INVOICE_NUMBER,
                    PaymentIntegrationPredicateUtil.STRING_DATA.apply(chargeDTO.getInvoiceNumber()));
            paramMap.put(PaymentChargesConstants.PARAM_INVOICE_DATE, date);
            paramMap.put(PaymentChargesConstants.PARAM_INVOICE_RECIEVE_DATE, recieveDate);
            String ltlApprove = Optional.ofNullable(chargeDTO.getWorkFlowGroupType())
                    .filter(PaymentChargesConstants.LTL::equalsIgnoreCase)
                    .map(ltl -> PaymentChargesConstants.CHAR_Y).orElseGet(() -> PaymentChargesConstants.NOT);
            paramMap.put(PaymentChargesConstants.PARAM_LTL_FLAG, ltlApprove);
            paramMap.put(PaymentChargesConstants.PARAM_USER_ID,
                    UserUtil.getLoggedInUser());
            paramMap.put(PaymentChargesConstants.PARAM_EXT_CHARGE_ID, eachApprovedTokenId);
            updatedCount.addAndGet(namedParameterJdbcTemplate.update(sql, paramMap));
            status.set(true);
        });
        int externalChargeIdSize = chargeDTO.getExternalChargeIDList().size();
        int externalChargeIdUpdatedSize = updatedCount.get();
        log.info("Approve :: " + "Initial Count :: " + externalChargeIdSize);
        log.info("Approve :: Updated Count :: " + externalChargeIdUpdatedSize);

        if (externalChargeIdSize == externalChargeIdUpdatedSize) {
            // AUDIT FOR AMOUNT MISMATCHED AND APPROVED CHARGES
            chargeAuditRepo.createAuditRecordOnApprove(chargeDTO, date);
        } else {
            log.error("Exception occured at ChargeDAO::updateApproveDetails- Approve charges count Mismatch. " + PaymentChargesConstants.UNABLE_TO_APPROVE);
        }

        return status.get();
    }

    public Integer getExistingChargeCount(ChargeDTO chargeDTO) {
        String sql = sqlProperties.getProperty("getExistingChargeCount");
        log.info("QUERY FOR getExistingChargeCount IS :" + sql);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(PaymentChargesConstants.PARAM_EXT_CHARGE_ID, chargeDTO.getExternalChargeID());
        Integer chargeCount = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
        log.info("getExistingChargeCount :: " + chargeCount);

        return chargeCount;
    }

    private void buildCommonParamMapWithToken(ChargeDTO chargeDTO, String chargeCode,
                                              MapSqlParameterSource namedParameters) {
        namedParameters.addValue(PaymentChargesConstants.PARAM_ORDER_ID, chargeDTO.getOrderID());
        namedParameters.addValue(PaymentChargesConstants.PARAM_DISPATCH_NUMBER,
                Integer.valueOf(chargeDTO.getDispatchNumber()));
        namedParameters.addValue(PaymentChargesConstants.PARAM_CHARGE_CODE, StringUtils.trim(chargeCode));
        namedParameters.addValue(PaymentChargesConstants.PARAM_STOP_NUMBER,
                PaymentIntegrationPredicateUtil.STOP_VALUE.apply(chargeDTO.getStopNumber()));
        namedParameters.addValue(PaymentChargesConstants.PARAM_USER_ID,
                UserUtil.getLoggedInUser());
    }

    public Map<Integer, BigDecimal> validatDetails(ChargeDTO chargeDTO) {
        String sql = sqlProperties.getProperty("validateCharges");
        log.info("QUERY FOR validation IS :" + sql);
        Map<Integer, BigDecimal> amountMap = new HashMap<>();

        chargeDTO.getChargeCodeList().forEach((eachRejectedTokenId,value)->{
            log.info("Charge Token" + eachRejectedTokenId);
            Map<String, Object> paramMap = new HashMap<>();

            paramMap.put(PaymentChargesConstants.PARAM_EXT_CHARGE_ID, eachRejectedTokenId);
            paramMap.put(PaymentChargesConstants.PARAM_DISPATCH_NUMBER, chargeDTO.getDispatchNumber());
            paramMap.put(PaymentChargesConstants.PARAM_SCAC_CODE, chargeDTO.getScacCode());
            paramMap.put(PaymentChargesConstants.PARAM_ORDER_ID, chargeDTO.getOrderID());
            paramMap.put(PaymentChargesConstants.PARAM_CHARGE_CODE,value);
            try {
                BigDecimal chargeAmount = namedParameterJdbcTemplate.queryForObject(sql, paramMap, BigDecimal.class);
                log.info("Charge Amount for  each Token Id  :" + chargeAmount + "::" + eachRejectedTokenId);
                amountMap.put(eachRejectedTokenId, chargeAmount);
            } catch (Exception e) {
                log.error("Exception in Charge Amount for  validatDetails " + e.getMessage());
            }

        });

        return amountMap;

    }

    //END

    public BigDecimal validateTotalAmount(ChargeDTO chargeDTO) {
        String sql = sqlProperties.getProperty("totalamountvalidate");
        log.info("QUERY FOR validation IS :" + sql);
        Map<String, Object> paramMap = new HashMap<>();
        BigDecimal chargeAmount;
        paramMap.put(PaymentChargesConstants.PARAM_SCAC_CODE, chargeDTO.getScacCode());
        paramMap.put(PaymentChargesConstants.PARAM_DISPATCH_NUMBER, chargeDTO.getDispatchNumber());
        paramMap.put(PaymentChargesConstants.PARAM_ORDER_ID, chargeDTO.getOrderID());

        log.info("Charge Amount for  chargeDTO.getLoadNumber()  :" + chargeDTO.getLoadNumber() + "::chargeDTO.getScacCode() ::" + chargeDTO.getScacCode() + "::chargeDTO.getDispatchNumber() ::" + chargeDTO.getDispatchNumber());
        chargeAmount = namedParameterJdbcTemplate.queryForObject(sql, paramMap, BigDecimal.class);
        log.info("Charge Amount for  eachRejectedTokenId :" + chargeAmount + "::");
        return chargeAmount;

    }




    public int updateExpenceRowN(Integer externalID) {
        String sql = sqlProperties.getProperty("InActExpNRow");
        log.info("QUERY FOR getting updateExpenceRowN  IS :" + sql);
        Map<String, Object> paramMap = new HashMap<>();
        int externalIDcount = 0;
        paramMap.put(PaymentChargesConstants.PARAM_EXT_CHARGE_ID, externalID);
        log.info("Charge Amount for  chargeDTO.getLoadNumber()  :" + externalID);
        externalIDcount = namedParameterJdbcTemplate.update(sql, paramMap);
        log.info("Charge Amount for  eachRejectedTokenId externalIDcount :" + externalIDcount + "::");
        return externalIDcount;

    }

    // Auto Pay Archived load changes Start
    public int getCountFromTOrderByLoadNumber(String loadNumber) {
        String sql = sqlProperties.getProperty("getCountFromTOrderByLoadNumber");
        int count = 0;
        log.info("QUERY FOR getCountFromTOrderByLoadNumber IS : getCountFromTOrderByLoadNumber :: " + sql);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(PaymentChargesConstants.PARAM_LOAD_NUMBER, loadNumber);
        count = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
        if (count > 0) {
            return count;
        } else {
            return getCountFromTOrderWHByLoadNumber(paramMap);
        }
    }

    private int getCountFromTOrderWHByLoadNumber(Map<String, Object> paramMap) {
        String sql = sqlProperties.getProperty("getCountFromTOrderWByLoadNumber");
        int countW = 0;
        log.info("QUERY FOR getCountFromTOrderWByLoadNumber IS : getCountFromTOrderWByLoadNumber :: " + sql);
        countW = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
        return countW;
    }
    // Auto Pay Archived load changes

}

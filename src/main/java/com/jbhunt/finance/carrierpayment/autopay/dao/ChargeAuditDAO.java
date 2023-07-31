package com.jbhunt.finance.carrierpayment.autopay.dao;

import com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeAuditDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.util.LoadDivisionUtil;
import com.jbhunt.finance.carrierpayment.autopay.util.PaymentIntegrationPredicateUtil;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ChargeAuditDAO {

    @Autowired
    private  NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    @Qualifier("sqlConfigMapper")
    private  Properties sqlProperties;

    public void createAuditRecordOnApprove(ChargeDTO chargeDTO, Date date) {
        String sql = sqlProperties.getProperty("insertAuditRecord");
        log.info("QUERY FOR insertAuditRecord IS :" + sql);
        log.info("createAuditRecordOnApprove: MISMATCH Charges count: " + chargeDTO.getChargeAuditDTOs().size());
        Optional.ofNullable(chargeDTO).filter(dto -> !dto.getChargeAuditDTOs().isEmpty()).ifPresent(mismatched -> {
            log.info("CHARGES WITH MISMATCHED AMOUNT PRESENT - CREATING AUDIT");
            AtomicInteger updatedCount = new AtomicInteger(0);

            chargeDTO.getChargeAuditDTOs().forEach(auditDTO -> {
                log.info("Approved Charge:: " + auditDTO.getChargeCode());
                log.info("Approved Charge Amount:: " + auditDTO.getChargeAmount());
                Map<String, Object> paramMap = buildParams(chargeDTO, date, auditDTO);
                updatedCount.addAndGet(namedParameterJdbcTemplate.update(sql, paramMap));
            });
            log.info("Approve/AutoApprove :: " + "Initial Count :: " + chargeDTO.getChargeAuditDTOs().size());
            log.info("Approve/AutoApprove :: Updated Count :: " + updatedCount.get());
        });

        Optional.ofNullable(chargeDTO).filter(dto -> dto.getChargeAuditDTOs().isEmpty())
                .ifPresent(noRecord -> log.info("NO MISMATCH CHARGES!!!"));
    }

    private Map<String, Object> buildParams(ChargeDTO chargeDTO, Date date, ChargeAuditDTO auditDTO) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(PaymentChargesConstants.PARAM_ORDER_ID, chargeDTO.getOrderID());
        paramMap.put(PaymentChargesConstants.PARAM_CHARGE_CODE, StringUtils.trim(auditDTO.getChargeCode()));
        paramMap.put(PaymentChargesConstants.PARAM_DISPATCH_NUMBER, Integer.valueOf(auditDTO.getDispatchNumber()));
        paramMap.put(PaymentChargesConstants.PARAM_JOB_ID, auditDTO.getJobId());
        paramMap.put(PaymentChargesConstants.PARAM_DIVISION_CODE,
                LoadDivisionUtil.getDivisionCode(chargeDTO.getWorkFlowGroupType()));
        paramMap.put(PaymentChargesConstants.PARAM_PRJ_CODE, auditDTO.getProjectCode());
        paramMap.put(PaymentChargesConstants.PARAM_SCAC_CODE, auditDTO.getScacCode());
        paramMap.put(PaymentChargesConstants.PARAM_INVOICE_NUMBER,
                PaymentIntegrationPredicateUtil.STRING_DATA.apply(auditDTO.getInvoiceNumber()));
        paramMap.put(PaymentChargesConstants.PARAM_VENDOR_AMOUNT, auditDTO.getVendorChargeAmount());
        paramMap.put(PaymentChargesConstants.PARAM_REASON_CODE, auditDTO.getChargeReasonCode());
        paramMap.put(PaymentChargesConstants.PARAM_AMOUNT,
                PaymentIntegrationPredicateUtil.DECIMAL_DATA.apply(auditDTO.getChargeAmount()));
        paramMap.put(PaymentChargesConstants.PARAM_USER_ID,
                UserUtil.getLoggedInUser());

        String truncatedComment = Optional.ofNullable(auditDTO.getChargeReasonComment())
                .map(comment -> comment.substring(0, Math.min(comment.length(), 100)))
                .orElse(auditDTO.getChargeReasonComment());
        log.info("Approve/AutoApprove :: buildParams :: Charge Comment " + truncatedComment);
        paramMap.put(PaymentChargesConstants.PARAM_REASON_COMMENT, truncatedComment);
        paramMap.put(PaymentChargesConstants.PARAM_DIRECTOR_ID, auditDTO.getDirectorOverrideId());
        paramMap.put(PaymentChargesConstants.PARAM_INVOICE_DATE, date);
        return paramMap;
    }

}

package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.Parameter;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.repository.ChargeRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.ParameterRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.SpecificationAssociationRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.ChargeIntegrationService;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargeValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.*;

@Slf4j
@Service
public class ChargeValidationService {

    @Autowired
    private ChargeRepository chargeRepository;
    @Autowired
    private InvoiceValidationAndUpdateService invoiceValidationAndUpdateService;
    @Autowired
    private ChargeIntegrationService chargeIntegrationService;
    @Autowired
    private ParameterRepository parameterRepository;
    @Autowired
    private SpecificationAssociationRepository specificationAssociationRepository;
    @Autowired
    private AutoPayService autoPayService;


    public Map<String, Boolean> approveRejectChargeValidations(ChargeDTO chargeDTO) {
        log.info("CHARGE SERVICE :: Inside approveRejectChargeValidations ");
        Map<String, Boolean> errorMap = new LinkedHashMap<>();
        Optional.ofNullable(chargeDTO).ifPresent(nonDecisionCharge -> {
            errorMap.put(CarrierPaymentConstants.ERR_031,
                    ChargeValidateUtil.validateInvoiceDate(chargeDTO.getInvoiceDate()));
            log.info(CarrierPaymentConstants.LOG_ERROR_MAP + errorMap);
        });
        return errorMap;
    }

    //Start
    public Map<String, Boolean> updateInvoiceDetails(ChargeDTO chargeDTO, Map<String, Boolean> errorMap) {

        Optional.ofNullable(chargeDTO).filter(values -> !values.getChargeIdList().isEmpty()).ifPresent(
                validDate -> invoiceValidationAndUpdateService.checkOrUpdateInvoiceNumber(chargeDTO, errorMap));
        return errorMap;
    }

    public ChargeDTO validateAmountforEachCharge(ChargeDTO chargeDTO, Map<String, Boolean> errorMap) {
        List<String> chargeCodeList = new ArrayList<>();

        List<Charge> chargeList = chargeRepository.findAllById(chargeDTO.getChargeIdList());

        // FOR DB2s
        chargeDTO.setExternalChargeIDList(chargeList.stream().map(Charge::getExternalChargeID).collect(Collectors.toList()));
        chargeDTO.setChargeCodeList(chargeList.stream().collect(Collectors.toMap(Charge::getExternalChargeID,Charge::getChargeCode)));


        Map<Integer, BigDecimal> amountMap = chargeIntegrationService.validateCharges(chargeDTO);
        for (int i = 0; i < chargeList.size(); i++) {
            chargeCodeList = amountValidation(chargeList.get(i), amountMap, chargeDTO, chargeCodeList);
        }
        if (!chargeCodeList.isEmpty()) {
            showError(chargeCodeList, chargeDTO, errorMap);
        }
        return chargeDTO;
    }

    private List<String> amountValidation(Charge charge, Map<Integer, BigDecimal> amountMap, ChargeDTO chargeDTO, List<String> chargeCodeList) {

        BigDecimal chrgAmt = charge.getTotalApprovedChargeAmount();

        BigDecimal amount = amountMap.get(charge.getExternalChargeID());
        try {

            if (amount == null || (chrgAmt.compareTo(amount) == 1) || (chrgAmt.compareTo(amount) == -1)) {
                chargeDTO.getChargeIdList().remove(charge.getChargeId());
                String chargeCode = charge.getChargeCode();
                chargeCodeList.add(chargeCode);
            }
            return chargeCodeList;
        } catch (Exception e) {
            log.error("CHARGE SERVICE :: Inside amountValidation " + e.getMessage());
        }
        return chargeCodeList;
    }

    private Map<String, Boolean> showError(List<String> chargeCodeList, ChargeDTO chargeDTO, Map<String, Boolean> errorMap) {
        String chargeName = chargeCodeList.toString();
        chargeName = chargeName.substring(1, chargeName.length() - 1);
        if (chargeDTO.getChargeDecisionCode().equals("Reject")) {
            errorMap.put(chargeName + " " + "-" + " " + CarrierPaymentConstants.ERR_035, false);
        } else if (chargeDTO.getChargeDecisionCode().equals("Approve")) {
            errorMap.put(chargeName + " " + "-" + " " + CarrierPaymentConstants.ERR_036, false);

        }
        return errorMap;
    }

    public BigDecimal validateTotalAmount(ChargeDTO chargeDTO) {
        return chargeIntegrationService.validateTotalAmount(chargeDTO);
    }

    public Map<String, Boolean> validateChargeCodes(ChargeDTO chargeDTO, Map<String, Boolean> errorMap, Payment payment) {
        if (APPROVE.equalsIgnoreCase(chargeDTO.getChargeDecisionCode())) {
            List<Charge> charges = chargeRepository.findAllById(chargeDTO.getChargeIdList());
            List<String> chargeCodes = charges.stream().map(Charge::getChargeCode).collect(Collectors.toList());
            List<Parameter> parameters = fetchParamsByBU(CHARGE_BY_BU, CHARGE_CODE
                    , chargeDTO.getWorkFlowGroupType());
            if (CollectionUtils.isNotEmpty(parameters)) {
                if (!chargeCodes.stream().allMatch(code -> parameters.stream().map(parameter -> parameter.getSpecificationSub())
                        .anyMatch(sub -> sub.equalsIgnoreCase(code)))) {

                    errorMap.put("- Autopay failure due to charge code(s) do not exist in Workday", false);
                    autoPayService.logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_CHRGNOTINWD);
                }
            } else {
                errorMap.put("parameter list is null. ", false);
                autoPayService.logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_PARMNOTFND);
            }
        }
        return errorMap;
    }

    public List<Parameter> fetchParamsByBU(String specification, String classification, String groupType) {
        var current = LocalDateTime.now();
        List<Parameter> parameterList = null;
        var paramSpecification = specificationAssociationRepository
                .findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(specification, classification,
                        SYSTEM_VALUE, current, current);
        if (null != paramSpecification) {
            log.info("Configuration Check :: paramSpecification ID : " + paramSpecification.getSpecificationAssociationID());
            parameterList =  parameterRepository
                    .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndWorkflowSpecificationAssociationWorkflowGroupTypeCodeAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                            paramSpecification.getSpecificationAssociationID(), groupType, current, current);
        }
        return parameterList;
    }
}

package com.jbhunt.finance.carrierpayment.autopay.service.payment;


import com.jbhunt.finance.carrierpayment.autopay.dao.ChargeDAO;
import com.jbhunt.finance.carrierpayment.autopay.dao.CustomerChargeDAO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.CustomerChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.repository.ChargeRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.ParameterRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.SpecificationAssociationRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.ChargeIntegrationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.APPROVE;
import static com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillableChargeService {

    private final CustomerChargeDAO customerChargeDAO;
    private final ChargeDAO chargeDAO;
    private final ChargeRepository chargeRepository;
    private final ParameterRepository parameterRepository;
    private final SpecificationAssociationRepository specificationAssociationRepository;
    private final ChargeIntegrationHelper chargeIntegrationHelper;

    public void checkApprovedChargesAreAccessorialsAndCreateCustomerCharge(ChargeDTO chargeDTO) {
        List<Charge> chargeList = chargeRepository.findAllById(chargeDTO.getChargeIdList());
        List<Charge> alreadyApprovedChargesWithSameChargeCode = chargeRepository.findByCarrierPaymentCarrierPaymentIdAndChargeDecisionCodeAndChargeApplyToCustomerIndicatorAndExpirationTimestampIsNullAndChargeCodeIn(
                chargeDTO.getPaymentId(),APPROVE, 'Y', chargeList.stream().map(Charge :: getChargeCode).collect(Collectors.toList()));
        chargeList.addAll(alreadyApprovedChargesWithSameChargeCode.stream().filter(charge -> !chargeDTO.getChargeIdList().contains(charge.getChargeId())).collect(Collectors.toList()));

        if(chargeList.stream().count() > 1) {
            handleWhenMultipleChargesAreApproved(chargeDTO, chargeList);
        } else {
            handleWhenSingleChangeIsApproved(chargeDTO, chargeList);
        }
    }

    public void handleWhenSingleChangeIsApproved(ChargeDTO chargeDTO, List<Charge> chargeList) {
        List<Charge> alreadyApprovedChargesWithSameChargeCode = chargeRepository.findByCarrierPaymentCarrierPaymentIdAndChargeDecisionCodeAndChargeApplyToCustomerIndicatorAndExpirationTimestampIsNullAndChargeCodeIn(
                chargeDTO.getPaymentId(),APPROVE, 'Y', chargeList.stream().map(Charge :: getChargeCode).collect(Collectors.toList()));
        if(CollectionUtils.isNotEmpty(alreadyApprovedChargesWithSameChargeCode)) {
            List<Charge> approvedCharges = alreadyApprovedChargesWithSameChargeCode.stream().filter(charge -> !chargeDTO.getChargeIdList().contains(charge.getChargeId())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(approvedCharges)) {
                alreadyApprovedSameChargeCode(chargeDTO, chargeList, approvedCharges);
                return;
            }
        }
        Charge charge = chargeList.get(0);
        if (charge.getChargeApplyToCustomerIndicator() != null && 'Y' == charge.getChargeApplyToCustomerIndicator()) {
            List<CustomerChargeDTO> customerChargeDTOS = customerChargeDAO.getCustomerChargesWithExpenseRowAsN(chargeDTO.getLoadNumber(), charge.getChargeCode(),chargeDTO.getOrderID());
            if (customerChargeDTOS.size() > 1) {
                inactivateExistingAndAddNewCustomerCharge(chargeDTO, customerChargeDTOS, charge);
            } else if (customerChargeDTOS.isEmpty()) {
                addCustomerChargeWhenNoPaymentParametersPresent(chargeDTO, charge);
            } else if (customerChargeDTOS.size() == 1) {
                addCustomerChargeWhenOnlyOneChargePresentInDB(chargeDTO, charge, customerChargeDTOS);
            }
        }
    }

    public void addCustomerChargeWhenOnlyOneChargePresentInDB(ChargeDTO chargeDTO, Charge charge, List<CustomerChargeDTO> customerChargeDTOS) {
        Integer specificationAssociationId = specificationAssociationRepository.findSpecificationAssociationIDForSpecificationChargeLevel(CHARGELEVL,
                CHARGE_CODE, BUSINESS);
        Integer count = parameterRepository.fetchPaymentParameterCount(charge.getChargeCode(), specificationAssociationId);
        if(count >= 1) {
            inactivateExistingAndAddNewCustomerChargesWhenStopNumberZeroEligible(chargeDTO, charge, customerChargeDTOS);
        } else {
            Integer countForStop = parameterRepository.fetchPaymentParameterCountForStop(charge.getChargeCode(), specificationAssociationId);
            if(countForStop >= 1) {
                handleWhenMoreChargesPresentInDBForSameChargeCode(chargeDTO, charge, customerChargeDTOS);
            } else {
                inactivateExistingAndAddNewCustomerCharge(chargeDTO, customerChargeDTOS, charge);
            }
        }
    }

    public void handleWhenMoreChargesPresentInDBForSameChargeCode(ChargeDTO chargeDTO, Charge charge, List<CustomerChargeDTO> customerChargeDTOS) {
        Optional<CustomerChargeDTO> optionalCustomerChargeDTO = customerChargeDTOS.stream().filter(chargeDto -> chargeDto.getStopNumber() == 0).findFirst();
        if(optionalCustomerChargeDTO.isPresent()) {
            chargeDAO.updateExpenceRowN(optionalCustomerChargeDTO.get().getExternalChargeId());
            setStopNumberAndAddCustCharge(chargeDTO, charge);
        } else if(customerChargeDTOS.stream().anyMatch(chargeDto -> chargeDto.getStopNumber() != 0)) {
            customerChargeDTOS.stream().filter(chargeDto -> chargeDto.getStopNumber() != 0
                    && charge.getTotalApprovedChargeAmount() != null && chargeDto.getTotalChargeAmount() != null &&
                    charge.getTotalApprovedChargeAmount().compareTo(chargeDto.getTotalChargeAmount()) != 0).findFirst().ifPresent(chargeDTO2 -> {
                chargeDAO.updateExpenceRowN(chargeDTO2.getExternalChargeId());
                addCustCharge(charge, chargeDTO.getScacCode(), chargeDTO2.getStopNumber() / 1000,chargeDTO.getOrderID());
            });
        }
    }


    public void addCustomerChargeWhenNoPaymentParametersPresent(ChargeDTO chargeDTO, Charge charge) {
        Integer specificationAssociationId = specificationAssociationRepository.findSpecificationAssociationIDForSpecificationChargeLevel(CHARGELEVL,
                CHARGE_CODE, BUSINESS);
        Integer count = parameterRepository.fetchPaymentParameterCount(charge.getChargeCode(), specificationAssociationId);
        if(count >= 1) {
            addCustCharge(charge, chargeDTO.getScacCode(), null,chargeDTO.getOrderID());
        } else {
            Integer countForStop = parameterRepository.fetchPaymentParameterCountForStop(charge.getChargeCode(), specificationAssociationId);
            if(countForStop >= 1) {
                setStopNumberAndAddCustCharge(chargeDTO, charge);
            } else {
                addCustCharge(charge, chargeDTO.getScacCode(), null,chargeDTO.getOrderID());
            }
        }
    }

    private void setStopNumberAndAddCustCharge(ChargeDTO chargeDTO, Charge charge) {
        if (charge.getStopNumber() == 0) {
            addCustCharge(charge, chargeDTO.getScacCode(), 1,chargeDTO.getOrderID());
        } else {
            addCustCharge(charge, chargeDTO.getScacCode(), null,chargeDTO.getOrderID());
        }
    }

    private void handleWhenMultipleChargesAreApproved(ChargeDTO chargeDTO, List<Charge> chargeList) {
        List<Charge> chargesWithApplyToCustomerChargeFlagIsYes = chargeList.stream().filter(charge -> charge.getChargeApplyToCustomerIndicator()!= null &&
                'Y' == charge.getChargeApplyToCustomerIndicator()).collect(Collectors.toList());
        Map<String, List<Charge>> chargeCodeMap = chargesWithApplyToCustomerChargeFlagIsYes.stream().collect(Collectors.groupingBy(Charge::getChargeCode));
        for(String chargeCode : chargeCodeMap.keySet()) {
            List<CustomerChargeDTO> customerChargeDTOS = customerChargeDAO.getCustomerChargesWithExpenseRowAsN(chargeDTO.getLoadNumber(), chargeCode, chargeDTO.getOrderID());
            List<Charge> chargesWithSameChargeCode = chargeCodeMap.get(chargeCode);
            if(chargesWithSameChargeCode.size() > 1) {
                handleMultipleChargesWithSameChargeCode(chargeDTO, customerChargeDTOS, chargesWithSameChargeCode, true);
            } else {
                handleUniqueChargeCodeScenario(chargeDTO,  chargesWithSameChargeCode);
            }
        }
    }

    public void inactivateExistingAndAddNewCustomerChargesWhenStopNumberZeroEligible(ChargeDTO chargeDTO, Charge charge, List<CustomerChargeDTO> customerChargeDTOS) {
        if (isStopNumberEqualsZeroAndTotalChargeAmountAreEqual(charge, customerChargeDTOS)) {
            return;
        } else if (customerChargeDTOS.stream().anyMatch(chargeDto -> chargeDto.getStopNumber() != 0)) {
            customerChargeDTOS.stream().findFirst().ifPresent(chargeDTO2 -> {
                inactivateExistingAndAddNewCustomerCharge(chargeDTO,customerChargeDTOS, charge);
            });
        } else if (isStopNumberEqualsZeroAndTotalChargeAmountAreNotEqual(charge, customerChargeDTOS)) {
            customerChargeDTOS.stream().filter(chargeDto -> chargeDto.getStopNumber() == 0 &&
                    charge.getTotalApprovedChargeAmount().compareTo(chargeDto.getTotalChargeAmount()) != 0).findFirst().ifPresent(chargeDTO2 -> {
                chargeDAO.updateExpenceRowN(chargeDTO2.getExternalChargeId());
                addCustCharge(charge, chargeDTO.getScacCode(), null,chargeDTO.getOrderID());
            });
        }
    }

    private boolean isStopNumberEqualsZeroAndTotalChargeAmountAreEqual(Charge charge, List<CustomerChargeDTO> customerChargeDTOS) {
        return customerChargeDTOS.stream().anyMatch(chargeDto -> chargeDto.getStopNumber() == 0
                && charge.getTotalApprovedChargeAmount() != null && chargeDto.getTotalChargeAmount() != null &&
                charge.getTotalApprovedChargeAmount().compareTo(chargeDto.getTotalChargeAmount()) == 0);
    }

    private boolean isStopNumberEqualsZeroAndTotalChargeAmountAreNotEqual(Charge charge, List<CustomerChargeDTO> customerChargeDTOS) {
        return customerChargeDTOS.stream().anyMatch(chargeDto -> chargeDto.getStopNumber() == 0
                && charge.getTotalApprovedChargeAmount() != null && chargeDto.getTotalChargeAmount() != null &&
                charge.getTotalApprovedChargeAmount().compareTo(chargeDto.getTotalChargeAmount()) != 0);
    }

    private void handleUniqueChargeCodeScenario(ChargeDTO chargeDTO, List<Charge> chargesWithSameChargeCode) {
        handleWhenSingleChangeIsApproved(chargeDTO, chargesWithSameChargeCode);
    }

    private void handleMultipleChargesWithSameChargeCode(ChargeDTO chargeDTO, List<CustomerChargeDTO> customerChargeDTOS, List<Charge> chargesWithSameChargeCode,  Boolean executeFlag) {
        if(executeFlag) {
            List<Charge> alreadyApprovedChargesWithSameChargeCode = chargeRepository.findByCarrierPaymentCarrierPaymentIdAndChargeDecisionCodeAndChargeApplyToCustomerIndicatorAndExpirationTimestampIsNullAndChargeCodeIn(
                    chargeDTO.getPaymentId(), APPROVE, 'Y', chargesWithSameChargeCode.stream().map(Charge::getChargeCode).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(alreadyApprovedChargesWithSameChargeCode)) {
                List<Charge> approvedCharges = alreadyApprovedChargesWithSameChargeCode.stream().filter(charge -> !chargeDTO.getChargeIdList().contains(charge.getChargeId())).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(approvedCharges)) {
                    alreadyApprovedSameChargeCode(chargeDTO, chargesWithSameChargeCode, approvedCharges);
                    return;
                }
            }
        }
        for(Charge charge : chargesWithSameChargeCode) {
            inactivateExistingAndAddNewCustomerCharge(chargeDTO, customerChargeDTOS, charge);
        }
    }

    public void inactivateExistingAndAddNewCustomerCharge(ChargeDTO chargeDTO, List<CustomerChargeDTO> customerChargeDTOS, Charge charge) {
        if (isStopNumbersAndTotalChargeAmountsAreEquals(customerChargeDTOS, charge)) {
            return;
        } else {
            Optional<CustomerChargeDTO> customerChargeDTOOptional =customerChargeDTOS.stream().filter(chargeDto -> isStopNumbersEqualsAndTotalChargeAmountsAreNotEquals(charge, chargeDto)).findFirst();
            if (customerChargeDTOOptional.isPresent()) {
                chargeDAO.updateExpenceRowN(customerChargeDTOOptional.get().getExternalChargeId());
                addCustCharge(charge, chargeDTO.getScacCode(), null,chargeDTO.getOrderID());
            } else if (!customerChargeDTOS.stream().anyMatch(chargeDto -> charge.getStopNumber() != null && chargeDto.getStopNumber() != null &&
                    charge.getStopNumber().compareTo(chargeDto.getStopNumber() / 1000) == 0)) {
                addCustCharge(charge, chargeDTO.getScacCode(), null,chargeDTO.getOrderID());
            }
        }
    }

    private boolean isStopNumbersEqualsAndTotalChargeAmountsAreNotEquals(Charge charge, CustomerChargeDTO chargeDto) {
        return charge.getStopNumber() != null && chargeDto.getStopNumber() != null &&
                charge.getStopNumber().compareTo(chargeDto.getStopNumber() / 1000) == 0 &&
                charge.getTotalApprovedChargeAmount() != null && chargeDto.getTotalChargeAmount() != null &&
                charge.getTotalApprovedChargeAmount().compareTo(chargeDto.getTotalChargeAmount()) != 0;
    }

    private boolean isStopNumbersAndTotalChargeAmountsAreEquals(List<CustomerChargeDTO> customerChargeDTOS, Charge charge) {
        return customerChargeDTOS.stream().anyMatch(chargeDto -> charge.getStopNumber() != null && chargeDto.getStopNumber() != null &&
                charge.getStopNumber().compareTo(chargeDto.getStopNumber() / 1000) == 0 &&
                charge.getTotalApprovedChargeAmount() != null && chargeDto.getTotalChargeAmount() != null &&
                charge.getTotalApprovedChargeAmount().compareTo(chargeDto.getTotalChargeAmount()) == 0);
    }


    public Boolean addCustCharge(Charge charge, String scacCode, Integer stopNumber, Integer orderID) {
        AtomicBoolean status = new AtomicBoolean(false);
        String customerChargeCode = chargeIntegrationHelper.fetchCustomerChargeCode(charge.getChargeCode());
        log.info("addCustCharge :: CUSTOMER CHARGE FOR :: " + charge.getChargeCode() + " IS :: "
                +customerChargeCode);

        try {
            status.set(customerChargeDAO.insertCustomerChargekeyHolder(charge, orderID, customerChargeCode, scacCode, stopNumber));
            log.info("addCustomerCharge :: CUSTOMER CHARGE :: ExternalChargeBillingID" + charge.getExternalChargeBillingID());
        } catch (Exception e) {
            log.error("DB2 - Insert Customer Charge Exception ", e);
            // DB2 Insert Failed
            status.set(false);
        }

        Optional.ofNullable(charge.getChargeApplyToCustomerIndicator()).filter(indicator -> !indicator.equals('Y'))
                .ifPresent(customerCharge -> status.set(true));
        return status.get();
    }

    public void alreadyApprovedSameChargeCode(ChargeDTO chargeDTO, List<Charge> charges, List<Charge> alreadyApprovedChargesWithSameChargeCode) {
        Charge singleCharge = alreadyApprovedChargesWithSameChargeCode.get(0);
        List<CustomerChargeDTO> customerChargeDTOS = customerChargeDAO.getCustomerChargesWithExpenseRowAsN(chargeDTO.getLoadNumber(), singleCharge.getChargeCode(),chargeDTO.getOrderID());
        List<Charge> chargesWithApplyToCustomerChargeFlagIsYes = charges.stream().filter(charge -> charge.getChargeApplyToCustomerIndicator() != null &&
                'Y' == charge.getChargeApplyToCustomerIndicator() && singleCharge.getChargeCode().equalsIgnoreCase(charge.getChargeCode())).collect(Collectors.toList());
        if(alreadyApprovedChargesWithSameChargeCode.size() == 1) {

            boolean isStopNumberPresentInDB2 = customerChargeDTOS.stream().anyMatch(customerChargeDTO -> singleCharge.getStopNumber() != null && customerChargeDTO.getStopNumber() != null &&
                    singleCharge.getStopNumber().compareTo(customerChargeDTO.getStopNumber() / 1000) == 0);

            if (isStopNumberPresentInDB2) {
                handleMultipleChargesWithSameChargeCode(chargeDTO, customerChargeDTOS, chargesWithApplyToCustomerChargeFlagIsYes, false);
            } else {
                Optional<Charge> chargeWithStopNumberOne = chargesWithApplyToCustomerChargeFlagIsYes.stream().
                        filter(charge -> charge.getStopNumber() != null && charge.getStopNumber() == 1 ).findAny();
                if(singleCharge.getStopNumber() != null && singleCharge.getStopNumber() == 0 && chargeWithStopNumberOne.isPresent()) {
                    addCustCharge(chargeWithStopNumberOne.get(), chargeDTO.getScacCode(), 0,chargeDTO.getOrderID());
                }
                Optional<Charge> chargeWithStopNumberNinetyNine = chargesWithApplyToCustomerChargeFlagIsYes.stream().
                        filter(charge -> charge.getStopNumber() != null && charge.getStopNumber() == 99 ).findAny();
                if(chargeWithStopNumberNinetyNine.isPresent()) {
                    List<Charge> chargesWithStopNinetyNine = new ArrayList<>();
                    chargesWithStopNinetyNine.add(chargeWithStopNumberNinetyNine.get());
                    handleMultipleChargesWithSameChargeCode(chargeDTO, customerChargeDTOS, chargesWithStopNinetyNine, false);
                }

            }

        } else {
            Optional<Charge> chargeWithStopNumberZero = alreadyApprovedChargesWithSameChargeCode.stream().
                    filter(charge -> charge.getStopNumber() != null && charge.getStopNumber() == 0 ).findAny();
            boolean isZeroStopNumberPresentInDB2 = customerChargeDTOS.stream().anyMatch(customerChargeDTO -> chargeWithStopNumberZero.isPresent() && customerChargeDTO.getStopNumber() != null &&
                    chargeWithStopNumberZero.get().getStopNumber().compareTo(customerChargeDTO.getStopNumber() / 1000) == 0);
            if (isZeroStopNumberPresentInDB2) {
                handleMultipleChargesWithSameChargeCode(chargeDTO, customerChargeDTOS, chargesWithApplyToCustomerChargeFlagIsYes, false);
            } else {
                Optional<Charge> chargeWithStopNumberOne = chargesWithApplyToCustomerChargeFlagIsYes.stream().
                        filter(charge -> charge.getStopNumber() != null && charge.getStopNumber() == 1 ).findAny();
                if(chargeWithStopNumberZero.isPresent() && chargeWithStopNumberOne.isPresent()) {
                    addCustCharge(chargeWithStopNumberOne.get(), chargeDTO.getScacCode(), 0,chargeDTO.getOrderID());
                }
            }
        }
    }

}

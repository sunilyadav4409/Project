package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.entity.*;
import com.jbhunt.finance.carrierpayment.autopay.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.*;

@Slf4j
@Service
public class AutopayValidationService {
    @Autowired
    private ParameterRepository parameterRepository;
    @Autowired
    private SpecificationAssociationRepository specificationAssociationRepository;
    @Autowired
    private AutoPayService autoPayService;
    @Autowired
    private ProcessAutoPayService processAutoPayService;
    @Autowired
    private EstimationDueDateService estimationDueDateService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentStateService paymentStateService;
    @Autowired
    CarrierPaymentProcessEventRepository carrierPaymentProcessEventRepository;
    @Autowired
    private ElasticModificationService elasticModificationService;

    public boolean fetchBillToCodes(Payment payment) {
        var billToFlag = false;
        List<Parameter> parameters = fetchParams(BILL_TO_BYPASS, BILL_TO);
        if (CollectionUtils.isNotEmpty(parameters)) {
            log.info("Payments BillTo code :: " + payment.getBillToPartyId());
            if (null != payment.getBillToPartyId() && parameters.stream().noneMatch(y -> y.getSpecificationSub().equals(payment.getBillToPartyId().trim()))) {
                billToFlag = false;
            } else {
                log.info("Payments BillTo code :: " + payment.getBillToPartyId() + " Matched the parameter value. ");
                log.info(" Autopay failure reason :: " + CarrierPaymentConstants.ACT_AUTOPAY_FAIL_BILLTOBYPASS);
                autoPayService.logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_BILLTOBYPASS);
                billToFlag = true;
            }
        } else {
            log.info(" Parameter list size is empty and returning :: " + billToFlag);
        }
        return billToFlag;
    }

    public List<Parameter> fetchParams(String specification, String classification) {
        var current = LocalDateTime.now();
        List<Parameter> parameterList = null;
        var paramSpecification = specificationAssociationRepository
                .findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(specification, classification,
                        BUSINESS_VALUE, current, current);
        if (null != paramSpecification) {
            parameterList = parameterRepository
                    .findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
                            paramSpecification.getSpecificationAssociationID(), current, current);
        }
        return parameterList;
    }

    public boolean validateCalculatedInvoiceDate(Payment payment, LocalDateTime scanTimestamp) {
        var current = LocalDateTime.now();
        LocalDateTime calculatedInvoiceDate = estimationDueDateService.getTermStartDateForApproveScenarioByCurrentInvoicesScanDate(payment.getCarrierPaymentId(), scanTimestamp);
        if (Objects.nonNull(calculatedInvoiceDate) && calculatedInvoiceDate.compareTo(current) <= 0) {
            log.info(" AutoPay Eligible with Payment aging start Date :: " + payment.getEstimatedDueDate() + "System date :: " + current);
            return true;
        } else {
            if (Objects.nonNull(calculatedInvoiceDate)) {
                log.info(" AutoPay not Eligible ");
                log.info(" AutoPay failed reason " + CarrierPaymentConstants.ACT_AUTOPAY_FAIL_FUTURE_DATE + " ---- calculatedInvoiceDate is present in future.");
                autoPayService.logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_FUTURE_DATE);
                processAutoPayService.insertRecordInCarrierPaymentProcessEventTable(payment, EVENT_AUTOPAY, true, calculatedInvoiceDate);
                return false;
            } else {
                log.info(" AutoPay not Eligible ");
                log.info(" AutoPay failed -------  " + CarrierPaymentConstants.ACT_AUTOPAY_FAIL_FUTURE_DATE + " ---- calculatedInvoiceDate is not present.");
                autoPayService.logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_FUTURE_DATE);
                return false;
            }
        }
    }

    public boolean validateToUpdateProcessEventTable(CarrierPaymentProcessEvent carrierPaymentProcessEvent, Payment payment) {
        var activityDetailFlag = false;
        if (Objects.nonNull(payment.getActivities())) {
            activityDetailFlag = ACT_AUTOPAY_FAIL_FUTURE_DATE
                    .equalsIgnoreCase(payment.getActivities()
                            .stream()
                            .sorted(Comparator.comparing(Activity::getLstUpdS).reversed())
                            .findFirst()
                            .get()
                            .getActivityDetailTypeCode());
        }
        if (carrierPaymentProcessEvent != null && !activityDetailFlag) {
            processAutoPayService.updateCarrierPaymentProcessTable(payment);
        }
        return activityDetailFlag;
    }

    public boolean originDestinationShipmentValidation(Payment payment) {
        var originDestCheck = false;
        if (null != payment.getCarrierPaymentShipmentID() && null != payment.getCarrierPaymentShipmentID().getOriginLocationID()
        && null != payment.getCarrierPaymentShipmentID().getDestinationLocationID()) {
            originDestCheck = payment.getCarrierPaymentShipmentID().getOriginLocationID().equals(payment.getCarrierPaymentShipmentID().getDestinationLocationID());
        }
        return originDestCheck;
    }


        public boolean originDestinationPaymentValidation(Payment payment) {
            var originDestCheck1 = false;
            if (null != payment.getCarrierPaymentShipmentID() && null != payment.getCarrierPaymentShipmentID().getOriginLocationID() && null != payment.getDispatchDestinationLocationID()) {
                originDestCheck1 = payment.getCarrierPaymentShipmentID().getOriginLocationID().equals(payment.getDispatchDestinationLocationID());

            }
            return originDestCheck1;
        }
    public boolean dispatchOriginDestinationValidation(Payment payment) {
        var originDestCheck2 = false;
        if (null != payment.getDispatchOriginLocationID() && null != payment.getDispatchDestinationLocationID()) {
            originDestCheck2 = payment.getDispatchOriginLocationID().equals(payment.getDispatchDestinationLocationID());

        }
        return originDestCheck2;
    }




    public boolean zeroMileDispatchValidation(Payment payment) {
        // adding param for workflow groups
        // adding param for workflow groups
        var zeroMile = false;
            List<Parameter> parameters = fetchParams(ZERO_MILE, BOOLEAN);
            if (CollectionUtils.isNotEmpty(parameters)) {
                log.info("Payment workflow group:: " + payment.getGroupFlowTypeCode());

                zeroMile = parameters.stream()
                        .filter(a -> a.getWorkflowSpecificationAssociation().getWorkflowGroupTypeCode().equalsIgnoreCase(payment.getGroupFlowTypeCode()))
                        .filter(b -> b.getSpecificationSub().equalsIgnoreCase(String.valueOf(false)))
                        .anyMatch(y -> (Objects.isNull(payment.getDispatchMiles()))|| (Objects.nonNull(payment.getDispatchMiles() ) && payment.getDispatchMiles() >= y.getMinNumberValue().intValueExact()
                               && payment.getDispatchMiles() <=  y.getMaxNumberValue().intValueExact()) );
                if (zeroMile && originDestinationShipmentValidation(payment)) {
                    zeroMile = false;
                }
                else if (zeroMile && (originDestinationPaymentValidation(payment)) && dispatchOriginDestinationValidation(payment)) {
                    log.info(" AutoPay failed -------  " + CarrierPaymentConstants.ACT_AUTOPAY_FAIL_TCALLEDTOSHIPPER + " ---- as dispatch miles is zero and Tcalled back to shipper ");
                    autoPayService.logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_TCALLEDTOSHIPPER);
                }
                else {
                    zeroMile = false;
                }

            }
        return zeroMile;
    }
}

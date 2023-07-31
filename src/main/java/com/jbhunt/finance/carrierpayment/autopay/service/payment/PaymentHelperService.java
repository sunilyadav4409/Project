package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.ChargeOverride;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.repository.OverrideRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentRepository;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargeOverrideUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.REROUTE;

@Service
@Slf4j
public class PaymentHelperService {
    @Autowired
    private OverrideRepository overrideRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AutoPayService autoPayService;
    @Autowired
    private PaymentStateService paymentStateService;
    @Autowired
    private ElasticModificationService elasticModificationService;
    public ChargeOverride updateOvverideCharge(Charge existingCharge){

        ChargeOverride newOverride=null;
        if(Objects.nonNull(existingCharge.getChargeOverride())){

            ChargeOverride  existingOverride=existingCharge.getChargeOverride();
            log.info("updateDifferentCharges in ChargeOverride: " + existingOverride.getChargeOverrideId());
            // END EXISTING OVERRIDE AND SAVE
            ChargeOverrideUtil.endExistingOverride(existingOverride);
            overrideRepository.save(existingOverride);

            // CREATE NEW OVERRIDE AND MAP TO CHARGE
            newOverride = ChargeOverrideUtil.newChargeOverride(existingOverride);
            newOverride.setChargeId(existingOverride.getChargeId());

        }
        return newOverride;
    }

    public void setPaymentStatusToReroute(Payment payment) {
        String oldState = payment.getStatusFlowTypeCode();
        if (CarrierPaymentConstants.ICS.equalsIgnoreCase(payment.getGroupFlowTypeCode().trim()) && ! REROUTE.equalsIgnoreCase(payment.getStatusFlowTypeCode())) {
            payment.setStatusFlowTypeCode(REROUTE);
            log.info("Autopay fail not due to $ amount checks :: " + payment.getStatusFlowTypeCode());
            var savedPayment = paymentRepository.save(payment);
            autoPayService.logAutoRerouteActivity(savedPayment,oldState);
            paymentStateService.createNewPaymentState(savedPayment);
            elasticModificationService.updateElasticSearchForAutoPayFailure(savedPayment);
        }
    }

    public Integer getOrderID(Payment payment) {
        Integer orderID = null;
        if (null != payment.getCarrierPaymentShipmentID() && null != payment.getCarrierPaymentShipmentID().getLoadToken() ) {
            orderID = payment.getCarrierPaymentShipmentID().getLoadToken();
        }
        return orderID;
    }




}

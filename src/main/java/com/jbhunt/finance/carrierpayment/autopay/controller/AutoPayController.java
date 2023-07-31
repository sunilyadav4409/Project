package com.jbhunt.finance.carrierpayment.autopay.controller;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.AutopayProcessDTO;
import com.jbhunt.finance.carrierpayment.autopay.feature.FeatureFlags;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.AutoPayService;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.ProcessAutoPayService;
import com.jbhunt.requestmetric.annotation.RequestMetric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RestController
@RequestMapping(CarrierPaymentConstants.AUTOPAY)
@RequiredArgsConstructor
public class AutoPayController {

    private final AutoPayService autoPayService;
    private final ProcessAutoPayService processAutoPayService;
    private final FeatureFlags featureFlags;



    /**
     * @param paymentId
     */
    @PostMapping(value = CarrierPaymentConstants.CALL_AUTOPAY, produces = CarrierPaymentConstants.AUTOPAY_SERVICE_PRODUCES)
    @RequestMetric
    public ResponseEntity<Boolean> callAutoPayWebService(@PathVariable Integer paymentId) {
        try{
            log.info("Inside AutoPay Controller : callapws"+ "for payment ID"+ paymentId);
            Boolean status = autoPayService.callAutoPayWS(paymentId);
            if(status && featureFlags.publishChargeUpdatesToBilling.isEnabled()) {
                autoPayService.postChargesStatusToTopic(paymentId);
            }
            return ResponseEntity.ok(status);
        }
        catch (Exception e){
            log.info("Exception occurred while performing AutoPay" + e);
            throw e;
        }

    }

    @PostMapping(value = CarrierPaymentConstants.CALL_PROCESS_AUTOPAY, produces = CarrierPaymentConstants.AUTOPAY_SERVICE_PRODUCES)
    @RequestMetric
    public ResponseEntity<Boolean> postPaymentToProcessAutoPay(@RequestBody AutopayProcessDTO autopayProcessDTO) {
        try{
            log.info("Inside AutoPay Controller : callapws"+ "calling Point");
            return ResponseEntity.ok(processAutoPayService.postPaymentToProcessAutoPay(autopayProcessDTO));
        } catch (Exception e){
            log.info("Exception occurred while posting payment for Autopay Process" + e);
            throw e;
        }
    }

}

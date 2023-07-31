package com.jbhunt.finance.carrierpayment.autopay.service;


import com.jbhunt.biz.securepid.PIDCredentials;
import com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.PaymentHelperService;
import com.rtc056i.rtc056.RTC056Port;
import com.rtc056i.rtc056.RTC056Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.BindingProvider;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class RateIntegrationService {

    @Autowired
    private  PIDCredentials credentials;
    @Autowired
    private PaymentHelperService paymentHelperService;


    /**
     * @param userId
     * @return
     * @throws Exception
     */
    public TxnStatusDTO callRTC056(Payment payment, String userId) {
        String runTime = System.getProperty(PaymentChargesConstants.RUNTIME_ENVIRONMENT).toUpperCase();
        Integer orderId = paymentHelperService.getOrderID(payment);
        log.info("Order Id corresponding to load Number :: " + payment.getLoadNumber() + " is " + orderId);
        log.info("Inside RerateService : callRTC056");
        TxnStatusDTO txnStatusDTO = new TxnStatusDTO();
        RTC056Port rtc056Port = setupPort();

        log.info("Invoking RTC056Operation in " + runTime + " mode.");

        com.request.rtc056i.rtc056.ProgramInterface.Rt56InputChannelData rtc056InputCh = new com.request.rtc056i.rtc056.ProgramInterface.Rt56InputChannelData();
        rtc056InputCh.setRt56IFiller("");
        rtc056InputCh.setRt56ILtlToken(orderId);
        rtc056InputCh.setRt56IOrdCrNbr(payment.getLoadNumber());
        rtc056InputCh.setRt56ISrcNm("ORDER");
        rtc056InputCh.setRt56IUserId(userId);

        log.info("Inputs for RTC056Operation are:--- :Rt56ILtlToken:" + rtc056InputCh.getRt56ILtlToken()
                + ":Rt56IOrdCrNbr:" + rtc056InputCh.getRt56IOrdCrNbr() + ":setRt56ISrcNm:"
                + rtc056InputCh.getRt56ISrcNm() + ":setRt56IUserId:" + rtc056InputCh.getRt56IUserId() + ":Rt56IFiller:"
                + rtc056InputCh.getRt56IFiller());
        try {
            com.response.rtc056i.rtc056.ProgramInterface.Rt56OutputChannelData rtc056OutputCh = rtc056Port
                    .rtc056Operation(rtc056InputCh);

            Optional.ofNullable(rtc056OutputCh).filter(output -> Objects.nonNull(output.getRt56OReturnMsg()))
                    .ifPresent(message -> {
                        log.info("RTC056 Return Message: " + rtc056OutputCh.getRt56OReturnMsg());
                        txnStatusDTO.setSuccess(true);
                    });

        } catch (Exception e) {
            log.info("EXCEPTION BLOCK!!", e);
        }

        log.info("RTC056 Call Completed");
        return txnStatusDTO;
    }

    private RTC056Port setupPort() {
        String runTime = System.getProperty(PaymentChargesConstants.RUNTIME_ENVIRONMENT).toUpperCase();
        RTC056Service service = new RTC056Service();
        String webServiceUserid;
        String webServicePassword;
        RTC056Port rtc056Port;

        if (runTime.equalsIgnoreCase(PaymentChargesConstants.RUNTIME_TEST)) {
            rtc056Port = service.getRTC056PortTEST();
            webServiceUserid = credentials.getUsername();
            webServicePassword = credentials.getPassword();
        } else if (runTime.equalsIgnoreCase(PaymentChargesConstants.RUNTIME_QA)) {
            rtc056Port = service.getRTC056PortQA();
            webServiceUserid = credentials.getUsername();
            webServicePassword = credentials.getPassword();
        } else if (runTime.equalsIgnoreCase(PaymentChargesConstants.RUNTIME_PROD)) {
            rtc056Port = service.getRTC056PortPROD();
            webServiceUserid = credentials.getUsername();
            webServicePassword = credentials.getPassword();
        } else {
            rtc056Port = service.getRTC056PortDEV();
            webServiceUserid = credentials.getUsername();
            webServicePassword = credentials.getPassword();
        }

        BindingProvider bindingProvider = (BindingProvider) rtc056Port;
        bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, webServiceUserid);
        bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, webServicePassword);
        return rtc056Port;
    }
}

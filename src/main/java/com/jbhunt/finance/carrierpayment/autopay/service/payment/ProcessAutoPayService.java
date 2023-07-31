package com.jbhunt.finance.carrierpayment.autopay.service.payment;


import com.jbhunt.finance.carrierpayment.autopay.dao.CarrierPaymentParameterDAO;
import com.jbhunt.finance.carrierpayment.autopay.dto.AutopayProcessDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ParameterListingDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.PaymentAndProcessDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierPaymentProcessEvent;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.feature.FeatureFlags;
import com.jbhunt.finance.carrierpayment.autopay.repository.CarrierPaymentProcessEventRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.InvoiceRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentRepository;
import com.jbhunt.infrastructure.exception.JBHValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessAutoPayService {

    private final InvoiceRepository invoiceRepository;
    private final CarrierPaymentProcessEventRepository carrierPaymentProcessEventRepository;
    private final ProducerTemplate producerTemplate;
    private final CarrierPaymentParameterDAO carrierPaymentParameterDAO;
    private final PaymentRepository paymentRepository;
    private final FeatureFlags featureFlags;


    public boolean postPaymentToProcessAutoPay(AutopayProcessDTO autopayProcessDTO) {
        var calculatedDueDate = LocalDateTime.now();
        autopayProcessDTO.getPaymentIds().stream().forEach(paymentId -> {
            Payment payment = paymentRepository.findByCarrierPaymentId(paymentId);
            insertRecordInCarrierPaymentProcessEventTable(payment, autopayProcessDTO.getCallingPoint(), false ,calculatedDueDate);
        });
        return true;
    }

    public void insertRecordInCarrierPaymentProcessEventTable(Payment existingPayment, String callingPoint, boolean isDueToEstimatedDueDate,LocalDateTime calculatedDueDate) {
        log.info( " Inside -------------- insertRecordInCarrierPaymentProcessEventTable method  " );
        if (null == existingPayment) {
            return;
        }
        try{
            CarrierInvoiceHeader carrierInvoiceHeader =   invoiceRepository
                    .findTopByCarrierPaymentCarrierPaymentIdAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
                            existingPayment.getCarrierPaymentId(),
                            INVOICE_STATUS_REJECTED );

            Optional.ofNullable( carrierInvoiceHeader ).ifPresent(header -> {
                log.info( "EDI/External Invoice is present and InvoiceHeader ID is  "
                        + carrierInvoiceHeader.getCarrierInvoiceHeaderId() );
                ParameterListingDTO parameterListingDTO = carrierPaymentParameterDAO.getParametersForWorkFlowGroup(existingPayment.getGroupFlowTypeCode());
                boolean isPaper = PAPER.equalsIgnoreCase(carrierInvoiceHeader.getInvoiceSourceTypeCode());
                if(isPaper && !PENDING_STATUS_LIST.contains(existingPayment.getStatusFlowTypeCode()) && !featureFlags.paperInvcAutoPayProcess.isEnabled() ){
                    return;
                }
                if(parameterListingDTO != null && BigDecimal.ZERO.compareTo(parameterListingDTO.getMinNumberValue()) == 0 && !isDueToEstimatedDueDate) {
                    postToAutopayTopic(existingPayment, isPaper);
                    return;
                }
                int carrierProcessEventRecordCount = carrierPaymentProcessEventRepository.findCountOfCarrierPaymentProcessEvent(existingPayment.getCarrierPaymentId(), existingPayment.getGroupFlowTypeCode());
                log.info( " CarrierProcessEventRecordCount in CarrierPaymentProcessEvent Table is " + carrierProcessEventRecordCount);

                CarrierPaymentProcessEvent carrierPaymentProcessEvent = new CarrierPaymentProcessEvent();
                if (carrierProcessEventRecordCount == 0 ) {
                    log.info( " Inserting record in CarrierPaymentProcessEvent Table ");
                    setCarrierPaymentProcessEvent( existingPayment, callingPoint, carrierPaymentProcessEvent, calculatedDueDate,isPaper );
                    carrierPaymentProcessEventRepository.save( carrierPaymentProcessEvent );
                    log.info( " One record successfully inserted in CarrierPaymentProcessEvent Table ");
                    log.info( " CarrierPaymentProcessEvent Table processed date " + carrierPaymentProcessEvent.getProcessedTimestamp());

                }else if(carrierProcessEventRecordCount == 1){
                    log.info( " Updating record in CarrierPaymentProcessEvent Table  when callingPoint is "+ callingPoint);
                    carrierPaymentProcessEvent = carrierPaymentProcessEventRepository.findByCarrierPaymentIDAndCarrierPaymentWorkflowGroupTypeCodeAndProcessedTimestampIsNull(existingPayment.getCarrierPaymentId(), existingPayment.getGroupFlowTypeCode() );
                    carrierPaymentProcessEvent.setCrtPgmC(callingPoint);
                    carrierPaymentProcessEvent.setLstUpdPgmC(callingPoint);
                    if(isPaper){
                        carrierPaymentProcessEvent.setInvoiceSourceTypeCode("Paper");
                    }else {
                        carrierPaymentProcessEvent.setInvoiceSourceTypeCode(null);
                    }
                    carrierPaymentProcessEventRepository.save( carrierPaymentProcessEvent );
                    log.info( " One record successfully updated with " + callingPoint + " value in CarrierPaymentProcessEvent Table in CreateProgram and in LastUpdatedProgram columns ");
                }
            });
        }catch (Exception e){
            log.info( " Error while inserting row in CarrierPaymentProcessEvent Table ", e);
            throw new JBHValidationException( " Error while inserting row in CarrierPaymentProcessEvent Table" );
        }
        log.info( " Exiting -------------- insertRecordInCarrierPaymentProcessEventTable method  " );
    }

    private void setCarrierPaymentProcessEvent(Payment existingPayment, String callingPoint
            , CarrierPaymentProcessEvent carrierPaymentProcessEvent, LocalDateTime calculatedDueDate, boolean isPaper) {
        carrierPaymentProcessEvent.setCarrierPaymentID( existingPayment.getCarrierPaymentId() );
        carrierPaymentProcessEvent.setCarrierPaymentWorkflowGroupTypeCode( existingPayment.getGroupFlowTypeCode() );
        carrierPaymentProcessEvent.setEventTypeCode( "AutoApprve" );
        carrierPaymentProcessEvent.setEventDatetime(calculatedDueDate);
        carrierPaymentProcessEvent.setCrtS(LocalDateTime.now() );
        carrierPaymentProcessEvent.setCrtUid(existingPayment.getCrtUid());
        carrierPaymentProcessEvent.setCrtPgmC(callingPoint);
        carrierPaymentProcessEvent.setLstUpdS(LocalDateTime.now());
        carrierPaymentProcessEvent.setLstUpdUid(existingPayment.getLstUpdUid());
        carrierPaymentProcessEvent.setLstUpdPgmC(callingPoint);
        if(isPaper){
            carrierPaymentProcessEvent.setInvoiceSourceTypeCode("Paper");
        }
    }

    private void postToAutopayTopic( Payment payment, boolean paperInvoice){
        try {
            PaymentAndProcessDTO paymentAndProcessDTO = new PaymentAndProcessDTO();
            paymentAndProcessDTO.setPaymentId(payment.getCarrierPaymentId());
            Map<String, Object> headers = new HashMap<>();
            headers.put(JMSXGROUPID, payment.getLoadNumber());
            if (!paperInvoice) {
                headers.put(EVENTTYPE, EVENT_TYPE_AUTOPAY);
            }else {
                headers.put(EVENTTYPE, EVENT_TYPE_PAPER_INVOICE_AUTOPAY);
            }
            producerTemplate.requestBodyAndHeaders("direct://postToAutopayPaymentsTopic", paymentAndProcessDTO,headers);
        } catch (Exception e) {
            log.error("Exception while posting message to update Autopay Details"+ e);
        }
    }


    public void updateCarrierPaymentProcessTable(Payment payment) {
        try {
            log.info(" Inside---------- updateCarrierPaymentProcessTable Method where payment ID is " + payment.getCarrierPaymentId() + " and Workflow group is " + payment.getGroupFlowTypeCode());
            int countOfUpdatedRecord = carrierPaymentProcessEventRepository.updateCarrierPaymentProcessEvent(payment.getCarrierPaymentId(), payment.getGroupFlowTypeCode());
            log.info(" Update record count in CarrierPaymentProcessEvent Table is " + countOfUpdatedRecord);
            if (countOfUpdatedRecord >= 1) {
                log.info(" Successfully Updated records in CarrierPaymentProcessEvent Table  count  :  " + countOfUpdatedRecord);
            } else {
                log.info(" No records updated :  " + countOfUpdatedRecord);
            }
        } catch (Exception e) {
            log.info("Error while updating CarrierPaymentProcessEvent Table ", e);
            throw new JBHValidationException(" Error while performing SQL DB Operations ");
        }
        log.info(" Exiting ---------- updateCarrierPaymentProcessTable Method ");
    }


}

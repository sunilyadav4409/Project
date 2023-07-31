package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.constants.ElasticConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.PaymentSearchDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.mapper.ChargeMapper;
import com.jbhunt.finance.carrierpayment.autopay.properties.PaymentApplicationProperties;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.CalcAmountUtil;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.formatter;
import static org.elasticsearch.xcontent.XContentFactory.jsonBuilder;

@Service
@Slf4j
public class ElasticQueryBuilderService {

    @Autowired
    private PaymentApplicationProperties paymentApplicationProperties;

    @Autowired
    private ChargeMapper chargeMapper;

    public UpdateRequest updateElasticSearchForAutoPay(Payment payment) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(paymentApplicationProperties.getElasticSearchWorkFlowIndex());
        updateRequest.type(paymentApplicationProperties.getElasticDocType());
        updateRequest.id(payment.getCarrierPaymentId().toString());
        try {
            updateRequest.doc(jsonBuilder()
                    .startObject()
                    .field(ElasticConstants.WORKFLOW_STATUS, payment.getStatusFlowTypeCode())
                    .field(ElasticConstants.DISPATCH_NUMBER, payment.getDispatchNumber())
                    .field(ElasticConstants.TOTALAPPROVEDCHARGES, payment.getTotalApprovedInvoiceAmt()!=null?payment.getTotalApprovedInvoiceAmt().floatValue():getJSONObj(ElasticConstants.TOTALAPPROVEDCHARGES))
                    .endObject());
        } catch (IOException e) {
            log.error("Error while preparing json for updateElasticSearchForAutoPay");
        }
        return  updateRequest;
    }


    public UpdateRequest updatePaidStatusToElasticDocument(Payment payment, boolean isPaid, String invoiceNumber, PaymentSearchDTO paymentSearchDTO) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(paymentApplicationProperties.getElasticSearchWorkFlowIndex());
        updateRequest.type(paymentApplicationProperties.getElasticDocType());
        updateRequest.id(payment.getCarrierPaymentId().toString());
        try {
            if(isPaid){
                updateRequest.doc(jsonBuilder()
                        .startObject()
                        .field( ElasticConstants.WORKFLOW_STATUS, CarrierPaymentConstants.PAID)
                        .field( ElasticConstants.LAST_MODIFIED_BY, CarrierPaymentConstants.WORK_DAY )
                        .field( ElasticConstants.MODIFIED_TIME, LocalTime.now().toString() )
                        .field( ElasticConstants.MODIFIED_DATE, LocalDate.now().toString() )
                        .field(ElasticConstants.IGNORE_REASON_CODE, getJSONObj(ElasticConstants.IGNORE_REASON_CODE) )
                        .field(ElasticConstants.TOTALAPPROVEDCHARGES, payment.getTotalApprovedInvoiceAmt()!=null?payment.getTotalApprovedInvoiceAmt().floatValue():getJSONObj(ElasticConstants.TOTALAPPROVEDCHARGES))
                        .field(ElasticConstants.LAST_PROCESSED_ACTION, "ChargeCodeUpdated")
                        .field( ElasticConstants.LAST_PROCESSED_ACTION_PERFORMED_BY, UserUtil.getLoggedInUser() )
                        .field( ElasticConstants.LAST_PROCESSED_TIMESTAMP, LocalDateTime.now().format(formatter) )
                        .endObject());
            }else{
                BigDecimal totalCharges = getTotalFromPaymentCharges(payment);
                updateRequest.doc(jsonBuilder()
                        .startObject()
                        .field( ElasticConstants.INVOICENUMBER, updateInvoiceNumber(invoiceNumber, paymentSearchDTO))
                        // .field( ElasticConstants.INVOICENUMBER, invoiceNumber )
                        .field( ElasticConstants.TOTAL_CHARGES, totalCharges!=null?totalCharges.floatValue():getJSONObj(ElasticConstants.TOTAL_CHARGES) )
                        .field( ElasticConstants.LAST_MODIFIED_BY, UserUtil.getLoggedInUser() )
                        .field( ElasticConstants.MODIFIED_TIME, LocalTime.now().toString() )
                        .field( ElasticConstants.MODIFIED_DATE, LocalDate.now().toString() )
                        .field(ElasticConstants.IGNORE_REASON_CODE, getJSONObj( ElasticConstants.IGNORE_REASON_CODE ) )
                        .field(ElasticConstants.TOTALAPPROVEDCHARGES, payment.getTotalApprovedInvoiceAmt()!=null?payment.getTotalApprovedInvoiceAmt().floatValue():getJSONObj(ElasticConstants.TOTALAPPROVEDCHARGES))
                        .field(ElasticConstants.LAST_PROCESSED_ACTION, "ChargeCodeUpdated")
                        .field( ElasticConstants.LAST_PROCESSED_ACTION_PERFORMED_BY, UserUtil.getLoggedInUser() )
                        .field( ElasticConstants.LAST_PROCESSED_TIMESTAMP, LocalDateTime.now().format(formatter) )
                        .endObject());
            }
        } catch (IOException e) {
            log.error("Error while preparing json for updatePaidStatusToElasticDocument");
        }
        return  updateRequest;
    }

    public UpdateRequest updateElasticSearchProcess(Payment payment, String actionType, String reason) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(paymentApplicationProperties.getElasticSearchWorkFlowIndex());
        updateRequest.type(paymentApplicationProperties.getElasticDocType());
        updateRequest.id(payment.getCarrierPaymentId().toString());
        try {
            updateRequest.doc(jsonBuilder()
                    .startObject()
                    .field( ElasticConstants.TOTAL_CHARGES, payment.getTotalChargeAmount()!=null?payment.getTotalChargeAmount().floatValue():getJSONObj(ElasticConstants.TOTAL_CHARGES) )
                    .field(ElasticConstants.IGNORE_REASON_CODE, getJSONObj( ElasticConstants.IGNORE_REASON_CODE ))
                    .field(ElasticConstants.LASTPROCESSEDBY, setLastProcessedByForApprove(UserUtil.getLoggedInUser(), payment.getStatusFlowTypeCode()))
                    .field(ElasticConstants.IGNORE_REASON_CODE, ((actionType!=null && actionType.equalsIgnoreCase( CarrierPaymentConstants.IGNORE ))?reason:"") )
                    .field(ElasticConstants.SNOOZE_REASON_CODE, ((actionType!=null && actionType.equalsIgnoreCase( CarrierPaymentConstants.SNOOZE ))?reason:"") )
                    .field(ElasticConstants.WORKFLOW_STATUS, payment.getStatusFlowTypeCode() )
                    .field( ElasticConstants.LAST_MODIFIED_BY, UserUtil.getLoggedInUser() )
                    .field( ElasticConstants.MODIFIED_TIME, LocalTime.now().toString() )
                    .field( ElasticConstants.MODIFIED_DATE, LocalDate.now().toString() )
                    .field(ElasticConstants.DISPATCH_NUMBER, payment.getDispatchNumber())
                    .field(ElasticConstants.DAYS_APPROACHING, getEstimationDueDate(payment) )
                    .field( ElasticConstants.QUICK_PAY_IND, getQuickPayInd(payment) )
                    .field(ElasticConstants.TOTALAPPROVEDCHARGES, payment.getTotalApprovedInvoiceAmt()!=null?payment.getTotalApprovedInvoiceAmt().floatValue():getJSONObj(ElasticConstants.TOTALAPPROVEDCHARGES))
                    .endObject());
        } catch (IOException e) {
            log.error("Error while preparing json for updatePaidStatusToElasticDocument");
        }
        return  updateRequest;
    }

    private String getEstimationDueDate (Payment payment) {
        String estimationDueDate = null;
        if(CarrierPaymentConstants.PENDING_STATUS_LIST.contains(payment.getStatusFlowTypeCode()) && payment.getEstimatedDueDate() != null ){
            estimationDueDate = payment.getEstimatedDueDate().toString();
        }
        return estimationDueDate;
    }

    private boolean getQuickPayInd (Payment payment) {
        if(CarrierPaymentConstants.PENDING_STATUS_LIST.contains(payment.getStatusFlowTypeCode())){
            return Objects.equals(payment.getPaymentMethodTypeCode(), CarrierPaymentConstants.QUICK_PAY)
                    || Objects.equals(payment.getPaymentMethodTypeCode(), CarrierPaymentConstants.FREE_QUICK_PAY);
        }
        return false;
    }



    private List<String> updateInvoiceNumber(String invoiceNumber, PaymentSearchDTO paymentSearchDTO) {
        if(paymentSearchDTO!=null) {
            Optional.ofNullable( invoiceNumber ).ifPresent( invNum -> {
                List<String> invoiceNbrs = Optional.ofNullable( paymentSearchDTO.getInvoiceNumber() ).map( x -> x )
                        .orElse( new ArrayList<>() );
                if (!invoiceNbrs.contains( invoiceNumber )) {
                    invoiceNbrs.add( invoiceNumber );
                    paymentSearchDTO.setInvoiceNumber( invoiceNbrs );
                }
            } );
            return paymentSearchDTO.getInvoiceNumber();
        }else
            return new ArrayList<>(  );
    }

    private String setLastProcessedByForApprove(String userId, String status) {
        if(status.equalsIgnoreCase( CarrierPaymentConstants.APPROVE ))
            return userId;
        return "";
    }

    private BigDecimal getTotalFromPaymentCharges(Payment payment) {
        if(payment.getCarrierPaymentChargeList()!=null && !payment.getCarrierPaymentChargeList().isEmpty()){
            List<ChargeDTO> chargeDTOList = chargeMapper.chargeListToChargeDTOList(payment
                    .getCarrierPaymentChargeList().stream()
                    .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null).collect( Collectors.toList()));
            log.info("getTotalFromPaymentCharges : chargeDTOList size: " + chargeDTOList.size());
            return CalcAmountUtil.calculateTotal(chargeDTOList);
        }
        return BigDecimal.ZERO;

    }

    private String getJSONObj(String obj){
        String  jsonString = null;
        try {
            jsonString = new JSONObject()
                    .put(obj, JSONObject.NULL)
                    .toString();
        } catch (JSONException e) {
            log.error("Error while creating Json for unsnooze");
        }
        return jsonString;
    }

    public SearchRequest getPaymentSearchObj(Integer carrierPaymentID){
        SearchRequest searchRequest = new SearchRequest(paymentApplicationProperties.getElasticSearchWorkFlowIndex());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query( QueryBuilders.boolQuery()
                .must( QueryBuilders.matchQuery("paymentId.keyword",carrierPaymentID))
        );
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    public UpdateRequest updateElasticSearchForAutoPayFailure(Payment payment) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(paymentApplicationProperties.getElasticSearchWorkFlowIndex());
        updateRequest.type(paymentApplicationProperties.getElasticDocType());
        updateRequest.id(payment.getCarrierPaymentId().toString());
        try {
            updateRequest.doc(jsonBuilder()
                    .startObject()
                    .field( ElasticConstants.WORKFLOW_STATUS,payment.getStatusFlowTypeCode())
                    .field( ElasticConstants.LAST_MODIFIED_BY, CarrierPaymentConstants.SYSTEM)
                    .field( ElasticConstants.MODIFIED_TIME, LocalTime.now().toString() )
                    .field( ElasticConstants.MODIFIED_DATE, LocalDate.now().toString() )
                    .endObject());
        } catch (IOException e) {
            log.error("Error while preparing json for updateElasticSearchForAutoPay");
        }
        return  updateRequest;
    }



}

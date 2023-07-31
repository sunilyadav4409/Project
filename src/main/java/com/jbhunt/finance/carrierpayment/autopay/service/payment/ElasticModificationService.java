package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.google.gson.Gson;
import com.jbhunt.finance.carrierpayment.autopay.dto.PaymentSearchDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.mapper.ChargeMapper;
import com.jbhunt.finance.carrierpayment.autopay.properties.PaymentApplicationProperties;
import com.jbhunt.finance.carrierpayment.autopay.repository.ChargeRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentMethodRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ElasticModificationService {
    @Autowired
    private  ElasticQueryBuilderService elasticQueryBuilderService;
    @Autowired
    private  ChargeMapper chargeMapper;
    @Autowired
    private  ElasticUpdateService elasticUpdateService;
    @Autowired
    private MessagePostService messagePostService;
    @Autowired
    ChargeRepository chargeRepository;
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private PaymentApplicationProperties paymentApplicationProperties;
    @Autowired
    private ElasticClientService elasticClientService;

    public void updateElasticSearchForAutopay(Payment payment) {
        UpdateRequest updateRequest = elasticQueryBuilderService.updateElasticSearchForAutoPay(payment);
        elasticUpdateService.updateElasticSearch(updateRequest);
    }
    public void updateElasticSearchForAutoPayFailure(Payment payment) {
        UpdateRequest updateRequest = elasticQueryBuilderService.updateElasticSearchForAutoPayFailure(payment);
        elasticUpdateService.updateElasticSearch(updateRequest);
    }

    public void updatePaidStatusToElasticDocument(Payment existingPayment, boolean isPaid, String invoiceNumber) {
        PaymentSearchDTO paymentSearchDTO = getPaymentSearchDTOObj(existingPayment.getCarrierPaymentId());
        UpdateRequest updateRequest = elasticQueryBuilderService.updatePaidStatusToElasticDocument(existingPayment, isPaid, invoiceNumber, paymentSearchDTO );
        elasticUpdateService.updateElasticSearch(updateRequest);
    }

    public void updateElasticSearchProcess(Payment payment, String actionType, String reason, Boolean factored) {
        UpdateRequest updateRequest = elasticQueryBuilderService.updateElasticSearchProcess(payment, actionType, reason );
        elasticUpdateService.updateElasticSearch(updateRequest);
    }

    private PaymentSearchDTO getPaymentSearchDTOObj(Integer carrierPaymentID) {
        SearchRequest searchRequest=  elasticQueryBuilderService.getPaymentSearchObj(carrierPaymentID);
        SearchResponse searchResponse = elasticClientService.executeSearch(searchRequest);
        return convertSearchResponseToPaymentSearchDTO(searchResponse);
    }


    private PaymentSearchDTO convertSearchResponseToPaymentSearchDTO(SearchResponse searchResponse){
        PaymentSearchDTO paymentSearchDTO = new PaymentSearchDTO();
        SearchHit[] results = searchResponse.getHits().getHits();

        for (SearchHit hit: results) {
            String sourceAsString = hit.getSourceAsString();
            Gson gson =new Gson();
            paymentSearchDTO = gson.fromJson(sourceAsString, PaymentSearchDTO.class) ;
        }
        return paymentSearchDTO;
    }

}

package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.dto.ElasticUpdateResponseDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.PaymentEnrichmentDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.PaymentSearchDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.PaymentMethodType;
import org.springframework.http.ResponseEntity;

public class ElasticMocks {
/*    public static Search getSearchMockForrPaymentID(){
        Search search;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(CarrierPaymentConstants.PAYMENT_ID, 1234)));
       return new  Search.Builder(searchSourceBuilder.toString())
                .addIndex("test").build();

    }
    public static JestResult jestResultMock(){
        JsonObject searchResult = new JsonObject();
        searchResult.addProperty("paymentId","1423");
        searchResult.addProperty("tokenId",14238);
        JestResult result = new JestResult(new Gson());
        result.setJsonObject(searchResult);
        result.setSucceeded(true);
        return result;
    }*/
    public static PaymentSearchDTO getPaymentSearchDTO() {
        PaymentSearchDTO paymentSearchDTO = new PaymentSearchDTO();
        paymentSearchDTO.setPaymentId(1);
        paymentSearchDTO.setBillToCode("B123");
        paymentSearchDTO.setSnoozeReasonCode("System");
        return paymentSearchDTO;
    }
    public static ResponseEntity<ElasticUpdateResponseDTO> getElasticUpdateResponseDTO() {
        ElasticUpdateResponseDTO elasticUpdateResponseDTO = new ElasticUpdateResponseDTO();
         elasticUpdateResponseDTO.setId("3423");
         return ResponseEntity.ok(elasticUpdateResponseDTO);
    }
    public static PaymentEnrichmentDTO createPaymentEnrichmentDTO(){
        PaymentEnrichmentDTO paymentEnrichmentDTO = new PaymentEnrichmentDTO();
        paymentEnrichmentDTO.setQuickPay(true);
        paymentEnrichmentDTO.setBillToCode("test");
        paymentEnrichmentDTO.setShipmentId("jgad");
        paymentEnrichmentDTO.setFleetCode("hdgsh");
        paymentEnrichmentDTO.setBolNumber("test");
        paymentEnrichmentDTO.setProNumber("hdgf");
           return paymentEnrichmentDTO;
    }
    public static PaymentMethodType getpaymentMethodTypeMoc(){
        PaymentMethodType paymentMethodType = new PaymentMethodType();
        paymentMethodType.setSortSequence(1);
        paymentMethodType.setPaymentMethodTypeCode("test");
        return paymentMethodType;
    }
}

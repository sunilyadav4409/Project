package com.jbhunt.finance.carrierpayment.autopay.service.payment;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.LockedUsersDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.PaymentSearchDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.properties.PaymentApplicationProperties;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class MessagePostService {

    @Autowired
    private  ProducerTemplate producerTemplate;
    @Autowired
    private  PaymentApplicationProperties paymentApplicationProperties;

    public void postApprovedMessage(Payment payment) {
        String approverUserId = UserUtil.getLoggedInUser();

        log.info("Charge :: approverUserId :: " + approverUserId);
        LockedUsersDTO lockedUsersDTO = new LockedUsersDTO();
        lockedUsersDTO.setAction("UPDATE");
        lockedUsersDTO.setMessage( CarrierPaymentConstants.SYSTEM_USERID );
        lockedUsersDTO.setPaymentId(payment.getCarrierPaymentId());
        Message<LockedUsersDTO> buildApprovedMessage = buildApprovedMessage(lockedUsersDTO, "paymentId", payment.getCarrierPaymentId());
        // POSTING APPROVED USER MESSAGE
        postToTopic(buildApprovedMessage, paymentApplicationProperties.getAmqTopicName());

    }

    public  void postToElasticTopic(Integer paymentId, PaymentSearchDTO paymentSearchDTO) {
        Message<PaymentSearchDTO> message = buildElasticMessage( paymentSearchDTO,"paymentId", paymentId);
        postToTopic(message,paymentApplicationProperties.getAmqElasticTopicName());

    }
    private  void postToTopic(Message<?> message, String topicName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            producerTemplate.sendBodyAndHeaders(
                    "direct://postToPaymentTopic",
                    objectMapper.writeValueAsString(message.getPayload()), message.getHeaders());
        } catch (CamelExecutionException e) {
            log.info("Execution error while posting to Error Queue", e);
        } catch (JsonProcessingException ex) {
            log.info("Parsing error while posting posting to Error Queue", ex);
        }
    }
    private static Message<PaymentSearchDTO> buildElasticMessage(PaymentSearchDTO payload, String key, Object value) {
        return MessageBuilder.withPayload(payload).setHeader(key, String.valueOf(value)).build();
    }
    private static Message<LockedUsersDTO> buildApprovedMessage(LockedUsersDTO payLoad, String key, Object value) {
        return MessageBuilder.withPayload(payLoad).setHeader(key, String.valueOf(value)).build();
    }
}

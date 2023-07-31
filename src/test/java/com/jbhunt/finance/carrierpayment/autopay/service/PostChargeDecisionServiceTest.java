package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeDTOMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentEntityMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.TxnStatusDTOMocks;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PostChargeDecisionServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    @InjectMocks
    private PostChargeDecisionService postChargeDecisionService;

    @Mock
    private MessagePostService messagePostService;

    @Mock
    private ChargeAuditBuilder chargeAuditBuilder;

    @Mock
    private ProcessPaymentService processPaymentService;

    @Mock
    private PaymentActivityUpdateService paymentActivityUpdateService;

    @Mock
    private ChargeIntegrationService chargeIntegrationService;

    @Mock
    private ElasticModificationService elasticModificationService;

    @Test
    public void testAfterSaveTrue_Approve() {

        ChargeDTO chargeDTO = getChargeDTO("Approve");
        chargeDTO.setAutoPay(true);
        postChargeDecisionService.afterSave(chargeDTO, getTxnStatusDTO(true), new ActivityDTO(), getPayment());
        verify(chargeAuditBuilder, times(1)).buildChargeAuditDTO(Mockito.any());
        verify(chargeIntegrationService, times(1)).approveCharges(Mockito.any());
        verify(paymentActivityUpdateService, times(1)).updateInvoiceStatus(Mockito.any());
        verify(elasticModificationService, times(1)).updatePaidStatusToElasticDocument(Mockito.any(),Mockito.anyBoolean(),Mockito.any());

    }

    @Test
    public void testAfterSaveTrue_Reject() {

        postChargeDecisionService.afterSave(getChargeDTO("Reject"), getTxnStatusDTO(true), new ActivityDTO(),
                getPayment());
        verify(paymentActivityUpdateService, times(1)).updateInvoiceStatus(Mockito.any());
        verify(elasticModificationService, times(1)).updatePaidStatusToElasticDocument(Mockito.any(),Mockito.anyBoolean(),Mockito.any());
    }

    @Test
    public void testAfterSave_EmptyDecisionCode() {

        ChargeDTO chargeDTO = getChargeDTO("");
        chargeDTO.setJobId(1);
        chargeDTO.setProjectCode("ABCD");
        postChargeDecisionService.afterSave(chargeDTO, getTxnStatusDTO(true), new ActivityDTO(), getPayment());
        verify(paymentActivityUpdateService, times(1)).updateInvoiceStatus(Mockito.any());

    }

    @Test
    public void testAfterSave_EmptyChargeList() {
        postChargeDecisionService.afterSave(getChargeDTO("Reject"), getTxnStatusDTO(true), new ActivityDTO(),
                getPayment());

        verify(paymentActivityUpdateService, times(1)).updateInvoiceStatus(Mockito.any());
        verify(elasticModificationService, times(1)).updatePaidStatusToElasticDocument(Mockito.any(),Mockito.anyBoolean(),Mockito.any());
    }

    @Test
    public void testProcessPaymentFlow() {

        Mockito.when(processPaymentService.processPaymentFromCharge(Mockito.any())).thenReturn(true);

        postChargeDecisionService.processPayment(getTxnStatusDTO(true), getPayment());

        verify(messagePostService, times(1)).postApprovedMessage(Mockito.any());
    }

    private ChargeDTO getChargeDTO(String code) {
        ChargeDTO chargeDTO = ChargeDTOMocks.createChargeDTOMOcks();
        chargeDTO.setChargeIdList(Arrays.asList(1));
        chargeDTO.setChargeDecisionCode(code);
        chargeDTO.setUpdatedTimeList(Arrays.asList(NOW));
        chargeDTO.setInvoiceNumber("234fg548");
        chargeDTO.setHeaderId(1);
        chargeDTO.setPaymentId(1);
        chargeDTO.setInvoiceNumberSuffix("ABC");
        chargeDTO.setLoadNumber("J016999");
        chargeDTO.setQuickPayWaiverIndicator('Y');
        return chargeDTO;
    }

    private Payment getPayment() {
        Payment pay = PaymentEntityMocks.createPaymentEntityMocks();
        pay.setCarrierPaymentId(1);
        pay.setStatusFlowTypeCode("LTLApprv");
        List<Charge> chargeList = new ArrayList<>();
        Charge charge = new Charge();
        charge.setChargeDecisionCode("Approve");
        chargeList.add(charge);
        pay.setCarrierPaymentChargeList(chargeList);
        return pay;
    }

    private TxnStatusDTO getTxnStatusDTO(boolean b) {
        TxnStatusDTO txnStatusDTO = TxnStatusDTOMocks.createTxnStatusDTOMocks();
        txnStatusDTO.setSuccess(b);
        return txnStatusDTO;
    }
}

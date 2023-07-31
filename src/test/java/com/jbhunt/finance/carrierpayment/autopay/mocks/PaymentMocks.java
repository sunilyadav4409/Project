package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.entity.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jbhunt.finance.carrierpayment.autopay.mocks.CarrierInvoiceHeaderMocks.getCarrierInvoiceDetail;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.CarrierInvoiceHeaderMocks.getCarrierInvoiceDetail3;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeMocks.getChargeList;
import static com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeMocks.getChargeList9;

public class PaymentMocks {

    public static Payment getPaymentMock(){
        List<Charge>chargeList = new ArrayList<>();
        Charge firstCharge= new Charge();
        firstCharge.setChargeId(12);
        firstCharge.setChargeCode("bc");
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        chargeList.add(firstCharge);
        Charge secondCharge= new Charge();
        secondCharge.setChargeId(122);
        secondCharge.setChargeCode("bac");
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        secondCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        chargeList.add(secondCharge);
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendDisp");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setCarrierPaymentChargeList(chargeList);
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setVendorPaymentTerm(21);
        return payment;
    }

    public static Payment getPaymentMockTotalApprovedInvAmt(){
        List<Charge>chargeList = new ArrayList<>();
        Charge firstCharge= new Charge();
        firstCharge.setChargeId(12);
        firstCharge.setChargeCode("bc");
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        chargeList.add(firstCharge);
        Charge secondCharge= new Charge();
        secondCharge.setChargeId(122);
        secondCharge.setChargeCode("bac");
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        secondCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        chargeList.add(secondCharge);
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode( CarrierPaymentConstants.PENDING_AP);
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setCarrierPaymentChargeList(chargeList);
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setVendorPaymentTerm(21);
        payment.setTotalApprovedInvoiceAmt( new BigDecimal( "200.00" ));
        return payment;
    }

    public static Payment getPaymentMockFreePay(){
        List<Charge>chargeList = new ArrayList<>();
        Charge firstCharge= new Charge();
        firstCharge.setChargeId(12);
        firstCharge.setChargeCode("bc");
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        chargeList.add(firstCharge);
        Charge secondCharge= new Charge();
        secondCharge.setChargeId(122);
        secondCharge.setChargeCode("bac");
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        secondCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        chargeList.add(secondCharge);
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode( CarrierPaymentConstants.PENDING_AP);
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setCarrierPaymentChargeList(chargeList);
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setVendorPaymentTerm(21);
        payment.setTotalApprovedInvoiceAmt( new BigDecimal( "200.00" ));
        payment.setPaymentMethodTypeCode(CarrierPaymentConstants.FREE_QUICK_PAY );
        return payment;
    }

    public static Payment getPaymentMockEmptyChargeList(){
        List<Charge>chargeList = new ArrayList<>();
        Charge firstCharge= new Charge();
        firstCharge.setChargeId(12);
        firstCharge.setChargeCode("bc");
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        chargeList.add(firstCharge);
        Charge secondCharge= new Charge();
        secondCharge.setChargeId(122);
        secondCharge.setChargeCode("bac");
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        secondCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        chargeList.add(secondCharge);
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode( CarrierPaymentConstants.APPROVE);
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setCarrierPaymentChargeList(new ArrayList<>(  ));
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setVendorPaymentTerm(21);
        payment.setTotalApprovedInvoiceAmt( new BigDecimal( "200.00" ));
        payment.setPaymentMethodTypeCode(CarrierPaymentConstants.FREE_QUICK_PAY );
        return payment;
    }

    public static Payment getPaymentMockApproveStatus(){
        List<Charge>chargeList = new ArrayList<>();
        Charge firstCharge= new Charge();
        firstCharge.setChargeId(12);
        firstCharge.setChargeCode("bc");
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        chargeList.add(firstCharge);
        Charge secondCharge= new Charge();
        secondCharge.setChargeId(122);
        secondCharge.setChargeCode("bac");
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        secondCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        chargeList.add(secondCharge);
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode( CarrierPaymentConstants.APPROVE);
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setCarrierPaymentChargeList(chargeList);
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setVendorPaymentTerm(21);
        payment.setTotalApprovedInvoiceAmt( new BigDecimal( "200.00" ));
        payment.setPaymentMethodTypeCode(CarrierPaymentConstants.FREE_QUICK_PAY );
        return payment;
    }


    public static Payment getPaymentICSMock(){
        List<Charge>chargeList = new ArrayList<>();
        Charge firstCharge= new Charge();
        firstCharge.setChargeId(12);
        firstCharge.setChargeCode(CarrierPaymentConstants.TONU);
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        chargeList.add(firstCharge);
        Charge secondCharge= new Charge();
        secondCharge.setChargeId(122);
        secondCharge.setChargeCode("bac");
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        secondCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        chargeList.add(secondCharge);
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendDisp");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setCarrierPaymentChargeList(chargeList);
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("ICS");
        payment.setVendorPaymentTerm(1);
        payment.setTransitMode(CarrierPaymentConstants.TRANSMIT_MODE_TRAIN);
        return payment;
    }
    public static Payment getPaymentICSDuplicateChargeMock(){

        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendDisp");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setCarrierPaymentChargeList(ChargeMocks.getDuplicateChargeList());
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("ICS");
        payment.setVendorPaymentTerm(1);
        payment.setTransitMode(CarrierPaymentConstants.TRANSMIT_MODE_TRAIN);
        return payment;
    }
    public static Payment getPaymentLTLWithDuplicateChargeMock(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendDisp");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setCarrierPaymentChargeList(ChargeMocks.getChargeList());
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("LTL");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getDuplicateChargeList());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getListOfCarrierInvoiceHeaderMock());
        return payment;
    }
    public static Payment getPaymentLTLWithChargeDecionNullMock(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendDisp");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setCarrierPaymentChargeList(ChargeMocks.getChargeList());
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("LTL");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getChargeListInvoiceWithExpirationDateNotNullANDChargeDecisonCodeNullNumberMock());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getListOfCarrierInvoiceHeaderMock());
        return payment;
    }
    public static Payment getPaymentLTLWithZeroTotalCharge(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendDisp");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setCarrierPaymentChargeList(ChargeMocks.getChargeList());
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("LTL");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getChaargeListWithTotalChargeASZeroTotalApproveChargeAmmount());
        return payment;
    }
    public static Payment getPaymentLTLMock(){
        List<Charge>chargeList = new ArrayList<>();
        Charge firstCharge= new Charge();
        firstCharge.setChargeId(12);
        firstCharge.setChargeCode("bc");
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        chargeList.add(firstCharge);
        Charge secondCharge= new Charge();
        secondCharge.setChargeId(122);
        secondCharge.setChargeCode("bac");
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        secondCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        secondCharge.setChargeDecisionCode("test");
        chargeList.add(secondCharge);
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendDisp");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setCarrierPaymentChargeList(chargeList);
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("LTL");
        payment.setVendorPaymentTerm(1);
        return payment;
    }
    public static Payment getPaymentWithFOrNonLTLRejectedChargeCode(){
        Payment payment = new Payment();
        payment.setCarrierPaymentId(1);
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setGroupFlowTypeCode("NON_LTL");
        payment.setTotalChargeAmount(BigDecimal.valueOf(100.00));
        payment.setCarrierPaymentChargeList(ChargeMocks.getChargeListWithExpirationTimeNullandRejectStatus());
        return payment;
    }
    public static Payment getPayment() {
        Payment payment = new Payment();
        payment.setCarrierPaymentId(1);
        payment.setBillToPartyId("ABCDE");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setGroupFlowTypeCode("NON_LTL");
        payment.setTotalChargeAmount(BigDecimal.valueOf(100.00));
        return payment;
    }
    public static Payment getPaymentAPFail() {
        Payment payment = new Payment();
        payment.setCarrierPaymentId(1);
        payment.setBillToPartyId("ABCDE");
        payment.setStatusFlowTypeCode("Reroute");
        payment.setGroupFlowTypeCode("ICS");
        payment.setTotalChargeAmount(BigDecimal.valueOf(100.00));
        return payment;
    }
    public static Payment getPaymentAPFail1() {
        Payment payment = new Payment();
        payment.setCarrierPaymentId(1);
        payment.setBillToPartyId("ABCDE");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setGroupFlowTypeCode("ICS");
        payment.setTotalChargeAmount(BigDecimal.valueOf(100.00));
        return payment;
    }


    public static  Optional<Payment> chargeDecision() throws IOException {
        Payment pay = new Payment();
        pay.setCarrierPaymentId(1);
        pay.setStatusFlowTypeCode("LTLApprv");
        List<Charge> chargeList = new ArrayList<>();
        Charge charge = new Charge();
        charge.setChargeDecisionCode("Approve");
        chargeList.add(charge);
        pay.setCarrierPaymentChargeList(chargeList);
        return Optional.of(pay);
    }
    public static  Optional<Payment> chargeDecision1() throws IOException {
        Payment pay = new Payment();
        pay.setCarrierPaymentId(1);
        pay.setStatusFlowTypeCode("LTLApprv");
        List<Charge> chargeList = new ArrayList<>();
        Charge charge = new Charge();
        charge.setChargeDecisionCode("Reject");
        chargeList.add(charge);
        pay.setCarrierPaymentChargeList(chargeList);
        return Optional.of(pay);
    }

    public static Payment getConsolidatedPaymentLTLFuel(String status) {
        Payment payment = new Payment();
        payment.setCarrierPaymentId(1);
        payment.setGroupFlowTypeCode("LTL");
        payment.setScacCode("MSKD");
        payment.setLoadNumber("JW23456");
        payment.setDispatchNumber("1");
        payment.setCarrierPaymentChargeList(getChargeList9());
        List<CarrierInvoiceHeader> invoiceList = new ArrayList<>();
        CarrierInvoiceHeader invoiceHeader = new CarrierInvoiceHeader();
        invoiceHeader.setCarrierInvoiceHeaderId(1);
        invoiceHeader.setCarrierInvoiceNumber( "I12" );
        invoiceHeader.setInvoiceDate( LocalDateTime.now() );
        invoiceHeader.setCarrierInvoiceDetailList( getCarrierInvoiceDetail3() );
        invoiceHeader.setInvoiceSourceTypeCode("EDI");
        invoiceHeader.setAutoApproveEligibleInd( "Y" );
        invoiceList.add(invoiceHeader);
        payment.setCarrierInvoiceHeaderList(invoiceList);
        payment.setStatusFlowTypeCode(status);
        return payment;
    }

    public static Payment CheckRulesForAutoPayChargeCode(BigDecimal overrideAmt, BigDecimal totalCharge) {
        Payment payment = new Payment();
        List<Charge> chargeList = new ArrayList<>();
        Charge ch = new Charge();
        ChargeOverride override = new ChargeOverride();
        override.setOverrideAmount(overrideAmt);
        ch.setChargeOverride(override);
        ch.setTotalChargeAmount(totalCharge);
        ch.setTotalApprovedChargeAmount(totalCharge);
        ch.setChargeCode("TRANSIT");
        chargeList.add(ch);
        payment.setGroupFlowTypeCode("ABC");
        payment.setCarrierPaymentChargeList(chargeList);
        List<CarrierInvoiceHeader> invoiceList = new ArrayList<>();
        CarrierInvoiceHeader invoiceHeader = new CarrierInvoiceHeader();
        invoiceHeader.setInvoiceSourceTypeCode("EDI");
        CarrierInvoiceHeader paperInvoiceHeader = new CarrierInvoiceHeader();
        paperInvoiceHeader.setInvoiceSourceTypeCode("Paper");
        paperInvoiceHeader.setInvoiceStatusCode("");
        invoiceList.add(paperInvoiceHeader);
        payment.setCarrierInvoiceHeaderList(invoiceList);
        return payment;
    }

    public static Payment getConsolidatedPaymentLTL(String status) {
        Payment payment = new Payment();
        payment.setCarrierPaymentId(1);
        payment.setGroupFlowTypeCode("LTL");
        payment.setScacCode("MSKD");
        payment.setLoadNumber("JW23456");
        payment.setDispatchNumber("1");
        payment.setCarrierPaymentChargeList(getChargeList());
        List<CarrierInvoiceHeader> invoiceList = new ArrayList<>();
        CarrierInvoiceHeader invoiceHeader = new CarrierInvoiceHeader();
        invoiceHeader.setCarrierInvoiceHeaderId(1);
        invoiceHeader.setCarrierInvoiceNumber( "I12" );
        invoiceHeader.setInvoiceDate( LocalDateTime.now() );
        invoiceHeader.setCarrierInvoiceDetailList( getCarrierInvoiceDetail() );
        invoiceHeader.setInvoiceSourceTypeCode("EDI");
        invoiceHeader.setAutoApproveEligibleInd( "Y" );
        invoiceList.add(invoiceHeader);
        payment.setCarrierInvoiceHeaderList(invoiceList);
        payment.setStatusFlowTypeCode(status);
        return payment;
    }

    public static Payment getPaymentJBIWithChargeDecionNullMock(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setDispatchMiles(400);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeListInvoiceWithExpirationDateNotNullANDChargeDecisonCodeNullNumberMock());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getJBIListOfCarrierInvoiceHeaderMock());
        return payment;
    }
    public static Payment getPaymentJBIWithChargeDecionNullMock21(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeListInvoiceWithExpirationDateNotNullAisonCodeNullNumberMock());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getJBIListOfCarrierInvoiceHeaderMock());
        return payment;
    }
    public static Payment getPaymentJBIWithChargeDecionMock(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeListInvoiceWithExpirationDateNotMock());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithoutPaymentMock());
        return payment;
    }
    public static Payment getPaymentJBIWithChargeDecionMock12(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeListInvoiceWithExpirationDateNotMock12());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithoutPaymentMock());
        return payment;
    }
    public static Payment getPaymentJBIWithChargeDecionMock20(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeListInvoiceWithExpirationDateNotMock20());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithoutPaymentMock20());
        return payment;
    }
    public static Payment getPaymentJBIWithChargeDecionMock22(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeListInvoiceWithExpirationDateNotMock22());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithoutPaymentMock22());
        return payment;
    }
    public static Payment getPaymentJBIWithChargeDecionMock25(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeListInvoiceWithExpirationDateNotMock25());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithoutPaymentMock22());
        return payment;
    }
    public static Payment getPaymentJBIWithDuplicatesMock(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeListInvoiceWithDuplicatesMock());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithoutPaymentMock());
        return payment;
    }
    public static Payment getPaymentJBIWithDuplicatesMock1(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeListInvoiceWithDuplicatesMock1());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithoutPaymentMock());
        return payment;
    }
    public static Payment getPaymentJBIWithDuplicatesMock2(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeListInvoiceWithDuplicatesMock5());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithoutPaymentMock());
        return payment;
    }
    public static Payment getPaymentJBIWithDuplicatesMock25(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setScacCode("ABCD");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeListInvoiceWithDuplicatesMock1());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderListMatch());
        return payment;
    }
    public static Payment getPaymentJBIWithDuplicatesMock4(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("03");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeListInvoiceWithDuplicatesMock4());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithoutPaymentMock4());
        return payment;
    }
    public static Payment getPaymentJBIWithJbisMock() {
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeListInvoiceWithExpirationDateNotMock());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderWithoutPaymentMock());
        return payment;
    }
    public static Payment getPaymentJBIWithChargeDecionMock5(){
        Payment payment = new Payment();
        payment.setBillToPartyId("test");
        payment.setCarrierPaymentId(1234);
        payment.setDispatchNumber("01");
        payment.setBillToPartyId("234");
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setDispatchStatus("PendDisp");
        payment.setDispatchYear(2019);
        payment.setFleetCode("hgdnaf");
        payment.setProjectCode("hjmgf");
        payment.setEstimatedDueDate(LocalDate.now());
        payment.setPaymentMethodTypeCode("QuickPay");
        payment.setGroupFlowTypeCode("JBI");
        payment.setVendorPaymentTerm(1);
        payment.setCarrierPaymentChargeList(ChargeMocks.getJbiChargeTransportationGroupChargeApprovedMock());
        payment.setCarrierInvoiceHeaderList(CarrierInvoiceHeaderMocks.getCarrierInvoiceHeaderApprovedFuelMock());
        return payment;
    }

    public static Payment getPaymentRequiredDocumentProcessed() {
        Payment payment = new Payment();
        payment.setCarrierPaymentId(1);
        payment.setStatusFlowTypeCode("PendingAP");
        payment.setGroupFlowTypeCode("NON_LTL");
        payment.setCrtS(LocalDateTime.now().minusMinutes(2));
        payment.setGroupFlowTypeCode("JBI");
        payment.setDispatchNumber("1");
        payment.setTotalChargeAmount(BigDecimal.valueOf(100.00));
        List<CarrierInvoiceHeader> invoiceList = new ArrayList<>();
        CarrierInvoiceHeader invoiceHeader = new CarrierInvoiceHeader();
        invoiceHeader.setInvoiceSourceTypeCode("Paper");
        invoiceHeader.setInvoiceStatusCode("Processed");
        invoiceList.add(invoiceHeader);
        payment.setCarrierInvoiceHeaderList(invoiceList);
        return payment;
    }
}

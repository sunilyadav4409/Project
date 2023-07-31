package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.ChargeOverride;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChargeMocks {

    public static List<Charge> getChaargeListMock() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("110.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("110.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setChargeCode("ywehg");
        firstCharge.setStopNumber(01);
        firstCharge.setExternalChargeID(1234);
        firstCharge.setChargeApplyToCustomerIndicator('Y');
        firstCharge.setExternalChargeBillingID(12432);
        firstCharge.setInvoiceNumberSuffix("13243");
        ChargeOverride chargeOverride = new ChargeOverride();
        chargeOverride.setOverrideAmount(new BigDecimal("120.00"));
        chargeOverride.setCharge(firstCharge);
        chargeOverride.setChargeId(1234);
        firstCharge.setChargeOverride(chargeOverride);
        Charge secondCharge = new Charge();
        chargeList.add(firstCharge);
        secondCharge.setChargeUnitRateAmount(new BigDecimal("80.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("90.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("110.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setChargeCode("uyagfs");
        secondCharge.setStopNumber(02);
        secondCharge.setInvoiceNumberSuffix("3434");
        chargeList.add(secondCharge);
        return chargeList;
    }

    public static List<Charge> getChaargeListWithTotalChargeASZeroMock() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("110.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setChargeCode("ywehg");
        firstCharge.setStopNumber(01);
        firstCharge.setExternalChargeID(1234);
        firstCharge.setChargeApplyToCustomerIndicator('Y');
        firstCharge.setExternalChargeBillingID(12432);
        ChargeOverride chargeOverride = new ChargeOverride();
        chargeOverride.setOverrideAmount(new BigDecimal("0"));
        chargeOverride.setCharge(firstCharge);
        chargeOverride.setChargeId(1234);
        firstCharge.setChargeOverride(chargeOverride);
        Charge secondCharge = new Charge();
        chargeList.add(firstCharge);
        secondCharge.setChargeUnitRateAmount(new BigDecimal("80.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("90.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setChargeCode("uyagfs");
        secondCharge.setStopNumber(02);
        chargeList.add(secondCharge);
        return chargeList;
    }

    public static List<Charge> getChaargeListWithTotalChargeASZeroAndSameChargeCodeAsInvoiceMock() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("110.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setChargeCode(CarrierInvoiceHeaderMocks.createMock().getCarrierInvoiceDetailList().get(0).getChargeCode());
        firstCharge.setStopNumber(01);
        firstCharge.setExternalChargeID(1234);
        firstCharge.setChargeApplyToCustomerIndicator('Y');
        firstCharge.setExternalChargeBillingID(12432);
        ChargeOverride chargeOverride = new ChargeOverride();
        chargeOverride.setOverrideAmount(new BigDecimal("0"));
        chargeOverride.setCharge(firstCharge);
        chargeOverride.setChargeId(1234);
        firstCharge.setChargeOverride(chargeOverride);
        Charge secondCharge = new Charge();
        chargeList.add(firstCharge);
        secondCharge.setChargeUnitRateAmount(new BigDecimal("80.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("90.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setChargeCode(CarrierInvoiceHeaderMocks.createMock().getCarrierInvoiceDetailList().get(1).getChargeCode());
        secondCharge.setStopNumber(02);
        chargeList.add(secondCharge);
        return chargeList;
    }

    public static List<Charge> getChaargeListWithTotalChargeASZeroTotalApproveChargeAmmount() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setChargeCode(CarrierInvoiceHeaderMocks.createMock().getCarrierInvoiceDetailList().get(0).getChargeCode());
        firstCharge.setStopNumber(01);
        firstCharge.setExternalChargeID(1234);
        firstCharge.setChargeApplyToCustomerIndicator('Y');
        firstCharge.setExternalChargeBillingID(12432);
        ChargeOverride chargeOverride = new ChargeOverride();
        chargeOverride.setOverrideAmount(new BigDecimal("0"));
        chargeOverride.setCharge(firstCharge);
        chargeOverride.setChargeId(1234);
        firstCharge.setChargeOverride(chargeOverride);
        Charge secondCharge = new Charge();
        chargeList.add(firstCharge);
        secondCharge.setChargeUnitRateAmount(new BigDecimal("80.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setChargeCode("fuel");
        secondCharge.setStopNumber(02);
        chargeList.add(secondCharge);
        return chargeList;
    }

    public static List<Charge> getChaargeListInvoiceNumberMock() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("110.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("110.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setInvoiceNumberSuffix("A");
        chargeList.add(firstCharge);
        Charge secondCharge = new Charge();
        secondCharge.setChargeUnitRateAmount(new BigDecimal("80.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("90.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("110.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setInvoiceNumberSuffix("B");
        chargeList.add(secondCharge);
        return chargeList;
    }

    public static List<Charge> getChargeListInvoiceWithExpirationDateNotNullANDChargeDecisonCodeNullNumberMock() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("110.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("110.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode(null);
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("FUELSURCHG");
        chargeList.add(firstCharge);
        Charge secondCharge = new Charge();
        secondCharge.setChargeUnitRateAmount(new BigDecimal("80.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("90.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("110.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setInvoiceNumberSuffix("B");
        secondCharge.setChargeDecisionCode(null);
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setChargeCode("test");
        chargeList.add(secondCharge);
        return chargeList;
    }

    public static Charge getCharge1(Integer id) {
        Charge charge = new Charge();
        CarrierInvoiceHeader header = new CarrierInvoiceHeader();
        header.setCarrierInvoiceHeaderId(1);
        charge.setChargeId(id);
        charge.setChargeCode("LUMPLDPAY");
        charge.setChargeOverride(getChargeOverride());
        charge.setLstUpdS(NOW);
        charge.setExternalChargeID(1);
        charge.setCarrierInvoiceHeader(header);
        return charge;
    }

    public static ChargeOverride getChargeOverride() {
        ChargeOverride chargeOverride = new ChargeOverride();
        return chargeOverride;
    }

    private static final LocalDateTime NOW = LocalDateTime.now();

    public static List<Charge> getChargeList(LocalDateTime dTime) {
        List<Charge> carrierPaymentChargeList = new ArrayList<>();
        Charge charge = new Charge();
        charge.setExpirationTimestamp(dTime);
        charge.setTotalApprovedChargeAmount(BigDecimal.TEN);
        carrierPaymentChargeList.add(charge);
        return carrierPaymentChargeList;
    }


    public static List<Charge> getChargeList() {
        List<Charge> listcharge = new ArrayList<>();
        Charge charge = new Charge();
        charge.setChargeId(1);
        charge.setChargeDecisionCode("Approve");
        charge.setExpirationTimestamp(LocalDateTime.now());
        charge.setInvoiceNumberSuffix("ABC");
        charge.setChargeCode("TRANSIT");
        listcharge.add(charge);
        return listcharge;
    }

    public static List<Charge> getDuplicateChargeList() {
        List<Charge> listcharge = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeId(12);
        firstCharge.setChargeCode(CarrierPaymentConstants.TONU);
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        listcharge.add(firstCharge);
        Charge secondCharge = new Charge();
        secondCharge.setChargeId(122);
        secondCharge.setChargeCode(CarrierPaymentConstants.TONU);
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        secondCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        listcharge.add(secondCharge);
        return listcharge;
    }

    public static List<Charge> getChargeListWithExpirationTimeNullandRejectStatus() {
        List<Charge> listcharge = new ArrayList<>();
        Charge charge = new Charge();
        charge.setChargeId(1);
        charge.setChargeDecisionCode(CarrierPaymentConstants.REJECT_STATUS);
        charge.setExpirationTimestamp(null);
        charge.setInvoiceNumberSuffix("ABC");
        listcharge.add(charge);
        return listcharge;
    }

    public static Charge getCharge(Integer id) {
        Charge charge = new Charge();
        CarrierInvoiceHeader header = new CarrierInvoiceHeader();
        header.setCarrierInvoiceHeaderId(1);
        charge.setChargeId(id);
        charge.setChargeCode("LUMPLDPAY");
        charge.setChargeOverride(getChargeOverride1());
        charge.setLstUpdS(NOW);
        charge.setCarrierInvoiceHeader(header);
        charge.setStopNumber(1);
        charge.setChargeDecisionCode("Approve");
        return charge;
    }

    public static List<Charge> getChargeCodeValidation(Integer id) {
        List<Charge> listcharge = new ArrayList<>();

        Charge charge = new Charge();
        CarrierInvoiceHeader header = new CarrierInvoiceHeader();
        header.setCarrierInvoiceHeaderId(1);
        charge.setChargeId(id);
        charge.setChargeCode("AMS");
        charge.setChargeOverride(getChargeOverride1());
        charge.setLstUpdS(NOW);
        charge.setCarrierInvoiceHeader(header);
        charge.setStopNumber(1);
        listcharge.add(charge);
        Charge secondCharge = new Charge();
        secondCharge.setChargeId(122);
        secondCharge.setChargeCode("TRANSIT");
        secondCharge.setExpirationTimestamp(null);
        listcharge.add(secondCharge);
        return listcharge;
    }
    public static List<Charge> getChargeCodeValidation2(Integer id) {
        List<Charge> listcharge = new ArrayList<>();

        Charge charge = new Charge();
        CarrierInvoiceHeader header = new CarrierInvoiceHeader();
        header.setCarrierInvoiceHeaderId(1);
        charge.setChargeId(id);
        charge.setChargeCode("TRANSIT");
        charge.setChargeOverride(getChargeOverride1());
        charge.setLstUpdS(NOW);
        charge.setCarrierInvoiceHeader(header);
        charge.setStopNumber(1);
        listcharge.add(charge);
        Charge secondCharge = new Charge();
        secondCharge.setChargeId(122);
        secondCharge.setChargeCode("FUELSURCHG");
        secondCharge.setExpirationTimestamp(null);
        listcharge.add(secondCharge);
        return listcharge;
    }


    public static ChargeOverride getChargeOverride1() {
        ChargeOverride chargeOverride = new ChargeOverride();
        chargeOverride.setChargeOverrideId(1);
        return chargeOverride;
    }

    public static Charge getCharge() {
        Charge charge = new Charge();
        charge.setExternalChargeID(1);
        charge.setChargeId(2);
        charge.setStopNumber(1000);
        return charge;
    }


    public static List<Charge> getChargeList9() {
        List<Charge> list = new ArrayList<>();
        //Charge charge = new Charge();
        //charge.setLstUpdS(LocalDateTime.now());
        list.add(getCharge_1(1, "Transit", 90));
        //Charge charge1 = new Charge();
        //charge1.setLstUpdS(LocalDateTime.MIN);
        list.add(getCharge_1(2, "FUELSURCHG", 100));
        return list;
    }


    public static List<Charge> getChargeList10() {
        List<Charge> list = new ArrayList<>();
        //Charge charge = new Charge();
        //charge.setLstUpdS(LocalDateTime.now());
        list.add(getCharge_1(1, "Transit", 90));
        Charge charge1 = getCharge_1(2, "FUELSURCHG", 410); //FUELSURCHG
        charge1.setChargeOverride(null);
        //charge1.setLstUpdS(LocalDateTime.MIN);
        list.add(charge1);
        list.add(getCharge_1(2, "LUMPLDPAY", 150));
        return list;
    }

    public static List<Charge> getChargeList11() {
        List<Charge> list = new ArrayList<>();
        //Charge charge = new Charge();
        //charge.setLstUpdS(LocalDateTime.now());
        //  list.add(getCharge_1(1,"Transit",90));
        Charge charge1 = getCharge_1(2, "FUELSURCHG", 250);
        charge1.setChargeOverride(null);
        //charge1.setLstUpdS(LocalDateTime.MIN);
        list.add(charge1);
        list.add(getCharge_1(2, "LUMPLDPAY", 160));
        return list;
    }


    static Charge getCharge_1(Integer id, String code, int amount) {
        Charge charge = new Charge();
        charge.setChargeOverride(getChargeOverride());
        charge.setExternalChargeID(id);
        charge.setChargeId(1);
        charge.setStopNumber(0);
        charge.setTotalApprovedChargeAmount(new BigDecimal(amount));
        charge.setExpirationTimestamp(null);
        charge.setChargeDecisionCode(null);
        charge.setChargeCode(code);
        return charge;
    }

    public static List<Charge> getJbiChargeListInvoiceWithExpirationDateNotNullANDChargeDecisonCodeNullNumberMock() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("110.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("110.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode(null);
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("FUELSURCHG");
        chargeList.add(firstCharge);

        return chargeList;
    }

    public static List<Charge> getJbiChargeListInvoiceWithExpirationDateNotNullAisonCodeNullNumberMock() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("10.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("10.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode(null);
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("STOPOFF");
        chargeList.add(firstCharge);
        Charge secondCharge = new Charge();
        secondCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("110.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("110.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setChargeDecisionCode(null);
        secondCharge.setInvoiceNumberSuffix("A");
        secondCharge.setChargeCode("CARTAGE");
        chargeList.add(secondCharge);

        return chargeList;
    }

    public static List<Charge> getJbiChargeListInvoiceWithExpirationDateNotMock() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("110.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("110.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode(null);
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("TRANSIT");
        chargeList.add(firstCharge);
        return chargeList;
    }

    public static List<Charge> getJbiChargeTransportationGroupChargeApprovedMock() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode(null);
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("TRANSIT");
        chargeList.add(firstCharge);
        Charge secondCharge = new Charge();
        secondCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setChargeDecisionCode(null);
        secondCharge.setInvoiceNumberSuffix("A");
        secondCharge.setChargeDecisionCode("Approve");
        secondCharge.setChargeCode("FUELSURCHG");
        chargeList.add(secondCharge);
        return chargeList;
    }
    public static List<Charge> getJbiChargeTransportationGroupChargeApprovedMockAMS() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("50.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode(null);
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("AMS");
        chargeList.add(firstCharge);
        return chargeList;
    }
    public static List<Charge> getJbiChargeTransportationGroupChargeApprovedMockAMSMatch() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("150.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode(null);
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("AMS");
        chargeList.add(firstCharge);
        return chargeList;
    }


    public static List<Charge> getJbiChargeListInvoiceWithExpirationDateNotMock12() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("110.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("110.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode(null);
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("TRANSIT");
        firstCharge.setChargeId(458745);
        chargeList.add(firstCharge);

        Charge secondCharge = new Charge();
        secondCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setChargeDecisionCode(null);
        secondCharge.setInvoiceNumberSuffix("A");
        secondCharge.setChargeCode("TRANSIT");
        secondCharge.setChargeId(45852);
        chargeList.add(secondCharge);

        Charge thirdCharge = new Charge();
        thirdCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        thirdCharge.setTotalApprovedChargeAmount(new BigDecimal("10.00"));
        thirdCharge.setTotalChargeAmount(new BigDecimal("10.00"));
        thirdCharge.setLstUpdS(LocalDateTime.now());
        thirdCharge.setExpirationTimestamp(null);
        thirdCharge.setChargeDecisionCode(null);
        thirdCharge.setInvoiceNumberSuffix("A");
        thirdCharge.setChargeCode("STOPOFF");
        thirdCharge.setChargeId(369845);
        chargeList.add(thirdCharge);

        Charge fourthCharge = new Charge();
        fourthCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        fourthCharge.setTotalApprovedChargeAmount(new BigDecimal("10.00"));
        fourthCharge.setTotalChargeAmount(new BigDecimal("10.00"));
        fourthCharge.setLstUpdS(LocalDateTime.now());
        fourthCharge.setExpirationTimestamp(null);
        fourthCharge.setChargeDecisionCode(null);
        fourthCharge.setInvoiceNumberSuffix("A");
        fourthCharge.setChargeCode("STOPOFF");
        fourthCharge.setChargeId(52558);
        chargeList.add(fourthCharge);

        Charge fifthCharge = new Charge();
        fifthCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        fifthCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        fifthCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        fifthCharge.setLstUpdS(LocalDateTime.now());
        fifthCharge.setExpirationTimestamp(null);
        fifthCharge.setChargeDecisionCode(null);
        fifthCharge.setInvoiceNumberSuffix("A");
        fifthCharge.setChargeCode("CALL");
        fifthCharge.setChargeId(696969);
        chargeList.add(fifthCharge);

        Charge sixthCharge = new Charge();
        sixthCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        sixthCharge.setTotalApprovedChargeAmount(new BigDecimal("10.00"));
        sixthCharge.setTotalChargeAmount(new BigDecimal("10.00"));
        sixthCharge.setLstUpdS(LocalDateTime.now());
        sixthCharge.setExpirationTimestamp(null);
        sixthCharge.setChargeDecisionCode(null);
        sixthCharge.setInvoiceNumberSuffix("A");
        sixthCharge.setChargeCode("CARTAGE");
        sixthCharge.setChargeId(252555);
        chargeList.add(sixthCharge);
        return chargeList;
    }

    public static List<Charge> getJbiChargeListInvoiceWithDuplicatesMock() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        Charge secondCharge = new Charge();
        Charge thirdCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("110.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("110.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode(null);
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("TRANSIT");
        chargeList.add(firstCharge);
        secondCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("10.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("10.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setChargeDecisionCode(null);
        secondCharge.setInvoiceNumberSuffix("A");
        secondCharge.setChargeCode("STOPOFF");
        chargeList.add(secondCharge);
        thirdCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        thirdCharge.setTotalApprovedChargeAmount(new BigDecimal("10.00"));
        thirdCharge.setTotalChargeAmount(new BigDecimal("10.00"));
        thirdCharge.setLstUpdS(LocalDateTime.now());
        thirdCharge.setExpirationTimestamp(null);
        thirdCharge.setChargeDecisionCode(null);
        thirdCharge.setInvoiceNumberSuffix("A");
        thirdCharge.setChargeCode("STOPOFF");
        chargeList.add(thirdCharge);
        return chargeList;
    }

    public static List<Charge> getJbiChargeListInvoiceWithDuplicatesMock1() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        Charge secondCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("10.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("110.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode(null);
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("TRANSIT");
        firstCharge.setChargeId(45877);
        chargeList.add(firstCharge);
        secondCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("10.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("10.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setChargeDecisionCode(null);
        secondCharge.setInvoiceNumberSuffix("A");
        secondCharge.setChargeCode("STOPOFF");
        secondCharge.setChargeId(85214);
        chargeList.add(secondCharge);
        Charge thirdCharge = new Charge();
        thirdCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        thirdCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        thirdCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        thirdCharge.setLstUpdS(LocalDateTime.now());
        thirdCharge.setExpirationTimestamp(null);
        thirdCharge.setChargeDecisionCode(null);
        thirdCharge.setInvoiceNumberSuffix("A");
        thirdCharge.setChargeId(98587);
        thirdCharge.setChargeCode("CALL");
        chargeList.add(thirdCharge);
        return chargeList;
    }

    public static List<Charge> getJbiChargeListInvoiceWithDuplicatesMock4() {
        List<Charge> chargeList = new ArrayList<>();
        Charge secondCharge = new Charge();
        secondCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("10.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("10.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setChargeDecisionCode(null);
        secondCharge.setInvoiceNumberSuffix("A");
        secondCharge.setChargeCode("STOPOFF");
        chargeList.add(secondCharge);
        return chargeList;
    }

    public static List<Charge> getJbiChargeListInvoiceWithDuplicatesMock5() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        Charge secondCharge = new Charge();
        Charge thirdCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("10.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("10.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("10.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode(null);
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("TRANSIT");
        firstCharge.setChargeId(56895);
        chargeList.add(firstCharge);
        secondCharge.setChargeUnitRateAmount(new BigDecimal("5.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("5.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("5.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setChargeDecisionCode(null);
        secondCharge.setInvoiceNumberSuffix("A");
        secondCharge.setChargeId(25874);
        secondCharge.setChargeCode("STOPOFF");
        chargeList.add(secondCharge);
        thirdCharge.setChargeUnitRateAmount(new BigDecimal("5.00"));
        thirdCharge.setTotalApprovedChargeAmount(new BigDecimal("5.00"));
        thirdCharge.setTotalChargeAmount(new BigDecimal("5.00"));
        thirdCharge.setLstUpdS(LocalDateTime.now());
        thirdCharge.setExpirationTimestamp(null);
        thirdCharge.setChargeDecisionCode(null);
        thirdCharge.setInvoiceNumberSuffix("A");
        thirdCharge.setChargeCode("STOPOFF");
        thirdCharge.setChargeId(21458);
        chargeList.add(thirdCharge);

        Charge fourthCharge = new Charge();
        fourthCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        fourthCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        fourthCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        fourthCharge.setLstUpdS(LocalDateTime.now());
        fourthCharge.setExpirationTimestamp(null);
        fourthCharge.setChargeDecisionCode(null);
        fourthCharge.setInvoiceNumberSuffix("A");
        fourthCharge.setChargeCode("TONU");
        fourthCharge.setChargeId(123587);
        chargeList.add(fourthCharge);
        return chargeList;
    }

    public static List<Charge> getJbiChargeListInvoiceWithExpirationDateNotMock20() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode(null);
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("TRANSIT");
        firstCharge.setChargeId(12345);
        firstCharge.setChargeId(56895);
        chargeList.add(firstCharge);

        Charge secondCharge = new Charge();
        secondCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setChargeDecisionCode(null);
        secondCharge.setInvoiceNumberSuffix("A");
        secondCharge.setChargeCode("FUELSURCHG");
        secondCharge.setChargeId(123569);
        secondCharge.setChargeId(54887);
        chargeList.add(secondCharge);

        Charge thirdCharge = new Charge();
        thirdCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        thirdCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        thirdCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        thirdCharge.setLstUpdS(LocalDateTime.now());
        thirdCharge.setExpirationTimestamp(null);
        thirdCharge.setChargeDecisionCode(null);
        thirdCharge.setInvoiceNumberSuffix("A");
        thirdCharge.setChargeDecisionCode("Approve");
        thirdCharge.setChargeCode("STOPOFF");
        thirdCharge.setChargeId(12348);
        thirdCharge.setChargeId(48751);
        chargeList.add(thirdCharge);

        Charge fourthCharge = new Charge();
        fourthCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        fourthCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        fourthCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        fourthCharge.setLstUpdS(LocalDateTime.now());
        fourthCharge.setExpirationTimestamp(null);
        fourthCharge.setChargeDecisionCode("PAID");
        fourthCharge.setChargeDecisionCode(null);
        fourthCharge.setInvoiceNumberSuffix("A");
        fourthCharge.setChargeCode("LAY");
        fourthCharge.setChargeId(12845);
        chargeList.add(fourthCharge);

        Charge fifthCharge = new Charge();
        fifthCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        fifthCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        fifthCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        fifthCharge.setLstUpdS(LocalDateTime.now());
        fifthCharge.setExpirationTimestamp(null);
        fifthCharge.setChargeDecisionCode(null);
        fifthCharge.setInvoiceNumberSuffix("A");
        fifthCharge.setChargeCode("CALL");
        fifthCharge.setChargeId(72345);
        chargeList.add(fifthCharge);

        Charge sixthCharge = new Charge();
        sixthCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        sixthCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        sixthCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        sixthCharge.setLstUpdS(LocalDateTime.now());
        sixthCharge.setExpirationTimestamp(null);
        sixthCharge.setChargeDecisionCode(null);
        sixthCharge.setInvoiceNumberSuffix("A");
        sixthCharge.setChargeCode("CARTAGE");
        sixthCharge.setChargeId(56895);
        chargeList.add(sixthCharge);
        return chargeList;
    }

    public static List<Charge> getJbiChargeListInvoiceWithExpirationDateNotMock22() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode(null);
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("TRANSIT");
        firstCharge.setChargeId(12345);
        chargeList.add(firstCharge);

        Charge secondCharge = new Charge();
        secondCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setChargeDecisionCode(null);
        secondCharge.setInvoiceNumberSuffix("A");
        secondCharge.setChargeCode("FUELSURCHG");
        secondCharge.setChargeId(123569);
        chargeList.add(secondCharge);

        Charge thirdCharge = new Charge();
        thirdCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        thirdCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        thirdCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        thirdCharge.setLstUpdS(LocalDateTime.now());
        thirdCharge.setExpirationTimestamp(null);
        thirdCharge.setChargeDecisionCode(null);
        thirdCharge.setExpirationTimestamp(LocalDateTime.now());
        thirdCharge.setInvoiceNumberSuffix("A");
        thirdCharge.setChargeCode("STOPOFF");
        thirdCharge.setChargeId(12348);
        chargeList.add(thirdCharge);

        Charge fourthCharge = new Charge();
        fourthCharge.setChargeUnitRateAmount(new BigDecimal("150.00"));
        fourthCharge.setTotalApprovedChargeAmount(new BigDecimal("150.00"));
        fourthCharge.setTotalChargeAmount(new BigDecimal("150.00"));
        fourthCharge.setLstUpdS(LocalDateTime.now());
        fourthCharge.setExpirationTimestamp(null);
        fourthCharge.setChargeDecisionCode(null);
        fourthCharge.setExpirationTimestamp(null);
        fourthCharge.setInvoiceNumberSuffix("A");
        fourthCharge.setChargeCode("LAY");
        fourthCharge.setChargeId(12845);
        chargeList.add(fourthCharge);

        Charge fifthCharge = new Charge();
        fifthCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        fifthCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        fifthCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        fifthCharge.setLstUpdS(LocalDateTime.now());
        fifthCharge.setExpirationTimestamp(null);
        fifthCharge.setChargeDecisionCode(null);
        fifthCharge.setInvoiceNumberSuffix("A");
        fifthCharge.setChargeCode("CALL");
        fifthCharge.setChargeId(72345);
        chargeList.add(fifthCharge);

        Charge sixthCharge = new Charge();
        sixthCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        sixthCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        sixthCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        sixthCharge.setLstUpdS(LocalDateTime.now());
        sixthCharge.setExpirationTimestamp(null);
        sixthCharge.setChargeDecisionCode(null);
        sixthCharge.setInvoiceNumberSuffix("A");
        sixthCharge.setChargeCode("CARTAGE");
        sixthCharge.setChargeId(56895);
        chargeList.add(sixthCharge);
        return chargeList;
    }

    public static List<Charge> getJbiChargeListInvoiceWithExpirationDateNotMock25() {
        List<Charge> chargeList = new ArrayList<>();
        Charge firstCharge = new Charge();
        firstCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        firstCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        firstCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        firstCharge.setLstUpdS(LocalDateTime.now());
        firstCharge.setExpirationTimestamp(null);
        firstCharge.setChargeDecisionCode("Approve");
        firstCharge.setInvoiceNumberSuffix("A");
        firstCharge.setChargeCode("TRANSIT");
        firstCharge.setChargeId(12345);
        chargeList.add(firstCharge);

        Charge secondCharge = new Charge();
        secondCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        secondCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        secondCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        secondCharge.setLstUpdS(LocalDateTime.now());
        secondCharge.setExpirationTimestamp(null);
        secondCharge.setChargeDecisionCode("Approve");
        secondCharge.setInvoiceNumberSuffix("A");
        secondCharge.setChargeCode("FUELSURCHG");
        secondCharge.setChargeId(123569);
        chargeList.add(secondCharge);

        Charge thirdCharge = new Charge();
        thirdCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        thirdCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        thirdCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        thirdCharge.setLstUpdS(LocalDateTime.now());
        thirdCharge.setExpirationTimestamp(null);
        thirdCharge.setChargeDecisionCode(null);
        thirdCharge.setExpirationTimestamp(LocalDateTime.now());
        thirdCharge.setInvoiceNumberSuffix("A");
        thirdCharge.setChargeCode("STOPOFF");
        thirdCharge.setChargeId(12348);
        chargeList.add(thirdCharge);

        Charge fourthCharge = new Charge();
        fourthCharge.setChargeUnitRateAmount(new BigDecimal("100.00"));
        fourthCharge.setTotalApprovedChargeAmount(new BigDecimal("100.00"));
        fourthCharge.setTotalChargeAmount(new BigDecimal("100.00"));
        fourthCharge.setLstUpdS(LocalDateTime.now());
        fourthCharge.setExpirationTimestamp(null);
        fourthCharge.setChargeDecisionCode(null);
        fourthCharge.setExpirationTimestamp(null);
        fourthCharge.setInvoiceNumberSuffix("A");
        fourthCharge.setChargeCode("LAY");
        fourthCharge.setChargeId(12845);
        chargeList.add(fourthCharge);

        Charge fifthCharge = new Charge();
        fifthCharge.setChargeUnitRateAmount(new BigDecimal("0.00"));
        fifthCharge.setTotalApprovedChargeAmount(new BigDecimal("0.00"));
        fifthCharge.setTotalChargeAmount(new BigDecimal("0.00"));
        fifthCharge.setLstUpdS(LocalDateTime.now());
        fifthCharge.setExpirationTimestamp(null);
        fifthCharge.setChargeDecisionCode(null);
        fifthCharge.setInvoiceNumberSuffix("A");
        fifthCharge.setChargeCode("CALL");
        fifthCharge.setChargeId(72345);
        chargeList.add(fifthCharge);
        return chargeList;

    }
}

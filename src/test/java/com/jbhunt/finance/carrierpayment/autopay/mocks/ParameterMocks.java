package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ParameterListingDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParameterMocks {
    public  static List<ParameterListingDTO> getParameterMocksForAutoApprove(){
        List<ParameterListingDTO> paramList = new ArrayList<>(  );
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(new BigDecimal(0.00));
        param.setParameterSpecificationTypeCode( CarrierPaymentConstants.SPECIFICATION_AUTOPAY_LMT);
        param.setSpecificationSub( "TRANSIT" );
        param.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        paramList.add( param );
        return paramList;
    }
    public  static List<ParameterListingDTO> getParameterMocksForAutoApproveListMatch(){
        List<ParameterListingDTO> paramList = new ArrayList<>();
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(new BigDecimal(250.00));
        param.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        //  param.setSpecificationSub( "test" );
        param.setParameterSpecificationTypeCode( CarrierPaymentConstants.SPECIFICATION_AUTOPAY_LMT);
        paramList.add(param);
        ParameterListingDTO param1 = new ParameterListingDTO();
        param1.setMinNumberValue(new BigDecimal(-20.00));
        param1.setMaxNumberValue(new BigDecimal(5.00));
        param1.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param1.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param1.setParameterSpecificationTypeCode( CarrierPaymentConstants.SPECIFICATION_AUTOPAY_TOLERANCE);
        paramList.add(param1);
        ParameterListingDTO param2 = new ParameterListingDTO();
        param2.setMinNumberValue(new BigDecimal(200.00));
        param2.setMaxNumberValue(new BigDecimal(1.00));
        param2.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param2.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param2.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_OVER_PERCENTAGE );
        paramList.add(param2);
        ParameterListingDTO param3= new ParameterListingDTO();
        param3.setMinNumberValue(new BigDecimal(200.00));
        param3.setMaxNumberValue(new BigDecimal(1.00));
        param3.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param3.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param3.setSpecificationSub( "TRANSIT" );//FUELSURCHG
        param3.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGEGROUP );
        paramList.add(param3);
        ParameterListingDTO param4= new ParameterListingDTO();
        param4.setMinNumberValue(new BigDecimal(0.00));
        param4.setMaxNumberValue(new BigDecimal(0.00));
        param4.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param4.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param4.setSpecificationSub( "false" );
        param4.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGE_THRESHOLD_REQUIRED );
        paramList.add(param4);

        ParameterListingDTO param5= new ParameterListingDTO();
        param5.setMinNumberValue(new BigDecimal(0.00));
        param5.setMaxNumberValue(new BigDecimal(0.00));
        param5.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param5.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param5.setSpecificationSub( "test" );
        param5.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGE_THRESHOLD_RANGE );
        paramList.add(param5);
        return paramList;

    }
    public static List<ParameterListingDTO> getParameterListings() {
        List<ParameterListingDTO> paramList = new ArrayList<>();
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(new BigDecimal(0.00));
        param.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param.setParameterSpecificationTypeCode( CarrierPaymentConstants.SPECIFICATION_AUTOPAY_LMT);
        paramList.add(param);
        ParameterListingDTO param1 = new ParameterListingDTO();
        param1.setMinNumberValue(new BigDecimal(-20.00));
        param1.setMaxNumberValue(new BigDecimal(5.00));
        param1.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param1.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param1.setParameterSpecificationTypeCode( CarrierPaymentConstants.SPECIFICATION_AUTOPAY_TOLERANCE);
        paramList.add(param1);
        return paramList;
    }

    public static List<ParameterListingDTO> getParameterListings1() {
        List<ParameterListingDTO> paramList = new ArrayList<>();
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(new BigDecimal(250.00));
        param.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        //  param.setSpecificationSub( "test" );
        param.setParameterSpecificationTypeCode( CarrierPaymentConstants.SPECIFICATION_AUTOPAY_LMT);
        paramList.add(param);
        ParameterListingDTO param1 = new ParameterListingDTO();
        param1.setMinNumberValue(new BigDecimal(-20.00));
        param1.setMaxNumberValue(new BigDecimal(5.00));
        param1.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param1.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param1.setParameterSpecificationTypeCode( CarrierPaymentConstants.SPECIFICATION_AUTOPAY_TOLERANCE);
        paramList.add(param1);
        ParameterListingDTO param2 = new ParameterListingDTO();
        param2.setMinNumberValue(new BigDecimal(200.00));
        param2.setMaxNumberValue(new BigDecimal(1.00));
        param2.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param2.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param2.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_OVER_PERCENTAGE );
        paramList.add(param2);
        ParameterListingDTO param3= new ParameterListingDTO();
        param3.setMinNumberValue(new BigDecimal(200.00));
        param3.setMaxNumberValue(new BigDecimal(1.00));
        param3.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param3.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param3.setSpecificationSub( "FUELSURCHG" );//FUELSURCHG
        param3.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGEGROUP );
        paramList.add(param3);
        ParameterListingDTO param4= new ParameterListingDTO();
        param4.setMinNumberValue(new BigDecimal(0.00));
        param4.setMaxNumberValue(new BigDecimal(0.00));
        param4.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param4.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param4.setSpecificationSub( "false" );
        param4.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGE_THRESHOLD_REQUIRED );
        paramList.add(param4);

        ParameterListingDTO param5= new ParameterListingDTO();
        param5.setMinNumberValue(new BigDecimal(0.00));
        param5.setMaxNumberValue(new BigDecimal(0.00));
        param5.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param5.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param5.setSpecificationSub( "test" );
        param5.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGE_THRESHOLD_RANGE );
        paramList.add(param5);

        return paramList;
    }
    public static List<ParameterListingDTO> getParameterListings5() {
        List<ParameterListingDTO> paramList = new ArrayList<>();
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(new BigDecimal(250.00));
        param.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        //  param.setSpecificationSub( "test" );
        param.setParameterSpecificationTypeCode( CarrierPaymentConstants.SPECIFICATION_AUTOPAY_LMT);
        paramList.add(param);
        ParameterListingDTO param1 = new ParameterListingDTO();
        param1.setMinNumberValue(new BigDecimal(-20.00));
        param1.setMaxNumberValue(new BigDecimal(5.00));
        param1.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param1.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param1.setParameterSpecificationTypeCode( CarrierPaymentConstants.SPECIFICATION_AUTOPAY_TOLERANCE);
        paramList.add(param1);
        ParameterListingDTO param2 = new ParameterListingDTO();
        param2.setMinNumberValue(new BigDecimal(200.00));
        param2.setMaxNumberValue(new BigDecimal(1.00));
        param2.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param2.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param2.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_OVER_PERCENTAGE );
        paramList.add(param2);
        ParameterListingDTO param3= new ParameterListingDTO();
        param3.setMinNumberValue(new BigDecimal(200.00));
        param3.setMaxNumberValue(new BigDecimal(1.00));
        param3.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param3.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param3.setSpecificationSub( "FUELSURCHG" );//FUELSURCHG
        param3.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGEGROUP );
        paramList.add(param3);
        ParameterListingDTO param4= new ParameterListingDTO();
        param4.setMinNumberValue(new BigDecimal(0.00));
        param4.setMaxNumberValue(new BigDecimal(0.00));
        param4.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param4.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param4.setSpecificationSub( "true" );
        param4.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGE_THRESHOLD_REQUIRED );
        paramList.add(param4);

        ParameterListingDTO param5= new ParameterListingDTO();
        param5.setMinNumberValue(new BigDecimal(0.00));
        param5.setMaxNumberValue(new BigDecimal(0.00));
        param5.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param5.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param5.setSpecificationSub( "test" );
        param5.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGE_THRESHOLD_RANGE );
        paramList.add(param5);

        return paramList;
    }

    public static List<ParameterListingDTO> getParameterListings2() {
        List<ParameterListingDTO> paramList = new ArrayList<>();
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(new BigDecimal(0.00));
        param.setParameterSpecificationTypeCode( CarrierPaymentConstants.SPECIFICATION_AUTOPAY_LMT);
       // paramList.add(param);
        ParameterListingDTO param1 = new ParameterListingDTO();
        param1.setSpecificationSub( "TRANSIT" );
        param1.setMinNumberValue(new BigDecimal(-20.00));
        param1.setMaxNumberValue(new BigDecimal(5.00));
        param1.setParameterSpecificationTypeCode( CarrierPaymentConstants.SPECIFICATION_AUTOPAY_TOLERANCE);
        paramList.add(param1);
        ParameterListingDTO param3= new ParameterListingDTO();
        param3.setMinNumberValue(new BigDecimal(200.00));
        param3.setMaxNumberValue(new BigDecimal(1.00));
        param3.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param3.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param3.setSpecificationSub( "FUELSURCHG" );//FUELSURCHG
        param3.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGEGROUP );
        paramList.add(param3);
        return paramList;
    }

    public static List<ParameterListingDTO> getParameterListings3() {
        List<ParameterListingDTO> paramList = new ArrayList<>();
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(new BigDecimal(0.00));
        param.setParameterSpecificationTypeCode( CarrierPaymentConstants.SPECIFICATION_AUTOPAY_LMT);
        // paramList.add(param);
        ParameterListingDTO param1 = new ParameterListingDTO();
        param1.setSpecificationSub( "TRANSIT" );
        param1.setMinNumberValue(new BigDecimal(-20.00));
        param1.setMaxNumberValue(new BigDecimal(5.00));
        param1.setParameterSpecificationTypeCode( CarrierPaymentConstants.SPECIFICATION_AUTOPAY_TOLERANCE);
        paramList.add(param1);
        ParameterListingDTO param3= new ParameterListingDTO();
        param3.setMinNumberValue(new BigDecimal(200.00));
        param3.setMaxNumberValue(new BigDecimal(1.00));
        param3.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param3.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param3.setSpecificationSub( "TRANSIT" );//FUELSURCHG
        param3.setParameterSpecificationTypeCode(CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGEGROUP );
        paramList.add(param3);
        return paramList;
    }
    public static List<ParameterListingDTO> getParameterListings4() {
        List<ParameterListingDTO> paramList = new ArrayList<>();
        ParameterListingDTO param = new ParameterListingDTO();
        param.setSpecificationSub( "ADMIN CHG" );
        param.setMinNumberValue(new BigDecimal(0.00));
        param.setOperatorCode("Between");
        param.setMaxNumberValue(new BigDecimal(0.00));
        param.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        paramList.add(param);
        ParameterListingDTO param3= new ParameterListingDTO();
        param3.setMinNumberValue(new BigDecimal(0.00));
        param3.setMaxNumberValue(new BigDecimal(0.00));
        param3.setOperatorCode("Between");
        param3.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param3.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param3.setSpecificationSub( "CACOMPCHG" );//FUELSURCHG
        paramList.add(param3);
        return paramList;
    }
    public static List<ParameterListingDTO> getParameterListingsMock() {
        List<ParameterListingDTO> paramList = new ArrayList<>();
        ParameterListingDTO param = new ParameterListingDTO();
        param.setSpecificationSub( "ADMIN CHG" );
        param.setMinNumberValue(new BigDecimal(0.00));
        param.setOperatorCode("Between");
        param.setMaxNumberValue(new BigDecimal(20.00));
        param.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
         paramList.add(param);
        ParameterListingDTO param3= new ParameterListingDTO();
        param3.setMinNumberValue(new BigDecimal(0.00));
        param3.setMaxNumberValue(new BigDecimal(100.00));
        param3.setOperatorCode("Between");
        param3.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param3.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        param3.setSpecificationSub( "CACOMPCHG" );//FUELSURCHG
        paramList.add(param3);
        return paramList;
    }

}

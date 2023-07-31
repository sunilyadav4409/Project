package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.jbhunt.finance.carrierpayment.autopay.mocks.CarrierInvoiceHeaderMocks.jbiCreateMock3;

public class CarrierInvoiceDetailMock {
    public static List<CarrierInvoiceDetail> getCarrierInvoiceDetail() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(100.00));
        carrierInvoiceDetail.setChargeCode( "Transit" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        return carrierInvoiceDetailList;
    }

    public static List<CarrierInvoiceDetail> getCarrierInvoiceDetail1() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(100.00));
        carrierInvoiceDetail.setChargeCode( "Transit" );
        carrierInvoiceDetail.setCarrierInvoiceHeader(jbiCreateMock3().get(0));
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        return carrierInvoiceDetailList;
    }
    public static List<CarrierInvoiceDetail> getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(203));
        carrierInvoiceDetail.setChargeCode( "Transit" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        return carrierInvoiceDetailList;
    }
    public static List<CarrierInvoiceDetail> getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay1() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(0));
        carrierInvoiceDetail.setChargeCode( "Transit" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        CarrierInvoiceDetail carrierInvoiceDetailNext = new CarrierInvoiceDetail();
        carrierInvoiceDetailNext.setCarrierInvoiceDetailId(2);
        carrierInvoiceDetailNext.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(201));
        carrierInvoiceDetailNext.setChargeCode( "FUELSURCHG" );
        carrierInvoiceDetailList.add(carrierInvoiceDetailNext);
        return carrierInvoiceDetailList;
    }
    public static List<CarrierInvoiceDetail> getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay4() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(1));
        carrierInvoiceDetail.setChargeCode( "Transit" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        CarrierInvoiceDetail carrierInvoiceDetailNext = new CarrierInvoiceDetail();
        carrierInvoiceDetailNext.setCarrierInvoiceDetailId(2);
        carrierInvoiceDetailNext.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(200));
        carrierInvoiceDetailNext.setChargeCode( "FUELSURCHG" );
        carrierInvoiceDetailList.add(carrierInvoiceDetailNext);
        return carrierInvoiceDetailList;
    }
    public static List<CarrierInvoiceDetail> getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay2() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(201));
        carrierInvoiceDetail.setChargeCode( "FUELSURCHG" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        CarrierInvoiceDetail carrierInvoiceDetailNext = new CarrierInvoiceDetail();
        carrierInvoiceDetailNext.setCarrierInvoiceDetailId(2);
        carrierInvoiceDetailNext.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(1));
        carrierInvoiceDetailNext.setChargeCode( "test" );
        carrierInvoiceDetailList.add(carrierInvoiceDetailNext);
        return carrierInvoiceDetailList;
    }
    public static List<CarrierInvoiceDetail> getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay5() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(201));
        carrierInvoiceDetail.setChargeCode( "FUELSURCHG" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        CarrierInvoiceDetail carrierInvoiceDetailNext = new CarrierInvoiceDetail();
        carrierInvoiceDetailNext.setCarrierInvoiceDetailId(2);
        carrierInvoiceDetailNext.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(0));
        carrierInvoiceDetailNext.setChargeCode( "test" );
        carrierInvoiceDetailList.add(carrierInvoiceDetailNext);
        return carrierInvoiceDetailList;
    }
    public static List<CarrierInvoiceDetail> getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPay3() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(201));
        carrierInvoiceDetail.setChargeCode( "FUELSURCHG" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        CarrierInvoiceDetail carrierInvoiceDetailNext = new CarrierInvoiceDetail();
        carrierInvoiceDetailNext.setCarrierInvoiceDetailId(2);
        carrierInvoiceDetailNext.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(0));
        carrierInvoiceDetailNext.setChargeCode( "test" );
        carrierInvoiceDetail.setCarrierInvoiceHeader(jbiCreateMock3().get(0));
        carrierInvoiceDetailList.add(carrierInvoiceDetailNext);
        return carrierInvoiceDetailList;
    }
    public static List<CarrierInvoiceDetail> getDuplicateCarrierInvoiceDetail() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(100.00));
        carrierInvoiceDetail.setChargeCode( "Transit" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        CarrierInvoiceDetail carrierInvoiceDetail1 = new CarrierInvoiceDetail();
        carrierInvoiceDetail1.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail1.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(100.00));
        carrierInvoiceDetail1.setChargeCode( "Transit" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail1);
        return carrierInvoiceDetailList;
    }
    public static List<CarrierInvoiceDetail> getDuplicateCarrierInvoiceDetail123() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(100.00));
        carrierInvoiceDetail.setChargeCode( "Transit" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        CarrierInvoiceDetail carrierInvoiceDetail1 = new CarrierInvoiceDetail();
        carrierInvoiceDetail1.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail1.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(100.00));
        carrierInvoiceDetail1.setChargeCode( "Transit" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail1);
        return carrierInvoiceDetailList;
    }
    public static List<CarrierInvoiceDetail> getDuplicateCarrierInvoiceDetail1234() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail1 = new CarrierInvoiceDetail();
        carrierInvoiceDetail1.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail1.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(90.00));
        carrierInvoiceDetail1.setChargeCode( "CACOMPCHG" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail1);
        CarrierInvoiceDetail carrierInvoiceDetail2 = new CarrierInvoiceDetail();
        carrierInvoiceDetail2.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail2.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(10.00));
        carrierInvoiceDetail2.setChargeCode( "ADMIN CHG");
        carrierInvoiceDetailList.add(carrierInvoiceDetail2);
        return carrierInvoiceDetailList;
    }
    public static List<CarrierInvoiceDetail> getCarrierInvoiceDetailWIthHighUnitRateAmountToSatisfyAutoPayListMatch() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(10));
        carrierInvoiceDetail.setChargeCode( "TRANSIT" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        return carrierInvoiceDetailList;
    }
}

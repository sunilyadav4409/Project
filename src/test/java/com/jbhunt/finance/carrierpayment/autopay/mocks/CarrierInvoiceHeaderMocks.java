package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceDetail;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.EDI;
import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.PAPER;

public class CarrierInvoiceHeaderMocks {

    public static List<CarrierInvoiceHeader> getListOfCarrierInvoiceHeaderMock(){
        List<CarrierInvoiceHeader> carrierInvoiceHeaderList = new ArrayList<>();
        CarrierInvoiceHeader firstInvoice = new CarrierInvoiceHeader();
        firstInvoice.setCarrierInvoiceHeaderId(3245);
        firstInvoice.setCarrierInvoiceNumber("yuftd");
        firstInvoice.setInvoiceDate(LocalDateTime.now());
        firstInvoice.setInvoiceStatusCode("Processed");
        firstInvoice.setScanTimestamp(LocalDateTime.now());
        firstInvoice.setDispatchNumber("3");
        firstInvoice.setInvoiceSourceTypeCode("PAPER");
        carrierInvoiceHeaderList.add(firstInvoice);
        CarrierInvoiceHeader secondInvoice = new CarrierInvoiceHeader();
        secondInvoice.setCarrierInvoiceHeaderId(345);
        secondInvoice.setCarrierInvoiceNumber("fsg");
        secondInvoice.setScanTimestamp(LocalDateTime.now());
        secondInvoice.setInvoiceDate(LocalDateTime.now());
        secondInvoice.setDispatchNumber("435");
        secondInvoice.setInvoiceSourceTypeCode("EDI");
        carrierInvoiceHeaderList.add(secondInvoice);
        return carrierInvoiceHeaderList;
    }

    public static List<CarrierInvoiceHeader> getJBIListOfCarrierInvoiceHeaderMock(){
        List<CarrierInvoiceHeader> carrierInvoiceHeaderList = new ArrayList<>();
        CarrierInvoiceHeader firstInvoice = new CarrierInvoiceHeader();
        firstInvoice.setCarrierInvoiceHeaderId(3245);
        firstInvoice.setCarrierInvoiceNumber("yuftd");
        firstInvoice.setInvoiceDate(LocalDateTime.now());
        firstInvoice.setInvoiceStatusCode("Processed");
        firstInvoice.setScanTimestamp(LocalDateTime.now());
        firstInvoice.setDispatchNumber("3");
        firstInvoice.setInvoiceSourceTypeCode("PAPER");
        carrierInvoiceHeaderList.add(firstInvoice);
        CarrierInvoiceHeader secondInvoice = new CarrierInvoiceHeader();
        secondInvoice.setCarrierInvoiceHeaderId(345);
        secondInvoice.setCarrierInvoiceNumber("fsg");
        secondInvoice.setInvoiceStatusCode("abc");
        secondInvoice.setScanTimestamp(LocalDateTime.now());
        secondInvoice.setInvoiceDate(LocalDateTime.now());
        secondInvoice.setDispatchNumber("435");
        secondInvoice.setInvoiceSourceTypeCode("EDI");
        carrierInvoiceHeaderList.add(secondInvoice);
        return carrierInvoiceHeaderList;
    }
    public static List<CarrierInvoiceHeader> getCarrierInvoiceHeaderWithoutPaymentMock(){
        List<CarrierInvoiceHeader> carrierInvoiceHeaderList = new ArrayList<>();
        CarrierInvoiceHeader firstInvoice = new CarrierInvoiceHeader();
        firstInvoice.setCarrierInvoiceHeaderId(3245);
        firstInvoice.setCarrierInvoiceNumber("yuftd");
        firstInvoice.setInvoiceDate(LocalDateTime.now());
        firstInvoice.setInvoiceStatusCode(" ");
        firstInvoice.setScanTimestamp(LocalDateTime.now());
        firstInvoice.setDispatchNumber("3");
        firstInvoice.setInvoiceSourceTypeCode("EDI");
        carrierInvoiceHeaderList.add(firstInvoice);
        CarrierInvoiceHeader secondInvoice = new CarrierInvoiceHeader();
        secondInvoice.setCarrierInvoiceHeaderId(345);
        secondInvoice.setCarrierInvoiceNumber("fsg");
        secondInvoice.setInvoiceStatusCode("abc");
        secondInvoice.setScanTimestamp(LocalDateTime.now());
        secondInvoice.setInvoiceDate(LocalDateTime.now());
        secondInvoice.setDispatchNumber("435");
        secondInvoice.setInvoiceSourceTypeCode("EDI");
        carrierInvoiceHeaderList.add(secondInvoice);
        return carrierInvoiceHeaderList;
    }
    public static List<CarrierInvoiceHeader> getCarrierInvoiceHeaderListMatch(){
        List<CarrierInvoiceHeader> carrierInvoiceHeaderList = new ArrayList<>();
        CarrierInvoiceHeader firstInvoice = new CarrierInvoiceHeader();
        firstInvoice.setCarrierInvoiceHeaderId(3245);
        firstInvoice.setCarrierInvoiceNumber("yuftd");
        firstInvoice.setInvoiceDate(LocalDateTime.now());
        firstInvoice.setInvoiceStatusCode(" ");
        firstInvoice.setScanTimestamp(LocalDateTime.now());
        firstInvoice.setDispatchNumber("3");
        firstInvoice.setInvoiceSourceTypeCode("EDI");
        carrierInvoiceHeaderList.add(firstInvoice);
        return carrierInvoiceHeaderList;
    }
    public static List<CarrierInvoiceHeader> getCarrierInvoiceHeaderWithoutPaymentMock20(){
        List<CarrierInvoiceHeader> carrierInvoiceHeaderList = new ArrayList<>();
        CarrierInvoiceHeader firstInvoice = new CarrierInvoiceHeader();
        firstInvoice.setCarrierInvoiceHeaderId(3245);
        firstInvoice.setCarrierInvoiceNumber("yuftd");
        firstInvoice.setInvoiceDate(LocalDateTime.now());
        firstInvoice.setInvoiceStatusCode(" ");
        firstInvoice.setScanTimestamp(LocalDateTime.now());
        firstInvoice.setDispatchNumber("3");
        firstInvoice.setInvoiceSourceTypeCode("EDI");
        carrierInvoiceHeaderList.add(firstInvoice);
        CarrierInvoiceHeader secondInvoice = new CarrierInvoiceHeader();
        secondInvoice.setCarrierInvoiceHeaderId(345);
        secondInvoice.setCarrierInvoiceNumber("fsg");
        secondInvoice.setInvoiceStatusCode("abc");
        secondInvoice.setInvoiceDate(LocalDateTime.now());
        secondInvoice.setDispatchNumber("3");
        secondInvoice.setInvoiceSourceTypeCode("EDI");
        carrierInvoiceHeaderList.add(secondInvoice);
        return carrierInvoiceHeaderList;
    }
    public static List<CarrierInvoiceHeader> getCarrierInvoiceHeaderWithoutPaymentMock22(){
        List<CarrierInvoiceHeader> carrierInvoiceHeaderList = new ArrayList<>();
        CarrierInvoiceHeader firstInvoice = new CarrierInvoiceHeader();
        firstInvoice.setCarrierInvoiceHeaderId(3245);
        firstInvoice.setCarrierInvoiceNumber("yuftd");
        firstInvoice.setInvoiceDate(LocalDateTime.now());
        firstInvoice.setInvoiceStatusCode(" ");
        firstInvoice.setScanTimestamp(LocalDateTime.now());
        firstInvoice.setDispatchNumber("3");
        firstInvoice.setInvoiceSourceTypeCode("EDI");
        carrierInvoiceHeaderList.add(firstInvoice);
        CarrierInvoiceHeader secondInvoice = new CarrierInvoiceHeader();
        secondInvoice.setCarrierInvoiceHeaderId(345);
        secondInvoice.setCarrierInvoiceNumber("fsg");
        secondInvoice.setInvoiceStatusCode("abc");
        secondInvoice.setScanTimestamp(LocalDateTime.now());
        secondInvoice.setInvoiceDate(LocalDateTime.now());
        secondInvoice.setDispatchNumber("3");
        secondInvoice.setInvoiceSourceTypeCode("EDI");
        carrierInvoiceHeaderList.add(secondInvoice);
        return carrierInvoiceHeaderList;
    }
    public static List<CarrierInvoiceHeader> getCarrierInvoiceHeaderApprovedFuelMock(){
        List<CarrierInvoiceHeader> carrierInvoiceHeaderList = new ArrayList<>();
        CarrierInvoiceHeader firstInvoice = new CarrierInvoiceHeader();
        firstInvoice.setCarrierInvoiceHeaderId(3245);
        firstInvoice.setCarrierInvoiceNumber("yuftd");
        firstInvoice.setInvoiceDate(LocalDateTime.now());
        firstInvoice.setInvoiceStatusCode(" ");
        firstInvoice.setScanTimestamp(LocalDateTime.now());
        firstInvoice.setDispatchNumber("3");
        firstInvoice.setInvoiceSourceTypeCode("EDI");
        carrierInvoiceHeaderList.add(firstInvoice);
        return carrierInvoiceHeaderList;
    }
    public static List<CarrierInvoiceHeader> getCarrierInvoiceHeaderWithoutPaymentMock4(){
        List<CarrierInvoiceHeader> carrierInvoiceHeaderList = new ArrayList<>();
        CarrierInvoiceHeader firstInvoice = new CarrierInvoiceHeader();
        firstInvoice.setCarrierInvoiceHeaderId(3245);
        firstInvoice.setCarrierInvoiceNumber("yuftd");
        firstInvoice.setInvoiceDate(LocalDateTime.now());
        firstInvoice.setInvoiceStatusCode("Pending");
        firstInvoice.setScanTimestamp(LocalDateTime.now());
        firstInvoice.setDispatchNumber("3");
        firstInvoice.setInvoiceSourceTypeCode("EDI");
        carrierInvoiceHeaderList.add(firstInvoice);
        CarrierInvoiceHeader secondInvoice = new CarrierInvoiceHeader();
        secondInvoice.setCarrierInvoiceHeaderId(345);
        secondInvoice.setCarrierInvoiceNumber("fsg");
        secondInvoice.setInvoiceStatusCode("Rejected");
        secondInvoice.setScanTimestamp(LocalDateTime.now());
        secondInvoice.setInvoiceDate(LocalDateTime.now());
        secondInvoice.setDispatchNumber("435");
        secondInvoice.setInvoiceSourceTypeCode("EDI");
        carrierInvoiceHeaderList.add(secondInvoice);
        return carrierInvoiceHeaderList;
    }
    public static CarrierInvoiceHeader getCarrierInvoiceHeaderWithPaymentMock(){
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(3245);
        carrierInvoiceHeader.setCarrierInvoiceNumber("yuftd");
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setDispatchNumber("3");
        carrierInvoiceHeader.setInvoiceStatusCode("Pending");
        carrierInvoiceHeader.setInvoiceSourceTypeCode(EDI);
        carrierInvoiceHeader.setCarrierPayment(PaymentMocks.getPaymentMock());
        return carrierInvoiceHeader;
    }
    public static CarrierInvoiceHeader getCarrierInvoiceHeaderWithoutInvoiceNumberMock(){
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(3245);
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setDispatchNumber("3");
        carrierInvoiceHeader.setCarrierPayment(PaymentMocks.getPaymentMock());
        return carrierInvoiceHeader;
    }

    public static List<CarrierInvoiceHeader> jbiCreateMock(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiMock();
        carrierInvoiceHeader.setInvoiceStatusCode("pending");
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        return list;
    }
    public static List<CarrierInvoiceHeader> jbiCreateMock123(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiFuelApprovedMock();
        carrierInvoiceHeader.setInvoiceStatusCode("pending");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        return list;
    }
    public static List<CarrierInvoiceHeader> jbiCreateMock21(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiMock21();
        carrierInvoiceHeader.setInvoiceStatusCode("pending");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        return list;
    }
    public static List<CarrierInvoiceHeader> jbiCreateMock1(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiMock1();
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setInvoiceStatusCode("pending");
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        return list;
    }
    public static List<CarrierInvoiceHeader> jbiCreateMock12(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiMock12();
        carrierInvoiceHeader.setInvoiceStatusCode("pending");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        CarrierInvoiceHeader carrierInvoiceHeader1=createjbiMock1();
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader1.setInvoiceStatusCode("pending");
        carrierInvoiceHeader1.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader1.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        list.add(carrierInvoiceHeader1);
        return list;
    }
    public static List<CarrierInvoiceHeader> jbiCreateMock20(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiMock20();
        carrierInvoiceHeader.setInvoiceStatusCode("pending");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        CarrierInvoiceHeader carrierInvoiceHeader1=createjbiMock1();
        carrierInvoiceHeader1.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader1.setInvoiceStatusCode("pending");
        carrierInvoiceHeader1.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        list.add(carrierInvoiceHeader1);
        return list;
    }
    public static List<CarrierInvoiceHeader> jbiCreateMock22(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiMock22();
        carrierInvoiceHeader.setInvoiceStatusCode("pending");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        CarrierInvoiceHeader carrierInvoiceHeader1=createjbiMock1();
        carrierInvoiceHeader1.setInvoiceStatusCode("pending");
        carrierInvoiceHeader1.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader1.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        list.add(carrierInvoiceHeader1);
        return list;
    }
    public static List<CarrierInvoiceHeader> jbiCreateMock25(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiMock25();
        carrierInvoiceHeader.setInvoiceStatusCode("pending");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        CarrierInvoiceHeader carrierInvoiceHeader1=createjbiMock1();
        carrierInvoiceHeader1.setInvoiceStatusCode("pending");
        carrierInvoiceHeader1.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader1.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        list.add(carrierInvoiceHeader1);
        return list;
    }
    public static List<CarrierInvoiceHeader> jbiCreateMock2(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiMock2();
        carrierInvoiceHeader.setInvoiceStatusCode("pending");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        return list;
    }
    public static List<CarrierInvoiceHeader> jbiCreateMock3(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiMock3();
        carrierInvoiceHeader.setInvoiceStatusCode("Reroute");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        return list;
    }
    public static List<CarrierInvoiceHeader> jbiCreateMock34(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiMock34();
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setInvoiceStatusCode("Reroute");
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        return list;
    }
    public static List<CarrierInvoiceHeader> jbiCreateMock4(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiMock4();
        carrierInvoiceHeader.setInvoiceStatusCode("pending");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        return list;
    }
    public static List<CarrierInvoiceHeader> jbiCreateMock5(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiMock5();
        carrierInvoiceHeader.setInvoiceStatusCode("pending");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        return list;
    }

    public static List<CarrierInvoiceHeader> jbiRejectedCreateMock1(){
        CarrierInvoiceHeader carrierInvoiceHeader=createjbiMock1();
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setInvoiceStatusCode(CarrierPaymentConstants.INVOICE_STATUS_REJECTED);
        carrierInvoiceHeader.setCarrierPaymentWorkflowGroupTypeCode("JBI");
        List<CarrierInvoiceHeader>  list= new ArrayList<>();
        list.add(carrierInvoiceHeader);
        return list;
    }
    public static CarrierInvoiceHeader createjbiMock(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("FUELSURCHG");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(10));
        CarrierInvoiceDetail invoiceDetail2 = new CarrierInvoiceDetail();
        invoiceDetail2.setChargeCode("FUELSURCHG");
        invoiceDetail2.setCarrierChargeUnitRateAmount(new BigDecimal(110));
       // detailList.add(invoiceDetail1);
        detailList.add(invoiceDetail2);
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;
    }
    public static CarrierInvoiceHeader createjbiFuelApprovedMock(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("TRANSIT");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(100));
        detailList.add(invoiceDetail1);
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;
    }
    public static CarrierInvoiceHeader createjbiMock21(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("STOPOFF");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(10));
        CarrierInvoiceDetail invoiceDetail2 = new CarrierInvoiceDetail();
        invoiceDetail2.setChargeCode("CALL");
        invoiceDetail2.setCarrierChargeUnitRateAmount(new BigDecimal(110));
         detailList.add(invoiceDetail1);
        detailList.add(invoiceDetail2);
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;
    }
    public static CarrierInvoiceHeader createjbiMock6(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("TRANSIT");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(0));
        CarrierInvoiceDetail invoiceDetail2 = new CarrierInvoiceDetail();
        invoiceDetail2.setChargeCode("FUELSURCHG");
        invoiceDetail2.setCarrierChargeUnitRateAmount(new BigDecimal(0));
        // detailList.add(invoiceDetail1);
        detailList.add(invoiceDetail2);
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;
    }

    public static CarrierInvoiceHeader createjbiMock1(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("TRANSIT");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(10));
        CarrierInvoiceDetail invoiceDetail2 = new CarrierInvoiceDetail();
        invoiceDetail2.setChargeCode("TRANSIT");
        invoiceDetail2.setCarrierChargeUnitRateAmount(new BigDecimal(110));
        // detailList.add(invoiceDetail1);
        detailList.add(invoiceDetail2);
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;
    }
    public static CarrierInvoiceHeader createjbiMock20() {
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("TRANSIT");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(100));
        detailList.add(invoiceDetail1);

        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;

    }
    public static CarrierInvoiceHeader createjbiMock22() {
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("TRANSIT");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(100.00));
        detailList.add(invoiceDetail1);
        CarrierInvoiceDetail invoiceDetail2 = new CarrierInvoiceDetail();
        invoiceDetail2.setChargeCode("LAY");
        invoiceDetail2.setCarrierChargeUnitRateAmount(new BigDecimal(150.00));
        detailList.add(invoiceDetail2);

        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail());
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;

    }
    public static CarrierInvoiceHeader createjbiMock25() {
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail2 = new CarrierInvoiceDetail();
        invoiceDetail2.setChargeCode("LAY");
        invoiceDetail2.setCarrierChargeUnitRateAmount(new BigDecimal(100.00));
        detailList.add(invoiceDetail2);

        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;

    }

        public static CarrierInvoiceHeader createjbiMock12(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("TRANSIT");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(90));
        detailList.add(invoiceDetail1);


        CarrierInvoiceDetail invoiceDetail2 = new CarrierInvoiceDetail();
        invoiceDetail2.setChargeCode("FUELSURCHG");
        invoiceDetail2.setCarrierChargeUnitRateAmount(new BigDecimal(5));
        detailList.add(invoiceDetail2);


        CarrierInvoiceDetail invoiceDetail3 = new CarrierInvoiceDetail();
        invoiceDetail3.setChargeCode("STOPOFF");
        invoiceDetail3.setCarrierChargeUnitRateAmount(new BigDecimal(20));
        detailList.add(invoiceDetail3);

        CarrierInvoiceDetail invoiceDetail4 = new CarrierInvoiceDetail();
        invoiceDetail4.setChargeCode("DETENTION");
        invoiceDetail4.setCarrierChargeUnitRateAmount(new BigDecimal(0));
        detailList.add(invoiceDetail4);


        CarrierInvoiceDetail invoiceDetail5 = new CarrierInvoiceDetail();
        invoiceDetail5.setChargeCode("CARTAGE");
        invoiceDetail5.setCarrierChargeUnitRateAmount(new BigDecimal(5));
        detailList.add(invoiceDetail5);


        CarrierInvoiceDetail invoiceDetail6 = new CarrierInvoiceDetail();
        invoiceDetail6.setChargeCode("CARTAGE");
        invoiceDetail6.setCarrierChargeUnitRateAmount(new BigDecimal(5));
        detailList.add(invoiceDetail6);


        CarrierInvoiceDetail invoiceDetail7 = new CarrierInvoiceDetail();
        invoiceDetail7.setChargeCode("FUELSURCHG");
        invoiceDetail7.setCarrierChargeUnitRateAmount(new BigDecimal(5));
        detailList.add(invoiceDetail7);

        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;
    }
    public static CarrierInvoiceHeader createjbiMock2(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("TRANSIT");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(10));
        CarrierInvoiceDetail invoiceDetail2 = new CarrierInvoiceDetail();
        invoiceDetail2.setChargeCode("TRANSIT");
        invoiceDetail2.setCarrierChargeUnitRateAmount(new BigDecimal(110));
        // detailList.add(invoiceDetail1);
        CarrierInvoiceDetail invoiceDetail3= new CarrierInvoiceDetail();
        invoiceDetail3.setChargeCode("STOPOFF");
        invoiceDetail3.setCarrierChargeUnitRateAmount(new BigDecimal(10));
        detailList.add(invoiceDetail3);
        CarrierInvoiceDetail invoiceDetail4= new CarrierInvoiceDetail();
        invoiceDetail4.setChargeCode("STOPOFF");
        invoiceDetail4.setCarrierChargeUnitRateAmount(new BigDecimal(10));
        detailList.add(invoiceDetail4);
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;
    }
    public static CarrierInvoiceHeader createjbiMock3(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("TRANSIT");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(10));
        detailList.add(invoiceDetail1);
        CarrierInvoiceDetail invoiceDetail4= new CarrierInvoiceDetail();
        invoiceDetail4.setChargeCode("STOPOFF");
        invoiceDetail4.setCarrierChargeUnitRateAmount(new BigDecimal(10));
        detailList.add(invoiceDetail4);
        CarrierInvoiceDetail invoiceDetail2= new CarrierInvoiceDetail();
        invoiceDetail2.setChargeCode("CARTAGE");
        invoiceDetail2.setCarrierChargeUnitRateAmount(new BigDecimal(0));
        detailList.add(invoiceDetail2);
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;
    }
    public static CarrierInvoiceHeader createjbiMock34(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("TRANSIT");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(10));
        detailList.add(invoiceDetail1);
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;
    }
    public static CarrierInvoiceHeader createjbiMock4(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail4= new CarrierInvoiceDetail();
        invoiceDetail4.setChargeCode("STOPOFF");
        invoiceDetail4.setCarrierChargeUnitRateAmount(new BigDecimal(10));
        detailList.add(invoiceDetail4);
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;
    }
    public static CarrierInvoiceHeader createjbiMock5(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("TRANSIT");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(100));
        CarrierInvoiceDetail invoiceDetail4= new CarrierInvoiceDetail();
        invoiceDetail4.setChargeCode("STOPOFF");
        invoiceDetail4.setCarrierChargeUnitRateAmount(new BigDecimal(0));
        detailList.add(invoiceDetail4);
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;
    }


    public static CarrierInvoiceHeader createMock(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("TRANSIT");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(10));
        CarrierInvoiceDetail invoiceDetail2 = new CarrierInvoiceDetail();
        invoiceDetail2.setChargeCode("TRANSIT");
        invoiceDetail2.setCarrierChargeUnitRateAmount(new BigDecimal(100));
        detailList.add(invoiceDetail1);
        detailList.add(invoiceDetail2);
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        return carrierInvoiceHeader;
    }

    public static CarrierInvoiceHeader createPaperInvoiceMock(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("TRANSIT");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(100));
        CarrierInvoiceDetail invoiceDetail2 = new CarrierInvoiceDetail();
        invoiceDetail2.setChargeCode("TRANSIT");
        invoiceDetail2.setCarrierChargeUnitRateAmount(new BigDecimal(100));
        detailList.add(invoiceDetail1);
        detailList.add(invoiceDetail2);
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        carrierInvoiceHeader.setInvoiceSourceTypeCode(PAPER);
        carrierInvoiceHeader.setInvoiceStatusCode("Pending");
        return carrierInvoiceHeader;
    }
    public static CarrierInvoiceHeader createPaperInvoiceMock1(){
        List<CarrierInvoiceDetail> detailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail1 = new CarrierInvoiceDetail();
        invoiceDetail1.setChargeCode("TRANSIT");
        invoiceDetail1.setCarrierChargeUnitRateAmount(new BigDecimal(100));
        CarrierInvoiceDetail invoiceDetail2 = new CarrierInvoiceDetail();
        invoiceDetail2.setChargeCode("TRANSIT");
        invoiceDetail2.setCarrierChargeUnitRateAmount(new BigDecimal(100));
        detailList.add(invoiceDetail1);
        detailList.add(invoiceDetail2);
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceHeaderId(1);
        carrierInvoiceHeader.setCarrierInvoiceNumber("I12");
        carrierInvoiceHeader.setScanTimestamp(LocalDateTime.now());
        carrierInvoiceHeader.setInvoiceDate(LocalDateTime.now());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail());
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        carrierInvoiceHeader.setInvoiceSourceTypeCode(PAPER);
        carrierInvoiceHeader.setInvoiceStatusCode("Rejected");
        return carrierInvoiceHeader;
    }
    static List<CarrierInvoiceDetail> getCarrierInvoiceDetail() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(100.00));
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        return carrierInvoiceDetailList;
    }

    public static CarrierInvoiceHeader getInvoiceHeader_1() {
        CarrierInvoiceHeader invoiceHeader = new CarrierInvoiceHeader();
        invoiceHeader.setCarrierInvoiceHeaderId(1);
        invoiceHeader.setCarrierInvoiceNumber( "I12" );
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail = new CarrierInvoiceDetail();
        invoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetailList.add(invoiceDetail);
        invoiceHeader.setCarrierInvoiceDetailList( getCarrierInvoiceDetail() );
        invoiceHeader.setInvoiceSourceTypeCode("EDI");
        invoiceHeader.setInvoiceDate( LocalDateTime.now() );
        invoiceHeader.setScanTimestamp(LocalDateTime.now());
        invoiceHeader.setAutoApproveEligibleInd( "Y" );
        return invoiceHeader;
    }
    public static CarrierInvoiceHeader getInvoiceHeader_2() {
        CarrierInvoiceHeader invoiceHeader = new CarrierInvoiceHeader();
        invoiceHeader.setCarrierInvoiceHeaderId(1);
        invoiceHeader.setCarrierInvoiceNumber( "I12" );
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail = new CarrierInvoiceDetail();
        invoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetailList.add(invoiceDetail);
        invoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail3() );
        invoiceHeader.setInvoiceSourceTypeCode("EDI");
        invoiceHeader.setScanTimestamp(LocalDateTime.now());
        invoiceHeader.setInvoiceDate( LocalDateTime.now() );
        invoiceHeader.setAutoApproveEligibleInd( "Y" );
        return invoiceHeader;
    }

    public static CarrierInvoiceHeader getInvoiceHeader_3() {
        CarrierInvoiceHeader invoiceHeader = new CarrierInvoiceHeader();
        invoiceHeader.setCarrierInvoiceHeaderId(1);
        invoiceHeader.setCarrierInvoiceNumber("I12");
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail = new CarrierInvoiceDetail();
        invoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetailList.add(invoiceDetail);
        invoiceHeader.setCarrierInvoiceDetailList(getCarrierInvoiceDetail4());
        invoiceHeader.setInvoiceSourceTypeCode("EDI");
     invoiceHeader.setScanTimestamp(LocalDateTime.now());

        invoiceHeader.setInvoiceDate(LocalDateTime.now());
        invoiceHeader.setAutoApproveEligibleInd("Y");
        return invoiceHeader;
    }
    public  static  List<CarrierInvoiceDetail> getCarrierInvoiceDetail3() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(100.00));
        carrierInvoiceDetail.setChargeCode( "FUELSURCHG" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        return carrierInvoiceDetailList;
    }
    public  static  List<CarrierInvoiceDetail> getCarrierInvoiceDetail4() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(150.00));
        carrierInvoiceDetail.setChargeCode( "LUMPLDPAY" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);

        CarrierInvoiceDetail carrierInvoiceDetail1 = new CarrierInvoiceDetail();
        carrierInvoiceDetail1.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail1.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(250.00));
        carrierInvoiceDetail1.setChargeCode( "Transit" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail1);
        return carrierInvoiceDetailList;
    }
    public static  List<CarrierInvoiceDetail> getCarrierInvoiceDetail5() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(150.00));
        carrierInvoiceDetail.setChargeCode( "FUELSURCHG" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);

        CarrierInvoiceDetail carrierInvoiceDetail1 = new CarrierInvoiceDetail();
        carrierInvoiceDetail1.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail1.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(250.00));
        carrierInvoiceDetail1.setChargeCode( "Transit" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail1);
        return carrierInvoiceDetailList;
    }
    public static  List<CarrierInvoiceDetail> getCarrierInvoiceDetail01() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(150.00));
        carrierInvoiceDetail.setChargeCode( "AMS" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        return carrierInvoiceDetailList;
    }
    public static  List<CarrierInvoiceDetail> getCarrierInvoiceDetail15() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(150.00));
        carrierInvoiceDetail.setChargeCode( "FUELSURCHG" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);

        CarrierInvoiceDetail carrierInvoiceDetail1 = new CarrierInvoiceDetail();
        carrierInvoiceDetail1.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail1.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(250.00));
        carrierInvoiceDetail1.setChargeCode( "Transit" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail1);

        CarrierInvoiceDetail carrierInvoiceDetail2 = new CarrierInvoiceDetail();
        carrierInvoiceDetail2.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail2.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(0.00));
        carrierInvoiceDetail2.setChargeCode( "AMS" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail2);
        return carrierInvoiceDetailList;
    }

    public static CarrierInvoiceHeader getInvoiceHeader() {
        CarrierInvoiceHeader header = new CarrierInvoiceHeader();
        header.setCarrierInvoiceNumber("1");
        header.setCarrierInvoiceHeaderId(1);
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail invoiceDetail = new CarrierInvoiceDetail();
        invoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetailList.add(invoiceDetail);
        header.setCarrierInvoiceDetailList(carrierInvoiceDetailList);
        header.setScanTimestamp(LocalDateTime.now());
        header.setInvoiceDate( LocalDateTime.now());
        header.setAutoApproveEligibleInd( "Y" );
        return header;
    }
    public  static  List<CarrierInvoiceDetail> getCarrierInvoiceDetail6() {
        List<CarrierInvoiceDetail> carrierInvoiceDetailList = new ArrayList<>();
        CarrierInvoiceDetail carrierInvoiceDetail = new CarrierInvoiceDetail();
        carrierInvoiceDetail.setCarrierInvoiceDetailId(1);
        carrierInvoiceDetail.setCarrierChargeUnitRateAmount(BigDecimal.valueOf(100.00));
        carrierInvoiceDetail.setChargeCode( "TRANSIT" );
        carrierInvoiceDetailList.add(carrierInvoiceDetail);
        return carrierInvoiceDetailList;
    }

}

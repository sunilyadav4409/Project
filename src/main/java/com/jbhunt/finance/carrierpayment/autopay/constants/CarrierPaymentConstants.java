package com.jbhunt.finance.carrierpayment.autopay.constants;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CarrierPaymentConstants {

    private CarrierPaymentConstants() {

    }

    public static final String SYSTEM = "SYSTEM";
    public static final String SYSTEM_VALUE = "System";
    public static final String SYSTEM_USERID = "pidfna1";
    public static final String BUSINESS_VALUE = "Business";

    public static final String Y = "Y";
    public static final String ERR_031 = "Invalid Invoice Date";
    public static final String ERR_035  ="Unable To Reject Charge MisMatch";
    public static final String ERR_036  ="Unable To Approve Charge MisMatch";
    public static final String ERR_UPDATING_INVOICE = "Error Updating Invoice Header";
    public static final String INVALID_INV_NUM = "Invalid Invoice Number";
    public static final String WARNING_STALE_RECORD = "You are working on stale record. Do you want to refresh?";
    public static final String WARNING_AUTO_APPROVED = "You are working on a payment which is Already Auto Approved by the System. Do you want to refresh?";
    public static final int LIMIT_2 = 2;

    public static final String CFP_APRV = "CFPAprv";
    public static final String CHRG_APRVD = "ChrgAprvd";
    public static final String CHRG_RJCTD = "ChrgRjctd";
    public static final String APPROVED = "Approve";
    public static final String REJECT_STATUS = "Reject";
    public static final String AUTO_APPROVED = "AutoApprv";
    public static final String TONU = "TONU";
    public static final String TRANSMIT_MODE_TRAIN = "TRAIN";
    public static final String CANCEL_JOB = "RcvCnclJob";
    public static final String ICS = "ICS";
    public static final String LTL = "LTL";
    public static final String PENDING_LTL = "LTLApprv";
    public static final String PENDING_AP = "PendingAP";
    public static final String PENDING ="pending";
    public static final String REROUTE = "Reroute";
    public static final String PAID = "PAID";
    public static final String EVENT_CHARGES_POST_APPVL = "ChrgPstApr";
    public static final String EVENT_PRE_PRCS = "PreProcess";
    public static final String INVOICE_STATUS_PROCESSED = "Processed";
    public static final String INVOICE_STATUS_ACKLNGD = "Aknowledgd";
    public static final String INVOICE_STATUS_REJECTED = "Rejected";
    public static final String REJECT_CHARGE = "RejectChrg";
    public static final String ID = "Id";
    public static final String CHARGE_BY_BU = "CHARGEBYBU";
    public static final String CHARGE_CODE = "ChargeCode";
    public static final String BILL_TO_BYPASS = "AABIL2BYPS";
    public static final String BILL_TO = "BillTo";
    public static final String AFTER_APPROVE = "POSTAPROVE";
    public static final String AFTER_INVOICE = "POSTINVOIC";
    public static final String ZERO_MILE = "AAZEROMILE";
    public static final String BOOLEAN = "BOOLEAN";




    // AutoPay Changes Start
    public static final String SPECIFICATION_AUTOPAY_TOLERANCE = "AutoAprTol";
    public static final String SPECIFICATION_AUTOPAY_OVER_PERCENTAGE = "AutoAprOvr";
    public static final String SPECIFICATION_AUTOPAY_CHARGEGROUP = "AAprChgGrp";
    public static final String SPECIFICATION_AUTOPAY_CHARGE_THRESHOLD_REQUIRED = "AAChgTheRq";
    public static final String SPECIFICATION_AUTOPAY_CHARGE_THRESHOLD_RANGE = "AAChgThred";
    // AutoPay Changes End
    // Apply to Customer Changes Start
    public static final String SPECIFICATION_APPLY_TO_CUSTOMER = "ApplyToCus";
    public static final String SPECIFICATION_APPLY_TO_CUSTOMER_CHARGES_EXCLUDE = "App2CusChg";
    // Apply to Customer Changes End

    public static final String SPECIFICATION_AUTOPAY_LMT = "AutoApprov";

    public static final String ACT_SRC_PERSON = "Person";
    public static final String ACT_APP = "App";
    public static final String SNOOZE = "Snooze";
    public static final String IGNORE = "Ignore";
    public static final String APPROVE = "Approve";
    public static final String BOL = "BOL";
    public static final String DELIVERY_RECEIPT = "Delrcpt";
    public static final String LUMPER = "Lumper";
    public static final String BOL_NAME = "Bill of Lading";
    public static final String DELIVERY_RECEIPT_NAME = "Delivery Receipt";
    public static final String INVOICE = "Invoice";
    public static final String REQUIRED = "Required";

    public static final String ACT_AUTOPAY = "Autopay";
    public static final String ACT_AUTOPAY_PERFORM = "EDIProcesd";
    // AutoPay Failure Reason Changes Start
    public static final String ACT_AUTOPAY_FAIL = "AutPayFail";
    public static final String ACT_AUTOPAY_FAIL_CHGEXEDTOL = "ChgExedTol";
    public static final String ACT_AUTOPAY_FAIL_CHGLSTEXCP = "ChgLstExcp";
    public static final String ACT_AUTOPAY_FAIL_DUPECHGECD = "DupeChgeCD";
    public static final String ACT_AUTOPAY_FAIL_EXEDPCNTT = "ExedPcnt";
    public static final String ACT_AUTOPAY_FAIL_EXEDTHRHLD = "ExedThrHld";
    public static final String ACT_AUTOPAY_FAIL_EXEDTOLNCE = "ExedTolnce";
    public static final String ACT_AUTOPAY_FAIL_FUELNOTRAN = "FuelNoTran";
    public static final String ACT_AUTOPAY_FAIL_PREVDECSN = "PrevDecsn";
    public static final String ACT_AUTOPAY_FAIL_TOTCPREXCP = "TotCprExcp";
    public static final String ACT_AUTOPAY_FAIL_OTHER = "Other";
    public static final String ACT_AUTOPAY_FAIL_DUPLICATE_INVOICE = "DupeIvc";
    public static final String ACT_AUTOPAY_FAIL_SUPPNOTWD = "SuppNotWD";
    public static final String ACT_AUTOPAY_FAIL_LDARCHIVED = "LDArchived";
    public static final String ACT_AUTOPAY_FAIL_CHRGNOTINWD = "ChrgNotWD";
    public static final String ACT_AUTOPAY_FAIL_IVCREJECT = "IvcReject";
    public static final String ACT_AUTOPAY_FAIL_NO_DISPATCH = "APPRNODISP";
    public static final String ACT_AUTOPAY_FAIL_PARMNOTFND = "PARMNOTFND";
    public static final String ACT_AUTOPAY_FAIL_BILLTOBYPASS = "BypasBilTo";
    public static final String ACT_AUTOPAY_FAIL_FUTURE_DATE = "FUTUREDATE";
    public static final String ACT_AUTOPAY_FAIL_AMOUNT_VARIANCE = "BOOKDSCRPY";
    public static final String ACT_AUTOPAY_FAIL_ZERO_MILE = "ZEROMILE";
    public static final String ACT_AUTOPAY_FAIL_MIXED_CURRENCY = "MIXEDCRNCY";
    public static final String ACT_AUTOPAY_FAIL_CURRENCY_MISMATCH = "CRNCYMISMA";
    public static final String ACT_AUTOPAY_FAIL_TCALLEDTOSHIPPER = "TCALLSHIPR";


    // AutoPay Failure Reason Changes End
    public static final String PROCESSED = "Processed";
    public static final String PAPER = "Paper";
    public static final String EDI ="EDI";

    public static final String QUICK_PAY = "QuickPay";
    public static final String FREE_QUICK_PAY = "FreePay";



    public static final String WORK_DAY = "WORKDAY";
    public static final String LOG_ERROR_MAP = "ERROR MAP";

    public static final String LOCK_UPDATE = "Update";
    public static final String ACTIV_MQ_TOPIC ="activemq:topic:";


    public static final Map<String, String> DOC_NAME_MAP = new HashMap<>();

    static {
        DOC_NAME_MAP.put(CarrierPaymentConstants.BOL, CarrierPaymentConstants.BOL_NAME);
        DOC_NAME_MAP.put(CarrierPaymentConstants.DELIVERY_RECEIPT, CarrierPaymentConstants.DELIVERY_RECEIPT_NAME);
        DOC_NAME_MAP.put(CarrierPaymentConstants.LUMPER, CarrierPaymentConstants.LUMPER);
        DOC_NAME_MAP.put(CarrierPaymentConstants.INVOICE, CarrierPaymentConstants.INVOICE);
    }




    public static final String DB2_AUDIT_AUTOPAY_REASON_CODE = "AdjChrgAmt";
    public static final String DB2_AUDIT_AUTOPAY_REASON_COMMENT = "Created by AutoApprove";


    public static final List<String> PENDING_STATUS_LIST = Arrays.asList(CarrierPaymentConstants.PENDING_LTL,
            CarrierPaymentConstants.PENDING_AP, CarrierPaymentConstants.REROUTE);

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


    public static final String TRANSIT = "TRANSIT";
    public static final String FUELSURCHG = "FUELSURCHG";
    public static final String PAYMENT_PROPERTIES_URL = "/static/ws_finance_carrierpayment_charges-messages";
    public static final String ENCODING_UTF = "UTF-8";

    //AutoPay Controller Links Stat
    public static final String AUTOPAY = "/autopay";
    public static final String CALL_AUTOPAY = "/callapws/{paymentId}";
    public static final String CALL_PROCESS_AUTOPAY = "/callAutoPayService";
    public static final String AUTOPAY_SERVICE_PRODUCES = "application/json";
    //AutoPay Controller Links End

    // AutoPay logging Reroute activity Changes Start
    public static final String ACT_REROUTE_TYPE = "CFPReroute";
    public static final String ACT_AUTOPAY_REROUTE_PERFORM = "ReroutAuto";
    public static final String AUTOPAY_CALLINGPOINT_INVOICE = "CarrierPaymentInvoice";
    // AutoPay logging Reroute activity Changes End

    public static final String EVENT_TYPE_AUTOPAY= "AUTOPAY_UPDATE";
    public static final String EVENT_TYPE_PAPER_INVOICE_AUTOPAY= "PAPER_INVOICE_AUTOPAY_UPDATE";
    public static final String JMSXGROUPID = "JMSXGroupID";
    public static final String EVENTTYPE = "eventType";
    public static final String EVENT_AUTOPAY = "Autopay";

    public static final String CLIENT_REGISTRATION = "carrierDataclientregistration";

}

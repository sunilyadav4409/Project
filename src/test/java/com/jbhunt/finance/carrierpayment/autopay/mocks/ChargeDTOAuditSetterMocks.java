package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceDetail;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.ChargeOverride;

import java.math.BigDecimal;
import java.util.List;

public class ChargeDTOAuditSetterMocks {

    public static CarrierInvoiceDetail getDetail(String code, BigDecimal amt) {
        CarrierInvoiceDetail invoiceDetail = new CarrierInvoiceDetail();
        invoiceDetail.setChargeCode(code);
        invoiceDetail.setCarrierChargeUnitRateAmount(amt);
        return invoiceDetail;
    }

    public static Charge getCharge(Integer id, String code, BigDecimal amt) {
        Charge charge = new Charge();
        charge.setChargeId(id);
        charge.setChargeCode(code);
        charge.setTotalChargeAmount(amt);
        charge.setTotalApprovedChargeAmount(amt);
        return charge;
    }

    public static CarrierInvoiceHeader getHeader(List<CarrierInvoiceDetail> detailList, String invoiceSourceTypeCode) {
        CarrierInvoiceHeader carrierInvoiceHeader = new CarrierInvoiceHeader();
        carrierInvoiceHeader.setCarrierInvoiceDetailList(detailList);
        carrierInvoiceHeader.setInvoiceSourceTypeCode(invoiceSourceTypeCode);
        return carrierInvoiceHeader;
    }

    public static ChargeDTO getChargeDTO(Integer id, boolean autoPayFlag) {
        ChargeDTO chargeDTO = new ChargeDTO();
        chargeDTO.setChargeId(id);
        chargeDTO.setChargeCode("LUMPUNLD");
        chargeDTO.setHeaderId(1);
        chargeDTO.setAutoPay(autoPayFlag);
        chargeDTO.setTotalChargeAmount(new BigDecimal(100));
        return chargeDTO;
    }

    public static ChargeOverride getChargeOverride() {
        ChargeOverride chargeOverride = new ChargeOverride();
        chargeOverride.setOverrideAmount(BigDecimal.valueOf(100.00));
        chargeOverride.setVendorAmount(BigDecimal.valueOf(50.00));
        chargeOverride.setReasonComment("Reason");
        chargeOverride.setDeviationReasonCode("Dev");
        chargeOverride.setOverrideReasonCode("Ovrd");
        chargeOverride.setChargeOverrideId(1);
        chargeOverride.setOverrideApproverId("System");
        return chargeOverride;
    }
}

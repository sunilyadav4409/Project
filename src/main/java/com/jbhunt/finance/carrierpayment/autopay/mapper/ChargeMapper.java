package com.jbhunt.finance.carrierpayment.autopay.mapper;

import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeAuditDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeOverrideDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.ChargeOverride;
import org.mapstruct.DecoratedWith;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
@DecoratedWith(ChargeMapperDecorator.class)
public interface ChargeMapper {

    @Mapping(target = "invoiceNumber", expression = "java(charge.getCarrierInvoiceHeader() != null ? charge.getCarrierInvoiceHeader().getCarrierInvoiceNumber() : null)")
    @Mapping(target = "invoiceDate", source = "carrierInvoiceHeader.invoiceDate")
    @Mapping(target = "carrierDocumentNumber", source = "carrierInvoiceHeader.carrierDocumentNumber")
    @Mapping(target = "invoiceReceivedDate", source = "carrierInvoiceHeader.invoiceReceivedDate")
    ChargeDTO chargeToChargeDTO(Charge charge);

    @InheritInverseConfiguration
    Charge chargeDTOToCharge(ChargeDTO chargeDTO);

    List<ChargeDTO> chargeListToChargeDTOList(List<Charge> chargeList);

    ChargeOverrideDTO chargeOverrideToChargeOverrideDTO(ChargeOverride chargeOverride);

    @InheritInverseConfiguration
    ChargeOverride chargeOverrideDTOToChargeOverride(ChargeOverrideDTO chargeOverrideDTO);

    ChargeAuditDTO chargeDTOToChargeAuditDTO(ChargeDTO chargeDTO);

}

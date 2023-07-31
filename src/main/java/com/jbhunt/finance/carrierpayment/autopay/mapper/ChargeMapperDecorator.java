package com.jbhunt.finance.carrierpayment.autopay.mapper;

import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public abstract class ChargeMapperDecorator implements ChargeMapper {

    @Autowired
    private ChargeMapper chargeMapper;

    @Override
    public ChargeDTO chargeToChargeDTO(Charge charge) {
        ChargeDTO chargeDTO = chargeMapper.chargeToChargeDTO(charge);
        Optional.ofNullable(charge.getCarrierInvoiceHeader()).ifPresent(header -> {
            chargeDTO.setInvoiceNumber(header.getCarrierInvoiceNumber());
            Optional.ofNullable(header.getInvoiceDate()).ifPresent(date -> chargeDTO.setInvoiceDate(date.toString()));
        });
        return chargeDTO;
    }

}

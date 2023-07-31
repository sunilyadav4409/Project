package com.jbhunt.finance.carrierpayment.autopay.mapper;

import java.util.List;

import com.jbhunt.finance.carrierpayment.autopay.dto.QuickPayIndicatorResponseDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.TOrderChargeMessageDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface TorderChargeMapper {

    @Mapping(target = "totalChargeAmount", ignore = true)
    @Mapping(target = "dispatchTokenId", source = "jobId")
    public Payment tOrderChargeMessageToPayment(TOrderChargeMessageDTO tOrderChargeMessageDTO);

    public List<QuickPayIndicatorResponseDTO> mapCharges(List<Charge> charge);

    public QuickPayIndicatorResponseDTO map(Charge charge);

}

package com.jbhunt.finance.carrierpayment.autopay.service.payment;


import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.mapper.ChargeMapper;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.CalcAmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ChargeUtilService {

    @Autowired
    private ChargeMapper chargeMapper;


    public BigDecimal getAmount(List<Charge> allActiveCharges) {
        List<ChargeDTO> chargeDTOList = Optional.ofNullable(allActiveCharges).filter(list -> !list.isEmpty())
                .map(paymentCharges -> chargeMapper.chargeListToChargeDTOList(
                        paymentCharges.stream().filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                                .collect(Collectors.toList())))
                .orElse(new ArrayList<>());
        log.info("getAmount: chargeDTOList.size() " + chargeDTOList.size());
        return CalcAmountUtil.calculateTotal(chargeDTOList);
    }

}

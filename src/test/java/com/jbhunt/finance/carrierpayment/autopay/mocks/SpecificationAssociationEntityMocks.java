package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.dto.ParameterListingDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SpecificationAssociationEntityMocks {

    public static List<ParameterListingDTO> getParameterList5() {
        List<ParameterListingDTO> paramList = new ArrayList<>();
        ParameterListingDTO param = new ParameterListingDTO();
        param.setMinNumberValue(BigDecimal.valueOf(10));
        param.setSpecificationSub( "Transit" ); //Transit
        param.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param.setExpirationDate( LocalDateTime.now().plusDays(2).toString());

        ParameterListingDTO param1 = new ParameterListingDTO();
        param1.setMinNumberValue(BigDecimal.valueOf(10));
        param1.setSpecificationSub( "FUELSURCHG" ); //Transit
        param1.setEffectiveDate( LocalDateTime.now().minusDays(2).toString());
        param1.setExpirationDate( LocalDateTime.now().plusDays(2).toString());
        paramList.add(param);
        paramList.add(param1);
        return paramList;
    }

}

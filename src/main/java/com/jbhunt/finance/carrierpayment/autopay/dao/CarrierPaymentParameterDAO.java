package com.jbhunt.finance.carrierpayment.autopay.dao;

import com.jbhunt.finance.carrierpayment.autopay.dto.ParameterListingDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class CarrierPaymentParameterDAO {
    @Autowired
    private  NamedParameterJdbcTemplate paymentsDbJdbcTemplate;
    @Autowired
    @Qualifier("sqlConfigMapper")
    private  Properties sqlProperties;


    @Cacheable(value = "getParametersForWorkFlowGroup" , key = "#carrierPaymentWorkflowGroupTypeCode")
    public List<ParameterListingDTO> getAllParametersForWorkFlowGroup(String carrierPaymentWorkflowGroupTypeCode) {
        String sql =sqlProperties.getProperty("getParametersList");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("carrierPaymentWorkflowGroupTypeCode",carrierPaymentWorkflowGroupTypeCode);
        log.info("Fetch Parameters List query started at"+ LocalDateTime.now() );
        List<ParameterListingDTO> parameterListingsList = paymentsDbJdbcTemplate.query(sql,paramMap, new BeanPropertyRowMapper<>(ParameterListingDTO.class));
        log.info("Fetch Parameters List query finished at"+ LocalDateTime.now() );
        if( !parameterListingsList.isEmpty()){
            log.info("Number of Parameters List records are : " + parameterListingsList.size());
            return parameterListingsList;
        }else
        {
            return new ArrayList<>(  );
        }
    }

    public ParameterListingDTO getParametersForWorkFlowGroup(String carrierPaymentWorkflowGroupTypeCode) {
        String query = sqlProperties.getProperty("getAutoApprDelayParameters");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("carrierPaymentWorkflowGroupTypeCode",carrierPaymentWorkflowGroupTypeCode);
        log.info("Fetch Parameters List query started at"+ LocalDateTime.now() );
        List<ParameterListingDTO> parameterListingDTO = paymentsDbJdbcTemplate.query(query,paramMap, new BeanPropertyRowMapper<>(ParameterListingDTO.class));
        return Optional.ofNullable(parameterListingDTO).filter(list -> !list.isEmpty()).map(dtoList -> dtoList.get(0))
                .orElseGet(ParameterListingDTO::new);
    }
}


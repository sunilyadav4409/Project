package com.jbhunt.finance.carrierpayment.autopay.dao;

import com.jbhunt.finance.carrierpayment.autopay.dto.ParameterListingDTO;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ParameterMocks;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class CarrierPaymentParameterDAOTest {
    @Mock
    NamedParameterJdbcTemplate paymentsDbJdbcTemplate;
    @Mock
    private Properties sqlProperties;

    @InjectMocks
    CarrierPaymentParameterDAO carrierPaymentParameterDAO;

    @Test
    public void testgetAllParametersForWorkFlowGroup()  {
        String sql= "test SQL";
        java.util.Date  date= new java.util.Date();
        List<ParameterListingDTO> mockresult = ParameterMocks.getParameterListings();
        when(sqlProperties.getProperty("getParametersList")).thenReturn(sql);
        when(paymentsDbJdbcTemplate.query(any(), any(HashMap.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(mockresult);
        List<ParameterListingDTO> result = carrierPaymentParameterDAO.getAllParametersForWorkFlowGroup("ICS");
        Assert.assertEquals(mockresult,result);
    }
    @Test
    public void testgetAllParametersForWorkFlowGroupempty()  {
        String sql= "test SQL";
        java.util.Date  date= new java.util.Date();
        List<ParameterListingDTO> mockresult = new ArrayList<>();
        when(sqlProperties.getProperty("getParametersList")).thenReturn(sql);
        when(paymentsDbJdbcTemplate.query(any(), any(HashMap.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(mockresult);
        List<ParameterListingDTO> result = carrierPaymentParameterDAO.getAllParametersForWorkFlowGroup("ICS");
        Assert.assertEquals(mockresult,result);
    }

   @Test
    public void getParametersForWorkFlowGroupTest() {
        String sql= "test SQL";
        when(sqlProperties.getProperty("getAutoApprDelayParameters")).thenReturn(sql);
        List<ParameterListingDTO> mockresult = new ArrayList<>();
        ParameterListingDTO parameterListingDTO = new ParameterListingDTO();
        parameterListingDTO.setCarrierPaymentWorkflowGroupTypeCode("ICS");
        mockresult.add(parameterListingDTO);
        when(paymentsDbJdbcTemplate.query(any(), any(HashMap.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(mockresult);
        ParameterListingDTO parameterListingEntityOptional = carrierPaymentParameterDAO.getParametersForWorkFlowGroup("ICS");
        Assertions.assertNotNull(parameterListingEntityOptional);
        Assertions.assertEquals("ICS", parameterListingEntityOptional.getCarrierPaymentWorkflowGroupTypeCode());
    }


}

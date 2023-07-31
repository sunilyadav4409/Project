package com.jbhunt.finance.carrierpayment.autopay.dao;

import com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.CustomerChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeEntityMocks;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerChargeDAOTest {

    @InjectMocks
    CustomerChargeDAO customerChargeDAO;
    @Mock
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Mock
    private Properties sqlProperties;

    @Before
    public void setup() {
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .subject("subject")
                .claim("preferred_username",  "jisaad0")
                .claim("azp", "clientId")
                .build());
        setup(jwtAuthenticationToken);
    }

    @Test
    public void testInsertCustomerChargekeyHolder()  {
        Boolean chargeToken;
        String sql= "test SQL";
        when(sqlProperties.getProperty("addChargeKey")).thenReturn(sql);
        when(namedParameterJdbcTemplate.update(any(),any(),any(),any())).thenReturn(1);
        chargeToken = customerChargeDAO.insertCustomerChargekeyHolder(ChargeEntityMocks.createChargeEntityMocks(),1234, "LIFTGATE", "COW3", 0);
        Mockito.verify(namedParameterJdbcTemplate, times(1)).update(any(),any(),any(),any());
        assertNotNull(chargeToken);
        assertTrue(chargeToken);
    }
    @Test
    public void testInsertCustomerChargekeyHolderFalse()  {
        Boolean chargeToken;
        String sql= "test SQL";
        when(sqlProperties.getProperty("addChargeKey")).thenReturn(sql);
        when(namedParameterJdbcTemplate.update(any(),any(),any(),any())).thenReturn(0);
        chargeToken = customerChargeDAO.insertCustomerChargekeyHolder(ChargeEntityMocks.createChargeEntityMocks(),1234, "LIFTGATE", "COW3", 0);
        Mockito.verify(namedParameterJdbcTemplate, times(1)).update(any(),any(),any(),any());
        assertNotNull(chargeToken);
        assertFalse(chargeToken);
    }

    @Test
    public void getCustomerChargesWithExpenseRowAsN() {
        when(sqlProperties.getProperty("GetExpNRowsForBillableCharges")).thenReturn("Test sql");
        Mockito.when(namedParameterJdbcTemplate.query(Mockito.any(), Mockito.anyMap(), Mockito.any(BeanPropertyRowMapper.class))).thenReturn(new ArrayList());
        List<CustomerChargeDTO> result = customerChargeDAO.getCustomerChargesWithExpenseRowAsN(PaymentChargesConstants.PARAM_LOAD_NUMBER, PaymentChargesConstants.CHARGE_LUMPLDPAY,111111);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void getCustomerChargesWithExpenseRowAsNForLumpUlPay() {
        when(sqlProperties.getProperty("GetExpNRowsForBillableCharges")).thenReturn("Test sql");
        Mockito.when(namedParameterJdbcTemplate.query(Mockito.any(), Mockito.anyMap(), Mockito.any(BeanPropertyRowMapper.class))).thenReturn(new ArrayList());
        List<CustomerChargeDTO> result = customerChargeDAO.getCustomerChargesWithExpenseRowAsN(PaymentChargesConstants.PARAM_LOAD_NUMBER, PaymentChargesConstants.CHARGE_LUMPULPAY,111111);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void getCustomerChargesWithExpenseRowAsNForDRVRLDPAY() {
        when(sqlProperties.getProperty("GetExpNRowsForBillableCharges")).thenReturn("Test sql");
        Mockito.when(namedParameterJdbcTemplate.query(Mockito.any(), Mockito.anyMap(), Mockito.any(BeanPropertyRowMapper.class))).thenReturn(new ArrayList());
        List<CustomerChargeDTO> result = customerChargeDAO.getCustomerChargesWithExpenseRowAsN(PaymentChargesConstants.PARAM_LOAD_NUMBER, PaymentChargesConstants.CHARGE_DRVRLDPAY,111111);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void getCustomerChargesWithExpenseRowAsNForDRVRULPAY() {
        when(sqlProperties.getProperty("GetExpNRowsForBillableCharges")).thenReturn("Test sql");
        Mockito.when(namedParameterJdbcTemplate.query(Mockito.any(), Mockito.anyMap(), Mockito.any(BeanPropertyRowMapper.class))).thenReturn(new ArrayList());
        List<CustomerChargeDTO> result = customerChargeDAO.getCustomerChargesWithExpenseRowAsN(PaymentChargesConstants.PARAM_LOAD_NUMBER, PaymentChargesConstants.CHARGE_DRVRULPAY,111111);
        Assertions.assertTrue(result.isEmpty());
    }
    @Test
    public void getCustomerChargesWithExpenseRowAsNForOtherChargeCOdes() {
        when(sqlProperties.getProperty("GetExpNRowsForBillableCharges")).thenReturn("Test sql");
        Mockito.when(namedParameterJdbcTemplate.query(Mockito.any(), Mockito.anyMap(), Mockito.any(BeanPropertyRowMapper.class))).thenReturn(new ArrayList());
        List<CustomerChargeDTO> result = customerChargeDAO.getCustomerChargesWithExpenseRowAsN(PaymentChargesConstants.PARAM_LOAD_NUMBER, "Test123",111111);
        Assertions.assertTrue(result.isEmpty());
    }

    private void setup(JwtAuthenticationToken jwtAuthenticationToken) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
    }
}

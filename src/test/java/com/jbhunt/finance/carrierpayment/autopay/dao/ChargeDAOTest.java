package com.jbhunt.finance.carrierpayment.autopay.dao;

import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeTokenDTO;
import com.jbhunt.finance.carrierpayment.autopay.mocks.ChargeDTOMocks;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.math.BigDecimal;
import java.util.Properties;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChargeDAOTest {

    @InjectMocks
    ChargeDAO chargeDAO;
    @Mock
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Mock
    private Properties sqlProperties;
    @Mock
    ChargeAuditDAO chargeAuditDAO;
    //@Mock
   // private  OrderDAO orderDAO;
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
        chargeToken = chargeDAO.insertCustomerChargekeyHolder(ChargeDTOMocks.createAuditRecordOnApproveMocks());
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
        chargeToken = chargeDAO.insertCustomerChargekeyHolder(ChargeDTOMocks.createAuditRecordOnApproveMocks());
        Mockito.verify(namedParameterJdbcTemplate, times(1)).update(any(),any(),any(),any());
        assertNotNull(chargeToken);
        assertFalse(chargeToken);
    }
    @Test
    public void testUpdateCustChargefalse()  {
        ChargeTokenDTO chargeToken = new ChargeTokenDTO();
        String sql= "test SQL";
        when(sqlProperties.getProperty("updateCustCharge")).thenReturn(sql);
        when(namedParameterJdbcTemplate.update(any(),anyMap())).thenReturn(0);
        chargeToken = chargeDAO.updateCustCharge(ChargeDTOMocks.createChargeOverrideMocks());
        Mockito.verify(namedParameterJdbcTemplate, times(1)).update(any(),anyMap());
        assertNotNull(chargeToken);
        assertFalse(chargeToken.getIntegrationStatus());
    }
    @Test
    public void testUpdateCustCharge()  {
        ChargeTokenDTO chargeToken = new ChargeTokenDTO();
        String sql= "test SQL";
        when(sqlProperties.getProperty("updateCustCharge")).thenReturn(sql);
        when(namedParameterJdbcTemplate.update(any(),anyMap())).thenReturn(1);
        chargeToken = chargeDAO.updateCustCharge(ChargeDTOMocks.createAuditRecordOnApproveMocks());
        Mockito.verify(namedParameterJdbcTemplate, times(1)).update(any(),anyMap());
        assertNotNull(chargeToken);
        assertTrue(chargeToken.getIntegrationStatus());
    }
    @Test
    public void testUpdateCharge()  {
        ChargeTokenDTO chargeToken = new ChargeTokenDTO();
        String sql= "test SQL";
        when(sqlProperties.getProperty("updateCharge")).thenReturn(sql);
        when(namedParameterJdbcTemplate.update(any(),anyMap())).thenReturn(1);
        chargeToken = chargeDAO.updateCharge(ChargeDTOMocks.createChargeOverrideMocks());
        Mockito.verify(namedParameterJdbcTemplate, times(1)).update(any(),anyMap());
        assertNotNull(chargeToken);
        assertTrue(chargeToken.getIntegrationStatus());
    }
    @Test
    public void testUpdateChargeFalse()  {
        ChargeTokenDTO chargeToken = new ChargeTokenDTO();
        String sql= "test SQL";
        when(sqlProperties.getProperty("updateCharge")).thenReturn(sql);
        when(namedParameterJdbcTemplate.update(any(),anyMap())).thenReturn(0);
        chargeToken = chargeDAO.updateCharge(ChargeDTOMocks.createAuditRecordOnApproveMocks());
        Mockito.verify(namedParameterJdbcTemplate, times(1)).update(any(),anyMap());
        assertNotNull(chargeToken);
        assertFalse(chargeToken.getIntegrationStatus());
    }
    @Test
    public void testUpdateApproveDetailsFalse()  {
        Boolean chargeToken ;
        String sql= "test SQL";
        when(sqlProperties.getProperty("approveCharges")).thenReturn(sql);
        chargeToken = chargeDAO.updateApproveDetails(ChargeDTOMocks.createAuditRecordOnApproveMocks());
        assertNotNull(chargeToken);
        assertFalse(chargeToken);
    }
    @Test
    public void testUpdateApproveDetails()  {
        Boolean chargeToken ;
        String sql= "test SQL";
        when(sqlProperties.getProperty("approveCharges")).thenReturn(sql);
        when(namedParameterJdbcTemplate.update(any(),anyMap())).thenReturn(1);
        chargeToken = chargeDAO.updateApproveDetails(ChargeDTOMocks.createChargeOverrideWithExternalChargeIdListMocks());
        Mockito.verify(namedParameterJdbcTemplate, times(2)).update(any(),anyMap());
        assertNotNull(chargeToken);
        assertTrue(chargeToken);
    }
    @Test
    public void testGetExistingChargeCount()  {
        Integer chargeCount ;
        String sql= "test SQL";
        when(sqlProperties.getProperty("getExistingChargeCount")).thenReturn(sql);
        when(namedParameterJdbcTemplate.queryForObject(any(), anyMap(),eq(Integer.class))).thenReturn(1);
        chargeCount = chargeDAO.getExistingChargeCount(ChargeDTOMocks.createChargeOverrideWithExternalChargeIdListMocks());
        Mockito.verify(namedParameterJdbcTemplate, times(1)).queryForObject(any(), anyMap(),eq(Integer.class));
        assertNotNull(chargeCount);
        assertThat(chargeCount, is(1));
    }
    @Test
    public void testValidatDetails()  {
        Integer chargeCount ;
        String sql= "test SQL";
        when(sqlProperties.getProperty("validateCharges")).thenReturn(sql);
        when(namedParameterJdbcTemplate.queryForObject(any(), anyMap(),eq(BigDecimal.class))).thenReturn(new BigDecimal("1.00"));
        chargeDAO.validatDetails(ChargeDTOMocks.createChargeOverrideWithExternalChargeIdListMocks());
        Mockito.verify(namedParameterJdbcTemplate, times(2)).queryForObject(any(), anyMap(),eq(BigDecimal.class));

    }
    @Test
    public void testValidateTotalAmount()  {
        BigDecimal chargeAmount ;
        String sql= "test SQL";
        when(sqlProperties.getProperty("totalamountvalidate")).thenReturn(sql);
        when(namedParameterJdbcTemplate.queryForObject(any(), anyMap(),eq(BigDecimal.class))).thenReturn(new BigDecimal("1.00"));
        chargeAmount = chargeDAO.validateTotalAmount(ChargeDTOMocks.createChargeOverrideWithExternalChargeIdListMocks());
        Mockito.verify(namedParameterJdbcTemplate, times(1)).queryForObject(any(), anyMap(),eq(BigDecimal.class));
        assertNotNull(chargeAmount);

    }

    @Test
    public void testUpdateExpenceRowN()  {
        Integer externalIDcount ;
        String sql= "test SQL";
        when(sqlProperties.getProperty("InActExpNRow")).thenReturn(sql);
        when(namedParameterJdbcTemplate.update(any(),anyMap())).thenReturn(1);
        externalIDcount = chargeDAO.updateExpenceRowN(1234);
        Mockito.verify(namedParameterJdbcTemplate, times(1)).update(any(), anyMap());
        assertNotNull(externalIDcount);
        assertThat(externalIDcount, is(1));

    }

    @Test
    public void getCountFromTOrderByLoadNumberTest()  {
        String sql= "test SQL";
        when(sqlProperties.getProperty("getCountFromTOrderByLoadNumber")).thenReturn(sql);
        when(namedParameterJdbcTemplate.queryForObject(any(), anyMap(),eq(Integer.class))).thenReturn(1);
        int count =chargeDAO.getCountFromTOrderByLoadNumber("JW23456");
        verify(namedParameterJdbcTemplate, times(1)).queryForObject(any(), anyMap(),eq(Integer.class));
        assertEquals(1,count);
    }

    @Test
    public void getCountFromTOrderWByLoadNumberTest()  {
        String sql= "test SQL";
        when(sqlProperties.getProperty("getCountFromTOrderByLoadNumber")).thenReturn(sql);
        when(namedParameterJdbcTemplate.queryForObject(any(), anyMap(),eq(Integer.class))).thenReturn(0);
        int count = chargeDAO.getCountFromTOrderByLoadNumber("V686424");
        verify(namedParameterJdbcTemplate, times(2)).queryForObject(any(), anyMap(),eq(Integer.class));
        assertEquals(0,count);
    }
    private void setup(JwtAuthenticationToken jwtAuthenticationToken) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
    }
}

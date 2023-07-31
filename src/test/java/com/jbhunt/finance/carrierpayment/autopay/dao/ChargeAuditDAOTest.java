package com.jbhunt.finance.carrierpayment.autopay.dao;

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

import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ChargeAuditDAOTest {
    @InjectMocks
    ChargeAuditDAO chargeAuditDAO;

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
    public void testCreateAuditRecordOnApproveShouldNotCallJDBC()  {
        String sql= "test SQL";
        java.util.Date  date= new java.util.Date();

        when(sqlProperties.getProperty("insertAuditRecord")).thenReturn(sql);
        chargeAuditDAO.createAuditRecordOnApprove(ChargeDTOMocks.createChargeDTOMOcks(), date);
        Mockito.verify(namedParameterJdbcTemplate, times(0)).update(any(),anyMap());
    }
    @Test
    public void testCreateAuditRecordOnApproveShouldCallJDBC()  {
        String sql= "test SQL";
        java.util.Date  date= new java.util.Date();
        when(sqlProperties.getProperty("insertAuditRecord")).thenReturn(sql);
        when(namedParameterJdbcTemplate.update(any(),anyMap())).thenReturn(1);
        chargeAuditDAO.createAuditRecordOnApprove(ChargeDTOMocks.createAuditRecordOnApproveMocks(), date);
        Mockito.verify(namedParameterJdbcTemplate, times(2)).update(any(),anyMap());
    }

    private void setup(JwtAuthenticationToken jwtAuthenticationToken) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
    }
}

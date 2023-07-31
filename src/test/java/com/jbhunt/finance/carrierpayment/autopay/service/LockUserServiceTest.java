package com.jbhunt.finance.carrierpayment.autopay.service;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.mocks.OptimisticLockPaymentsMocks;
import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentMocks;
import com.jbhunt.finance.carrierpayment.autopay.repository.OptimisticLockRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.LockUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LockUserServiceTest {
    @InjectMocks
    LockUserService lockUserService;
    @Mock
    OptimisticLockRepository optimisticLockRepository;

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
    public void lockCurrentUserTest(){
        lockUserService.lockCurrentUser(PaymentMocks.getPaymentMock(),"hgd","hmgf");
        verify(optimisticLockRepository,times(1)).saveAll(anyList());
    }

    @Test
    public void checkForNotStaleTest(){
        when(optimisticLockRepository.findByCarrierPaymentIdAndLockUserIdAndExpirationTimestampIsNull(anyInt(),anyString())).thenReturn(null);
        when(optimisticLockRepository.findByLatestUpdatedUser(anyInt(),any(),any())).thenReturn(null);
        TxnStatusDTO txnStatusDTO = lockUserService.checkForNotStale(PaymentMocks.getPaymentMock());
        assertNotNull(txnStatusDTO);
        verify(optimisticLockRepository,times(1)).findByCarrierPaymentIdAndLockUserIdAndExpirationTimestampIsNull(anyInt(),anyString());
        verify(optimisticLockRepository,times(1)).findByLatestUpdatedUser(anyInt(),eq(CarrierPaymentConstants.LOCK_UPDATE),any());
    }
    @Test
    public void checkForNotStaleToGetStaleTest(){
        when(optimisticLockRepository.findByCarrierPaymentIdAndLockUserIdAndExpirationTimestampIsNull(anyInt(),anyString())).thenReturn(OptimisticLockPaymentsMocks.getListOfOptimisticLocks());
        when(optimisticLockRepository.findByLatestUpdatedUser(anyInt(),any(),any())).thenReturn(OptimisticLockPaymentsMocks.getListOfOptimisticLocks().get(0));
        TxnStatusDTO txnStatusDTO = lockUserService.checkForNotStale(PaymentMocks.getPaymentMock());
        assertNotNull(txnStatusDTO);
        assertEquals(CarrierPaymentConstants.WARNING_STALE_RECORD,txnStatusDTO.getWarning());
        verify(optimisticLockRepository,times(1)).findByCarrierPaymentIdAndLockUserIdAndExpirationTimestampIsNull(anyInt(),anyString());
        verify(optimisticLockRepository,times(1)).findByLatestUpdatedUser(anyInt(),eq(CarrierPaymentConstants.LOCK_UPDATE),any());
    }

    @Test
    public void checkForNotStaleToGetNotStaleTest(){
        when(optimisticLockRepository.findByCarrierPaymentIdAndLockUserIdAndExpirationTimestampIsNull(anyInt(),anyString())).thenReturn(OptimisticLockPaymentsMocks.getListOfOptimisticLocks());
        when(optimisticLockRepository.findByLatestUpdatedUser(anyInt(),any(),any())).thenReturn(OptimisticLockPaymentsMocks.getOptimisticLocks());
        TxnStatusDTO txnStatusDTO = lockUserService.checkForNotStale(PaymentMocks.getPaymentMock());
        assertNotNull(txnStatusDTO);
        assertEquals(true,txnStatusDTO.isSuccess());
        verify(optimisticLockRepository,times(1)).findByCarrierPaymentIdAndLockUserIdAndExpirationTimestampIsNull(anyInt(),anyString());
        verify(optimisticLockRepository,times(1)).findByLatestUpdatedUser(anyInt(),eq(CarrierPaymentConstants.LOCK_UPDATE),any());
    }

    @Test
    public void unlockCurrentUserTest() {
        Payment payment = PaymentMocks.getPaymentMock();
        Mockito.when(optimisticLockRepository.findByCarrierPaymentIdAndLockUserIdAndExpirationTimestampIsNull(anyInt(), anyString())).thenReturn(OptimisticLockPaymentsMocks.getListOfOptimisticLocks());
        lockUserService.unlockCurrentUser(payment, "UserTest");
        Mockito.verify(optimisticLockRepository, Mockito.times(1)).saveAll(Mockito.anyList());
    }

    private void setup(JwtAuthenticationToken jwtAuthenticationToken) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
    }


}

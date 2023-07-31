package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.SYSTEM_USERID;

@Slf4j
public class UserUtil {
    private UserUtil() {
    }
    public static Jwt getAccessToken() {
        return ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken();
    }
    public static String getLoggedInUser() {
        var userName = getAccessToken().getClaimAsString("preferred_username");
        if(userName!= null && userName.length() > 8) {
            return SYSTEM_USERID;
        }
        return  userName;
    }
}


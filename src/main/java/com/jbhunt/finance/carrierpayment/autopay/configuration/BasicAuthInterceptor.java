package com.jbhunt.finance.carrierpayment.autopay.configuration;

import com.jbhunt.biz.securepid.PIDCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import static com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants.*;


@Component
public class BasicAuthInterceptor implements ClientHttpRequestInterceptor {

    private PIDCredentials pidCredentials;

    @Autowired
    public BasicAuthInterceptor(PIDCredentials pidCredentials) {
        this.pidCredentials = pidCredentials;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        final String auth = pidCredentials.getUsername() + COLON + pidCredentials.getPassword();
        final byte[] encodeAuth = Base64.getEncoder().encode(auth.getBytes());
        final String authHeader = BASIC_AUTH_HEADER + new String(encodeAuth);
        request.getHeaders().add(AUTHORIZATION, authHeader);
        request.getHeaders().add(USER_NAME_TOKEN, getLoggedInUser());
        return execution.execute(request, body);
    }

    private String getLoggedInUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal).filter(UserDetails.class::isInstance).map(UserDetails.class::cast)
                .map(UserDetails::getUsername).orElse(null);
    }

}

package com.jbhunt.finance.carrierpayment.autopay.configuration;

import com.jbhunt.biz.securepid.PIDCredentials;
import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BasicAuthInterceptorTest {
    private static final String PID_USERNAME = "userName";
    private static final String PID_PASSWORD = "password";

    @InjectMocks
    @Spy
    private BasicAuthInterceptor target;

    @Mock
    private PIDCredentials pidCredentials;

    @Mock
    private HttpRequest mockedHttpRequest;

    @Mock
    private ClientHttpRequestExecution mockedExecution;

    @Before
    public void setup() {
        when(pidCredentials.getUsername()).thenReturn(PID_USERNAME);
        when(pidCredentials.getPassword()).thenReturn(PID_PASSWORD);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(target);
        Mockito.verifyNoMoreInteractions(mockedHttpRequest);
        Mockito.verifyNoMoreInteractions(mockedExecution);
    }

    @Test
    public void testIntercept() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        byte[] body = {};
        ClientHttpResponse response = new MockClientHttpResponse(body, HttpStatus.OK);
        String expectedAuthHeader = "Basic " + new String(Base64.encodeBase64((PID_USERNAME + ":" + PID_PASSWORD).getBytes(Charset.forName("US-ASCII"))));
        when(mockedHttpRequest.getHeaders()).thenReturn(httpHeaders);
        when(mockedExecution.execute(mockedHttpRequest, body)).thenReturn(response);
        ClientHttpResponse actual = target.intercept(mockedHttpRequest, body, mockedExecution);
        assertThat(actual, notNullValue());
        assertThat(actual, is(response));
        verify(target).intercept(mockedHttpRequest, body, mockedExecution);
        verify(mockedHttpRequest,times(2)).getHeaders();
        assertThat(httpHeaders.containsKey("Authorization"), is(true));
        assertThat(httpHeaders.get("Authorization"), hasSize(1));
        assertThat(httpHeaders.get("Authorization").get(0), is(expectedAuthHeader));
        verify(mockedExecution).execute(mockedHttpRequest, body);
    }

}

package com.jbhunt.finance.carrierpayment.autopay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.jbhunt.finance.carrierpayment.autopay.mocks.SupplierMock;
import com.jbhunt.finance.carrierpayment.autopay.repository.SupplierRepository;
import com.jbhunt.finance.carrierpayment.autopay.service.payment.SupplierService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SupplierServiceTest {

    @InjectMocks
    private SupplierService supplierService;
    @Mock
    SupplierRepository supplierRepository;
    @Mock
    WebClient supplierServiceClient;
    @Autowired
    private WireMockServer wireMockServer;

    @Before
    public void setup() throws IOException {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        supplierServiceClient = WebClient.create("http://localhost:" + wireMockServer.port());
    }

    @Test
    public void retrieveCarrierInformationFromSupplierServiceTest() throws Exception  {
        ReflectionTestUtils.setField(supplierService, "supplierServiceClient",supplierServiceClient);
        wireMockServer.stubFor(
                post(anyUrl())
                        .willReturn(aResponse()
                                .withHeader("content-type", "application/json;charset=UTF-8")
                                .withBody(getSupplierMock())
                                .withStatus(200)));
        var supplier=   supplierService.retrieveSupplierByScacAndCurrencyOrJustScac("test", "test");
        assertNotNull(supplier);
        Assert.assertEquals("ABC", supplier.getScacCode());
    }

    @Test
    public void retrieveCarrierInformationFromSupplierService404Test() throws Exception  {
        ReflectionTestUtils.setField(supplierService, "supplierServiceClient",supplierServiceClient);
        wireMockServer.stubFor(
                post(anyUrl())
                        .willReturn(aResponse()
                                .withHeader("content-type", "application/json;charset=UTF-8")
                                .withBody("")
                                .withStatus(404)));
        var supplier=   supplierService.retrieveSupplierByScacAndCurrencyOrJustScac("test", "test");
        assertNull(supplier);
    }
    @Test
    public void retrieveCarrierInformationFromSupplierService401Test() throws Exception  {
        ReflectionTestUtils.setField(supplierService, "supplierServiceClient",supplierServiceClient);
        wireMockServer.stubFor(
                post(anyUrl())
                        .willReturn(aResponse()
                                .withHeader("content-type", "application/json;charset=UTF-8")
                                .withBody("")
                                .withStatus(401)));
        var supplier=   supplierService.retrieveSupplierByScacAndCurrencyOrJustScac("test", "test");
        assertNull(supplier);
    }
    @Test
    public void retrieveCarrierInformationFromSupplierService500Test() throws Exception  {
        ReflectionTestUtils.setField(supplierService, "supplierServiceClient",supplierServiceClient);
        wireMockServer.stubFor(
                post(anyUrl())
                        .willReturn(aResponse()
                                .withHeader("content-type", "application/json;charset=UTF-8")
                                .withBody("")
                                .withStatus(500)));
        var supplier=   supplierService.retrieveSupplierByScacAndCurrencyOrJustScac("test", "test");
        assertNull(supplier);
    }



    private String getSupplierMock() throws JsonProcessingException {
        var supplierMock = SupplierMock.getSupplier();
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper.writeValueAsString(supplierMock);
    }
    @Test
    public void retrieveSupplierBySupplierIdTest() throws Exception  {

        Mockito.when(supplierRepository.findBySupplierID(123)).thenReturn( SupplierMock.getSupplier());
        var supplier=   supplierService.retrieveSupplierBySupplierId(123);
        verify(supplierRepository,times(1)).findBySupplierID(anyInt());
        assertNotNull(supplier);
    }
}
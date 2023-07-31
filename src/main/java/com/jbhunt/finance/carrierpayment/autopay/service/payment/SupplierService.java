package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.dto.SupplierDataExtractParams;
import com.jbhunt.finance.carrierpayment.autopay.entity.Supplier;
import com.jbhunt.finance.carrierpayment.autopay.repository.SupplierRepository;
import com.jbhunt.infrastructure.exception.JBHNonRecoverableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierService {
    private final WebClient supplierServiceClient;
    private final SupplierRepository supplierRepository;


    public Supplier retrieveSupplierByScacAndCurrencyOrJustScac(String scac, String currency) {
        var supplierDataExtractParams = new SupplierDataExtractParams();
        supplierDataExtractParams.setScacCode(scac);
        supplierDataExtractParams.setCurrencyCode(currency);
        try {
            return supplierServiceClient.post()
                    .uri("supplierRetrieval/getSupplierDetailsByScac")
                    .body(BodyInserters.fromValue(supplierDataExtractParams))
                    .retrieve()
                    .onStatus(HttpStatus:: isError, error -> {
                        log.error("Error while getting supplier details for SCAC : {} and Currency : {}", scac, currency);
                        throw new JBHNonRecoverableException("Exception while calling supplier");
                    })
                    .bodyToMono(Supplier.class).block();
        } catch (JBHNonRecoverableException e) {
            return  null;
        }
    }

    public Supplier retrieveSupplierBySupplierId(Integer supplierId) {
        return  supplierRepository.findBySupplierID(supplierId);
    }

}


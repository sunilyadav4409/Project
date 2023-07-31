package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.entity.DocumentOverride;
import com.jbhunt.finance.carrierpayment.autopay.entity.RequiredDocument;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequiredDocumentMock {

    public static List<RequiredDocument> getRequiredDocumentListScanDateNullMock() {
        List<RequiredDocument> requiredDocumentList = new ArrayList<>();
        RequiredDocument requiredDocument = new RequiredDocument();
        requiredDocument.setRequirementTypeCode("Required");
        requiredDocument.setCarrierRequiredDocumentID(24);
        requiredDocument.setDocumentTypeCode("LUMPER");
        requiredDocument.setPaymentId(PaymentMocks.getPaymentMock());
        requiredDocument.setDocumentScanDate(LocalDateTime.now());
        requiredDocument.setDocumentRequirementFulfillmentDate(LocalDateTime.now());
        requiredDocument.setDocumentRecievedDate(LocalDateTime.now());
        requiredDocument.setDocumentNumber("12");
        DocumentOverride documentOverride = new DocumentOverride();
        documentOverride.setOverridereasonCode("CustPdBOL");
        requiredDocument.setDocumentOverride(List.of(documentOverride));
        requiredDocumentList.add(requiredDocument);
        return requiredDocumentList;
    }

}

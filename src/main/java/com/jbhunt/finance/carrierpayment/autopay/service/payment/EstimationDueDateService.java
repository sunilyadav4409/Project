
package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.entity.RequiredDocument;
import com.jbhunt.finance.carrierpayment.autopay.repository.DocumentRepository;
import com.jbhunt.finance.carrierpayment.autopay.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.LUMPER;
import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants.REQUIRED;
import static com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants.JBI;

@Service
@Slf4j
public class EstimationDueDateService {

    @Autowired
    private  DocumentRepository documentRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    private Set<LocalDateTime> getRequiredDocumentScanDates(Integer carrierPaymentId, String groupFlowTypeCode) {
        Set<LocalDateTime> scanDateSet = new HashSet<>();
        if (!JBI.equalsIgnoreCase(groupFlowTypeCode)) {
            List<RequiredDocument> requiredDocuments = documentRepository.findByPaymentIdCarrierPaymentId(carrierPaymentId);
            scanDateSet.addAll(requiredDocuments.stream().filter(requiredDocument -> REQUIRED.equalsIgnoreCase(requiredDocument.getRequirementTypeCode())
                    && requiredDocument.getDocumentScanDate() != null
                    && !requiredDocument.getDocumentTypeCode().equals(LUMPER)).map(RequiredDocument::getDocumentScanDate).collect(Collectors.toSet()));

            scanDateSet.addAll(requiredDocuments.stream().filter(requiredDocument ->
                    requiredDocument.getDocumentOverride() != null && requiredDocument.getDocumentOverride().stream().anyMatch(documentOverride -> "CustPdBOL".equalsIgnoreCase(documentOverride.getOverridereasonCode())))
                    .filter(requiredDocument -> requiredDocument.getDocumentRequirementFulfillmentDate() != null).map(RequiredDocument::getDocumentRequirementFulfillmentDate).collect(Collectors.toSet()));
        }
        return scanDateSet;
    }

    private LocalDateTime getTermStartDate(LocalDateTime newInviceDocumentScanDate, Set<LocalDateTime> subList, Set<LocalDateTime> scanDateSet, LocalDate appointmentDate) {
        LocalDateTime startTermsDate = newInviceDocumentScanDate;
        if(CollectionUtils.isNotEmpty(subList)) {
            startTermsDate = subList.stream().sorted().findFirst().get();
        }

        if(appointmentDate != null) {
            scanDateSet.add(appointmentDate.atStartOfDay());
        }
        if(CollectionUtils.isNotEmpty(scanDateSet)) {
            if(startTermsDate != null) {
                scanDateSet.add(startTermsDate);
            }
            startTermsDate = scanDateSet.stream().max(LocalDateTime:: compareTo).get();
        }
        return startTermsDate;
    }


    public LocalDateTime getTermStartDateForApproveScenarioByCurrentInvoicesScanDate(Integer carrierPaymentId, LocalDateTime invoiceScanTimeStamp) {
        Payment payment = paymentRepository.findByCarrierPaymentId(carrierPaymentId);
        Set<LocalDateTime> subList = new HashSet<>();
        subList.add(invoiceScanTimeStamp);
        Set<LocalDateTime> scanDateSet = getRequiredDocumentScanDates(carrierPaymentId, payment.getGroupFlowTypeCode());
        return getTermStartDate(null, subList, scanDateSet, payment.getAppointmentDate());
    }
}

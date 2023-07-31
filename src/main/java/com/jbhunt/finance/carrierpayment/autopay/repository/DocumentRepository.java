package com.jbhunt.finance.carrierpayment.autopay.repository;

import com.jbhunt.finance.carrierpayment.autopay.entity.RequiredDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<RequiredDocument, Integer> {

    List<RequiredDocument> findByPaymentIdCarrierPaymentId(Integer paymentId);
}

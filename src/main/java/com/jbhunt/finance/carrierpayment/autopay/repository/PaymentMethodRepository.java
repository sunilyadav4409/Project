package com.jbhunt.finance.carrierpayment.autopay.repository;

import com.jbhunt.finance.carrierpayment.autopay.entity.PaymentMethodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "paymentmethod")
public interface PaymentMethodRepository extends JpaRepository<PaymentMethodType, String> {
    PaymentMethodType findByPaymentMethodTypeCode(@Param("paymentMethodTypeCode") String paymentMethodTypeCode);
}

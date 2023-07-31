package com.jbhunt.finance.carrierpayment.autopay.repository;

import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierPaymentChargeAuditDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface CarrierPaymentChargeAuditDetailRepository extends JpaRepository<CarrierPaymentChargeAuditDetail, Integer>, QuerydslPredicateExecutor<CarrierPaymentChargeAuditDetail> {
    List<CarrierPaymentChargeAuditDetail> findByChargeIdIn(List<Integer> chargeIds);
}

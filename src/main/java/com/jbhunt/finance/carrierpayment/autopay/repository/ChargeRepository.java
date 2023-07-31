package com.jbhunt.finance.carrierpayment.autopay.repository;

import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, Integer>, QuerydslPredicateExecutor<Charge> {

    List<Charge> findByCarrierPaymentCarrierPaymentIdAndExpirationTimestampIsNull(
            @Param("carrierPaymentId") Integer carrierPaymentId);

    List<Charge> findByHeaderId(@Param("headerId") Integer headerId);

    List<Charge> findByCarrierPaymentCarrierPaymentIdAndChargeDecisionCodeAndChargeApplyToCustomerIndicatorAndExpirationTimestampIsNullAndChargeCodeIn(
            @Param("carrierPaymentId") Integer carrierPaymentId,  @Param("chargeDecisionCode") String chargeDecisionCode,
            @Param("chargeApplyToCustomerIndicator") Character chargeApplyToCustomerIndicator,@Param("chargeCode") List<String> chargeCode);

}

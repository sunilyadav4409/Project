package com.jbhunt.finance.carrierpayment.autopay.repository;

import com.jbhunt.finance.carrierpayment.autopay.entity.PaymentShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentShipmentRepository extends JpaRepository<PaymentShipment, Integer>, QuerydslPredicateExecutor<PaymentShipment> {



        @Query(value = "select p.DivisionCode from CFP.CarrierPaymentShipment p where p.CarrierPaymentShipmentID =:shipmentID", nativeQuery = true)
         String findDivisionByCarrierPaymentShipmentID(@Param("shipmentID") Integer shipmentID);


}

package com.jbhunt.finance.carrierpayment.autopay.repository;


import com.jbhunt.finance.carrierpayment.autopay.entity.PaymentStateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "paymentstatelog")
public interface PaymentStateLogRepository extends JpaRepository<PaymentStateLog, Long> {

    List<PaymentStateLog> findByCarrierPaymentIdCarrierPaymentIdAndStatusFlowTypeCode(Integer paymentId,
                                                                                      String paymentStatus);

    @Query(value = "select * from CFP.PaymentStateLog p where p.CarrierPaymentID =:paymentId"
            + " and p.PaymentStateEndTimestamp is null", nativeQuery = true)
    PaymentStateLog findPaymentStateLog(@Param("paymentId") Integer paymentId);

    @Query(value = "select Top(1) * from CFP.PaymentStateLog p where p.CarrierPaymentID =:paymentId"
            + " and p.PaymentStateEndTimestamp is not null"
            + " order by p.PaymentStateEndTimestamp desc", nativeQuery = true)
    PaymentStateLog findPreviousPaymentStateLog(@Param("paymentId") Integer paymentId);


}

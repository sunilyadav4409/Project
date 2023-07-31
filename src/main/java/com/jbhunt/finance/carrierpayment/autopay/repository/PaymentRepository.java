package com.jbhunt.finance.carrierpayment.autopay.repository;


import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "paymentservice")

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Payment findByCarrierPaymentId(Integer carrierpaymentid);

    @Query(value = "select Top(1) * from CFP.CarrierPayment where CarrierPaymentID=:carrierPaymentId", nativeQuery = true)
    Payment findPaymentByCarrierPaymentId(@Param("carrierPaymentId") Integer carrierPaymentId);


}

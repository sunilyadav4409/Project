package com.jbhunt.finance.carrierpayment.autopay.repository;

import com.jbhunt.finance.carrierpayment.autopay.entity.OptimisticLockPayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OptimisticLockRepository
        extends JpaRepository<OptimisticLockPayments, Integer>, QuerydslPredicateExecutor<OptimisticLockPayments> {


    List<OptimisticLockPayments> findByCarrierPaymentIdAndLockUserIdAndExpirationTimestampIsNull(
            @Param("carrierPaymentId") Integer carrierPaymentId, @Param("lockUserId") String lockUserId);

    @Query(value = "select TOP(1) * from CFP.CarrierPaymentOptimisticLock where CarrierPaymentID=:carrierPaymentId and "
            + "LockActionTypeCode=:lockActionTypeCode and LastUpdateUserID !=:lockUserId and ExpirationTimestamp is null  ORDER BY EffectiveTimestamp DESC ", nativeQuery = true)
    OptimisticLockPayments findByLatestUpdatedUser(@Param("carrierPaymentId") Integer carrierPaymentId,
                                                   @Param("lockActionTypeCode") String lockActionTypeCode
            , @Param("lockUserId") String lockUserId);


}

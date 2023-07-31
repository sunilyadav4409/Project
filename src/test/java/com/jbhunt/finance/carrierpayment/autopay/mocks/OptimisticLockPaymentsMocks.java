package com.jbhunt.finance.carrierpayment.autopay.mocks;

import com.jbhunt.finance.carrierpayment.autopay.entity.OptimisticLockPayments;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OptimisticLockPaymentsMocks {

    public static List<OptimisticLockPayments> getListOfOptimisticLocks(){
        List<OptimisticLockPayments> optimisticLockPaymentsList = new ArrayList<>();
        OptimisticLockPayments optimisticLockPayments = new OptimisticLockPayments();
        optimisticLockPayments.setCarrierPaymentId(1);
        optimisticLockPayments.setLockUserId("gnfa");
        optimisticLockPayments.setEffectiveTimestamp(LocalDateTime.now());
        optimisticLockPayments.setLockActionTypeCode("hsg");
        optimisticLockPaymentsList.add(optimisticLockPayments);
        return  optimisticLockPaymentsList;
    }
    public static OptimisticLockPayments getOptimisticLocks(){
        LocalDateTime a =LocalDateTime.now();
        OptimisticLockPayments optimisticLockPayments = new OptimisticLockPayments();
        optimisticLockPayments.setCarrierPaymentId(1);
        optimisticLockPayments.setLockUserId("gnfa");
        optimisticLockPayments.setEffectiveTimestamp(a.minusMinutes(5));
        optimisticLockPayments.setLockActionTypeCode("hsg");
        return  optimisticLockPayments;
    }
}

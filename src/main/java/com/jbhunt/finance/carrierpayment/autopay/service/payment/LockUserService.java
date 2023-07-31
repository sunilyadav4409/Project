package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.OptimisticLockPayments;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import com.jbhunt.finance.carrierpayment.autopay.repository.OptimisticLockRepository;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class LockUserService {
    @Autowired
    OptimisticLockRepository optimisticLockRepository;
    public  void lockCurrentUser(Payment payment, String viewerUserId, String actionTypeCode) {
        log.info("LockUtil :: lockCurrentUser");
        Optional.ofNullable(payment).ifPresent(pmnt -> {
            List<OptimisticLockPayments> optimisticLocks = new ArrayList<>();
            createNewLockForCurrentUser(payment, viewerUserId, actionTypeCode, optimisticLocks);
            optimisticLockRepository.saveAll(optimisticLocks);
        });
    }

    public void unlockCurrentUser(Payment payment, String viewerUserId) {
        log.info("LockUtil:: unlockCurrentUser : " + viewerUserId);
        Optional.ofNullable(payment).ifPresent(pmnt -> {
            List<OptimisticLockPayments> optimisticLocks = new ArrayList<>();
            endExistingLocksForCurrentUser(payment, viewerUserId, optimisticLocks);
            optimisticLockRepository.saveAll(optimisticLocks);
        });
    }

    public  TxnStatusDTO checkForNotStale(Payment payment) {
        TxnStatusDTO txnStatusDTO = new TxnStatusDTO();
        String currentUserId = UserUtil.getLoggedInUser();
        Optional.ofNullable(payment).ifPresent(pmnt -> {
            log.info("STALE CHECK : LOADNUMBER : " + pmnt.getLoadNumber());
            List<OptimisticLockPayments> currentUserList = optimisticLockRepository
                    .findByCarrierPaymentIdAndLockUserIdAndExpirationTimestampIsNull(pmnt.getCarrierPaymentId(),
                            currentUserId);
            OptimisticLockPayments lastUpdateUser = optimisticLockRepository.findByLatestUpdatedUser(pmnt.getCarrierPaymentId(), CarrierPaymentConstants.LOCK_UPDATE,currentUserId);

            if (!CollectionUtils.isEmpty(currentUserList) && null != lastUpdateUser) {
                OptimisticLockPayments currentUser = currentUserList.stream()
                        .max((lock1, lock2) -> lock1.getEffectiveTimestamp().compareTo(lock2.getEffectiveTimestamp()))
                        .get();
                validateStale(txnStatusDTO, pmnt, lastUpdateUser, currentUser);
            } else {
                txnStatusDTO.setSuccess(true);
                log.info("No Others Users has Done Update Action ");
            }
        });
        log.info("NOT STALE ? :: " + txnStatusDTO.isSuccess());
        return txnStatusDTO;
    }
    private  void createNewLockForCurrentUser(Payment payment,
                                                    String viewerUserId,
                                                    String actionTypeCode,
                                                    List<OptimisticLockPayments> optimisticLocks) {

        log.info("LockUtil:: LOCK - CurrentUser : " + viewerUserId);
        log.info("LockUtil:: LOCK - ACTION : " + actionTypeCode);

        OptimisticLockPayments newLock = new OptimisticLockPayments();

        newLock.setCarrierPaymentId(payment.getCarrierPaymentId());
        newLock.setLockUserId(viewerUserId);
        newLock.setLockActionTypeCode(actionTypeCode);
        newLock.setEffectiveTimestamp(LocalDateTime.now());

        optimisticLocks.add(newLock);
    }

    private  void endExistingLocksForCurrentUser(Payment payment,
                                                       String viewerUserId,
                                                       List<OptimisticLockPayments> optimisticLocks) {

        List<OptimisticLockPayments> existingLocks = optimisticLockRepository
                .findByCarrierPaymentIdAndLockUserIdAndExpirationTimestampIsNull(payment.getCarrierPaymentId(), viewerUserId);

        log.info("LockUtil:: UNLOCK - CurrentUser : " + viewerUserId);
        log.info("LockUtil:: UNLOCK existingLocks size :: " + existingLocks.size());

        Optional.ofNullable(existingLocks).filter(list -> !list.isEmpty())
                .ifPresent(endExistingLocks -> endExistingLocks.forEach(existingLock -> {
                    existingLock.setExpirationTimestamp(LocalDateTime.now());
                    optimisticLocks.add(existingLock);
                }));
    }




    private static void validateStale(TxnStatusDTO txnStatusDTO,
                                      Payment pmnt,
                                      OptimisticLockPayments lastUpdateUser,
                                      OptimisticLockPayments currentUser) {

        log.info("Current User Timestamp for stale check " + currentUser.getEffectiveTimestamp());
        log.info("Last Updated User Timestamp for stale check " + lastUpdateUser.getEffectiveTimestamp());

        if (lastUpdateUser.getEffectiveTimestamp().isAfter(currentUser.getEffectiveTimestamp())) {
            log.info("Other User Timestamp for stale check " + lastUpdateUser.getEffectiveTimestamp());
            txnStatusDTO.setSuccess(false);
            String warningStatus = Optional.ofNullable(pmnt.getStatusFlowTypeCode())
                    .filter(status -> Objects.equals(status, CarrierPaymentConstants.AUTO_APPROVED))
                    .map(x -> CarrierPaymentConstants.WARNING_AUTO_APPROVED)
                    .orElse(CarrierPaymentConstants.WARNING_STALE_RECORD);
            txnStatusDTO.setWarning(warningStatus);
        } else {
            txnStatusDTO.setSuccess(true);
            log.info("Current User is the Latest One ");
        }
    }

}

package com.jbhunt.finance.carrierpayment.autopay.repository;


import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierPaymentProcessEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface CarrierPaymentProcessEventRepository extends JpaRepository<CarrierPaymentProcessEvent, Integer> {

    @Modifying
    @Transactional
    @Query(value = "update CFP.CarrierPaymentProcessEvent set ProcessedTimestamp=SYSDATETIMEOFFSET(), LastUpdateTimestamp=SYSDATETIMEOFFSET(), LastUpdateUserID = 'pidfna1', "
            + "LastUpdateProgramName = 'AutoPayService'  where CarrierPaymentID=:paymentId and CarrierPaymentWorkflowGroupTypeCode=:carrierPaymentWorkflowGroupTypeCode and EventTypeCode ='AutoApprve' and ProcessedTimestamp is null", nativeQuery = true)
    int updateCarrierPaymentProcessEvent(@Param("paymentId") Integer paymentId, @Param("carrierPaymentWorkflowGroupTypeCode") String carrierPaymentWorkflowGroupTypeCode );

    @Query(value = "select Top(1) * from CFP.CarrierPaymentProcessEvent where CarrierPaymentID=:carrierPaymentId and EventTypeCode ='AutoApprve' and ProcessedTimestamp is null order by CreateTimestamp desc", nativeQuery = true)
    CarrierPaymentProcessEvent findPaymentByCarrierPaymentId(@Param("carrierPaymentId") Integer carrierPaymentId);

    @Query(value = "select count(*) from CFP.CarrierPaymentProcessEvent where CarrierPaymentID=:paymentId and CarrierPaymentWorkflowGroupTypeCode=:carrierPaymentWorkflowGroupTypeCode and EventTypeCode ='AutoApprve' and ProcessedTimestamp is null", nativeQuery = true)
    public int findCountOfCarrierPaymentProcessEvent(@Param("paymentId") Integer paymentId, @Param("carrierPaymentWorkflowGroupTypeCode") String carrierPaymentWorkflowGroupTypeCode);

    CarrierPaymentProcessEvent findByCarrierPaymentIDAndCarrierPaymentWorkflowGroupTypeCodeAndProcessedTimestampIsNull(@Param("paymentId") Integer paymentId,
                                                                                                                       @Param("carrierPaymentWorkflowGroupTypeCode") String carrierPaymentWorkflowGroupTypeCode);


}

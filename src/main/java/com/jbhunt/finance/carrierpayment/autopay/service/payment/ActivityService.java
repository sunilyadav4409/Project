package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.dto.ActivityDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.mapper.ActivityMapper;
import com.jbhunt.finance.carrierpayment.autopay.entity.*;
import com.jbhunt.finance.carrierpayment.autopay.repository.*;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ActivityUtil;
import com.jbhunt.infrastructure.exception.JBHValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import static com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentErrorConstants.SAVING_TO_DB_FAILED;

@Service
@Slf4j
public class ActivityService {

    @Autowired
    private  ActivityRepository activityRepository;
    @Autowired
    private  ActivityMapper activityMapper;

    @Transactional
    public TxnStatusDTO saveActivityDetails(ActivityDTO activityDTO) {
        log.info("Inside saveActivityDetails: SCAC: " );
        TxnStatusDTO status = new TxnStatusDTO();

        Optional.ofNullable(activityDTO).ifPresent(activityDetail -> {
            Activity activity = activityMapper.activityDTOToActivity(activityDTO);
            try {

                Activity savedActivity = activityRepository.save(activity);
                status.setSuccess(true);
                activityDTO.setActivityTrackId(savedActivity.getActivityTrackId());
            } catch (Exception exception) {
                log.error("Exception while saving Activity ", exception);
                throw new JBHValidationException(SAVING_TO_DB_FAILED);
            }
        });

        log.info("Call to saveActivityDetails ends");
        return status;
    }
    @Transactional
    public void saveActivityAutoPayFailure(Payment payment, String actAutoPay, String actAutoPayPerform, String actApp,
                                           String system, String actDetailTypeCode){
        log.info("Inside saveActivityAutoPayFailure method start: Type:" + actDetailTypeCode);
        ActivityDTO activity = ActivityUtil.getActivityDTO( actAutoPay, actAutoPayPerform, actApp, system );
        activity.setActivityDetailTypeCode(actDetailTypeCode);
        Optional.ofNullable( payment ).ifPresent( act -> {
            activity.setCarrierPaymentId( payment.getCarrierPaymentId() );
            saveActivityDetails( activity );
        } );
        log.info("Inside saveActivityAutoPayFailure method end");
    }
}

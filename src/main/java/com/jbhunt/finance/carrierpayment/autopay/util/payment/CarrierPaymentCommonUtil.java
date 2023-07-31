package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import com.jbhunt.finance.carrierpayment.autopay.dto.PaymentSearchDTO;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
public class CarrierPaymentCommonUtil {

    private CarrierPaymentCommonUtil() {

    }

    public static String convertLocalDateTimeToString(LocalDateTime date, DateTimeFormatter formatter) {
        return Optional.ofNullable(date).map(x -> x.format(formatter)).orElse(null);
    }

    public static String convertLocalDateToString(LocalDate localDate, DateTimeFormatter formatter) {
        return Optional.ofNullable(localDate).map(x -> x.format(formatter)).orElse(null);
    }

    public static void updateModifiedDetails(PaymentSearchDTO elasticDTO, String modifiedUser) {
        elasticDTO.setLastModifiedBy(modifiedUser);
        elasticDTO.setModifiedTime(LocalTime.now().toString());
        elasticDTO.setModifiedDate(LocalDate.now().toString());
    }

}

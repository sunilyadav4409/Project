package com.jbhunt.finance.carrierpayment.autopay.entity.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Converter(autoApply = true)
public class DateTimeAttributeConverter implements AttributeConverter<LocalDateTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS xxxxx");
        return attribute != null ? formatter.format(attribute.atZone(ZoneId.systemDefault())): null;
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dateTimeString) {
        if (Optional.ofNullable(dateTimeString).isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS XXX");
            return LocalDateTime.parse(dateTimeString, formatter);
        }
        return null;
    }

}

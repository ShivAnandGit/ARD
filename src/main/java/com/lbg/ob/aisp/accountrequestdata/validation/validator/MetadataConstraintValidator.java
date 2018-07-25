package com.lbg.ob.aisp.accountrequestdata.validation.validator;

import com.lbg.ob.aisp.accountrequestdata.exception.ExceptionConstants;
import com.lbg.ob.aisp.accountrequestdata.exception.InvalidRequestException;
import com.lbg.ob.aisp.accountrequestdata.validation.MetadataConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * Created by pbabb1 on 12/18/17.
 */
public class MetadataConstraintValidator implements ConstraintValidator<MetadataConstraint, Map<String, Object>> {

    public static final String EXPIRATION_DATE_TIME = "ExpirationDateTime";
    public static final String TRANSACTION_FROM_DATE_TIME = "TransactionFromDateTime";
    public static final String TRANSACTION_TO_DATE_TIME = "TransactionToDateTime";

    @Override
    public void initialize(MetadataConstraint metadataConstraint) {
        // do nothing
    }

    @Override
    public boolean isValid(Map<String, Object> metadataMap, ConstraintValidatorContext constraintValidatorContext) {
        if (metadataMap.containsKey(EXPIRATION_DATE_TIME)) {
            ZonedDateTime expirationDateTime = parseAndGetDateTimeAtZone(EXPIRATION_DATE_TIME, metadataMap);
            if (!expirationDateTime.isAfter(getCurrentDateTimeAtZone(ZonedDateTime.now()))) {
                throw new InvalidRequestException("ExpirationDateTime is a date in past", ExceptionConstants.ARD_API_ERR_010);
            }
        }
        if (metadataMap.containsKey(TRANSACTION_FROM_DATE_TIME)) {
            parseAndGetDate(TRANSACTION_FROM_DATE_TIME, metadataMap);
        }
        if (metadataMap.containsKey(TRANSACTION_TO_DATE_TIME)) {
            parseAndGetDate(TRANSACTION_TO_DATE_TIME, metadataMap);
        }
        return true;
    }

    private LocalDateTime parseAndGetDate(String key, Map<String, Object> metadataMap) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse((String) metadataMap.get(key), DateTimeFormatter.ISO_DATE_TIME);
            return localDateTime;
        } catch (DateTimeParseException e) {
            throw new InvalidRequestException(key + " isn't in a valid date format", ExceptionConstants.ARD_API_ERR_009, e);
        }
    }

    private ZonedDateTime parseAndGetDateTimeAtZone(String key, Map<String, Object> metadataMap) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse((String) metadataMap.get(key), DateTimeFormatter.ISO_DATE_TIME).toInstant().atZone(ZoneId.systemDefault());
            return zonedDateTime;
        } catch (DateTimeParseException e) {
            throw new InvalidRequestException(key + " isn't in a valid date format", ExceptionConstants.ARD_API_ERR_009, e);
        }
    }

    private ZonedDateTime getCurrentDateTimeAtZone(ZonedDateTime dateTime) {
        try {
            ZonedDateTime currentDateTime = ZonedDateTime.parse(dateTime.toString(), DateTimeFormatter.ISO_DATE_TIME);
            return currentDateTime;
        } catch (DateTimeParseException e) {
            throw new InvalidRequestException("LocalDateTime isn't in a valid date format", ExceptionConstants.ARD_API_ERR_009, e);
        }
    }
}

package com.lbg.ob.aisp.accountrequestdata.test.validation;

import com.lbg.ob.aisp.accountrequestdata.data.CreateAccountInputData;
import com.lbg.ob.aisp.accountrequestdata.data.CreateAccountInputRequest;
import com.lbg.ob.aisp.accountrequestdata.exception.InvalidRequestException;
import com.lbg.ob.aisp.accountrequestdata.validation.validator.PermissionsConstraintValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
        "entitlementRevokeUrl=testValue",
})
public class MetadataConstraintValidatorTest {
    CreateAccountInputData data;
    CreateAccountInputRequest createAccountInput;


    @Autowired
    private Validator validator;

    @Before
    public void setup() {
        data = new CreateAccountInputData();
        createAccountInput = new CreateAccountInputRequest();
        createAccountInput.setCreateAccountInputData(data);
    }

    @Test
    public void shouldValidateValidDateFormats() {
        List<String> permissions = Arrays.asList(PermissionsConstraintValidator.READ_TRANSACTIONS_BASIC, PermissionsConstraintValidator.READ_TRANSACTIONS_DEBITS);
        data.setPermissions(permissions);
        data.set("ExpirationDateTime", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME));
        data.set("TransactionFromDateTime", "2017-06-01T09:24:30.975Z");
        data.set("TransactionToDateTime", "2017-06-01T09:24:30.975Z");
        Set<ConstraintViolation<CreateAccountInputRequest>> validate = validator.validate(createAccountInput);
        assertEquals(0, validate.size());
    }

    @Test (expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenExpiryDateIsADateInPast() throws Throwable {
        List<String> permissions = Arrays.asList(PermissionsConstraintValidator.READ_TRANSACTIONS_BASIC, PermissionsConstraintValidator.READ_TRANSACTIONS_DEBITS);
        data.setPermissions(permissions);
        data.set("ExpirationDateTime", "2017-06-01T09:24:30.975Z");
        data.set("TransactionFromDateTime", "2017-06-01T09:24:30.975Z");
        data.set("TransactionToDateTime", "2017-06-01T09:24:30.975Z");
        try {
            Set<ConstraintViolation<CreateAccountInputRequest>> validate = validator.validate(createAccountInput);
        } catch (ValidationException ex) {
            //InvalidRequestException triggers ValidationException, so catching ValidationException and throwing the cause
            throw ex.getCause();
        }
    }

    @Test (expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenExpiryDateTimeIsInvalid() throws Throwable {
        List<String> permissions = Arrays.asList(PermissionsConstraintValidator.READ_TRANSACTIONS_BASIC, PermissionsConstraintValidator.READ_TRANSACTIONS_DEBITS);
        data.setPermissions(permissions);
        data.set("ExpirationDateTime", "2017-06-1aaT09:24:30.975Z");
        data.set("TransactionFromDateTime", "2017-06-01T09:24:30.975Z");
        data.set("TransactionToDateTime", "2017-06-01T09:24:30.975Z");
        try {
            Set<ConstraintViolation<CreateAccountInputRequest>> validate = validator.validate(createAccountInput);
        } catch (ValidationException ex) {
            //InvalidRequestException triggers ValidationException, so catching ValidationException and throwing the cause
            throw ex.getCause();
        }
    }

    @Test (expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenTransactionFromDateTimeIsInvalid() throws Throwable {
        List<String> permissions = Arrays.asList(PermissionsConstraintValidator.READ_TRANSACTIONS_BASIC, PermissionsConstraintValidator.READ_TRANSACTIONS_DEBITS);
        data.setPermissions(permissions);
        data.set("ExpirationDateTime", "2017-06-01T09:24:30.975Z");
        data.set("TransactionFromDateTime", "2017-06-aaT09:24:30.975Z");
        data.set("TransactionToDateTime", "2017-06-01T09:24:30.975Z");
        try {
            Set<ConstraintViolation<CreateAccountInputRequest>> validate = validator.validate(createAccountInput);
        } catch (ValidationException ex) {
            //InvalidRequestException triggers ValidationException, so catching ValidationException and throwing the cause
            throw ex.getCause();
        }
    }

    @Test (expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenTransactionToDateTimeIsInvalid() throws Throwable {
        List<String> permissions = Arrays.asList(PermissionsConstraintValidator.READ_TRANSACTIONS_BASIC, PermissionsConstraintValidator.READ_TRANSACTIONS_DEBITS);
        data.setPermissions(permissions);
        data.set("ExpirationDateTime", "2017-06-01T09:24:30.975Z");
        data.set("TransactionFromDateTime", "2017-06-01T09:24:30.975Z");
        data.set("TransactionToDateTime", "aa17-06-01T09:24:30.975Z");
        try {
            Set<ConstraintViolation<CreateAccountInputRequest>> validate = validator.validate(createAccountInput);
        } catch (ValidationException ex) {
            //InvalidRequestException triggers ValidationException, so catching ValidationException and throwing the cause
            throw ex.getCause();
        }
    }

}

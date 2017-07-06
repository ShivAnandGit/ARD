package com.lbg.aaf.entitlement.entitlementaccountrequestdata.test.validation;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.CreateAccountInputData;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.CreateAccountInputRequest;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.InvalidRequestException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.validation.validator.PermissionsConstraintValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.lbg.aaf.entitlement.entitlementaccountrequestdata.validation.validator.PermissionsConstraintValidator.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PermissionsConstraintValidatorTest {
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
    public void shouldValidateAValidPermissionsList() {
        List<String> permissions = Arrays.asList(PermissionsConstraintValidator.READ_BENEFICIARIES_DETAIL);
        data.setPermissions(permissions);
        Set<ConstraintViolation<CreateAccountInputRequest>> validate = validator.validate(createAccountInput);
        assertEquals(0, validate.size());
    }

    @Test
    public void shouldValidateAValidComboPermissionsList() {
        List<String> permissions = Arrays.asList(PermissionsConstraintValidator.READ_TRANSACTIONS_BASIC, READ_TRANSACTIONS_DEBITS);
        data.setPermissions(permissions);
        Set<ConstraintViolation<CreateAccountInputRequest>> validate = validator.validate(createAccountInput);
        assertEquals(0, validate.size());
    }

    @Test (expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenInvalidPermissionNameSpecified() throws Throwable {
        List<String> permissions = Arrays.asList("Abcd");
        data.setPermissions(permissions);
        try {
            Set<ConstraintViolation<CreateAccountInputRequest>> validate = validator.validate(createAccountInput);
        } catch (ValidationException ex) {
            //InvalidRequestException triggers ValidationException, so catching ValidationException and throwing the cause
            throw ex.getCause();
        }
    }

    @Test (expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenInvalidPermissionComboSpecified() throws Throwable {
        List<String> permissions = Arrays.asList(READ_ACCOUNTS_BASIC, READ_ACCOUNTS_DETAIL);
        data.setPermissions(permissions);
        try {
            Set<ConstraintViolation<CreateAccountInputRequest>> validate = validator.validate(createAccountInput);
        } catch (ValidationException ex) {
            //InvalidRequestException triggers ValidationException, so catching ValidationException and throwing the cause
            throw ex.getCause();
        }
    }

    @Test (expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenMissingPermissionFromRequiredCombinations() throws Throwable {
        List<String> permissions = Arrays.asList(READ_TRANSACTIONS_BASIC);
        data.setPermissions(permissions);
        try {
            Set<ConstraintViolation<CreateAccountInputRequest>> validate = validator.validate(createAccountInput);
        } catch (ValidationException ex) {
            //InvalidRequestException triggers ValidationException, so catching ValidationException and throwing the cause
            throw ex.getCause();
        }
    }

    @Test (expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenMissingPermissionsFromList() throws Throwable {
        List<String> permissions = new ArrayList<>();
        data.setPermissions(permissions);
        try {
            Set<ConstraintViolation<CreateAccountInputRequest>> validate = validator.validate(createAccountInput);
        } catch (ValidationException ex) {
            //InvalidRequestException triggers ValidationException, so catching ValidationException and throwing the cause
            throw ex.getCause();
        }
    }
}

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
        "entitlementRevokeUrl=testValue",
})
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
        List<String> permissions = Arrays.asList(PermissionsConstraintValidator.READ_TRANSACTIONS_BASIC, PermissionsConstraintValidator.READ_TRANSACTIONS_DEBITS);
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

    @Test
    public void shouldPassWhenValidPermissionComboSpecified() throws Throwable {
        List<String> permissions = Arrays.asList(PermissionsConstraintValidator.READ_ACCOUNTS_BASIC, PermissionsConstraintValidator.READ_ACCOUNTS_DETAIL);
        data.setPermissions(permissions);
        try {
            Set<ConstraintViolation<CreateAccountInputRequest>> validate = validator.validate(createAccountInput);
            assertEquals(0, validate.size());
        } catch (ValidationException ex) {
            //InvalidRequestException triggers ValidationException, so catching ValidationException and throwing the cause
            throw ex.getCause();
        }
    }

    @Test (expected = InvalidRequestException.class)
    public void shouldThrowExceptionWhenMissingPermissionFromRequiredCombinations() throws Throwable {
        List<String> permissions = Arrays.asList(PermissionsConstraintValidator.READ_TRANSACTIONS_BASIC);
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

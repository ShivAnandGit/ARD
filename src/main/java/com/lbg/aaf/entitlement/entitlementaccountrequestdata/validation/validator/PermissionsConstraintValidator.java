package com.lbg.aaf.entitlement.entitlementaccountrequestdata.validation.validator;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.ExceptionConstants;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.exception.InvalidRequestException;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.AccountRequestDataConstant;
import com.lbg.aaf.entitlement.entitlementaccountrequestdata.validation.PermissionsConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.*;


public final class PermissionsConstraintValidator implements ConstraintValidator<PermissionsConstraint, List<String>> {

    public static final String READ_ACCOUNTS_BASIC = "ReadAccountsBasic";
    public static final String READ_ACCOUNTS_DETAIL = "ReadAccountsDetail";
    public static final String READ_BALANCES = "ReadBalances";
    public static final String READ_PRODUCTS = "ReadProducts";
    public static final String READ_TRANSACTIONS_BASIC = "ReadTransactionsBasic";
    public static final String READ_TRANSACTIONS_DETAIL = "ReadTransactionsDetail";
    public static final String READ_TRANSACTIONS_CREDITS = "ReadTransactionsCredits";
    public static final String READ_TRANSACTIONS_DEBITS = "ReadTransactionsDebits";
    public static final String READ_BENEFICIARIES_BASIC = "ReadBeneficiariesBasic";
    public static final String READ_BENEFICIARIES_DETAIL = "ReadBeneficiariesDetail";
    public static final String READ_DIRECT_DEBITS = "ReadDirectDebits";
    public static final String READ_STANDING_ORDERS_BASIC = "ReadStandingOrdersBasic";
    public static final String READ_STANDING_ORDERS_DETAIL = "ReadStandingOrdersDetail";
    public static final String PERMISSIONS_ERROR_MESSAGE = "Bad request::One or more permissions is invalid or combinations of permissions is not correct";

    private Set<String> allowedValues;

    private List<List<String>> combinationsNotAllowed;

    private Map<String, List<String>> combinationsRequired;

    @Override
    public void initialize(PermissionsConstraint constraintAnnotation) {
        allowedValues = new HashSet<>();
        allowedValues.add(READ_ACCOUNTS_BASIC);
        allowedValues.add(READ_ACCOUNTS_DETAIL);
        allowedValues.add(READ_BALANCES);
        allowedValues.add(READ_PRODUCTS);
        allowedValues.add(READ_TRANSACTIONS_BASIC);
        allowedValues.add(READ_TRANSACTIONS_DETAIL);
        allowedValues.add(READ_TRANSACTIONS_CREDITS);
        allowedValues.add(READ_TRANSACTIONS_DEBITS);
        allowedValues.add(READ_BENEFICIARIES_BASIC);
        allowedValues.add(READ_BENEFICIARIES_DETAIL);
        allowedValues.add(READ_DIRECT_DEBITS);
        allowedValues.add(READ_STANDING_ORDERS_BASIC);
        allowedValues.add(READ_STANDING_ORDERS_DETAIL);

        combinationsNotAllowed = new ArrayList<>();
        combinationsNotAllowed.add(Arrays.asList(READ_ACCOUNTS_BASIC, READ_ACCOUNTS_DETAIL));
        combinationsNotAllowed.add(Arrays.asList(READ_BENEFICIARIES_BASIC, READ_BENEFICIARIES_DETAIL));
        combinationsNotAllowed.add(Arrays.asList(READ_STANDING_ORDERS_BASIC, READ_STANDING_ORDERS_DETAIL));
        combinationsNotAllowed.add(Arrays.asList(READ_TRANSACTIONS_BASIC, READ_TRANSACTIONS_DETAIL));

        combinationsRequired = new HashMap<>();
        combinationsRequired.put(READ_TRANSACTIONS_BASIC, Arrays.asList(READ_TRANSACTIONS_CREDITS, READ_TRANSACTIONS_DEBITS));
        combinationsRequired.put(READ_TRANSACTIONS_DETAIL, Arrays.asList(READ_TRANSACTIONS_CREDITS, READ_TRANSACTIONS_DEBITS));
        combinationsRequired.put(READ_TRANSACTIONS_CREDITS, Arrays.asList(READ_TRANSACTIONS_BASIC, READ_TRANSACTIONS_DETAIL));
        combinationsRequired.put(READ_TRANSACTIONS_DEBITS, Arrays.asList(READ_TRANSACTIONS_BASIC, READ_TRANSACTIONS_DETAIL));

    }

    @Override
    public boolean isValid(List<String> values, ConstraintValidatorContext context) {
        if(values == null || values.isEmpty()) {
            throw new InvalidRequestException(PERMISSIONS_ERROR_MESSAGE, ExceptionConstants.ARD_API_ERR_007);
        }
        //check for allowed values
        validateAllowedValues(values);
        //If any of the not allowed combinations are there in the list of permissions, then throw exception
        validateCombosNotAllowed(values);

        //Check for combinations that are required
        validateCombosRequired(values);
        return true;
    }

    private void validateCombosRequired(List<String> values) {
        for(Map.Entry<String, List<String>> comboEntry: combinationsRequired.entrySet()) {
            String key = comboEntry.getKey();
            if(values.contains(key)) {
                List<String> combinations = comboEntry.getValue();
                //check if atleast 1 combo permissions exist in values
                Optional<String> first = combinations.stream().filter(combo -> values.contains(combo)).findFirst();
                if(!first.isPresent()) {
                    throw new InvalidRequestException(PERMISSIONS_ERROR_MESSAGE, ExceptionConstants.ARD_API_ERR_007);
                } else {
                    break;
                }
            }
        }
    }

    private void validateCombosNotAllowed(List<String> values) {
        for(List<String> combo: combinationsNotAllowed) {
            //checking if the not allowed combos have atleast 1 value that aren't there in the list of values.
            Optional<String> first = combo.stream().filter(permission -> !values.contains(permission)).findFirst();
            if(!first.isPresent()) {
                throw new InvalidRequestException(PERMISSIONS_ERROR_MESSAGE, ExceptionConstants.ARD_API_ERR_007);
            }
        }
    }

    private void validateAllowedValues(List<String> values) {
        //checking if the list of allowedValues contains the permission in the permissions list
        Optional<String> first = values.stream().filter(val -> !allowedValues.contains(val)).findFirst();
        if(first.isPresent()) {
            throw new InvalidRequestException(PERMISSIONS_ERROR_MESSAGE, ExceptionConstants.ARD_API_ERR_007);
        }
    }

}

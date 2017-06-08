package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

public enum PermissionsEnum {

    READACCOUNTS("ReadAccounts"), READACCOUNTSSENSITIVE("ReadAccountsSensitive"), READBALANCES("ReadBalances"), READBENEFICIARIES("ReadBeneficiaries"), READBENEFICIARIESSENSITIVE("ReadBeneficiariesSensitive"), READDIRECTDEBITS("ReadDirectDebits"), READSTANDINGORDERS("ReadStandingOrders"), READSTANDINGORDERSSENSITIVE("ReadStandingOrdersSensitive"), READTRANSACTIONS("ReadTransactions"), READTRANSACTIONSCREDITS("ReadTransactionsCredits"), READTRANSACTIONSDEBITS("ReadTransactionsDebits"), READTRANSACTIONSSENSITIVE("ReadTransactionsSensitive");

    private String value;
    public String getValue() {
        return value;
    }
    
    PermissionsEnum(String value){
        this.value =value;
    }
}

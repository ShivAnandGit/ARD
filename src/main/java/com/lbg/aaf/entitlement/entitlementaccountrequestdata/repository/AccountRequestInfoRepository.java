package com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequest;

public interface AccountRequestInfoRepository extends JpaRepository<AccountRequest, Long> {

    AccountRequest findByAccountRequestExternalIdentifierAndProviderClientId(String accountRequestExternalIdentifier, String providerClientId ); 
    
    AccountRequest findByAccountRequestExternalIdentifier(String accountRequestExternalIdentifier ); 
    
}

package com.lbg.ob.aisp.accountrequestdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lbg.ob.aisp.accountrequestdata.data.AccountRequest;

public interface AccountRequestInfoRepository extends JpaRepository<AccountRequest, Long> {

    AccountRequest findByAccountRequestExternalIdentifierAndProviderClientIdAndAccountRequestStatus(String accountRequestExternalIdentifier, String providerClientId, String accountRequestStatus );
    
    AccountRequest findByAccountRequestExternalIdentifier(String accountRequestExternalIdentifier ); 
    
}

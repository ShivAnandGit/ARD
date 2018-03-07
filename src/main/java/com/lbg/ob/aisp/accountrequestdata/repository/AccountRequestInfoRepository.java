package com.lbg.ob.aisp.accountrequestdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lbg.ob.aisp.accountrequestdata.data.AccountRequest;

public interface AccountRequestInfoRepository extends JpaRepository<AccountRequest, Long> {

	@Query("SELECT a FROM AccountRequest a WHERE a.accountRequestExternalIdentifier = ?1 AND a.providerClientId = ?2 AND a.accountRequestStatus IN ('AwaitingAuthorisation','Authorised')")
	AccountRequest findByAccountRequestExternalIdentifierAndProviderClientIdAndAccountRequestStatus(String accountRequestExternalIdentifier, String providerClientId);
    
    AccountRequest findByAccountRequestExternalIdentifier(String accountRequestExternalIdentifier ); 
    
}

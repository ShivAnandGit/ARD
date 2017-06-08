package com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestAssociatedAccountResource;

public interface AccountRequestAssociatedAccountRepository extends JpaRepository<AccountRequestAssociatedAccountResource, Long> {

    List<AccountRequestAssociatedAccountResource> findByAccountRequestInfoId(Long accountRequestInfoId ); 
}

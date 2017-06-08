package com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.AccountRequestStatusHistory;

public interface AccountRequestStatusChangeHistoryRepository extends JpaRepository<AccountRequestStatusHistory, Long> {

}

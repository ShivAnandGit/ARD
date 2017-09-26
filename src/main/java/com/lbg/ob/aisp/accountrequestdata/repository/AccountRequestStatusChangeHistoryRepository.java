package com.lbg.ob.aisp.accountrequestdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lbg.ob.aisp.accountrequestdata.data.AccountRequestStatusHistory;

public interface AccountRequestStatusChangeHistoryRepository extends JpaRepository<AccountRequestStatusHistory, Long> {

}

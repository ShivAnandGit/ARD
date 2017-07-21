package com.lbg.aaf.entitlement.entitlementaccountrequestdata.data;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.util.Util;

@Entity
@Table(name = "ACCT_REQUEST_STATUS_HIST")
public final class AccountRequestStatusHistory {

    @Id
    @SequenceGenerator(name = "acct_request_status_hist_id", sequenceName = "ACCT_REQUEST_STATUS_HIST_SEQ", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acct_request_status_hist_id")
    @Column(name = "ACCT_REQUEST_STATUS_HIST_ID", updatable = false, nullable = false)
    private Long accountRequestStatusHistoryId;

    @Column(name = "ACCT_REQUEST_ID", updatable = false, nullable = false)
    private Long accountRequestInfoId;

    @Column(name = "STATUS_UPDATED_DATE_TIME", updatable = false, nullable = false)
    private Timestamp statusUpdatedDateTime;

    @Column(name = "ACCOUNT_REQUEST_STATUS", updatable = false, nullable = false)
    private String accountRequestStatus;

    @Column(name = "STATUS_UPDATED_BY_ROLE", updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private InternalUserRoleEnum statusUpdatedByRole;

    public AccountRequestStatusHistory() {
        this.setStatusUpdatedDateTime();
    }

    public void setAccountRequestStatusChangeHistoryId(Long accountRequestStatusChangeHistoryId) {
        this.accountRequestStatusHistoryId = accountRequestStatusChangeHistoryId;
    }

    public void setAccountRequestInfoId(Long accountRequestInfoId) {
        this.accountRequestInfoId = accountRequestInfoId;
    }

    public Timestamp getStatusUpdatedDateTime() {
        return new Timestamp(this.statusUpdatedDateTime.getTime());
    }

    public void setStatusUpdatedDateTime() {
        this.statusUpdatedDateTime = Util.getCurrentTimestamp();
    }

    public void setResourceStatus( String status) {
        this.accountRequestStatus = status;
    }

    public void setStatusUpdatedByRole(InternalUserRoleEnum statusUpdatedByRole) {
        this.statusUpdatedByRole = statusUpdatedByRole;
    }

    public Long getAccountRequestStatusHistoryId() {
        return accountRequestStatusHistoryId;
    }

    public Long getAccountRequestInfoId() {
        return accountRequestInfoId;
    }

    public String getAccountRequestStatus() {
        return accountRequestStatus;
    }

    public InternalUserRoleEnum getStatusUpdatedByRole() {
        return statusUpdatedByRole;
    }
}

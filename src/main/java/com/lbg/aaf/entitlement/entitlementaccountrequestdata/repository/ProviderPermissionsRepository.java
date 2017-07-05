package com.lbg.aaf.entitlement.entitlementaccountrequestdata.repository;

import com.lbg.aaf.entitlement.entitlementaccountrequestdata.data.ProviderPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProviderPermissionsRepository extends JpaRepository<ProviderPermission, Long>{
    List<ProviderPermission> findByCode(String code);
}

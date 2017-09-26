package com.lbg.ob.aisp.accountrequestdata.repository;

import com.lbg.ob.aisp.accountrequestdata.data.ProviderPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProviderPermissionsRepository extends JpaRepository<ProviderPermission, Long>{
    List<ProviderPermission> findByCode(String code);
}

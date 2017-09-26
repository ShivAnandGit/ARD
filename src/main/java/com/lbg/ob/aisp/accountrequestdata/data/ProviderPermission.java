package com.lbg.ob.aisp.accountrequestdata.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lbg.ob.aisp.accountrequestdata.util.ProviderPermissionDeserializer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Created by pbabb1 on 6/20/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = ProviderPermissionDeserializer.class)
@Entity
@Table(name = "PROVIDER_PERMISSION")
public class ProviderPermission {
    @Id
    @SequenceGenerator(name = "provider_permission_id", sequenceName = "PROVIDER_PERMISSION_SEQ", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "provider_permission_id")
    @Column(name = "PROVIDER_PERMISSION_ID", updatable = false, nullable = false)
    private Long refPermissionsId;

    @Column(name = "CODE")
    private String code;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SECTION_HEADER")
    private String sectionHeader;

    @JsonProperty("Code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("SectionHeader")
    public String getSectionHeader() {
        return sectionHeader;
    }

    public void setSectionHeader(String sectionHeader) {
        this.sectionHeader = sectionHeader;
    }




    public void setRefPermissionsId(Long refPermissionsId) {
        this.refPermissionsId = refPermissionsId;
    }
}

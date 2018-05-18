package com.amdocs.homing.datamodel;

import java.util.List;

public class LicenseSolutions {

    private List<String> licenseKeyGroupUUID;

    private String resourceModuleName;

    private List<String> entitlementPoolInvariantUUID;

    private String serviceResourceId;

    private List<String> entitlementPoolUUID;

    private List<String> licenseKeyGroupInvariantUUID;

    public List<String> getLicenseKeyGroupUUID() {
        return licenseKeyGroupUUID;
    }

    public void setLicenseKeyGroupUUID(List<String> licenseKeyGroupUUID) {
        this.licenseKeyGroupUUID = licenseKeyGroupUUID;
    }

    public String getResourceModuleName() {
        return resourceModuleName;
    }

    public void setResourceModuleName(String resourceModuleName) {
        this.resourceModuleName = resourceModuleName;
    }

    public List<String> getEntitlementPoolInvariantUUID() {
        return entitlementPoolInvariantUUID;
    }

    public void setEntitlementPoolInvariantUUID(List<String> entitlementPoolInvariantUUID) {
        this.entitlementPoolInvariantUUID = entitlementPoolInvariantUUID;
    }

    public String getServiceResourceId() {
        return serviceResourceId;
    }

    public void setServiceResourceId(String serviceResourceId) {
        this.serviceResourceId = serviceResourceId;
    }

    public List<String> getEntitlementPoolUUID() {
        return entitlementPoolUUID;
    }

    public void setEntitlementPoolUUID(List<String> entitlementPoolUUID) {
        this.entitlementPoolUUID = entitlementPoolUUID;
    }

    public List<String> getLicenseKeyGroupInvariantUUID() {
        return licenseKeyGroupInvariantUUID;
    }

    public void setLicenseKeyGroupInvariantUUID(List<String> licenseKeyGroupInvariantUUID) {
        this.licenseKeyGroupInvariantUUID = licenseKeyGroupInvariantUUID;
    }

    /*@Override
    public String toString() {
        return LicenseSolutions.class.getSimpleName() + "[licenseKeyGroupUUID = " + licenseKeyGroupUUID
                + ", resourceModuleName = " + resourceModuleName + ", entitlementPoolInvariantUUID = "
                + entitlementPoolInvariantUUID + ", serviceResourceId = " + serviceResourceId
                + ", entitlementPoolUUID = " + entitlementPoolUUID + ", licenseKeyGroupInvariantUUID = "
                + licenseKeyGroupInvariantUUID + "]";
    }*/
}

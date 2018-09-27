package org.openecomp.mso.client.has.entities;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "entitlementPoolUUID",
        "licenseKeyGroupUUID"
})
public class ExistingLicenses {

    @JsonProperty("entitlementPoolUUID")
    private List<String> entitlementPoolUUID = null;
    @JsonProperty("licenseKeyGroupUUID")
    private List<String> licenseKeyGroupUUID = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("entitlementPoolUUID")
    public List<String> getEntitlementPoolUUID() {
        return entitlementPoolUUID;
    }

    @JsonProperty("entitlementPoolUUID")
    public void setEntitlementPoolUUID(List<String> entitlementPoolUUID) {
        this.entitlementPoolUUID = entitlementPoolUUID;
    }

    @JsonProperty("licenseKeyGroupUUID")
    public List<String> getLicenseKeyGroupUUID() {
        return licenseKeyGroupUUID;
    }

    @JsonProperty("licenseKeyGroupUUID")
    public void setLicenseKeyGroupUUID(List<String> licenseKeyGroupUUID) {
        this.licenseKeyGroupUUID = licenseKeyGroupUUID;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

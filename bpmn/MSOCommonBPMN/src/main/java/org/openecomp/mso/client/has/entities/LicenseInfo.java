package org.openecomp.mso.client.has.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "licenseDemands"
})
public class LicenseInfo {

    @JsonProperty("licenseDemands")
    private List<LicenseDemand> licenseDemands = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("licenseDemands")
    public List<LicenseDemand> getLicenseDemands() {
        return licenseDemands;
    }

    @JsonProperty("licenseDemands")
    public void setLicenseDemands(List<LicenseDemand> licenseDemands) {
        this.licenseDemands = licenseDemands;
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
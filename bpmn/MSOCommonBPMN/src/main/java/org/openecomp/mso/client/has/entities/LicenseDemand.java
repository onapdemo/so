package org.openecomp.mso.client.has.entities;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.openecomp.mso.bpmn.core.domain.ModelInfo;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "resourceModuleName",
        "serviceResourceId",
        "resourceModelInfo",
        "existingLicenses"
})
public class LicenseDemand {

    @JsonProperty("resourceModuleName")
    private String resourceModuleName;
    @JsonProperty("serviceResourceId")
    private String serviceResourceId;
    @JsonProperty("resourceModelInfo")
    private ModelInfo resourceModelInfo;
    @JsonProperty("existingLicenses")
    private ExistingLicenses existingLicenses;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("resourceModuleName")
    public String getResourceModuleName() {
        return resourceModuleName;
    }

    @JsonProperty("resourceModuleName")
    public void setResourceModuleName(String resourceModuleName) {
        this.resourceModuleName = resourceModuleName;
    }

    @JsonProperty("serviceResourceId")
    public String getServiceResourceId() {
        return serviceResourceId;
    }

    @JsonProperty("serviceResourceId")
    public void setServiceResourceId(String serviceResourceId) {
        this.serviceResourceId = serviceResourceId;
    }

    @JsonProperty("resourceModelInfo")
    public ModelInfo getResourceModelInfo() {
        return resourceModelInfo;
    }

    @JsonProperty("resourceModelInfo")
    public void setResourceModelInfo(ModelInfo resourceModelInfo) {
        this.resourceModelInfo = resourceModelInfo;
    }

    @JsonProperty("existingLicenses")
    public ExistingLicenses getExistingLicenses() {
        return existingLicenses;
    }

    @JsonProperty("existingLicenses")
    public void setExistingLicenses(ExistingLicenses existingLicenses) {
        this.existingLicenses = existingLicenses;
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
package org.openecomp.mso.client.has.entities;

import com.fasterxml.jackson.annotation.*;
import org.openecomp.mso.bpmn.core.domain.ModelInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "resourceModuleName",
        "serviceResourceId",
        "tenantId",
        "resourceModelInfo",
        "existingCandidates",
        "excludedCandidates",
        "requiredCandidates"
})
public class PlacementDemand {

    @JsonProperty("resourceModuleName")
    private String resourceModuleName;
    @JsonProperty("serviceResourceId")
    private String serviceResourceId;
    @JsonProperty("tenantId")
    private String tenantId;
    @JsonProperty("resourceModelInfo")
    private ModelInfo resourceModelInfo;
    @JsonProperty("existingCandidates")
    private List<ExistingCandidate> existingCandidates = null;
    @JsonProperty("excludedCandidates")
    private List<ExcludedCandidate> excludedCandidates = null;
    @JsonProperty("requiredCandidates")
    private List<RequiredCandidate> requiredCandidates = null;
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

    @JsonProperty("tenantId")
    public String getTenantId() {
        return tenantId;
    }

    @JsonProperty("tenantId")
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @JsonProperty("resourceModelInfo")
    public ModelInfo getResourceModelInfo() {
        return resourceModelInfo;
    }

    @JsonProperty("resourceModelInfo")
    public void setResourceModelInfo(ModelInfo resourceModelInfo) {
        this.resourceModelInfo = resourceModelInfo;
    }

    @JsonProperty("existingCandidates")
    public List<ExistingCandidate> getExistingCandidates() {
        return existingCandidates;
    }

    @JsonProperty("existingCandidates")
    public void setExistingCandidates(List<ExistingCandidate> existingCandidates) {
        this.existingCandidates = existingCandidates;
    }

    @JsonProperty("excludedCandidates")
    public List<ExcludedCandidate> getExcludedCandidates() {
        return excludedCandidates;
    }

    @JsonProperty("excludedCandidates")
    public void setExcludedCandidates(List<ExcludedCandidate> excludedCandidates) {
        this.excludedCandidates = excludedCandidates;
    }

    @JsonProperty("requiredCandidates")
    public List<RequiredCandidate> getRequiredCandidates() {
        return requiredCandidates;
    }

    @JsonProperty("requiredCandidates")
    public void setRequiredCandidates(List<RequiredCandidate> requiredCandidates) {
        this.requiredCandidates = requiredCandidates;
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
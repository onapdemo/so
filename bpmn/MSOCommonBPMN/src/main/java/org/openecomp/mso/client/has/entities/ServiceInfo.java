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
        "serviceInstanceId",
        "serviceName",
        "modelInfo"
})
public class ServiceInfo {

    @JsonProperty("serviceInstanceId")
    private String serviceInstanceId;
    @JsonProperty("serviceName")
    private String serviceName;
    @JsonProperty("modelInfo")
    private ModelInfo modelInfo;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("serviceInstanceId")
    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    @JsonProperty("serviceInstanceId")
    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    @JsonProperty("serviceName")
    public String getServiceName() {
        return serviceName;
    }

    @JsonProperty("serviceName")
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @JsonProperty("modelInfo")
    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    @JsonProperty("modelInfo")
    public void setModelInfo(ModelInfo modelInfo) {
        this.modelInfo = modelInfo;
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
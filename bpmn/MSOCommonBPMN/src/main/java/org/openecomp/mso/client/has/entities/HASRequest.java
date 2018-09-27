package org.openecomp.mso.client.has.entities;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "requestInfo",
        "placementInfo",
        "serviceInfo",
        "licenseInfo"
})
public class HASRequest {

    @JsonProperty("requestInfo")
    private RequestInfo requestInfo;
    @JsonProperty("placementInfo")
    private PlacementInfo placementInfo;
    @JsonProperty("serviceInfo")
    private ServiceInfo serviceInfo;
    @JsonProperty("licenseInfo")
    private LicenseInfo licenseInfo;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("requestInfo")
    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    @JsonProperty("requestInfo")
    public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    @JsonProperty("placementInfo")
    public PlacementInfo getPlacementInfo() {
        return placementInfo;
    }

    @JsonProperty("placementInfo")
    public void setPlacementInfo(PlacementInfo placementInfo) {
        this.placementInfo = placementInfo;
    }

    @JsonProperty("serviceInfo")
    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    @JsonProperty("serviceInfo")
    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    @JsonProperty("licenseInfo")
    public LicenseInfo getLicenseInfo() {
        return licenseInfo;
    }

    @JsonProperty("licenseInfo")
    public void setLicenseInfo(LicenseInfo licenseInfo) {
        this.licenseInfo = licenseInfo;
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

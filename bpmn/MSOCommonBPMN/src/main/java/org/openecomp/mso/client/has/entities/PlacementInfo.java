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
        "requestParameters",
        "subscriberInfo",
        "placementDemands"
})
public class PlacementInfo {

    @JsonProperty("requestParameters")
    private RequestParameters requestParameters;
    @JsonProperty("subscriberInfo")
    private SubscriberInfo subscriberInfo;
    @JsonProperty("placementDemands")
    private List<PlacementDemand> placementDemands = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("requestParameters")
    public RequestParameters getRequestParameters() {
        return requestParameters;
    }

    @JsonProperty("requestParameters")
    public void setRequestParameters(RequestParameters requestParameters) {
        this.requestParameters = requestParameters;
    }

    @JsonProperty("subscriberInfo")
    public SubscriberInfo getSubscriberInfo() {
        return subscriberInfo;
    }

    @JsonProperty("subscriberInfo")
    public void setSubscriberInfo(SubscriberInfo subscriberInfo) {
        this.subscriberInfo = subscriberInfo;
    }

    @JsonProperty("placementDemands")
    public List<PlacementDemand> getPlacementDemands() {
        return placementDemands;
    }

    @JsonProperty("placementDemands")
    public void setPlacementDemands(List<PlacementDemand> placementDemands) {
        this.placementDemands = placementDemands;
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
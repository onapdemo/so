package org.openecomp.mso.client.has.entities;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "globalSubscriberId",
        "subscriberName"
})
public class SubscriberInfo {

    @JsonProperty("globalSubscriberId")
    private String globalSubscriberId;
    @JsonProperty("subscriberName")
    private String subscriberName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("globalSubscriberId")
    public String getGlobalSubscriberId() {
        return globalSubscriberId;
    }

    @JsonProperty("globalSubscriberId")
    public void setGlobalSubscriberId(String globalSubscriberId) {
        this.globalSubscriberId = globalSubscriberId;
    }

    @JsonProperty("subscriberName")
    public String getSubscriberName() {
        return subscriberName;
    }

    @JsonProperty("subscriberName")
    public void setSubscriberName(String subscriberName) {
        this.subscriberName = subscriberName;
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
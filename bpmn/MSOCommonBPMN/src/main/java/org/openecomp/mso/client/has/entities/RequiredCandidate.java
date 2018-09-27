package org.openecomp.mso.client.has.entities;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "identifierType",
        "cloudOwner",
        "identifiers"
})
public class RequiredCandidate {

    @JsonProperty("identifierType")
    private String identifierType;
    @JsonProperty("cloudOwner")
    private String cloudOwner;
    @JsonProperty("identifiers")
    private List<String> identifiers = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("identifierType")
    public String getIdentifierType() {
        return identifierType;
    }

    @JsonProperty("identifierType")
    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    @JsonProperty("cloudOwner")
    public String getCloudOwner() {
        return cloudOwner;
    }

    @JsonProperty("cloudOwner")
    public void setCloudOwner(String cloudOwner) {
        this.cloudOwner = cloudOwner;
    }

    @JsonProperty("identifiers")
    public List<String> getIdentifiers() {
        return identifiers;
    }

    @JsonProperty("identifiers")
    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
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
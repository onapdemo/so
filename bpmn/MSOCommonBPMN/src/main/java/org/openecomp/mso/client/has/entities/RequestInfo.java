package org.openecomp.mso.client.has.entities;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "transactionId",
        "requestId",
        "callbackUrl",
        "sourceId",
        "requestType",
        "numSolutions",
        "optimizers",
        "timeout"
})
public class RequestInfo {

    @JsonProperty("transactionId")
    private String transactionId;
    @JsonProperty("requestId")
    private String requestId;
    @JsonProperty("callbackUrl")
    private String callbackUrl;
    @JsonProperty("sourceId")
    private String sourceId;
    @JsonProperty("requestType")
    private String requestType;
    @JsonProperty("numSolutions")
    private Integer numSolutions;
    @JsonProperty("optimizers")
    private List<String> optimizers = null;
    @JsonProperty("timeout")
    private Integer timeout;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    @JsonProperty("transactionId")
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @JsonProperty("requestId")
    public String getRequestId() {
        return requestId;
    }

    @JsonProperty("requestId")
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @JsonProperty("callbackUrl")
    public String getCallbackUrl() {
        return callbackUrl;
    }

    @JsonProperty("callbackUrl")
    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    @JsonProperty("sourceId")
    public String getSourceId() {
        return sourceId;
    }

    @JsonProperty("sourceId")
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @JsonProperty("requestType")
    public String getRequestType() {
        return requestType;
    }

    @JsonProperty("requestType")
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    @JsonProperty("numSolutions")
    public Integer getNumSolutions() {
        return numSolutions;
    }

    @JsonProperty("numSolutions")
    public void setNumSolutions(Integer numSolutions) {
        this.numSolutions = numSolutions;
    }

    @JsonProperty("optimizers")
    public List<String> getOptimizers() {
        return optimizers;
    }

    @JsonProperty("optimizers")
    public void setOptimizers(List<String> optimizers) {
        this.optimizers = optimizers;
    }

    @JsonProperty("timeout")
    public Integer getTimeout() {
        return timeout;
    }

    @JsonProperty("timeout")
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
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
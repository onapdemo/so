package org.openecomp.mso.client.has.entities;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "customerLatitude",
        "customerLongitude",
        "customerName"
})
public class RequestParameters {

    @JsonProperty("customerLatitude")
    private Double customerLatitude;
    @JsonProperty("customerLongitude")
    private Double customerLongitude;
    @JsonProperty("customerName")
    private String customerName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("customerLatitude")
    public Double getCustomerLatitude() {
        return customerLatitude;
    }

    @JsonProperty("customerLatitude")
    public void setCustomerLatitude(Double customerLatitude) {
        this.customerLatitude = customerLatitude;
    }

    @JsonProperty("customerLongitude")
    public Double getCustomerLongitude() {
        return customerLongitude;
    }

    @JsonProperty("customerLongitude")
    public void setCustomerLongitude(Double customerLongitude) {
        this.customerLongitude = customerLongitude;
    }

    @JsonProperty("customerName")
    public String getCustomerName() {
        return customerName;
    }

    @JsonProperty("customerName")
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
package com.amdocs.homing;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class HomingRestTest {

    @Test
    public void getPlacementSuggestionsBadrequest() {

        HomingRest homingRest = new HomingRest();
        Response response = homingRest.getPlacementSuggestions("");

        assertNotNull(response);
        assertTrue(400 == response.getStatus());
    }

    @Test
    public void getPlacementSuggestionsInput() {

        HomingRest homingRest = new HomingRest();
        Response response = homingRest.getPlacementSuggestions("{\"value\":\"this_is_junk_value\"}");

        assertNotNull(response);
        assertTrue(400 == response.getStatus());
        assertTrue(response.getEntity().toString().contains("Can not extract callbackUrl"));
    }

    @Test
    public void getPlacementSuggestionsSunnyDay() {

        HomingRest homingRest = new HomingRest();
        // This is not a complete request, a subset, just for testing
        // Complete Request Example here - https://wiki.onap.org/pages/viewpage.action?pageId=25435066
        Response response = homingRest.getPlacementSuggestions(
                "{" +
                "\"requestInfo\": {" +
                "\"transactionId\": \"aaaa5198-b70a-49db-a5eb-fa080cbfc3b7\"," +
                "\"requestId\": \"7d2e2469-8708-47c3-a0d4-73fa28a8a50c\"," +
                "\"callbackUrl\": \"https://wiki.onap.org:5000/callbackUrl/\"," +
                "\"sourceId\": \"SO\"," +
                "\"requestType\": \"create\"," +
                "\"numSolutions\": 1," +
                "\"optimizers\": [\"placement\"]," +
                "\"timeout\": 600" +
                "}" +
                "}");

        assertNotNull(response);
        assertTrue(202 == response.getStatus());
        String responseStr = response.getEntity().toString();
        assertTrue(responseStr!=null);
        assertTrue(responseStr.contains("7d2e2469-8708-47c3-a0d4-73fa28a8a50c"));
    }
}

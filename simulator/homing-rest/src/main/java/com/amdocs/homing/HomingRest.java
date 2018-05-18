package com.amdocs.homing;

import com.amdocs.homing.datamodel.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.apache.http.HttpStatus;
import rest.APIResponse;
import rest.RESTClient;
import rest.RESTConfig;
import rest.RESTException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Path("/v1")
public class HomingRest {

    // TODO Move other locations
    private final String CONFIG_PATH = "/etc/homing/config.d";
    private final String CONFIG_FILE_NAME = "has.properties";

    @HEAD
    @GET
    @Path("/healthcheck")
    @Produces(MediaType.TEXT_HTML)
    public Response healthcheck() {

        String CHECK_HTML = "<!DOCTYPE html><html><head><meta charset=\"ISO-8859-1\"><title>Health Check</title>"
                + "</head><body>Hoaming Rest, Application is ready!</body></html>";
        return Response.ok().entity(CHECK_HTML).build();
    }

    // TODO : MediaType.APPLICATION_JSON, With Proper POJO
    @POST
    @Path("/placement")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlacementSuggestions(final String request) {

        if (request == null || request.trim().isEmpty()) {

            return Response.status(HttpStatus.SC_BAD_REQUEST)
                    .type(MediaType.TEXT_PLAIN)
                    .entity("Bad request - '" + request + "'")
                    .build();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String callbackUrl = null;
        String transactionId = "";
        String requestId = "";

        try {
            // TODO - ObjectModed
            JsonNode jsonNode = objectMapper.readTree(request);
            JsonNode requestInfo = jsonNode.get("requestInfo");
            callbackUrl = requestInfo.get("callbackUrl").toString();
            callbackUrl = callbackUrl.replaceAll("\"", "").trim();

            jsonNode = requestInfo.get("transactionId");
            if (jsonNode != null) {
                transactionId = jsonNode.toString();
                transactionId = transactionId.replaceAll("\"", "").trim();
            }

            jsonNode = requestInfo.get("requestId");
            if (jsonNode != null) {
                requestId = jsonNode.toString();
                requestId = requestId.replaceAll("\"", "").trim();
            }

        } catch (Exception e) {

            //TODO log in proper logger
            e.printStackTrace();

            return Response.status(HttpStatus.SC_BAD_REQUEST)
                    .type(MediaType.TEXT_PLAIN)
                    .entity("Bad request, Can not extract callbackUrl")
                    .build();
        }

        PlacementTask paPlacementTask = new PlacementTask(request, callbackUrl);
        new Thread(paPlacementTask).start();

        String responseBody = "{" +
                "\"transactionId\": \"" + transactionId + "\"," +
                "\"requestId\": \"" + requestId + "\"," +
                "\"requestStatus\": \"accepted\"," +
                "\"statusMessage\": \"Call me back after some time!\"" +
                "}";

        return Response.status(HttpStatus.SC_ACCEPTED)
                .type(MediaType.TEXT_PLAIN)
                .entity(responseBody)
                .build();
    }

    private class PlacementTask implements Runnable {

        final String request;
        final String callbackUrl;

        private PlacementTask(String request, String callbackUrl) {
            this.request = request;
            this.callbackUrl = callbackUrl;
        }

        public void run() {

            sendSugessions(callbackUrl);
        }
    }

    private void sendSugessions(final String callbackUrl) {

        try {

            String solutions = createDummyResponse();
            RESTConfig restConfig = new RESTConfig(callbackUrl);
            RESTClient restClient = new RESTClient(restConfig);
            APIResponse response = restClient.httpPost(solutions);

            Thread.sleep(60_000); // Just Like That!
            System.out.println("Response from callbackUrl = " + response.getResponseBodyAsString());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (RESTException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    private String createDummyResponse() throws JsonProcessingException {

        // TODO think something better
        String configPath = System.getProperty(CONFIG_PATH);

        // If not set read from Resources
        if (configPath == null || configPath.trim().isEmpty()) {

            ClassLoader classLoader = HomingRest.class.getClassLoader();
            configPath = classLoader.getResource(CONFIG_FILE_NAME).toString().substring(5);
        }

        String cloudOwner = "ATT";
        String cloudRegionId = "EastUS";
        try (FileReader reader = new FileReader(configPath)) {

            Properties properties = new Properties();
            properties.load(reader);
            cloudOwner = properties.getProperty("org.onap.homing.cloudOwner");
            cloudRegionId = properties.getProperty("org.onap.homing.cloudRegionId");

        } catch (IOException e) {
            e.printStackTrace();
        }

        PlacementSugession placementSugession = new PlacementSugession();
        placementSugession.setStatusMessage("Returning Dummy Responses");

        Solutions solutions = new Solutions();
        placementSugession.setSolutions(solutions);

        PlacementSolutions placementSolutions = new PlacementSolutions();

        List<PlacementSolutions> arrPolutions = new ArrayList<PlacementSolutions>();
        arrPolutions.add(placementSolutions);
        arrPolutions.add(placementSolutions);
        List<List<PlacementSolutions>> listOfList = new ArrayList<>();
        listOfList.add(arrPolutions);
        solutions.setPlacementSolutions(listOfList);

        Solution solution = new Solution();
        placementSolutions.setSolution(solution);

        ArrayList<AssignmentInfo> assignmentInfos = new ArrayList<AssignmentInfo>();
        AssignmentInfo assignmentInfo = new AssignmentInfo();
        assignmentInfo.setKey("cloudOwner");
        assignmentInfo.setValue(cloudOwner);
        assignmentInfos.add(assignmentInfo);

        assignmentInfo = new AssignmentInfo();
        assignmentInfo.setKey("cloudRegionId");
        assignmentInfo.setValue(cloudRegionId);
        assignmentInfos.add(assignmentInfo);

        placementSolutions.setAssignmentInfo(assignmentInfos);

        ArrayList<LicenseSolutions> arrLicense = new ArrayList<LicenseSolutions>();
        LicenseSolutions licenseSolutions = new LicenseSolutions();
        arrLicense.add(licenseSolutions);
        solutions.setLicenseSolutions(arrLicense);

        ObjectMapper objectMapper = new ObjectMapper();

        String dummyResponse = objectMapper.writeValueAsString(placementSugession);

        return dummyResponse;
    }

}

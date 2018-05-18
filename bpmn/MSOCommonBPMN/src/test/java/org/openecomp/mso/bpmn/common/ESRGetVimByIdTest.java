package org.openecomp.mso.bpmn.common;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.openecomp.mso.bpmn.core.WorkflowException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openecomp.mso.bpmn.mock.StubResponseESR.mockCloudRegionESR;

public class ESRGetVimByIdTest extends WorkflowTest {

    private static final String CLOUD_OWNER = "ATT";
    private static final String CLOUD_REGION_ID = "EastUS";
    private final long WAIT_TIME = 60_000;
    private final String msoRequestId = "466ec94f-f615-4a97-a7f8-df21d4997321";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(28090);

    @Test
    @Deployment(resources = {"subprocess/ESRGetVimById.bpmn"})
    public void sunnyDay() {

        logStart();

        mockCloudRegionESR(200, CLOUD_OWNER, CLOUD_REGION_ID, "esrGetVIMResponse.xml");

        String businessKey = UUID.randomUUID().toString();
        Map<String, Object> variables = new HashMap<String, Object>();

        variables.put("mso-request-id", msoRequestId);
        variables.put("ESRGETVIM_cloudOwner", CLOUD_OWNER);
        variables.put("ESRGETVIM_cloudRegionId", CLOUD_REGION_ID);
        variables.put("isDebugLogEnabled", "true");

        invokeSubProcess("ESRGetVimById", businessKey, variables);
        waitForProcessEnd(businessKey, WAIT_TIME);

        Object indicator = getVariableFromHistory(businessKey, "ESRGETVIM_SuccessIndicator");
        assertNotNull(indicator);
        boolean isSuccess = (boolean) indicator;
        assertTrue(isSuccess);

        Object responseObj = getVariableFromHistory(businessKey, "ESRGETVIM_Response");
        assertNotNull(responseObj);
        String response = (String) responseObj;
        assertTrue(response.contains("<user-name>user_name</user-name>"));

        logEnd();
    }

    @Test
    @Deployment(resources = {"subprocess/ESRGetVimById.bpmn"})
    public void erroFromServer() {

        logStart();

        mockCloudRegionESR(400, CLOUD_OWNER, CLOUD_REGION_ID, "esrGetVIMResponse.xml");

        String businessKey = UUID.randomUUID().toString();
        Map<String, Object> variables = new HashMap<String, Object>();

        variables.put("mso-request-id", msoRequestId);
        variables.put("ESRGETVIM_cloudOwner", CLOUD_OWNER);
        variables.put("ESRGETVIM_cloudRegionId", CLOUD_REGION_ID);
        variables.put("isDebugLogEnabled", "true");

        invokeSubProcess("ESRGetVimById", businessKey, variables);

        waitForProcessEnd(businessKey, WAIT_TIME);

        Object exception = getVariableFromHistory(businessKey, "WorkflowException");
        assertNotNull(exception);

        logEnd();
    }

    @Test
    @Deployment(resources = {"subprocess/ESRGetVimById.bpmn"})
    public void serverError404() {

        logStart();

        //init(400, CLOUD_OWNER, CLOUD_REGION_ID, GOOD_RESPONSE);

        String businessKey = UUID.randomUUID().toString();
        Map<String, Object> variables = new HashMap<String, Object>();

        variables.put("mso-request-id", msoRequestId);
        variables.put("ESRGETVIM_cloudOwner", CLOUD_OWNER);
        variables.put("ESRGETVIM_cloudRegionId", CLOUD_REGION_ID);
        variables.put("isDebugLogEnabled", "true");

        invokeSubProcess("ESRGetVimById", businessKey, variables);

        waitForProcessEnd(businessKey, WAIT_TIME);

        Object exception = getVariableFromHistory(businessKey, "WorkflowException");
        assertNotNull(exception);
        assertTrue(exception instanceof WorkflowException);
        Object responseObj = getVariableFromHistory(businessKey, "ESRGETVIM_Response");
        assertNotNull(responseObj);
        String response = (String) responseObj;
        assertTrue(response.contains("HTTP ERROR 404"));

        logEnd();
    }
}


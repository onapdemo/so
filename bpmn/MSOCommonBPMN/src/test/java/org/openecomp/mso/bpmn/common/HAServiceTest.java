package org.openecomp.mso.bpmn.common;

import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;
import org.openecomp.mso.bpmn.core.WorkflowException;
import org.openecomp.mso.bpmn.mock.FileUtil;
import org.openecomp.mso.bpmn.mock.StubHAService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HAServiceTest extends WorkflowTest {

    private final CallbackSet callbacks = new CallbackSet();

    private final String msoRequestId = "466ec94f-f615-4a97-a7f8-df21d4997321";
    private final long WAIT_TIME = 60_000;

    public HAServiceTest() {
        callbacks.put("SOLUTION_FOUND", JSON, "HAS_SolutionResponse", FileUtil.readResourceFile("__files/notifyHASolution.json"));
        callbacks.put("NO_SOLUTION_FOUND", JSON, "HAS_SolutionResponse", "{}");
    }

    @Test
    @Deployment(resources = {
            "subprocess/HAService.bpmn",
            "subprocess/ReceiveWorkflowMessage.bpmn"
    })
    public void sunnyDay() {

        logStart();

        StubHAService.mockHASPost(202);

        String businessKey = UUID.randomUUID().toString();
        Map<String, Object> variables = new HashMap<>();

        variables.put("mso-request-id", msoRequestId);
        variables.put("isDebugLogEnabled", "true");

        invokeSubProcess("HAService", businessKey, variables);

        injectWorkflowMessages(callbacks, "SOLUTION_FOUND");

        waitForProcessEnd(businessKey, WAIT_TIME);

        Object successObj = getVariableFromHistory(businessKey, "HAS_SuccessIndicator");
        assertNotNull(successObj);
        boolean isSuccess = (boolean) successObj;
        assertTrue(isSuccess);

        Object acceptedObj = getVariableFromHistory(businessKey, "HAS_ResponseCode");
        assertNotNull(acceptedObj);
        int isAccepted = (int) acceptedObj;
        assertTrue(isAccepted == 202);

        Object cloudOwner = getVariableFromHistory(businessKey, "HAS_cloudOwner");
        assertNotNull(cloudOwner);

        Object cloudRegionId = getVariableFromHistory(businessKey, "HAS_cloudRegionId");
        assertNotNull(cloudRegionId);

        logEnd();
    }

    @Test
    @Deployment(resources = {
            "subprocess/HAService.bpmn",
            "subprocess/ReceiveWorkflowMessage.bpmn"
    })
    public void badResponseFromHAS(){

        logStart();

        StubHAService.mockHASPost(400);

        String businessKey = UUID.randomUUID().toString();
        Map<String, Object> variables = new HashMap<>();

        variables.put("mso-request-id", msoRequestId);
        variables.put("isDebugLogEnabled", "true");

        invokeSubProcess("HAService", businessKey, variables);

        waitForProcessEnd(businessKey, WAIT_TIME);

        Object exceptionObj = getVariableFromHistory(businessKey, "WorkflowException");
        assertNotNull(exceptionObj);

        Object responseCode = getVariableFromHistory(businessKey, "HAS_ResponseCode");
        assertNotNull(responseCode);
        assertTrue(400==(int)responseCode);

        logEnd();
    }


    @Test
    @Deployment(resources = {
            "subprocess/HAService.bpmn",
            "subprocess/ReceiveWorkflowMessage.bpmn"
    })
    public void noHASolution() {

        logStart();

        StubHAService.mockHASPost(202);

        String businessKey = UUID.randomUUID().toString();
        Map<String, Object> variables = new HashMap<>();

        variables.put("mso-request-id", msoRequestId);
        variables.put("isDebugLogEnabled", "true");

        invokeSubProcess("HAService", businessKey, variables);

        injectWorkflowMessages(callbacks, "NO_SOLUTION_FOUND");

        waitForProcessEnd(businessKey, WAIT_TIME);

        Object successObj = getVariableFromHistory(businessKey, "HAS_SuccessIndicator");
        assertNotNull(successObj);
        assertTrue(!(boolean)successObj);

        Object exceptionObj = getVariableFromHistory(businessKey, "WorkflowException");
        assertNotNull(exceptionObj);
        assertTrue(exceptionObj instanceof WorkflowException);

        Object responseCode = getVariableFromHistory(businessKey, "HAS_ResponseCode");
        assertNotNull(responseCode);
        assertTrue(202==(int)responseCode);

        Object cloudOwner = getVariableFromHistory(businessKey, "HAS_cloudOwner");
        assertTrue(cloudOwner==null);

        Object cloudRegionId = getVariableFromHistory(businessKey, "HAS_cloudRegionId");
        assertTrue(cloudRegionId==null);

        logEnd();
    }
}

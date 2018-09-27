package org.openecomp.mso.bpmn.common;

import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;
import org.openecomp.mso.bpmn.core.WorkflowException;
import org.openecomp.mso.bpmn.mock.StubMultiVimAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MultiVimAdapterTest extends WorkflowTest {

    private final CallbackSet callbacks = new CallbackSet();

    private final String vnfId = "7d2e2469-8708-47c3-a0d4-73fa28a8a50c";
    private final String vfModuleName = "CB_BASE_VF";
    private final String vnfModelName = "vnfModelName";
    private final String msoRequestId = "466ec94f-f615-4a97-a7f8-df21d4997321";
    private final String serviceInstanceId = "466ec94f-f615-4a97-a7f8-df21d499755";
    private final String toscaCsarArtifactUuid = "aaaa5198-b70a-49db-a5eb-fa080cbfc3b7";
    private final String cloudOwner = "ATT";
    private final String cloudRegionId = "EastUS";

    private final long WAIT_TIME = 60_000;

    private final String notificationMsgSuccess =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                    "<CreateVfModuleMultiVimResponse>" +
                    "    <vfModuleCreated>true</vfModuleCreated>\n" +
                    "    <vfModuleStackId>7d2e2469-8708-47c3-a0d4-73fa28a8a77d</vfModuleStackId>" +
                    "    <vnfId>7d2e2469-8708-47c3-a0d4-73fa28a8a50c</vnfId>" +
                    "</CreateVfModuleMultiVimResponse>";

    private final String notificationMsgFail =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                    "<CreateVfModuleMultiVimResponse>" +
                    "    <vfModuleCreated>false</vfModuleCreated>\n" +
                    "    <vfModuleStackId>failed: Input stream may not be null</vfModuleStackId>" +
                    "    <vnfId>7d2e2469-8708-47c3-a0d4-73fa28a8a50c</vnfId>" +
                    "</CreateVfModuleMultiVimResponse>";

    public MultiVimAdapterTest(){

        callbacks.put("MULTIVIM-SUCCESS", XML, "MVIMRESTAdapterResponse", notificationMsgSuccess);
        callbacks.put("MULTIVIM-FAIL", XML, "MVIMRESTAdapterResponse", notificationMsgFail);
        callbacks.put("MULTIVIM-NULL", XML, "MVIMRESTAdapterResponse", "");
    }

    @Test
    @Deployment(resources = {
            "subprocess/MultiVimAdapter.bpmn",
            "subprocess/ReceiveWorkflowMessage.bpmn"
    })
    public void testMultiVimAdapterRequiredParams() {

        logStart();

        StubMultiVimAdapter.mockMultiVimPost(202, vnfId);

        String businessKey = UUID.randomUUID().toString();
        Map<String, Object> variables = getEnvVariables();
        // It is Required, Which I am going to test
        variables.put("vnfId", "");

        invokeSubProcess("MultiVimAdapter", businessKey, variables);

        waitForProcessEnd(businessKey, WAIT_TIME);

        Object indicator = getVariableFromHistory(businessKey, "MVIMREST_SuccessIndicator");
        assertNotNull(indicator);
        boolean isSuccess = (boolean)indicator;
        assertTrue(!isSuccess);

        Object exception = getVariableFromHistory(businessKey, "WorkflowException");
        assertNotNull(exception);
        assertTrue(exception instanceof WorkflowException);
        WorkflowException workflowException = (WorkflowException) exception;
        String message = workflowException.getErrorMessage();
        System.out.print("\n\n\t message = " + message + "\n\n\n");
        assertTrue(message.contains("MultiVimAdapter, Required 'vnfId' variable not found"));

        logEnd();
    }


    @Test
    @Deployment(resources = {
            "subprocess/MultiVimAdapter.bpmn",
            "subprocess/ReceiveWorkflowMessage.bpmn"
    })
    public void testMultiVimAdapterSuccess() {

        logStart();

        StubMultiVimAdapter.mockMultiVimPost(202, vnfId);

        String businessKey = UUID.randomUUID().toString();
        Map<String, Object> variables = getEnvVariables();

        invokeSubProcess("MultiVimAdapter", businessKey, variables);

        injectWorkflowMessages(callbacks, "MULTIVIM-SUCCESS");

        waitForProcessEnd(businessKey, WAIT_TIME);

        Object indicator = getVariableFromHistory(businessKey, "MVIMREST_SuccessIndicator");
        assertNotNull(indicator);
        boolean isSuccess = (boolean)indicator;
        assertTrue(isSuccess);

        Object object = getVariableFromHistory(businessKey, "NotificationResponse");
        assertNotNull(object);

        String multiVimAdapterResponse = (String) object;
        System.out.print("\n\n\t multiVimAdapterResponse = " + multiVimAdapterResponse + "\n\n\n");
        assertTrue(multiVimAdapterResponse.contains("<CreateVfModuleMultiVimResponse>"));

        assertTrue(multiVimAdapterResponse.contains("<vfModuleCreated>true</vfModuleCreated>"));

        logEnd();
    }

    @Test
    @Deployment(resources = {
            "subprocess/MultiVimAdapter.bpmn",
            "subprocess/ReceiveWorkflowMessage.bpmn"
    })
    public void testMultiVimAdapterFailed() {

        logStart();

        StubMultiVimAdapter.mockMultiVimPost(202, vnfId);

        String businessKey = UUID.randomUUID().toString();
        Map<String, Object> variables = getEnvVariables();

        invokeSubProcess("MultiVimAdapter", businessKey, variables);

        injectWorkflowMessages(callbacks, "MULTIVIM-FAIL");

        waitForProcessEnd(businessKey, WAIT_TIME);

        Object indicator = getVariableFromHistory(businessKey, "MVIMREST_SuccessIndicator");
        assertNotNull(indicator);
        boolean isSuccess = (boolean)indicator;
        assertTrue(!isSuccess);

        Object exception = getVariableFromHistory(businessKey, "WorkflowException");
        assertNotNull(exception);
        assertTrue(exception instanceof WorkflowException);

        Object object = getVariableFromHistory(businessKey, "NotificationResponse");
        assertNotNull(object);

        String multiVimAdapterResponse = (String) object;
        System.out.print("\n\n\t multiVimAdapterResponse = " + multiVimAdapterResponse + "\n\n\n");
        assertTrue(multiVimAdapterResponse.contains("<CreateVfModuleMultiVimResponse>"));

        assertTrue(multiVimAdapterResponse.contains("<vfModuleCreated>false</vfModuleCreated>"));

        logEnd();
    }

    @Test
    @Deployment(resources = {
            "subprocess/MultiVimAdapter.bpmn",
            "subprocess/ReceiveWorkflowMessage.bpmn"
    })
    public void testMultiVimAdapterNotificationMsgNull() {

        logStart();

        StubMultiVimAdapter.mockMultiVimPost(202, vnfId);

        String businessKey = UUID.randomUUID().toString();
        Map<String, Object> variables = getEnvVariables();

        invokeSubProcess("MultiVimAdapter", businessKey, variables);

        injectWorkflowMessages(callbacks, "MULTIVIM-NULL");

        waitForProcessEnd(businessKey, WAIT_TIME);

        Object indicator = getVariableFromHistory(businessKey, "MVIMREST_SuccessIndicator");
        assertNotNull(indicator);
        boolean isSuccess = (boolean)indicator;
        assertTrue(!isSuccess);

        Object exception = getVariableFromHistory(businessKey, "WorkflowException");
        assertNotNull(exception);
        assertTrue(exception instanceof WorkflowException);

        Object object = getVariableFromHistory(businessKey, "NotificationResponse");
        assertNotNull(object);

        String multiVimAdapterResponse = (String) object;
        System.out.print("\n\n\t multiVimAdapterResponse = " + multiVimAdapterResponse + "\n\n\n");
        assertTrue(multiVimAdapterResponse.isEmpty());

        logEnd();
    }

    private Map<String, Object> getEnvVariables(){

        Map<String, Object> variables = new HashMap<>();
        variables.put("mso-request-id", msoRequestId);
        variables.put("mso-service-instance-id", serviceInstanceId);
        variables.put("vnfId", vnfId);
        variables.put("vnfModelName", vnfModelName);
        variables.put("vfModuleName", vfModuleName);
        variables.put("toscaCsarArtifactUuid", toscaCsarArtifactUuid);
        variables.put("cloudOwner", cloudOwner);
        variables.put("cloudRegionId", cloudRegionId);
        variables.put("isDebugLogEnabled", "true");
        variables.put("cloudType", "azure");
        return variables;
    }
}

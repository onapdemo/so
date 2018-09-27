package org.openecomp.mso.bpmn.common.scripts

import com.fasterxml.jackson.databind.ObjectMapper
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.runtime.Execution
import org.openecomp.mso.bpmn.core.json.JsonUtils
import org.openecomp.mso.client.has.entities.HASRequest
import org.openecomp.mso.client.has.entities.RequestInfo

import static org.apache.commons.lang3.StringUtils.isBlank

class HAService extends AbstractServiceTaskProcessor {

    private final String PREFIX = 'HAS_'
    private final String MESSAGE_TYPE = PREFIX + 'SolutionResponse'
    private final String DEFAULT_TIMEOUT = 'PT5M'

    private MsoUtils msoUtils = new MsoUtils()
    private ExceptionUtil exceptionUtil = new ExceptionUtil()
    private ObjectMapper objectMapper = new ObjectMapper()

    private void initializeVariables(Execution execution) {

        execution.setVariable('prefix', PREFIX)
        execution.setVariable(PREFIX + 'messageType', MESSAGE_TYPE)
        execution.setVariable(PREFIX + 'timeout', DEFAULT_TIMEOUT)
        setSuccessIndicator(execution, false)
        //Read timeout from property file.
        def multivimCallbackTimeout = execution.getVariable('URN_mso_has_timeout')
        if (multivimCallbackTimeout != null) {
            execution.setVariable(PREFIX + 'timeout', multivimCallbackTimeout)
        }
    }

    void preProcessRequest(DelegateExecution execution) {

        def method = getClass().getSimpleName() + '.preProcessRequest(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        try {
            initializeVariables(execution)

            def msoRequestId = execution.getVariable('mso-request-id')
            if (isBlank(msoRequestId)) {
                exceptionUtil.buildAndThrowWorkflowException(execution, 2000, method + ", mso-request-id is mandatory")
            }

            String messageId = msoRequestId + '-' + System.currentTimeMillis()
            execution.setVariable(PREFIX + 'correlator', messageId)
            execution.setVariable(MESSAGE_TYPE + '_CORRELATOR', messageId)

            def notificationUrl = createCallbackURL(execution, MESSAGE_TYPE, messageId)
            def useQualifiedHostName = execution.getVariable('URN_mso_use_qualified_host')

            logDebug('notificationUrl: ' + notificationUrl, isDebugLogEnabled)
            logDebug('QualifiedHostName: ' + useQualifiedHostName, isDebugLogEnabled)

            if ('true'.equals(useQualifiedHostName)) {
                notificationUrl = utils.getQualifiedHostNameForCallback(notificationUrl)
            }

            logDebug('Final notificationUrl: ' + notificationUrl, isDebugLogEnabled)

            // Create Request
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setCallbackUrl(notificationUrl);

            HASRequest hasRequest = new HASRequest();
            hasRequest.setRequestInfo(requestInfo);

            String request = objectMapper.writeValueAsString(hasRequest);

            logDebug(method + ', Request = ' + request, isDebugLogEnabled)

            execution.setVariable(PREFIX + 'Request', request)

            def hasURL = execution.getVariable('URN_mso_has_endpoint')
            execution.setVariable(PREFIX + 'Url', hasURL)

            logDebug(method + ', hasURL = ' + hasURL, isDebugLogEnabled)

        } catch (BpmnError e) {
            throw e
        } catch (Exception e) {
            String msg = 'Caught exception in ' + method + ": " + e
            logError(msg)
            exceptionUtil.buildAndThrowWorkflowException(execution, 2000, msg)
        }

    }

    public void logResponse(Execution execution) {

        def method = getClass().getSimpleName() + '.logResponse(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        try {
            def responseCode = execution.getVariable(PREFIX + 'ResponseCode')
            def response = execution.getVariable(PREFIX + 'Response')
            logDebug(method + ' : responseCode =  ' + responseCode, isDebugLogEnabled)
            logDebug(method + ' : response =  ' + response, isDebugLogEnabled)
        } catch (BpmnError e) {
            throw e
        } catch (Exception e) {
            String msg = 'Caught exception in ' + method + ": " + e
            logError(msg)
            exceptionUtil.buildAndThrowWorkflowException(execution, 2000, msg)
        }
    }

    public void postSuccess(Execution execution) {

        def method = getClass().getSimpleName() + '.postSuccessStatus(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)
        try {
            String workflowResponse = execution.getVariable('WorkflowResponse');
            logDebug(method + ' : WorkflowResponse = ' + workflowResponse, isDebugLogEnabled)

            if (workflowResponse == null || workflowResponse.isEmpty()) {
                exceptionUtil.buildAndThrowWorkflowException(execution, 417, method + ", Empty WorkflowResponse")
            }

            JsonUtils jsonUtils = new JsonUtils()

            String string = jsonUtils.getJsonValue(workflowResponse, "solutions.placementSolutions")
            ArrayList<String> firstList = jsonUtils.StringArrayToList(execution, string)
            string = firstList.get(0)
            firstList = jsonUtils.StringArrayToList(execution, string)
            string = firstList.get(0)
            string = jsonUtils.getJsonValue(string, "assignmentInfo")
            firstList = jsonUtils.StringArrayToList(execution, string)

            String cloudOwner = null;
            String cloudRegionId = null;
            for (String assignmentInfo : firstList){

                String key = jsonUtils.getJsonValueForKey(assignmentInfo, "key")
                switch (key){
                    case "cloudOwner":
                        cloudOwner = jsonUtils.getJsonValueForKey(assignmentInfo, "value")
                        break
                    case "cloudRegionId":
                        cloudRegionId = jsonUtils.getJsonValueForKey(assignmentInfo, "value")
                        break
                }
            }

            if(isBlank(cloudOwner) || isBlank(cloudRegionId)){
                exceptionUtil.buildAndThrowWorkflowException(execution, 2000, method + ", incorrect HAS response, cloudOwner = " + cloudOwner + ", cloudRegionId = " + cloudRegionId)
            }

            execution.setVariable(PREFIX + 'SolutionResponse', workflowResponse)
            execution.setVariable(PREFIX + 'cloudOwner', cloudOwner)
            execution.setVariable(PREFIX + 'cloudRegionId', cloudRegionId)

            setSuccessIndicator(execution, true)

        } catch (BpmnError e) {
            throw e
        } catch (Exception e) {
            String msg = 'Caught exception in ' + method + ": " + e
            logError(msg)
            exceptionUtil.buildAndThrowWorkflowException(execution, 2000, msg)
        }
    }
}

package org.openecomp.mso.bpmn.common.scripts

import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.runtime.Execution
import org.json.JSONObject

/**
 * Mandatory Inputs -
 *  vnfId
 *  vfModuleName
 *  inputs : a Map for all inputs
 *  mso-request-id
 *  mso-service-instance-id
 *  toscaCsarArtifactUuid
 *  cloudOwner
 *  cloudRegionId
 *
 * Expected return values -
 *  multiVimAdapterResponse
 *  WorkflowException
 */
class MultiVimAdapter extends AbstractServiceTaskProcessor {

    private final long poolingInterval = 5000;
    private final String PREFIX = 'MVIMREST_'
    private final String MESSAGE_TYPE = 'MVIMRESTAdapterResponse'
    private final String DEFAULT_TIMEOUT = 'PT15M'

    private MsoUtils msoUtils = new MsoUtils()
    private ExceptionUtil exceptionUtil = new ExceptionUtil()

    private void initializeVariables(Execution execution) {
        execution.setVariable('prefix', PREFIX)
        execution.setVariable(PREFIX + 'messageType', MESSAGE_TYPE)
        execution.setVariable(PREFIX + 'timeout', DEFAULT_TIMEOUT)
        setSuccessIndicator(execution, false)
        //Read timeout from property file.
        def multivimCallbackTimeout = execution.getVariable('URN_mso_multivim_timeout')
        if(multivimCallbackTimeout!=null){
            execution.setVariable(PREFIX + 'timeout', multivimCallbackTimeout)
        }
    }

    /**
     * MultiMim Pre-Processing before Http Post Call to Multi Vim Rest
     *
     * @param execution the execution
     */
    public void preProcessRequest(Execution execution) {

        def method = getClass().getSimpleName() + '.preProcessRequest(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)
        initializeVariables(execution)

        try {

            checkAllRequiredValues(execution, "vnfId", "vfModuleName", "vnfModelName", "mso-request-id", "mso-service-instance-id", "toscaCsarArtifactUuid", "cloudOwner", "cloudRegionId", "cloudType")

            def vnfId = execution.getVariable('vnfId')
            def vnfModelName = execution.getVariable('vnfModelName')

            String multiVimAUrl = execution.getVariable('URN_mso_multivim_adapters_vnf_rest_endpoint') + '/' + vnfId + '/vf-modules'
            execution.setVariable(PREFIX + 'multiVimAdapterUrl', multiVimAUrl)

            logDebug('multiVimAdapterUrl = ' + multiVimAUrl, isDebugLogEnabled)

            def msoRequestId = execution.getVariable('mso-request-id')
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

            Map<String, Object> inputMap = (Map<String, Object>) execution.getVariable('inputs')
            def inputStr = creteInputJsonSrt(execution, inputMap)

            def vfModuleName = execution.getVariable('vfModuleName')
            def toscaCsarArtifactUuid = execution.getVariable('toscaCsarArtifactUuid')

            def msoServiceInstanceId = execution.getVariable("mso-service-instance-id")

            def cloudOwner = execution.getVariable("cloudOwner")
            def cloudRegionId = execution.getVariable("cloudRegionId")
            def multicloudName = cloudOwner + "_" + cloudRegionId // same as VIM ID

            def cloudType = execution.getVariable("cloudType")

            String createVnfARequest =
                    """<createVfModuleMultiVimRequest>
                    <vnfId>${vnfId}</vnfId>
                    <vnfModelName>${vnfModelName}</vnfModelName>
                    <vfModuleName>${vfModuleName}</vfModuleName>
                    <multicloudName>${multicloudName}</multicloudName>
                    <cloudType>${cloudType}</cloudType>
                    <toscaCsarArtifactUuid>${toscaCsarArtifactUuid}</toscaCsarArtifactUuid>
                    <inputs>${inputStr}</inputs>
                    <notificationUrl>${notificationUrl}</notificationUrl>
                    <msoRequest>
                        <requestId>${msoRequestId}</requestId>
                        <serviceInstanceId>${msoServiceInstanceId}</serviceInstanceId>
                    </msoRequest>
                </createVfModuleMultiVimRequest>"""

            execution.setVariable("MVIMREST_multiVimAdapterRequest", createVnfARequest)

            logDebug('MVIMREST_multiVimAdapterRequest = ' + createVnfARequest, isDebugLogEnabled)

            // Get the Basic Auth credentials for the MultiVimAdapter
            String basicAuthValue = execution.getVariable('URN_mso_adapters_po_auth')
            if (basicAuthValue == null || basicAuthValue.isEmpty()) {
                logError(getProcessKey(execution) + ': mso:adapters:po:auth URN mapping is not defined')
            } else {
                logDebug(getProcessKey(execution) + ': Obtained BasicAuth credentials for multiVimAdapter:' + basicAuthValue, isDebugLogEnabled)
                try {
                    def encodedString = utils.getBasicAuth(basicAuthValue, execution.getVariable('URN_mso_msoKey'))
                    execution.setVariable(PREFIX + 'basicAuthHeaderValue', encodedString)
                } catch (IOException ex) {
                    logError(getProcessKey(execution) + ': Unable to encode BasicAuth credentials for multiVimAdapter', ex)
                }
            }
            execution.setVariable(PREFIX + 'basicAuthValue', basicAuthValue)

        } catch (BpmnError e) {
            throw e
        } catch (Exception e) {
            String msg = 'Caught exception in ' + method + ": " + e
            logError(msg)
            exceptionUtil.buildAndThrowWorkflowException(execution, 2000, msg)
        }
    }

    public void postSuccessStatus(Execution execution) {

        def method = getClass().getSimpleName() + '.postSuccessStatus(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        try {

            String notificationMessage = execution.getVariable('NotificationResponse');
            logDebug(method + ' : notificationMessage = ' + notificationMessage, isDebugLogEnabled)
            if (notificationMessage == null || notificationMessage.isEmpty()) {
                exceptionUtil.buildAndThrowWorkflowException(execution, 417, method + ", Empty notificationMessage")
            }

            String successStatus = msoUtils.getNodeText1(notificationMessage, "vfModuleCreated")
            if (successStatus == null || !"true".equals(successStatus)) {
                exceptionUtil.buildAndThrowWorkflowException(execution, 417, method + ", Failed to create, notificationMessage = " + notificationMessage)
            }

            setSuccessIndicator(execution, true)
            execution.setVariable('multiVimAdapterResponse', notificationMessage)

        } catch (BpmnError e) {
            throw e
        } catch (Exception e) {
            String msg = 'Caught exception in ' + method + ": " + e
            logError(msg)
            exceptionUtil.buildAndThrowWorkflowException(execution, 2000, msg)
        }
    }


    private String creteInputJsonSrt(Execution execution, Map<String, Object> inputMap) {

        def method = getClass().getSimpleName() + '.creteInputJsonSrt(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')

        logDebug('Entered ' + method, isDebugLogEnabled)

        String returnJson = "{\"inputs\":{}}"

        if (inputMap == null || inputMap.size() < 1) {
            return returnJson
        }

        JSONObject jsonObj = new JSONObject(returnJson)
        JSONObject inputsObject = jsonObj.getJSONObject("inputs")
        for (Map.Entry<String, Object> entry : inputMap) {
            inputsObject.put(entry.getKey(), entry.getValue())
        }

        returnJson = jsonObj.toString()
        logDebug(method + " : jsonStr = " + returnJson, isDebugLogEnabled)
        return returnJson
    }

    public void logResponse(Execution execution) {

        def method = getClass().getSimpleName() + '.logResponse(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        try {

            def response = execution.getVariable('MVIMREST_Response')
            def responseCode = execution.getVariable('MVIMREST_ResponseCode')
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

    private void checkAllRequiredValues(Execution execution, String... requiredVariables) {

        def method = getClass().getSimpleName() + '.postSuccessStatus(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        for (String variable : requiredVariables) {
            def value = execution.getVariable(variable)
            if (value == null || ((value instanceof CharSequence) && value.length() == 0)) {
                logDebug(method + ',  value = ' + value, isDebugLogEnabled)
                exceptionUtil.buildAndThrowWorkflowException(execution, 1002, "MultiVimAdapter, Required '" + variable + "' variable not found")
            }
        }
    }
}
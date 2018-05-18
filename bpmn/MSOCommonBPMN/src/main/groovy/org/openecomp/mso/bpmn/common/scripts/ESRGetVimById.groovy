package org.openecomp.mso.bpmn.common.scripts

import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.runtime.Execution
import org.openecomp.mso.rest.APIResponse

import static org.apache.commons.lang3.StringUtils.isBlank

/**
 * This BPMN expects two mandatory inputs from out side -
 * 1. cloudOwner
 * 2. cloudRegionId
 */
class ESRGetVimById extends AbstractServiceTaskProcessor {

    private final String PREFIX = 'ESRGETVIM_'

    private AaiUtil aaiUtil = new AaiUtil(this)
    private ExceptionUtil exceptionUtil = new ExceptionUtil()


    public void preProcessRequest(Execution execution) {

        def method = getClass().getSimpleName() + '.preProcessRequest(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        try {
            execution.setVariable('prefix', PREFIX)
            setSuccessIndicator(execution, false)

            String cloudOwner = execution.getVariable(PREFIX + 'cloudOwner')
            String cloudRegionId = execution.getVariable(PREFIX + 'cloudRegionId')

            if (isBlank(cloudOwner)) {
                exceptionUtil.buildAndThrowWorkflowException(execution, 2000, "ESRGetVimById cloudOwner is mandatory")
            }

            if (isBlank(cloudRegionId)) {
                exceptionUtil.buildAndThrowWorkflowException(execution, 2000, "ESRGetVimById cloudRegionId is mandatory")
            }

            String aaiEndpoint = aaiUtil.getCloudInfrastructureCloudRegionEndpoint(execution)
            aaiEndpoint = aaiEndpoint.substring(0, aaiEndpoint.lastIndexOf("/cloud-region/") + 14)
            logDebug(method + ' : aaiEndpoint = ' + aaiEndpoint, isDebugLogEnabled)

            String esrUrl = aaiEndpoint + cloudOwner + '/' + cloudRegionId + '?depth=all'
            logDebug(method + ' : esrUrl = ' + esrUrl, isDebugLogEnabled)

            execution.setVariable(PREFIX + 'esrUrl', esrUrl)

        } catch (BpmnError e) {
            throw e
        } catch (Exception e) {
            String msg = 'Caught exception in ' + method + ": " + e
            logError(msg)
            exceptionUtil.buildAndThrowWorkflowException(execution, 2000, msg)
        }

    }

    public void queryESR(Execution execution) {

        def method = getClass().getSimpleName() + '.queryESR(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        def endPoint = execution.getVariable(PREFIX + 'esrUrl')

        logDebug(method + ' : endPoint = ' + endPoint, isDebugLogEnabled)

        try {

            AaiUtil aaiUtil = new AaiUtil(this)
            APIResponse response = aaiUtil.executeAAIGetCall(execution, endPoint)

            def responseData = response.getResponseBodyAsString()
            def statusCode = response.getStatusCode()

            logDebug(method + ' : responseCode =  ' + statusCode, isDebugLogEnabled)
            logDebug(method + ' : response =  ' + responseData, isDebugLogEnabled)

            execution.setVariable(PREFIX + 'ResponseCode', statusCode)
            execution.setVariable(PREFIX + 'Response', responseData)

        } catch (Exception ex) {
            logError("Exception occurred while executing AAI-ESR GET :" + ex.getMessage(), isDebugLogEnabled)
            exceptionUtil.buildAndThrowWorkflowException(execution, 5000, "Internal Error - Occured in queryESR-for-Vim-Details.")
        }
    }


    public void postSuccessStatus(Execution execution) {

        def method = getClass().getSimpleName() + '.postSuccessStatus(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        try {

            String responseCode = execution.getVariable(PREFIX + 'ResponseCode')
            logDebug(method + ' : responseCode =  ' + responseCode, isDebugLogEnabled)

            String response = execution.getVariable(PREFIX + 'Response')
            logDebug(method + ' : response =  ' + response, isDebugLogEnabled)

            // TODO some validations
            if (isBlank(response)) {
                exceptionUtil.buildAndThrowWorkflowException(execution, 417, method + ", Empty response from ESR")
            }

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

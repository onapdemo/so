/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.mso.bpmn.infrastructure.scripts

import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.math.NumberUtils
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.runtime.Execution
import org.openecomp.mso.bpmn.common.scripts.*
import org.openecomp.mso.bpmn.core.RollbackData
import org.openecomp.mso.bpmn.core.WorkflowException
import org.openecomp.mso.bpmn.core.json.JsonUtils
import org.openecomp.mso.rest.APIResponse
import org.openecomp.mso.rest.RESTClient
import org.openecomp.mso.rest.RESTConfig
import org.springframework.web.util.UriUtils
import org.w3c.dom.*
import org.xml.sax.InputSource

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import static org.apache.commons.lang3.StringUtils.isBlank

public class DoCreateVfModuleMultiVim extends VfModuleBase {

    private String PREFIX = "DCVFMMV_"
    private ExceptionUtil exceptionUtil = new ExceptionUtil()
    private JsonUtils jsonUtil = new JsonUtils()
    private SDNCAdapterUtils sdncAdapterUtils = new SDNCAdapterUtils(this)
    private CatalogDbUtils catalogDbUtils = new CatalogDbUtils()

    /**
     * Validates the request message and sets up the workflow.
     * @param execution the execution
     */
    public void preProcessRequest(Execution execution) {
        def method = getClass().getSimpleName() + '.preProcessRequest(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        execution.setVariable('prefix', PREFIX)
        try {
            def rollbackData = execution.getVariable("rollbackData")
            if (rollbackData == null) {
                rollbackData = new RollbackData()
            }

            execution.setVariable("DCVFMMV_vnfParamsExistFlag", false)
            execution.setVariable("DCVFMMV_oamManagementV4Address", "")
            execution.setVariable("DCVFMMV_oamManagementV6Address", "")

            String request = execution.getVariable("DoCreateVfModuleMultiVimRequest")

            if (isBlank(request)) {
                // Building Block-type request
                String vfModuleModelInfo = execution.getVariable("vfModuleModelInfo")
                def serviceModelInfo = execution.getVariable("serviceModelInfo")
                logDebug("serviceModelInfo: " + serviceModelInfo, isDebugLogEnabled)
                def vnfModelInfo = execution.getVariable("vnfModelInfo")

                //tenantId
                def tenantId = execution.getVariable("tenantId")
                execution.setVariable("DCVFMMV_tenantId", tenantId)
                rollbackData.put("VFMODULE", "tenantid", tenantId)
                //volumeGroupId
                def volumeGroupId = execution.getVariable("volumeGroupId")
                execution.setVariable("DCVFMMV_volumeGroupId", volumeGroupId)
                //volumeGroupName
                def volumeGroupName = execution.getVariable("volumeGroupName")
                execution.setVariable("DCVFMMV_volumeGroupName", volumeGroupName)
                //cloudSiteId
                def cloudSiteId = execution.getVariable("lcpCloudRegionId")
                execution.setVariable("DCVFMMV_cloudSiteId", cloudSiteId)
                rollbackData.put("VFMODULE", "aiccloudregion", cloudSiteId)
                logDebug("cloudSiteId: " + cloudSiteId, isDebugLogEnabled)
                //vnfType
                def vnfType = execution.getVariable("vnfType")
                execution.setVariable("DCVFMMV_vnfType", vnfType)
                rollbackData.put("VFMODULE", "vnftype", vnfType)
                logDebug("vnfType: " + vnfType, isDebugLogEnabled)
                //vnfName
                def vnfName = execution.getVariable("vnfName")
                execution.setVariable("DCVFMMV_vnfName", vnfName)
                rollbackData.put("VFMODULE", "vnfname", vnfName)
                logDebug("vnfName: " + vnfName, isDebugLogEnabled)
                //vnfId
                def vnfId = execution.getVariable("vnfId")
                execution.setVariable("DCVFMMV_vnfId", vnfId)
                rollbackData.put("VFMODULE", "vnfid", vnfId)
                logDebug("vnfId: " + vnfId, isDebugLogEnabled)
                //vfModuleName
                def vfModuleName = execution.getVariable("vfModuleName")
                execution.setVariable("DCVFMMV_vfModuleName", vfModuleName)
                rollbackData.put("VFMODULE", "vfmodulename", vfModuleName)
                logDebug("vfModuleName: " + vfModuleName, isDebugLogEnabled)
                //vfModuleModelName
                def vfModuleModelName = jsonUtil.getJsonValue(vfModuleModelInfo, "modelName")
                execution.setVariable("DCVFMMV_vfModuleModelName", vfModuleModelName)
                rollbackData.put("VFMODULE", "vfmodulemodelname", vfModuleModelName)
                logDebug("vfModuleModelName: " + vfModuleModelName, isDebugLogEnabled)
                //modelCustomizationUuid
                def modelCustomizationUuid = jsonUtil.getJsonValue(vfModuleModelInfo, "modelCustomizationUuid")
                execution.setVariable("DCVFMMV_modelCustomizationUuid", modelCustomizationUuid)
                rollbackData.put("VFMODULE", "modelcustomizationuuid", modelCustomizationUuid)
                logDebug("modelCustomizationUuid: " + modelCustomizationUuid, isDebugLogEnabled)
                //vfModuleId
                def vfModuleId = execution.getVariable("vfModuleId")
                execution.setVariable("DCVFMMV_vfModuleId", vfModuleId)
                logDebug("vfModuleId: " + vfModuleId, isDebugLogEnabled)
                def requestId = execution.getVariable("msoRequestId")
                execution.setVariable("DCVFMMV_requestId", requestId)
                logDebug("requestId: " + requestId, isDebugLogEnabled)
                rollbackData.put("VFMODULE", "msorequestid", requestId)
                // Set mso-request-id to request-id for VNF Adapter interface
                execution.setVariable("mso-request-id", requestId)
                //serviceId
                def serviceId = execution.getVariable("serviceId")
                execution.setVariable("DCVFMMV_serviceId", serviceId)
                logDebug("serviceId: " + serviceId, isDebugLogEnabled)
                //serviceInstanceId
                def serviceInstanceId = execution.getVariable("serviceInstanceId")
                execution.setVariable("DCVFMMV_serviceInstanceId", serviceInstanceId)
                rollbackData.put("VFMODULE", "serviceInstanceId", serviceInstanceId)
                logDebug("serviceInstanceId: " + serviceInstanceId, isDebugLogEnabled)
                //source - HARDCODED
                def source = "VID"
                execution.setVariable("DCVFMMV_source", source)
                rollbackData.put("VFMODULE", "source", source)
                logDebug("source: " + source, isDebugLogEnabled)
                //backoutOnFailure
                def disableRollback = execution.getVariable("disableRollback")
                def backoutOnFailure = true
                if (disableRollback != null && disableRollback == true) {
                    backoutOnFailure = false
                }
                execution.setVariable("DCVFMMV_backoutOnFailure", backoutOnFailure)
                logDebug("backoutOnFailure: " + backoutOnFailure, isDebugLogEnabled)
                //isBaseVfModule
                def isBaseVfModule = execution.getVariable("isBaseVfModule")
                execution.setVariable("DCVFMMV_isBaseVfModule", isBaseVfModule)
                logDebug("isBaseVfModule: " + isBaseVfModule, isDebugLogEnabled)
                //asdcServiceModelVersion
                def asdcServiceModelVersion = execution.getVariable("asdcServiceModelVersion")
                execution.setVariable("DCVFMMV_asdcServiceModelVersion", asdcServiceModelVersion)
                logDebug("asdcServiceModelVersion: " + asdcServiceModelVersion, isDebugLogEnabled)
                //personaModelId
                execution.setVariable("DCVFMMV_personaModelId", jsonUtil.getJsonValue(vfModuleModelInfo, "modelInvariantUuid"))
                //personaModelVersion
                execution.setVariable("DCVFMMV_personaModelVersion", jsonUtil.getJsonValue(vfModuleModelInfo, "modelUuid"))
                //vfModuleLabel
                def vfModuleLabel = execution.getVariable("vfModuleLabel")
                if (vfModuleLabel != null) {
                    execution.setVariable("DCVFMMV_vfModuleLabel", vfModuleLabel)
                    logDebug("vfModuleLabel: " + vfModuleLabel, isDebugLogEnabled)
                }
                //Get or Generate UUID
                String uuid = execution.getVariable("DCVFMMV_uuid")
                if (uuid == null) {
                    uuid = UUID.randomUUID()
                    logDebug("Generated messageId (UUID) is: " + uuid, isDebugLogEnabled)
                } else {
                    logDebug("Found messageId (UUID) is: " + uuid, isDebugLogEnabled)
                }
                //isVidRequest
                String isVidRequest = execution.getVariable("isVidRequest")
                // default to true
                if (isVidRequest == null || isVidRequest.isEmpty()) {
                    execution.setVariable("isVidRequest", "true")
                }
                //globalSubscriberId
                String globalSubscriberId = execution.getVariable("globalSubscriberId")
                execution.setVariable("DCVFMMV_globalSubscriberId", globalSubscriberId)
                logDebug("globalSubsrciberId: " + globalSubscriberId, isDebugLogEnabled)
                Map<String, String> vfModuleInputParams = execution.getVariable("vfModuleInputParams")
                if (vfModuleInputParams != null) {
                    execution.setVariable("DCVFMMV_vnfParamsMap", vfModuleInputParams)
                    execution.setVariable("DCVFMMV_vnfParamsExistFlag", true)
                }
                //usePreload
                def usePreload = execution.getVariable("usePreload")
                execution.setVariable("DCVFMMV_usePreload", usePreload)
                logDebug("usePreload: " + usePreload, isDebugLogEnabled)

                vnfModelInfo = execution.getVariable("vnfModelInfo")
                utils.log("DEBUG", "VnfModelInfo: " + vnfModelInfo, isDebugLogEnabled)
                def vnfModelName = jsonUtil.getJsonValue(vnfModelInfo, "modelName")
                utils.log("DEBUG", "vnf Model Name: " + vnfModelName, isDebugLogEnabled)
                execution.setVariable("DCVFMMV_vnfModelName", vnfModelName)

                def serviceModelVersion = JsonUtils.getJsonValue(serviceModelInfo, "modelVersion")
                def serviceModelInvariantId = JsonUtils.getJsonValue(serviceModelInfo, "modelInvariantUuid")

                def service = catalogDbUtils.getServiceResourcesByServiceModelInvariantUuidAndServiceModelVersion(execution, serviceModelInvariantId, serviceModelVersion, "v2")
                if (service == null) {
                    exceptionUtil.buildAndThrowWorkflowException(execution, 7000, "no service found for invariantUuid: " + serviceModelInvariantId + " version: " + serviceModelVersion)
                }
                def toscaCsarArtifactUUID = JsonUtils.getJsonValueForKey(service, "toscaCsarArtifactUUID")
                if (isBlank(toscaCsarArtifactUUID)) {
                    exceptionUtil.buildAndThrowWorkflowException(execution, 7000, "toscaCsarArtifactUUID not available")
                }
                execution.setVariable("${PREFIX}toscaCsarArtifactUUID", toscaCsarArtifactUUID)

            } else {
                // The info is inside the request - DEAD CODE
                utils.logAudit("DoCreateVfModule request: " + request)

                //tenantId
                def tenantId = ""
                if (utils.nodeExists(request, "tenant-id")) {
                    tenantId = utils.getNodeText(request, "tenant-id")
                }
                execution.setVariable("DCVFMMV_tenantId", tenantId)
                rollbackData.put("VFMODULE", "tenantid", tenantId)
                //volumeGroupId
                def volumeGroupId = ""
                if (utils.nodeExists(request, "volume-group-id")) {
                    volumeGroupId = utils.getNodeText(request, "volume-group-id")
                }
                execution.setVariable("DCVFMMV_volumeGroupId", volumeGroupId)
                //volumeGroupId
                def volumeGroupName = ""
                if (utils.nodeExists(request, "volume-group-name")) {
                    volumeGroupName = utils.getNodeText(request, "volume-group-name")
                }
                execution.setVariable("DCVFMMV_volumeGroupName", volumeGroupName)
                //cloudSiteId
                def cloudSiteId = ""
                if (utils.nodeExists(request, "aic-cloud-region")) {
                    cloudSiteId = utils.getNodeText(request, "aic-cloud-region")
                }
                execution.setVariable("DCVFMMV_cloudSiteId", cloudSiteId)
                rollbackData.put("VFMODULE", "aiccloudregion", cloudSiteId)
                logDebug("cloudSiteId: " + cloudSiteId, isDebugLogEnabled)
                //vnfType
                def vnfType = ""
                if (utils.nodeExists(request, "vnf-type")) {
                    vnfType = utils.getNodeText(request, "vnf-type")
                }
                execution.setVariable("DCVFMMV_vnfType", vnfType)
                rollbackData.put("VFMODULE", "vnftype", vnfType)
                logDebug("vnfType: " + vnfType, isDebugLogEnabled)
                //vnfName
                def vnfName = ""
                if (utils.nodeExists(request, "vnf-name")) {
                    vnfName = utils.getNodeText(request, "vnf-name")
                }
                execution.setVariable("DCVFMMV_vnfName", vnfName)
                rollbackData.put("VFMODULE", "vnfname", vnfName)
                logDebug("vnfName: " + vnfName, isDebugLogEnabled)
                //vnfId
                def vnfId = ""
                if (utils.nodeExists(request, "vnf-id")) {
                    vnfId = utils.getNodeText(request, "vnf-id")
                }
                execution.setVariable("DCVFMMV_vnfId", vnfId)
                rollbackData.put("VFMODULE", "vnfid", vnfId)
                logDebug("vnfId: " + vnfId, isDebugLogEnabled)
                //vfModuleName
                def vfModuleName = ""
                if (utils.nodeExists(request, "vf-module-name")) {
                    vfModuleName = utils.getNodeText(request, "vf-module-name")
                }
                execution.setVariable("DCVFMMV_vfModuleName", vfModuleName)
                rollbackData.put("VFMODULE", "vfmodulename", vfModuleName)
                logDebug("vfModuleName: " + vfModuleName, isDebugLogEnabled)
                //vfModuleModelName
                def vfModuleModelName = ""
                if (utils.nodeExists(request, "vf-module-model-name")) {
                    vfModuleModelName = utils.getNodeText(request, "vf-module-model-name")
                }
                execution.setVariable("DCVFMMV_vfModuleModelName", vfModuleModelName)
                rollbackData.put("VFMODULE", "vfmodulemodelname", vfModuleModelName)
                logDebug("vfModuleModelName: " + vfModuleModelName, isDebugLogEnabled)
                //modelCustomizationUuid
                def modelCustomizationUuid = ""
                if (utils.nodeExists(request, "model-customization-id")) {
                    modelCustomizationUuid = utils.getNodeText(request, "model-customization-id")
                }
                execution.setVariable("DCVFMMV_modelCustomizationUuid", modelCustomizationUuid)
                rollbackData.put("VFMODULE", "modelcustomizationuuid", modelCustomizationUuid)
                logDebug("modelCustomizationUuid: " + modelCustomizationUuid, isDebugLogEnabled)
                //vfModuleId
                def vfModuleId = ""
                if (utils.nodeExists(request, "vf-module-id")) {
                    vfModuleId = utils.getNodeText(request, "vf-module-id")
                }
                execution.setVariable("DCVFMMV_vfModuleId", vfModuleId)
                logDebug("vfModuleId: " + vfModuleId, isDebugLogEnabled)
                def requestId = ""
                if (utils.nodeExists(request, "request-id")) {
                    requestId = utils.getNodeText(request, "request-id")
                }
                execution.setVariable("DCVFMMV_requestId", requestId)
                logDebug("requestId: " + requestId, isDebugLogEnabled)
                //serviceId
                def serviceId = ""
                if (utils.nodeExists(request, "service-id")) {
                    serviceId = utils.getNodeText(request, "service-id")
                }
                execution.setVariable("DCVFMMV_serviceId", serviceId)
                logDebug("serviceId: " + serviceId, isDebugLogEnabled)
                //serviceInstanceId
                def serviceInstanceId = ""
                if (utils.nodeExists(request, "service-instance-id")) {
                    serviceInstanceId = utils.getNodeText(request, "service-instance-id")
                }
                execution.setVariable("DCVFMMV_serviceInstanceId", serviceInstanceId)
                rollbackData.put("VFMODULE", "serviceInstanceId", serviceInstanceId)
                logDebug("serviceInstanceId: " + serviceInstanceId, isDebugLogEnabled)
                //source
                def source = ""
                if (utils.nodeExists(request, "source")) {
                    source = utils.getNodeText(request, "source")
                }
                execution.setVariable("DCVFMMV_source", source)
                rollbackData.put("VFMODULE", "source", source)
                logDebug("source: " + source, isDebugLogEnabled)
                //backoutOnFailure
                NetworkUtils networkUtils = new NetworkUtils()
                def backoutOnFailure = networkUtils.isRollbackEnabled(execution, request)
                execution.setVariable("DCVFMMV_backoutOnFailure", backoutOnFailure)
                logDebug("backoutOnFailure: " + backoutOnFailure, isDebugLogEnabled)
                //isBaseVfModule
                def isBaseVfModule = "false"
                if (utils.nodeExists(request, "is-base-vf-module")) {
                    isBaseVfModule = utils.getNodeText(request, "is-base-vf-module")
                }
                execution.setVariable("DCVFMMV_isBaseVfModule", isBaseVfModule)
                logDebug("isBaseVfModule: " + isBaseVfModule, isDebugLogEnabled)

                //asdcServiceModelVersion
                def asdcServiceModelVersion = ""
                if (utils.nodeExists(request, "asdc-service-model-version")) {
                    asdcServiceModelVersion = utils.getNodeText(request, "asdc-service-model-version")
                }
                execution.setVariable("DCVFMMV_asdcServiceModelVersion", asdcServiceModelVersion)
                logDebug("asdcServiceModelVersion: " + asdcServiceModelVersion, isDebugLogEnabled)

                //personaModelId
                def personaModelId = ""
                if (utils.nodeExists(request, "persona-model-id")) {
                    personaModelId = utils.getNodeText(request, "persona-model-id")
                }
                execution.setVariable("DCVFMMV_personaModelId", personaModelId)
                logDebug("personaModelId: " + personaModelId, isDebugLogEnabled)

                //personaModelVersion
                def personaModelVersion = ""
                if (utils.nodeExists(request, "persona-model-version")) {
                    personaModelVersion = utils.getNodeText(request, "persona-model-version")
                }
                execution.setVariable("DCVFMMV_personaModelVersion", personaModelVersion)
                logDebug("personaModelVersion: " + personaModelVersion, isDebugLogEnabled)

                // Process the parameters

                String vnfParamsChildNodes = utils.getChildNodes(request, "vnf-params")
                if (vnfParamsChildNodes == null || vnfParamsChildNodes.length() < 1) {
                    utils.log("DEBUG", "Request contains NO VNF Params", isDebugLogEnabled)
                } else {
                    utils.log("DEBUG", "Request does contain VNF Params", isDebugLogEnabled)
                    execution.setVariable("DCVFMMV_vnfParamsExistFlag", true)

                    InputSource xmlSource = new InputSource(new StringReader(request));
                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    docFactory.setNamespaceAware(true)
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder()
                    Document xml = docBuilder.parse(xmlSource)
                    //Get params, build map
                    Map<String, String> paramsMap = new HashMap<String, String>()
                    NodeList paramsList = xml.getElementsByTagNameNS("*", "param")

                    for (int z = 0; z < paramsList.getLength(); z++) {
                        Node node = paramsList.item(z)
                        String paramValue = node.getTextContent()
                        NamedNodeMap e = node.getAttributes()
                        String paramName = e.getNamedItem("name").getTextContent()
                        paramsMap.put(paramName, paramValue)
                    }
                    execution.setVariable("DCVFMMV_vnfParamsMap", paramsMap)
                }
            }

            //Get or Generate UUID
            String uuid = execution.getVariable("DCVFMMV_uuid")
            if (uuid == null) {
                uuid = UUID.randomUUID()
                logDebug("Generated messageId (UUID) is: " + uuid, isDebugLogEnabled)
            } else {
                logDebug("Found messageId (UUID) is: " + uuid, isDebugLogEnabled)
            }
            // Get sdncVersion, default to empty
            String sdncVersion = execution.getVariable("sdncVersion")
            if (sdncVersion == null) {
                sdncVersion = ""
            }
            logDebug("sdncVersion: " + sdncVersion, isDebugLogEnabled)
            execution.setVariable("DCVFMMV_sdncVersion", sdncVersion)

            execution.setVariable("DCVFMMV_uuid", uuid)
            execution.setVariable("DCVFMMV_baseVfModuleId", "")
            execution.setVariable("DCVFMMV_baseVfModuleHeatStackId", "")
            execution.setVariable("DCVFMMV_heatStackId", "")
            execution.setVariable("DCVFMMV_contrailServiceInstanceFqdn", "")
            execution.setVariable("DCVFMMV_volumeGroupStackId", "")
            execution.setVariable("DCVFMMV_cloudRegionForVolume", "")
            execution.setVariable("DCVFMMV_contrailNetworkPolicyFqdnList", "")
            execution.setVariable("DCVFMMV_vnfTypeToQuery", "generic-vnf")
            rollbackData.put("VFMODULE", "rollbackPrepareUpdateVfModule", "false")
            rollbackData.put("VFMODULE", "rollbackUpdateAAIVfModule", "false")
            rollbackData.put("VFMODULE", "rollbackVnfAdapterCreate", "false")
            rollbackData.put("VFMODULE", "rollbackSDNCRequestActivate", "false")
            rollbackData.put("VFMODULE", "rollbackSDNCRequestAssign", "false")
            rollbackData.put("VFMODULE", "rollbackCreateAAIVfModule", "false")
            rollbackData.put("VFMODULE", "rollbackCreateNetworkPoliciesAAI", "false")
            rollbackData.put("VFMODULE", "rollbackUpdateVnfAAI", "false")

            String sdncCallbackUrl = (String) execution.getVariable('URN_mso_workflow_sdncadapter_callback')
            if (sdncCallbackUrl == null || sdncCallbackUrl.trim().isEmpty()) {
                def msg = 'Required variable \'URN_mso_workflow_sdncadapter_callback\' is missing'
                logError(msg)
                exceptionUtil.buildAndThrowWorkflowException(execution, 2000, msg)
            }
            execution.setVariable("DCVFMMV_sdncCallbackUrl", sdncCallbackUrl)
            utils.logAudit("SDNC Callback URL: " + sdncCallbackUrl)
            logDebug("SDNC Callback URL is: " + sdncCallbackUrl, isDebugLogEnabled)


            execution.setVariable("rollbackData", rollbackData)
        } catch (BpmnError b) {
            throw b
        } catch (Exception e) {
            exceptionUtil.buildAndThrowWorkflowException(execution, 2000, "Internal Error encountered in PreProcess method!")
        }
    }

    public void prepareHomingRequest(Execution execution) {
    }

    public void processHomingResponse(Execution execution) {

        def method = getClass().getSimpleName() + '.processHomingResponse(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        try {
            def cloudOwner = execution.getVariable('HAS_cloudOwner');
            def cloudRegionId = execution.getVariable('HAS_cloudRegionId');

            if(isBlank(cloudOwner) || isBlank(cloudRegionId)) {
                exceptionUtil.buildAndThrowWorkflowException(execution, 2000, method + ", incorrect HAS response, cloudOwner = " + cloudOwner + ", cloudRegionId = " + cloudRegionId)
            }

            execution.setVariable(PREFIX + "cloudOwner", cloudOwner) // ATT
            execution.setVariable(PREFIX + "cloudRegionId", cloudRegionId) // EastUS
        } catch (Exception e) {
            logError("Cound not extract auth credentials", e)
            exceptionUtil.buildAndThrowWorkflowException(execution, 2000, method + ", Can not process Homing response, " + e.getMessage())
        }
    }

    public void prepareESRRequest(Execution execution) {
        // TODO: Implement this method later
    }

    public void processESRResponse(Execution execution) {

        def method = getClass().getSimpleName() + '.processESRResponse(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        String esrResponse = execution.getVariable('ESRGETVIM_Response')
        logDebug('processESRResponse : esrResponse = ' + esrResponse , isDebugLogEnabled)

        try {

            def cloudType = utils.getNodeText1(esrResponse, "cloud-type")
            logDebug(method + ', cloudType = ' + cloudType, isDebugLogEnabled)
            execution.setVariable(PREFIX + 'cloudType', cloudType)

            def subscriberId = utils.getNodeText1(esrResponse, "cloud-extra-info")

            InputSource source = new InputSource(new StringReader(esrResponse))
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance()
            docFactory.setNamespaceAware(true)
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder()
            Document outputsXml = docBuilder.parse(source)

            NodeList esrEystemInfoList = outputsXml.getElementsByTagNameNS("*", "esr-system-info-list")
            Element esrSysteInfo = (Element) esrEystemInfoList.item(0)
            String userName = esrSysteInfo.getElementsByTagNameNS("*", "user-name").item(0).getTextContent()
            String password = esrSysteInfo.getElementsByTagNameNS("*", "password").item(0).getTextContent()
            String tenantId = esrSysteInfo.getElementsByTagNameNS("*", "default-tenant").item(0).getTextContent()

            logDebug(method + ', subscriberId = ' + subscriberId, isDebugLogEnabled)
            logDebug(method + ', userName = ' + userName, isDebugLogEnabled)
            logDebug(method + ', password = ' + password, isDebugLogEnabled)

            execution.setVariable(PREFIX + 'subscriberId', subscriberId)
            execution.setVariable(PREFIX + 'clientId', userName)
            execution.setVariable(PREFIX + 'clientSecret', password)
            execution.setVariable(PREFIX + 'tenantId', tenantId)

        } catch (Exception e) {
            logError("Cound not extract auth credentials", e)
            exceptionUtil.buildAndThrowWorkflowException(execution, 2000, method + ", Could not extract auth credentials, " + e.getMessage())
        }

    }

    /**
     * Validates a workflow response.
     * @param execution the execution
     * @param responseVar the execution variable in which the response is stored
     * @param responseCodeVar the execution variable in which the response code is stored
     * @param errorResponseVar the execution variable in which the error response is stored
     */
    public void validateWorkflowResponse(Execution execution, String responseVar,
                                         String responseCodeVar, String errorResponseVar) {
        SDNCAdapterUtils sdncAdapterUtils = new SDNCAdapterUtils(this)
        sdncAdapterUtils.validateSDNCResponse(execution, responseVar, responseCodeVar, errorResponseVar)
    }

    /**
     * Sends the empty, synchronous response back to the API Handler.
     * @param execution the execution
     */
    public void sendResponse(Execution execution) {
        def method = getClass().getSimpleName() + '.sendResponse(' +
                'execution=' + execution.getId() +
                ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        try {
            sendWorkflowResponse(execution, 200, "")
            logDebug('Exited ' + method, isDebugLogEnabled)
        } catch (BpmnError e) {
            throw e;
        } catch (Exception e) {
            logError('Caught exception in ' + method, e)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, 'Internal Error')
        }
    }

    /**
     * Using the received vnfId and vfModuleId, query AAI to get the corresponding VNF info.
     * A 200 response is expected with the VNF info in the response body. Will find out the base module info
     * and existing VNF's name for add-on modules
     *
     * @param execution The flow's execution instance.
     */
    public void postProcessCreateAAIVfModule(Execution execution) {
        def method = getClass().getSimpleName() + '.getVfModule(' +
                'execution=' + execution.getId() +
                ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        try {
            def createResponse = execution.getVariable('DCVFMMV_createVfModuleResponse')
            utils.logAudit("createVfModule Response: " + createResponse)

            def rollbackData = execution.getVariable("rollbackData")
            String vnfName = utils.getNodeText1(createResponse, 'vnf-name')
            if (vnfName != null) {
                execution.setVariable('DCVFMMV_vnfName', vnfName)
                logDebug("vnfName retrieved from AAI is: " + vnfName, isDebugLogEnabled)
                rollbackData.put("VFMODULE", "vnfname", vnfName)
            }
            String vnfId = utils.getNodeText1(createResponse, 'vnf-id')
            execution.setVariable('DCVFMMV_vnfId', vnfId)
            logDebug("vnfId is: " + vnfId, isDebugLogEnabled)
            String vfModuleId = utils.getNodeText1(createResponse, 'vf-module-id')
            execution.setVariable('DCVFMMV_vfModuleId', vfModuleId)
            logDebug("vfModuleId is: " + vfModuleId, isDebugLogEnabled)
            String vfModuleIndex = utils.getNodeText1(createResponse, 'vf-module-index')
            execution.setVariable('DCVFMMV_vfModuleIndex', vfModuleIndex)
            logDebug("vfModuleIndex is: " + vfModuleIndex, isDebugLogEnabled)
            rollbackData.put("VFMODULE", "vnfid", vnfId)
            rollbackData.put("VFMODULE", "vfmoduleid", vfModuleId)
            rollbackData.put("VFMODULE", "rollbackCreateAAIVfModule", "true")
            rollbackData.put("VFMODULE", "rollbackPrepareUpdateVfModule", "true")
            execution.setVariable("rollbackData", rollbackData)
        } catch (Exception ex) {
            ex.printStackTrace()
            logDebug('Exception occurred while postProcessing CreateAAIVfModule request:' + ex.getMessage(), isDebugLogEnabled)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, 'Bad response from CreateAAIVfModule' + ex.getMessage())
        }
        logDebug('Exited ' + method, isDebugLogEnabled)
    }

    /**
     * Using the received vnfId and vfModuleId, query AAI to get the corresponding VNF info.
     * A 200 response is expected with the VNF info in the response body. Will find out the base module info.
     *
     * @param execution The flow's execution instance.
     */
    public void queryAAIVfModule(Execution execution) {
        def isDebugLogEnabled = execution.getVariable("isDebugLogEnabled")
        def method = getClass().getSimpleName() + '.getVfModule(' +
                'execution=' + execution.getId() +
                ')'
        logDebug('Entered ' + method, isDebugLogEnabled)

        try {
            def vnfId = execution.getVariable('DCVFMMV_vnfId')
            def vfModuleId = execution.getVariable('DCVFMMV_vfModuleId')

            AaiUtil aaiUriUtil = new AaiUtil(this)
            String aai_uri = aaiUriUtil.getNetworkGenericVnfUri(execution)
            logDebug('AAI URI is: ' + aai_uri, isDebugLogEnabled)

            String endPoint = execution.getVariable("URN_aai_endpoint") + "${aai_uri}/" + UriUtils.encode(vnfId, "UTF-8") + "?depth=1"
            utils.logAudit("AAI endPoint: " + endPoint)

            try {
                RESTConfig config = new RESTConfig(endPoint);
                def responseData = ''
                def aaiRequestId = UUID.randomUUID().toString()
                RESTClient client = new RESTClient(config).
                        addHeader('X-TransactionId', aaiRequestId).
                        addHeader('X-FromAppId', 'MSO').
                        addHeader('Content-Type', 'application/xml').
                        addHeader('Accept', 'application/xml');
                logDebug('sending GET to AAI endpoint \'' + endPoint + '\'', isDebugLogEnabled)
                APIResponse response = client.httpGet()
                utils.logAudit("createVfModule - invoking httpGet() to AAI")

                responseData = response.getResponseBodyAsString()
                if (responseData != null) {
                    logDebug("Received generic VNF data: " + responseData, isDebugLogEnabled)

                }

                utils.logAudit("createVfModule - queryAAIVfModule Response: " + responseData)
                utils.logAudit("createVfModule - queryAAIVfModule ResponseCode: " + response.getStatusCode())

                execution.setVariable('DCVFMMV_queryAAIVfModuleResponseCode', response.getStatusCode())
                execution.setVariable('DCVFMMV_queryAAIVfModuleResponse', responseData)
                logDebug('Response code:' + response.getStatusCode(), isDebugLogEnabled)
                logDebug('Response:' + System.lineSeparator() + responseData, isDebugLogEnabled)
                if (response.getStatusCode() == 200) {
                    // Parse the VNF record from A&AI to find base module info
                    logDebug('Parsing the VNF data to find base module info', isDebugLogEnabled)
                    if (responseData != null) {
                        def vfModulesText = utils.getNodeXml(responseData, "vf-modules")
                        def xmlVfModules = new XmlSlurper().parseText(vfModulesText)
                        def vfModules = xmlVfModules.'**'.findAll { it.name() == "vf-module" }
                        int vfModulesSize = 0
                        for (i in 0..vfModules.size() - 1) {
                            def vfModuleXml = groovy.xml.XmlUtil.serialize(vfModules[i])
                            def isBaseVfModule = utils.getNodeText(vfModuleXml, "is-base-vf-module")

                            if (isBaseVfModule == "true") {
                                String baseModuleId = utils.getNodeText1(vfModuleXml, "vf-module-id")
                                execution.setVariable("DCVFMMV_baseVfModuleId", baseModuleId)
                                logDebug('Received baseVfModuleId: ' + baseModuleId, isDebugLogEnabled)
                                String baseModuleHeatStackId = utils.getNodeText1(vfModuleXml, "heat-stack-id")
                                execution.setVariable("DCVFMMV_baseVfModuleHeatStackId", baseModuleHeatStackId)
                                logDebug('Received baseVfModuleHeatStackId: ' + baseModuleHeatStackId, isDebugLogEnabled)
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace()
                logDebug('Exception occurred while executing AAI GET:' + ex.getMessage(), isDebugLogEnabled)
                exceptionUtil.buildAndThrowWorkflowException(execution, 1002, 'AAI GET Failed:' + ex.getMessage())
            }
            logDebug('Exited ' + method, isDebugLogEnabled)
        } catch (BpmnError e) {
            throw e;
        } catch (Exception e) {
            logError('Caught exception in ' + method, e)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, 'Error in queryAAIVfModule(): ' + e.getMessage())
        }
    }


    public void preProcessSDNCAssignRequest(Execution execution) {
        def isDebugLogEnabled = execution.getVariable("isDebugLogEnabled")
        execution.setVariable("prefix", PREFIX)
        logDebug(" ======== STARTED preProcessSDNCAssignRequest ======== ", isDebugLogEnabled)
        def vnfId = execution.getVariable("DCVFMMV_vnfId")
        def vfModuleId = execution.getVariable("DCVFMMV_vfModuleId")
        def serviceInstanceId = execution.getVariable("DCVFMMV_serviceInstanceId")
        logDebug("NEW VNF ID: " + vnfId, isDebugLogEnabled)
        utils.logAudit("NEW VNF ID: " + vnfId)

        try {

            //Build SDNC Request

            def svcInstId = ""
            if (serviceInstanceId == null || serviceInstanceId.isEmpty()) {
                svcInstId = vfModuleId
            } else {
                svcInstId = serviceInstanceId
            }

            String assignSDNCRequest = buildSDNCRequest(execution, svcInstId, "assign")

            assignSDNCRequest = utils.formatXml(assignSDNCRequest)
            execution.setVariable("DCVFMMV_assignSDNCRequest", assignSDNCRequest)
            logDebug("Outgoing AssignSDNCRequest is: \n" + assignSDNCRequest, isDebugLogEnabled)
            utils.logAudit("Outgoing AssignSDNCRequest is: \n" + assignSDNCRequest)

        } catch (Exception e) {
            utils.log("ERROR", "Exception Occured Processing preProcessSDNCAssignRequest. Exception is:\n" + e, isDebugLogEnabled)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, "Error Occurred during prepareProvision Method:\n" + e.getMessage())
        }
        logDebug("======== COMPLETED preProcessSDNCAssignRequest ======== ", isDebugLogEnabled)
    }

    public void preProcessSDNCGetRequest(Execution execution, String element) {
        def isDebugLogEnabled = execution.getVariable("isDebugLogEnabled")
        String sdncVersion = execution.getVariable("DCVFMMV_sdncVersion")
        execution.setVariable("prefix", PREFIX)
        utils.log("DEBUG", " ======== STARTED preProcessSDNCGetRequest Process ======== ", isDebugLogEnabled)
        try {
            def serviceInstanceId = execution.getVariable('DCVFMMV_serviceInstanceId')

            String uuid = execution.getVariable('testReqId') // for junits
            if (uuid == null) {
                uuid = execution.getVariable("mso-request-id") + "-" + System.currentTimeMillis()
            }

            def callbackUrl = execution.getVariable("DCVFMMV_sdncCallbackUrl")
            utils.logAudit("callbackUrl:" + callbackUrl)

            def vfModuleId = execution.getVariable('DCVFMMV_vfModuleId')

            def svcInstId = ""
            if (serviceInstanceId == null || serviceInstanceId.isEmpty()) {
                svcInstId = vfModuleId
            } else {
                svcInstId = serviceInstanceId
            }

            def msoAction = ""
            if (!sdncVersion.equals("1707")) {
                msoAction = "mobility"
            } else {
                msoAction = "vfmodule"
            }
            // For VNF, serviceOperation (URI for topology GET) will be retrieved from "selflink" element
            // in the response from GenericGetVnf
            // For VF Module, in 1707 serviceOperation will be retrieved from "object-path" element
            // in SDNC Assign Response
            // For VF Module for older versions, serviceOperation is constructed using vfModuleId

            String serviceOperation = ""
            if (element.equals("vnf")) {
                def vnfQueryResponse = execution.getVariable("DCVFMMV_vnfQueryResponse")
                serviceOperation = utils.getNodeText1(vnfQueryResponse, "selflink")
                utils.log("DEBUG", "VNF - service operation: " + serviceOperation, isDebugLogEnabled)
            } else if (element.equals("vfmodule")) {
                String response = execution.getVariable("DCVFMMV_assignSDNCAdapterResponse")
                utils.logAudit("DCVFMMV_assignSDNCAdapterResponse is: \n" + response)

                if (!sdncVersion.equals("1707")) {
                    serviceOperation = "/VNF-API:vnfs/vnf-list/" + vfModuleId
                    utils.log("DEBUG", "VF Module with sdncVersion before 1707 - service operation: " + serviceOperation, isDebugLogEnabled)
                } else {
                    String data = utils.getNodeXml(response, "response-data")
                    data = data.replaceAll("&lt;", "<")
                    data = data.replaceAll("&gt;", ">")
                    utils.log("DEBUG", "responseData: " + data, isDebugLogEnabled)
                    serviceOperation = utils.getNodeText1(data, "object-path")
                    utils.log("DEBUG", "VF Module with sdncVersion of 1707 - service operation: " + serviceOperation, isDebugLogEnabled)
                }
            }

            //!!!! TEMPORARY WORKAROUND FOR SDNC REPLICATION ISSUE
            sleep(5000)

            String SDNCGetRequest =
                    """<sdncadapterworkflow:SDNCAdapterWorkflowRequest xmlns:ns5="http://org.openecomp/mso/request/types/v1"
                                            xmlns:sdncadapterworkflow="http://org.openecomp/mso/workflow/schema/v1"
                                            xmlns:sdncadapter="http://org.openecomp/workflow/sdnc/adapter/schema/v1">
                    <sdncadapter:RequestHeader>
                    <sdncadapter:RequestId>${uuid}</sdncadapter:RequestId>
                    <sdncadapter:SvcInstanceId>${svcInstId}</sdncadapter:SvcInstanceId>
                    <sdncadapter:SvcAction>query</sdncadapter:SvcAction>
                    <sdncadapter:SvcOperation>${serviceOperation}</sdncadapter:SvcOperation>
                    <sdncadapter:CallbackUrl>${callbackUrl}</sdncadapter:CallbackUrl>
                    <sdncadapter:MsoAction>${msoAction}</sdncadapter:MsoAction>
                </sdncadapter:RequestHeader>
                    <sdncadapterworkflow:SDNCRequestData></sdncadapterworkflow:SDNCRequestData>
                </sdncadapterworkflow:SDNCAdapterWorkflowRequest>"""

            utils.logAudit("SDNCGetRequest: \n" + SDNCGetRequest)
            execution.setVariable("DCVFMMV_getSDNCRequest", SDNCGetRequest)
            utils.log("DEBUG", "Outgoing GetSDNCRequest is: \n" + SDNCGetRequest, isDebugLogEnabled)

        } catch (Exception e) {
            utils.log("ERROR", "Exception Occurred Processing preProcessSDNCGetRequest. Exception is:\n" + e, isDebugLogEnabled)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, "Error Occured during prepareProvision Method:\n" + e.getMessage())
        }
        utils.log("DEBUG", "======== COMPLETED preProcessSDNCGetRequest Process ======== ", isDebugLogEnabled)
    }


    public void preProcessMVAdapterRequest(Execution execution) {
        def method = getClass().getSimpleName() + '.VNFAdapterCreateVfModule(' +
                'execution=' + execution.getId() +
                ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        //Get variables
        def cloudSiteId = execution.getVariable("DCVFMMV_cloudSiteId")
        def tenantId = execution.getVariable("DCVFMMV_tenantId")
        def vnfType = execution.getVariable("DCVFMMV_vnfType")
        def vnfName = execution.getVariable("DCVFMMV_vnfName")
        def vnfId = execution.getVariable("DCVFMMV_vnfId")
        def vfModuleName = execution.getVariable("DCVFMMV_vfModuleName")
        def vfModuleModelName = execution.getVariable("DCVFMMV_vfModuleModelName")
        def vfModuleId = execution.getVariable("DCVFMMV_vfModuleId")
        def vfModuleIndex = execution.getVariable("DCVFMMV_vfModuleIndex")
        def requestId = execution.getVariable("DCVFMMV_requestId")
        def serviceId = execution.getVariable("DCVFMMV_serviceId")
        def serviceInstanceId = execution.getVariable("DCVFMMV_serviceInstanceId")
        def backoutOnFailure = execution.getVariable("DCVFMMV_backoutOnFailure")
        def volumeGroupId = execution.getVariable("DCVFMMV_volumeGroupId")
        def baseVfModuleId = execution.getVariable("DCVFMMV_baseVfModuleId")
        def baseVfModuleStackId = execution.getVariable("DCVFMMV_baseVfModuleHeatStackId")
        def asdcServiceModelVersion = execution.getVariable("DCVFMMV_asdcServiceModelVersion")
        def volumeGroupStackId = execution.getVariable("DCVFMMV_volumeGroupStackId")
        def modelCustomizationUuid = execution.getVariable("DCVFMMV_modelCustomizationUuid")
        def subscriberId = execution.getVariable("${PREFIX}subscriberId")
        def clientId = execution.getVariable("${PREFIX}clientId")
        def clientSecret = execution.getVariable("${PREFIX}clientSecret")

        Map<String, String> vnfParamsMap = execution.getVariable("DCVFMMV_vnfParamsMap")
        Map<String, Object> vfModuleParams
        //Get SDNC Response Data for VF Module Topology
        String vfModuleSdncGetResponse = execution.getVariable('DCVFMMV_getSDNCAdapterResponse')
        utils.logAudit("sdncGetResponse: " + vfModuleSdncGetResponse)
        def sdncVersion = execution.getVariable("sdncVersion")

        if (!sdncVersion.equals("1707")) {

            vfModuleParams = buildVfModuleParam(vnfParamsMap, vfModuleSdncGetResponse, vnfId, vnfName,
                    vfModuleId, vfModuleName, vfModuleIndex)
        } else {
            //Get SDNC Response Data for Vnf Topology
            String vnfSdncGetResponse = execution.getVariable('DCVFMMV_getVnfSDNCAdapterResponse')
            utils.logAudit("vnfSdncGetResponse: " + vnfSdncGetResponse)

            vfModuleParams = buildVfModuleParamFromCombinedTopologies(vnfParamsMap, vnfSdncGetResponse, vfModuleSdncGetResponse, vnfId, vnfName,
                    vfModuleId, vfModuleName, vfModuleIndex)
        }

        vfModuleParams.put("subscription_id", subscriberId)
        vfModuleParams.put("tenant_id", tenantId)
        vfModuleParams.put("client_id", clientId)
        vfModuleParams.put("client_secret", clientSecret)

        def svcInstId = isBlank(serviceInstanceId) ? serviceId : serviceInstanceId
        execution.setVariable("DCVFMV_serviceInstanceId", svcInstId)
        execution.setVariable("mso-service-instance-id", svcInstId)

        execution.setVariable("DCVFMMV_vnfParams", vfModuleParams)
    }

    /**
     * Validates the request, request id and service instance id.  If a problem is found,
     * a WorkflowException is generated and an MSOWorkflowException event is thrown. This
     * method also sets up the log context for the workflow.
     * @param execution the execution
     * @return the validated request
     */
    public String validateInfraRequest(Execution execution) {
        def method = getClass().getSimpleName() + '.validateInfraRequest(' +
                'execution=' + execution.getId() +
                ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        String processKey = getProcessKey(execution);
        def prefix = execution.getVariable("prefix")

        if (prefix == null) {
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, processKey + " prefix is null")
        }

        try {
            def request = execution.getVariable(prefix + 'Request')

            if (request == null) {
                request = execution.getVariable(processKey + 'Request')

                if (request == null) {
                    request = execution.getVariable('bpmnRequest')
                }

                setVariable(execution, processKey + 'Request', null);
                setVariable(execution, 'bpmnRequest', null);
                setVariable(execution, prefix + 'Request', request);
            }

            if (request == null) {
                exceptionUtil.buildAndThrowWorkflowException(execution, 1002, processKey + " request is null")
            }
            utils.logAudit("DoCreateVfModule Request: " + request)

            /*

            def requestId = execution.getVariable("mso-request-id")

            if (requestId == null) {
                exceptionUtil.buildAndThrowWorkflowException(execution, 1002, processKey + " request has no mso-request-id")
            }

            def serviceInstanceId = execution.getVariable("mso-service-instance-id")

            if (serviceInstanceId == null) {
                exceptionUtil.buildAndThrowWorkflowException(execution, 1002, processKey + " request message has no mso-service-instance-id")
            }

            utils.logContext(requestId, serviceInstanceId)
            */
            logDebug('Incoming message: ' + System.lineSeparator() + request, isDebugLogEnabled)
            logDebug('Exited ' + method, isDebugLogEnabled)
            return request
        } catch (BpmnError e) {
            throw e;
        } catch (Exception e) {
            logError('Caught exception in ' + method, e)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, "Invalid Message")
        }
    }

    public boolean isVolumeGroupIdPresent(Execution execution) {

        def method = getClass().getSimpleName() + '.isVolumeGroupIdPresent(' +
                'execution=' + execution.getId() +
                ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        def request = execution.getVariable('DoCreateVfModuleRequest')
        String volumeGroupId = utils.getNodeText1(request, "volume-group-id")
        if (volumeGroupId == null || volumeGroupId.isEmpty()) {
            logDebug('No volume group id is present', isDebugLogEnabled)
            return false
        } else {
            logDebug('Volume group id is present', isDebugLogEnabled)
            return true
        }

    }

    public boolean isVolumeGroupNamePresent(Execution execution) {

        def method = getClass().getSimpleName() + '.isVolumeGroupNamePresent(' +
                'execution=' + execution.getId() +
                ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        def request = execution.getVariable('DoCreateVfModuleRequest')
        String volumeGroupName = utils.getNodeText1(request, "volume-group-name")
        if (volumeGroupName == null || volumeGroupName.isEmpty()) {
            logDebug('No volume group name is present', isDebugLogEnabled)
            return false
        } else {
            logDebug('Volume group name is present', isDebugLogEnabled)
            return true
        }

    }

    public String buildSDNCRequest(Execution execution, String svcInstId, String action) {

        String uuid = execution.getVariable('testReqId') // for junits
        if (uuid == null) {
            uuid = execution.getVariable("mso-request-id") + "-" + System.currentTimeMillis()
        }
        def callbackURL = execution.getVariable("DCVFMMV_sdncCallbackUrl")
        def requestId = execution.getVariable("DCVFMMV_requestId")
        def serviceId = execution.getVariable("DCVFMMV_serviceId")
        def vnfType = execution.getVariable("DCVFMMV_vnfType")
        def vnfName = execution.getVariable("DCVFMMV_vnfName")
        def tenantId = execution.getVariable("DCVFMMV_tenantId")
        def source = execution.getVariable("DCVFMMV_source")
        def backoutOnFailure = execution.getVariable("DCVFMMV_backoutOnFailure")
        def vfModuleId = execution.getVariable("DCVFMMV_vfModuleId")
        def vfModuleName = execution.getVariable("DCVFMMV_vfModuleName")
        def vfModuleModelName = execution.getVariable("DCVFMMV_vfModuleModelName")
        def vnfId = execution.getVariable("DCVFMMV_vnfId")
        def cloudSiteId = execution.getVariable("DCVFMMV_cloudSiteId")
        def sdncVersion = execution.getVariable("DCVFMMV_sdncVersion")
        def serviceModelInfo = execution.getVariable("serviceModelInfo")
        def vnfModelInfo = execution.getVariable("vnfModelInfo")
        def vfModuleModelInfo = execution.getVariable("vfModuleModelInfo")
        String serviceEcompModelInformation = sdncAdapterUtils.modelInfoToEcompModelInformation(serviceModelInfo)
        String vnfEcompModelInformation = sdncAdapterUtils.modelInfoToEcompModelInformation(vnfModelInfo)
        String vfModuleEcompModelInformation = sdncAdapterUtils.modelInfoToEcompModelInformation(vfModuleModelInfo)
        def globalSubscriberId = execution.getVariable("DCVFMMV_globalSubscriberId")
        boolean usePreload = execution.getVariable("DCVFMMV_usePreload")
        String usePreloadToSDNC = usePreload ? "Y" : "N"
        def modelCustomizationUuid = execution.getVariable("DCVFMMV_modelCustomizationUuid")
        def modelCustomizationUuidString = ""
        if (!usePreload) {
            modelCustomizationUuidString = "<model-customization-uuid>" + modelCustomizationUuid + "</model-customization-uuid>"
        }

        String sdncVNFParamsXml = ""

        if (execution.getVariable("DCVFMMV_vnfParamsExistFlag") == true) {
            sdncVNFParamsXml = buildSDNCParamsXml(execution)
        } else {
            sdncVNFParamsXml = ""
        }

        String sdncRequest = ""

        if (!sdncVersion.equals("1707")) {

            sdncRequest =
                    """<sdncadapterworkflow:SDNCAdapterWorkflowRequest xmlns:ns5="http://org.openecomp/mso/request/types/v1"
                                                    xmlns:sdncadapterworkflow="http://org.openecomp/mso/workflow/schema/v1"
                                                    xmlns:sdncadapter="http://org.openecomp/workflow/sdnc/adapter/schema/v1">
       <sdncadapter:RequestHeader>
                <sdncadapter:RequestId>${uuid}</sdncadapter:RequestId>
                <sdncadapter:SvcInstanceId>${svcInstId}</sdncadapter:SvcInstanceId>
                <sdncadapter:SvcAction>${action}</sdncadapter:SvcAction>
                <sdncadapter:SvcOperation>vnf-topology-operation</sdncadapter:SvcOperation>
                <sdncadapter:CallbackUrl>${callbackURL}</sdncadapter:CallbackUrl>
        </sdncadapter:RequestHeader>
    <sdncadapterworkflow:SDNCRequestData>
        <request-information>
            <request-id>${requestId}</request-id>
            <request-action>VNFActivateRequest</request-action>
            <source>${source}</source>
            <notification-url/>
        </request-information>
        <service-information>
            <service-id>${serviceId}</service-id>
            <service-type>${serviceId}</service-type>
            <service-instance-id>${svcInstId}</service-instance-id>
            <subscriber-name>notsurewecare</subscriber-name>
        </service-information>
        <vnf-request-information>
            <vnf-id>${vfModuleId}</vnf-id>
            <vnf-type>${vfModuleModelName}</vnf-type>
            <vnf-name>${vfModuleName}</vnf-name>
            <generic-vnf-id>${vnfId}</generic-vnf-id>
            <generic-vnf-name>${vnfName}</generic-vnf-name>
            <generic-vnf-type>${vnfType}</generic-vnf-type>
            <aic-cloud-region>${cloudSiteId}</aic-cloud-region>
            <tenant>${tenantId}</tenant>
            ${modelCustomizationUuidString}
            <use-preload>${usePreloadToSDNC}</use-preload>
        ${sdncVNFParamsXml}
        </vnf-request-information>
    </sdncadapterworkflow:SDNCRequestData>
    </sdncadapterworkflow:SDNCAdapterWorkflowRequest>"""

        } else {

            sdncRequest =
                    """<sdncadapterworkflow:SDNCAdapterWorkflowRequest xmlns:ns5="http://org.openecomp/mso/request/types/v1"
                                                    xmlns:sdncadapterworkflow="http://org.openecomp/mso/workflow/schema/v1"
                                                    xmlns:sdncadapter="http://org.openecomp/workflow/sdnc/adapter/schema/v1">
       <sdncadapter:RequestHeader>
                <sdncadapter:RequestId>${uuid}</sdncadapter:RequestId>
                <sdncadapter:SvcInstanceId>${svcInstId}</sdncadapter:SvcInstanceId>
                <sdncadapter:SvcAction>${action}</sdncadapter:SvcAction>
                <sdncadapter:SvcOperation>vf-module-topology-operation</sdncadapter:SvcOperation>
                <sdncadapter:CallbackUrl>${callbackURL}</sdncadapter:CallbackUrl>
                <sdncadapter:MsoAction>generic-resource</sdncadapter:MsoAction>
        </sdncadapter:RequestHeader>
    <sdncadapterworkflow:SDNCRequestData>
        <request-information>
            <request-id>${requestId}</request-id>
            <request-action>CreateVfModuleInstance</request-action>
            <source>${source}</source>
            <notification-url/>
        </request-information>
        <service-information>
            <service-id>${serviceId}</service-id>
            <subscription-service-type>${serviceId}</subscription-service-type>
            ${serviceEcompModelInformation}
            <service-instance-id>${svcInstId}</service-instance-id>
            <global-customer-id>${globalSubscriberId}</global-customer-id>            
        </service-information>        
        <vnf-information>
            <vnf-id>${vnfId}</vnf-id>
            <vnf-type>${vnfType}</vnf-type>
            ${vnfEcompModelInformation}            
        </vnf-information>
        <vf-module-information>
            <vf-module-id>${vfModuleId}</vf-module-id>
            <vf-module-type>${vfModuleModelName}</vf-module-type>
            ${vfModuleEcompModelInformation}            
        </vf-module-information>
        <vf-module-request-input>            
            <vf-module-name>${vfModuleName}</vf-module-name>
            <tenant>${tenantId}</tenant>
            <aic-cloud-region>${cloudSiteId}</aic-cloud-region>            
        ${sdncVNFParamsXml}
        </vf-module-request-input>
      </sdncadapterworkflow:SDNCRequestData>
    </sdncadapterworkflow:SDNCAdapterWorkflowRequest>"""

            /*
            sdncRequest =
            """<sdncadapterworkflow:SDNCAdapterWorkflowRequest xmlns:ns5="http://org.openecomp/mso/request/types/v1"
                                                    xmlns:sdncadapterworkflow="http://org.openecomp/mso/workflow/schema/v1"
                                                    xmlns:sdncadapter="http://org.openecomp/workflow/sdnc/adapter/schema/v1">
       <sdncadapter:RequestHeader>
                <sdncadapter:RequestId>${requestId}</sdncadapter:RequestId>
                <sdncadapter:SvcInstanceId>${svcInstId}</sdncadapter:SvcInstanceId>
                <sdncadapter:SvcAction>${action}</sdncadapter:SvcAction>
                <sdncadapter:SvcOperation>vnf-topology-operation</sdncadapter:SvcOperation>
                <sdncadapter:CallbackUrl>${callbackURL}</sdncadapter:CallbackUrl>
        </sdncadapter:RequestHeader>
    <sdncadapterworkflow:SDNCRequestData>
        <request-information>
            <request-id>${requestId}</request-id>
            <request-action>CreateVfModuleInstance</request-action>
            <source>${source}</source>
            <notification-url/>
        </request-information>
        <service-information>
            <service-id>${serviceId}</service-id>
            <service-type>${serviceId}</service-type>
            ${serviceEcompModelInformation}
            <service-instance-id>${svcInstId}</service-instance-id>
            <global-customer-id>${globalSubscriberId}</global-customer-id>
        </service-information>
        <vnf-information>
            <vnf-id>${vnfId}</vnf-id>
            <vnf-type>${vnfType}</vnf-type>
            ${vnfEcompModelInformation}
        </vnf-information>
        <vf-module-information>
            <vf-module-id>${vfModuleId}</vf-module-id>
            <vf-module-type>${vfModuleModelName}</vf-module-type>
            ${vfModuleEcompModelInformation}
        </vf-module-information>
        <vf-module-request-input>
            <vf-module-name>${vfModuleName}</vf-module-name>
            <tenant>${tenantId}</tenant>
            <aic-cloud-region>${cloudSiteId}</aic-cloud-region>
        ${sdncVNFParamsXml}
        </vf-module-request-input>
    </sdncadapterworkflow:SDNCRequestData>
    </sdncadapterworkflow:SDNCAdapterWorkflowRequest>"""
            */

        }

        utils.logAudit("sdncRequest:  " + sdncRequest)
        return sdncRequest

    }

    public void preProcessSDNCActivateRequest(Execution execution) {
        def method = getClass().getSimpleName() + '.preProcessSDNCActivateRequest(' +
                'execution=' + execution.getId() +
                ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)
        execution.setVariable("prefix", PREFIX)
        logDebug(" ======== STARTED preProcessSDNCActivateRequest Process ======== ", isDebugLogEnabled)
        try {
            String vnfId = execution.getVariable("DCVFMMV_vnfId")
            String vfModuleId = execution.getVariable("DCVFMMV_vfModuleId")
            String serviceInstanceId = execution.getVariable("DCVFMMV_serviceInstanceId")

            def svcInstId = ""
            if (serviceInstanceId == null || serviceInstanceId.isEmpty()) {
                svcInstId = vfModuleId
            } else {
                svcInstId = serviceInstanceId
            }
            String activateSDNCRequest = buildSDNCRequest(execution, svcInstId, "activate")

            execution.setVariable("DCVFMMV_activateSDNCRequest", activateSDNCRequest)
            logDebug("Outgoing CommitSDNCRequest is: \n" + activateSDNCRequest, isDebugLogEnabled)
            utils.logAudit("Outgoing CommitSDNCRequest is: \n" + activateSDNCRequest)

        } catch (Exception e) {
            log.debug("Exception Occured Processing preProcessSDNCActivateRequest. Exception is:\n" + e, isDebugLogEnabled)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, "Error Occured during  preProcessSDNCActivateRequest Method:\n" + e.getMessage())
        }
        logDebug("======== COMPLETED  preProcessSDNCActivateRequest Process ======== ", isDebugLogEnabled)
    }

    public void postProcessVNFAdapterRequest(Execution execution) {
        def method = getClass().getSimpleName() + '.postProcessVNFAdapterRequest(' + 'execution=' + execution.getId() + ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)
        execution.setVariable("prefix", PREFIX)

        try {
            logDebug(" *** STARTED postProcessVNFAdapterRequest Process*** ", isDebugLogEnabled)

            String vnfResponse = execution.getVariable("${PREFIX}createMVAResponse")
            logDebug("VNF Adapter Response is: " + vnfResponse, isDebugLogEnabled)
            utils.logAudit("createMVAResponse is: \n" + vnfResponse)

            RollbackData rollbackData = execution.getVariable("rollbackData")
            if (vnfResponse != null) {

                if (vnfResponse.contains("CreateVfModuleMultiVimResponse")) {
                    logDebug("Received a Good Response from VNF Adapter for CREATE_VF_MODULE Call.", isDebugLogEnabled)
                    execution.setVariable("DCVFMMV_vnfVfModuleCreateCompleted", true)
                    String heatStackId = utils.getNodeText1(vnfResponse, "vfModuleStackId")
                    execution.setVariable("DCVFMMV_heatStackId", heatStackId)
                    logDebug("Received heat stack id from VNF Adapter: " + heatStackId, isDebugLogEnabled)
                    rollbackData.put("VFMODULE", "heatstackid", heatStackId)
                    // Parse vnfOutputs for network_fqdn
                    if (vnfResponse.contains("vfModuleOutputs")) {
                        def vfModuleOutputsXml = utils.getNodeXml(vnfResponse, "vfModuleOutputs")
                        InputSource source = new InputSource(new StringReader(vfModuleOutputsXml));
                        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                        docFactory.setNamespaceAware(true)
                        DocumentBuilder docBuilder = docFactory.newDocumentBuilder()
                        Document outputsXml = docBuilder.parse(source)

                        NodeList entries = outputsXml.getElementsByTagNameNS("*", "entry")
                        List contrailNetworkPolicyFqdnList = []
                        for (int i = 0; i < entries.getLength(); i++) {
                            Node node = entries.item(i)
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element) node
                                String key = element.getElementsByTagNameNS("*", "key").item(0).getTextContent()
                                if (key.equals("contrail-service-instance-fqdn")) {
                                    String contrailServiceInstanceFqdn = element.getElementsByTagNameNS("*", "value").item(0).getTextContent()
                                    logDebug("Obtained contrailServiceInstanceFqdn: " + contrailServiceInstanceFqdn, isDebugLogEnabled)
                                    execution.setVariable("DCVFMMV_contrailServiceInstanceFqdn", contrailServiceInstanceFqdn)
                                } else if (key.endsWith("contrail_network_policy_fqdn")) {
                                    String contrailNetworkPolicyFqdn = element.getElementsByTagNameNS("*", "value").item(0).getTextContent()
                                    logDebug("Obtained contrailNetworkPolicyFqdn: " + contrailNetworkPolicyFqdn, isDebugLogEnabled)
                                    contrailNetworkPolicyFqdnList.add(contrailNetworkPolicyFqdn)
                                } else if (key.equals("oam_management_v4_address")) {
                                    String oamManagementV4Address = element.getElementsByTagNameNS("*", "value").item(0).getTextContent()
                                    logDebug("Obtained oamManagementV4Address: " + oamManagementV4Address, isDebugLogEnabled)
                                    execution.setVariable("DCVFMMV_oamManagementV4Address", oamManagementV4Address)
                                } else if (key.equals("oam_management_v6_address")) {
                                    String oamManagementV6Address = element.getElementsByTagNameNS("*", "value").item(0).getTextContent()
                                    logDebug("Obtained oamManagementV6Address: " + oamManagementV6Address, isDebugLogEnabled)
                                    execution.setVariable("DCVFMMV_oamManagementV6Address", oamManagementV6Address)
                                }

                            }
                        }
                        if (!contrailNetworkPolicyFqdnList.isEmpty()) {
                            execution.setVariable("DCVFMMV_contrailNetworkPolicyFqdnList", contrailNetworkPolicyFqdnList)
                        }
                    }
                } else {
                    logDebug("Received a BAD Response from VNF Adapter for CREATE_VF_MODULE Call.", isDebugLogEnabled)
                    exceptionUtil.buildAndThrowWorkflowException(execution, 1002, "VNF Adapter Error")
                }
            } else {
                logDebug("Response from VNF Adapter is Null for CREATE_VF_MODULE Call.", isDebugLogEnabled)
                exceptionUtil.buildAndThrowWorkflowException(execution, 1002, "Empty response from VNF Adapter")
            }

            rollbackData.put("VFMODULE", "rollbackVnfAdapterCreate", "true")
            execution.setVariable("rollbackData", rollbackData)

        } catch (BpmnError b) {
            throw b
        } catch (Exception e) {
            logDebug("Internal Error Occured in PostProcess Method", isDebugLogEnabled)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, "Internal Error Occured in PostProcess Method")
        }
        logDebug(" *** COMPLETED postProcessVnfAdapterResponse Process*** ", isDebugLogEnabled)
    }


    public void preProcessUpdateAAIVfModuleRequestOrch(Execution execution) {
        def method = getClass().getSimpleName() + '.preProcessUpdateAAIVfModuleRequestOrch(' +
                'execution=' + execution.getId() +
                ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)
        execution.setVariable("prefix", PREFIX)
        logDebug(" ======== STARTED preProcessUpdateAAIVfModuleRequestOrch ======== ", isDebugLogEnabled)

        try {

            //Build UpdateAAIVfModule Request
            boolean setContrailServiceInstanceFqdn = false
            def contrailServiceInstanceFqdn = execution.getVariable("DCVFMMV_contrailServiceInstanceFqdn")
            if (!contrailServiceInstanceFqdn.equals("")) {
                setContrailServiceInstanceFqdn = true
            }

            String updateAAIVfModuleRequest = buildUpdateAAIVfModuleRequest(execution, false, true, true, setContrailServiceInstanceFqdn)

            updateAAIVfModuleRequest = utils.formatXml(updateAAIVfModuleRequest)
            execution.setVariable("DCVFMMV_updateAAIVfModuleRequest", updateAAIVfModuleRequest)
            logDebug("Outgoing UpdateAAIVfModuleRequest is: \n" + updateAAIVfModuleRequest, isDebugLogEnabled)
            utils.logAudit("Outgoing UpdateAAIVfModuleRequest is: \n" + updateAAIVfModuleRequest)

        } catch (Exception e) {
            utils.log("ERROR", "Exception Occured Processing preProcessUpdateAAIVfModuleRequestOrch. Exception is:\n" + e, isDebugLogEnabled)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, "Error Occured during preProcessUpdateAAIVfModuleRequestOrch Method:\n" + e.getMessage())
        }
        logDebug("======== COMPLETED preProcessUpdateAAIVfModuleRequestOrch ======== ", isDebugLogEnabled)

    }

    public void preProcessUpdateAAIVfModuleRequestGroup(Execution execution) {
        def method = getClass().getSimpleName() + '.preProcessUpdateAAIVfModuleRequestGroup(' +
                'execution=' + execution.getId() +
                ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)
        execution.setVariable("prefix", PREFIX)
        logDebug(" ======== STARTED preProcessUpdateAAIVfModuleRequestGroup ======== ", isDebugLogEnabled)

        try {

            //Build UpdateAAIVfModule Request

            String updateAAIVfModuleRequest = buildUpdateAAIVfModuleRequest(execution, true, false, false, false)

            updateAAIVfModuleRequest = utils.formatXml(updateAAIVfModuleRequest)
            execution.setVariable("DCVFMMV_updateAAIVfModuleRequest", updateAAIVfModuleRequest)
            logDebug("Outgoing UpdateAAIVfModuleRequest is: \n" + updateAAIVfModuleRequest, isDebugLogEnabled)
            utils.logAudit("Outgoing UpdateAAIVfModuleRequest is: \n" + updateAAIVfModuleRequest)

        } catch (Exception e) {
            utils.log("ERROR", "Exception Occured Processing preProcessUpdateAAIVfModuleRequestGroup. Exception is:\n" + e, isDebugLogEnabled)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, "Error Occured during preProcessUpdateAAIVfModuleRequestGroup Method:\n" + e.getMessage())
        }
        logDebug("======== COMPLETED  preProcessUpdateAAIVfModuleRequestGroup ======== ", isDebugLogEnabled)

    }

    public void validateSDNCResponse(Execution execution, String response, String method) {
        def isDebugLogEnabled = execution.getVariable("isDebugLogEnabled")
        execution.setVariable("prefix", PREFIX)
        logDebug(" *** STARTED ValidateSDNCResponse Process*** ", isDebugLogEnabled)

        WorkflowException workflowException = execution.getVariable("WorkflowException")
        boolean successIndicator = execution.getVariable("SDNCA_SuccessIndicator")

        utils.logAudit("workflowException: " + workflowException)

        SDNCAdapterUtils sdncAdapterUtils = new SDNCAdapterUtils(this)
        sdncAdapterUtils.validateSDNCResponse(execution, response, workflowException, successIndicator)

        utils.logAudit("SDNCResponse: " + response)

        String sdncResponse = response
        if (execution.getVariable(PREFIX + 'sdncResponseSuccess') == true) {
            logDebug("Received a Good Response from SDNC Adapter for " + method + " SDNC Call.  Response is: \n" + sdncResponse, isDebugLogEnabled)
            RollbackData rollbackData = execution.getVariable("rollbackData")

            if (method.equals("assign")) {
                rollbackData.put("VFMODULE", "rollbackSDNCRequestAssign", "true")
                execution.setVariable("CRTGVNF_sdncAssignCompleted", true)
            } else if (method.equals("activate")) {
                rollbackData.put("VFMODULE", "rollbackSDNCRequestActivate", "true")
            }
            execution.setVariable("rollbackData", rollbackData)
        } else {
            logDebug("Received a BAD Response from SDNC Adapter for " + method + " SDNC Call.", isDebugLogEnabled)
            throw new BpmnError("MSOWorkflowException")
        }
        logDebug(" *** COMPLETED ValidateSDNCResponse Process*** ", isDebugLogEnabled)
    }

    public void preProcessUpdateAfterCreateRequest(Execution execution) {
        def isDebugLogEnabled = execution.getVariable("isDebugLogEnabled")
        execution.setVariable("prefix", PREFIX)
        utils.log("DEBUG", " ======== STARTED preProcessRequest Process ======== ", isDebugLogEnabled)
        try {
            String response = execution.getVariable("DCVFMMV_assignSDNCAdapterResponse")
            utils.logAudit("DCVFMMV_assignSDNCAdapterResponse: " + response)

            String data = utils.getNodeXml(response, "response-data")
            data = data.replaceAll("&lt;", "<")
            data = data.replaceAll("&gt;", ">")
            String vnfId = utils.getNodeText1(data, "vnf-id")

            String uuid = execution.getVariable('testReqId') // for junits
            if (uuid == null) {
                uuid = execution.getVariable("mso-request-id") + "-" + System.currentTimeMillis()
            }

            String serviceOperation = "/VNF-API:vnfs/vnf-list/" + vnfId
            def callbackUrl = execution.getVariable("DCVFMMV_sdncCallbackUrl")
            utils.logAudit("callbackUrl: " + callbackUrl)

            String SDNCGetRequest =
                    """<sdncadapterworkflow:SDNCAdapterWorkflowRequest xmlns:ns5="http://org.openecomp/mso/request/types/v1"
                                            xmlns:sdncadapterworkflow="http://org.openecomp/mso/workflow/schema/v1"
                                            xmlns:sdncadapter="http://org.openecomp/workflow/sdnc/adapter/schema/v1">
                    <sdncadapter:RequestHeader>
                    <sdncadapter:RequestId>${uuid}</sdncadapter:RequestId>
                    <sdncadapter:SvcAction>query</sdncadapter:SvcAction>
                    <sdncadapter:SvcOperation>${serviceOperation}</sdncadapter:SvcOperation>
                    <sdncadapter:CallbackUrl>${callbackUrl}</sdncadapter:CallbackUrl>
                    <sdncadapter:MsoAction>mobility</sdncadapter:MsoAction>
                </sdncadapter:RequestHeader>
                    <sdncadapterworkflow:SDNCRequestData></sdncadapterworkflow:SDNCRequestData>
                </sdncadapterworkflow:SDNCAdapterWorkflowRequest>"""

            execution.setVariable("DCVFMMV_getSDNCRequest", SDNCGetRequest)
            utils.log("DEBUG", "Outgoing GetSDNCRequest is: \n" + SDNCGetRequest, isDebugLogEnabled)
            utils.logAudit("Outgoing GetSDNCRequest: " + SDNCGetRequest)

        } catch (Exception e) {
            utils.log("ERROR", "Exception Occured Processing preProcessSDNCGetRequest. Exception is:\n" + e, isDebugLogEnabled)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, "Error Occured during prepareProvision Method:\n" + e.getMessage())
        }
        utils.log("DEBUG", "======== COMPLETED preProcessSDNCGetRequest Process ======== ", isDebugLogEnabled)
    }

    public String buildUpdateAAIVfModuleRequest(Execution execution, boolean updateVolumeGroupId,
                                                boolean updateOrchestrationStatus, boolean updateHeatStackId, boolean updateContrailFqdn) {

        def vnfId = execution.getVariable("DCVFMMV_vnfId")
        def vfModuleId = execution.getVariable("DCVFMMV_vfModuleId")
        def volumeGroupIdString = ""
        if (updateVolumeGroupId) {
            volumeGroupIdString = "<volume-group-id>" + execution.getVariable("DCVFMMV_volumeGroupId") +
                    "</volume-group-id>"
        }
        def orchestrationStatusString = ""
        if (updateOrchestrationStatus) {
            orchestrationStatusString = "<orchestration-status>Created</orchestration-status>"
        }
        def heatStackIdString = ""
        if (updateHeatStackId) {
            heatStackIdString = "<heat-stack-id>" + execution.getVariable("DCVFMMV_heatStackId") + "</heat-stack-id>"
        }
        def contrailFqdnString = ""
        if (updateContrailFqdn) {
            contrailFqdnString = "<contrail-service-instance-fqdn>" + execution.getVariable("DCVFMMV_contrailServiceInstanceFqdn") +
                    "</contrail-service-instance-fqdn>"
        }

        String updateAAIVfModuleRequest =
                """<UpdateAAIVfModuleRequest>
                <vnf-id>${vnfId}</vnf-id>
                <vf-module-id>${vfModuleId}</vf-module-id>
                ${heatStackIdString}
                ${orchestrationStatusString}
                ${volumeGroupIdString}
                ${contrailFqdnString}
            </UpdateAAIVfModuleRequest>"""

        utils.logAudit("updateAAIVfModule Request: " + updateAAIVfModuleRequest)
        return updateAAIVfModuleRequest

    }

    public String buildSDNCParamsXml(Execution execution) {

        String params = ""
        StringBuilder sb = new StringBuilder()
        Map<String, String> paramsMap = execution.getVariable("DCVFMMV_vnfParamsMap")

        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            String paramsXml
            String key = entry.getKey();
            if (key.endsWith("_network")) {
                String requestKey = key.substring(0, key.indexOf("_network"))
                String requestValue = entry.getValue()
                paramsXml =
                        """<vnf-networks>
    <network-role>{ functx:substring-before-match(data($param/@name), '_network') }</network-role>
    <network-name>{ $param/text() }</network-name>
</vnf-networks>"""
            } else {
                paramsXml = ""
            }
            params = sb.append(paramsXml)
        }
        return params
    }

    public void queryCloudRegion(Execution execution) {
        def isDebugLogEnabled = execution.getVariable("isDebugLogEnabled")
        execution.setVariable("prefix", PREFIX)
        utils.log("DEBUG", " ======== STARTED queryCloudRegion ======== ", isDebugLogEnabled)

        try {
            String cloudRegion = execution.getVariable("DCVFMMV_cloudSiteId")

            // Prepare AA&I url
            String aai_endpoint = execution.getVariable("URN_aai_endpoint")
            AaiUtil aaiUtil = new AaiUtil(this)
            String aai_uri = aaiUtil.getCloudInfrastructureCloudRegionUri(execution)
            String queryCloudRegionRequest = "${aai_endpoint}${aai_uri}/" + cloudRegion
            utils.logAudit("CloudRegion Request: " + queryCloudRegionRequest)

            execution.setVariable("DCVFMMV_queryCloudRegionRequest", queryCloudRegionRequest)
            utils.log("DEBUG", "DCVFMMV_queryCloudRegionRequest - " + "\n" + queryCloudRegionRequest, isDebugLogEnabled)

            cloudRegion = aaiUtil.getAAICloudReqion(execution, queryCloudRegionRequest, "PO", cloudRegion)

            if ((cloudRegion != "ERROR")) {
                if (execution.getVariable("DCVFMMV_queryCloudRegionReturnCode") == "404") {
                    execution.setVariable("DCVFMMV_cloudRegionForVolume", "AAIAIC25")
                } else {
                    execution.setVariable("DCVFMMV_cloudRegionForVolume", cloudRegion)
                }
                execution.setVariable("DCVFMMV_isCloudRegionGood", true)
            } else {
                String errorMessage = "AAI Query Cloud Region Unsuccessful. AAI Response Code: " + execution.getVariable("DCVFMMV_queryCloudRegionReturnCode")
                utils.log("DEBUG", errorMessage, isDebugLogEnabled)
                exceptionUtil.buildAndThrowWorkflowException(execution, 2500, errorMessage)
                execution.setVariable("DCVFMMV_isCloudRegionGood", false)
            }
            utils.log("DEBUG", " is Cloud Region Good: " + execution.getVariable("DCVFMMV_isCloudRegionGood"), isDebugLogEnabled)

        } catch (BpmnError b) {
            utils.log("ERROR", "Rethrowing MSOWorkflowException", isDebugLogEnabled)
            throw b
        } catch (Exception ex) {
            // try error
            String errorMessage = "Bpmn error encountered in CreateVfModule flow. Unexpected Response from AAI - " + ex.getMessage()
            utils.log("ERROR", " AAI Query Cloud Region Failed.  Exception - " + "\n" + errorMessage, isDebugLogEnabled)
            exceptionUtil.buildAndThrowWorkflowException(execution, 500, "Exception occured during queryCloudRegion method")
        }
    }

    /**
     *
     * This method occurs when an MSOWorkflowException is caught.  It logs the
     * variables and ensures that the "WorkflowException" Variable is set.
     *
     */
    public void processBPMNException(Execution execution) {
        def isDebugEnabled = execution.getVariable("isDebugLogEnabled")
        execution.setVariable("prefix", PREFIX)
        try {
            utils.log("DEBUG", "Caught a BPMN Exception", isDebugEnabled)
            utils.log("DEBUG", "Started processBPMNException Method", isDebugEnabled)
            utils.log("DEBUG", "Variables List: " + execution.getVariables(), isDebugEnabled)
            if (execution.getVariable("WorkflowException") == null) {
                exceptionUtil.buildAndThrowWorkflowException(execution, 500, "Exception occured during DoCreateVfModule Sub Process")
            }

        } catch (Exception e) {
            utils.log("DEBUG", "Caught Exception during processBPMNException Method: " + e, isDebugEnabled)
        }
        utils.log("DEBUG", "Completed processBPMNException Method", isDebugEnabled)
    }

    public void prepareCreateAAIVfModuleVolumeGroupRequest(Execution execution) {
        def method = getClass().getSimpleName() + '.prepareCreateAAIVfModuleVolumeGroupRequest(' +
                'execution=' + execution.getId() +
                ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)
        execution.setVariable("prefix", PREFIX)
        logDebug(" ======== STARTED prepareCreateAAIVfModuleVolumeGroupRequest ======== ", isDebugLogEnabled)

        try {

            //Build CreateAAIVfModuleVolumeGroup Request

            def vnfId = execution.getVariable("DCVFMMV_vnfId")
            def vfModuleId = execution.getVariable("DCVFMMV_vfModuleId")
            def volumeGroupId = execution.getVariable("DCVFMMV_volumeGroupId")
            //def aicCloudRegion = execution.getVariable("DCVFMMV_cloudSiteId")
            def aicCloudRegion = execution.getVariable("DCVFMMV_cloudRegionForVolume")
            String createAAIVfModuleVolumeGroupRequest =
                    """<CreateAAIVfModuleVolumeGroupRequest>
                <vnf-id>${vnfId}</vnf-id>
                <vf-module-id>${vfModuleId}</vf-module-id>
                <volume-group-id>${volumeGroupId}</volume-group-id>
                <aic-cloud-region>${aicCloudRegion}</aic-cloud-region>
            </CreateAAIVfModuleVolumeGroupRequest>"""

            createAAIVfModuleVolumeGroupRequest = utils.formatXml(createAAIVfModuleVolumeGroupRequest)
            execution.setVariable("DCVFMMV_createAAIVfModuleVolumeGroupRequest", createAAIVfModuleVolumeGroupRequest)
            logDebug("Outgoing CreateAAIVfModuleVolumeGroupRequest is: \n" + createAAIVfModuleVolumeGroupRequest, isDebugLogEnabled)
            utils.logAudit("Outgoing CreateAAIVfModuleVolumeGroupRequest is: \n" + createAAIVfModuleVolumeGroupRequest)

        } catch (Exception e) {
            utils.log("ERROR", "Exception Occured Processing prepareCreateAAIVfModuleVolumeGroupRequest. Exception is:\n" + e, isDebugLogEnabled)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, "Error Occured during prepareCreateAAIVfModuleVolumeGroupRequest Method:\n" + e.getMessage())
        }
        logDebug("======== COMPLETED  prepareCreateAAIVfModuleVolumeGroupRequest ======== ", isDebugLogEnabled)

    }

    public void createNetworkPoliciesInAAI(Execution execution) {
        def method = getClass().getSimpleName() + '.createNetworkPoliciesInAAI(' +
                'execution=' + execution.getId() +
                ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)
        execution.setVariable("prefix", PREFIX)
        logDebug(" ======== STARTED createNetworkPoliciesInAAI ======== ", isDebugLogEnabled)

        try {
            // get variables
            List fqdnList = execution.getVariable("DCVFMMV_contrailNetworkPolicyFqdnList")
            int fqdnCount = fqdnList.size()
            def rollbackData = execution.getVariable("rollbackData")

            execution.setVariable("DCVFMMV_networkPolicyFqdnCount", fqdnCount)
            logDebug("DCVFMMV_networkPolicyFqdnCount - " + fqdnCount, isDebugLogEnabled)

            String aai_endpoint = execution.getVariable("URN_aai_endpoint")
            AaiUtil aaiUriUtil = new AaiUtil(this)
            String aai_uri = aaiUriUtil.getNetworkPolicyUri(execution)

            if (fqdnCount > 0) {

                // AII loop call over contrail network policy fqdn list
                for (i in 0..fqdnCount - 1) {

                    int counting = i + 1
                    String fqdn = fqdnList[i]

                    // Query AAI for this network policy FQDN

                    String queryNetworkPolicyByFqdnAAIRequest = "${aai_endpoint}${aai_uri}?network-policy-fqdn=" + UriUtils.encode(fqdn, "UTF-8")
                    utils.logAudit("AAI request endpoint: " + queryNetworkPolicyByFqdnAAIRequest)

                    def aaiRequestId = UUID.randomUUID().toString()
                    RESTConfig config = new RESTConfig(queryNetworkPolicyByFqdnAAIRequest);
                    RESTClient client = new RESTClient(config).addHeader("X-TransactionId", aaiRequestId)
                            .addHeader("X-FromAppId", "MSO")
                            .addHeader("Content-Type", "application/xml")
                            .addHeader("Accept", "application/xml");
                    APIResponse response = client.get()
                    int returnCode = response.getStatusCode()
                    execution.setVariable("DCVFMMV_aaiQqueryNetworkPolicyByFqdnReturnCode", returnCode)
                    logDebug(" ***** AAI query network policy Response Code, NetworkPolicy #" + counting + " : " + returnCode, isDebugLogEnabled)

                    String aaiResponseAsString = response.getResponseBodyAsString()

                    if (isOneOf(returnCode, 200, 201)) {
                        logDebug("The return code is: " + returnCode, isDebugLogEnabled)
                        // This network policy FQDN already exists in AAI
                        utils.logAudit(aaiResponseAsString)
                        execution.setVariable("DCVFMMV_queryNetworkPolicyByFqdnAAIResponse", aaiResponseAsString)
                        logDebug(" QueryAAINetworkPolicyByFQDN Success REST Response, , NetworkPolicy #" + counting + " : " + "\n" + aaiResponseAsString, isDebugLogEnabled)

                    } else {
                        if (returnCode == 404) {
                            // This network policy FQDN is not in AAI yet. Add it now
                            logDebug("The return code is: " + returnCode, isDebugLogEnabled)
                            logDebug("This network policy FQDN is not in AAI yet: " + fqdn, isDebugLogEnabled)
                            utils.logAudit("Network policy FQDN is not in AAI yet")
                            // Add the network policy with this FQDN to AAI
                            def networkPolicyId = UUID.randomUUID().toString()
                            logDebug("Adding network-policy with network-policy-id " + networkPolicyId, isDebugLogEnabled)

                            String aaiNamespace = aaiUriUtil.getNamespaceFromUri(execution, aai_uri)
                            logDebug('AAI namespace is: ' + aaiNamespace, isDebugLogEnabled)
                            String payload = """<network-policy xmlns="${aaiNamespace}">
                                   <network-policy-id>${networkPolicyId}</network-policy-id>
                                <network-policy-fqdn>${fqdn}</network-policy-fqdn>
                                <heat-stack-id>${execution.getVariable("DCVFMMV_heatStackId")}</heat-stack-id>
                                </network-policy>""" as String

                            execution.setVariable("DCVFMMV_addNetworkPolicyAAIRequestBody", payload)

                            String addNetworkPolicyAAIRequest = "${aai_endpoint}${aai_uri}/" + UriUtils.encode(networkPolicyId, "UTF-8")
                            utils.logAudit("AAI request endpoint: " + addNetworkPolicyAAIRequest)
                            logDebug("AAI request endpoint: " + addNetworkPolicyAAIRequest, isDebugLogEnabled)

                            def aaiRequestIdPut = UUID.randomUUID().toString()
                            RESTConfig configPut = new RESTConfig(addNetworkPolicyAAIRequest);
                            RESTClient clientPut = new RESTClient(configPut).addHeader("X-TransactionId", aaiRequestIdPut)
                                    .addHeader("X-FromAppId", "MSO")
                                    .addHeader("Content-Type", "application/xml")
                                    .addHeader("Accept", "application/xml");
                            logDebug("invoking PUT call to AAI with payload:" + System.lineSeparator() + payload, isDebugLogEnabled)
                            utils.logAudit("Sending PUT call to AAI with Endpoint /n" + addNetworkPolicyAAIRequest + " with payload /n" + payload)
                            APIResponse responsePut = clientPut.httpPut(payload)
                            int returnCodePut = responsePut.getStatusCode()
                            execution.setVariable("DCVFMMV_aaiAddNetworkPolicyReturnCode", returnCodePut)
                            logDebug(" ***** AAI add network policy Response Code, NetworkPolicy #" + counting + " : " + returnCodePut, isDebugLogEnabled)

                            String aaiResponseAsStringPut = responsePut.getResponseBodyAsString()
                            if (isOneOf(returnCodePut, 200, 201)) {
                                logDebug("The return code from adding network policy is: " + returnCodePut, isDebugLogEnabled)
                                // This network policy was created in AAI successfully
                                utils.logAudit(aaiResponseAsStringPut)
                                execution.setVariable("DCVFMMV_addNetworkPolicyAAIResponse", aaiResponseAsStringPut)
                                logDebug(" AddAAINetworkPolicy Success REST Response, , NetworkPolicy #" + counting + " : " + "\n" + aaiResponseAsStringPut, isDebugLogEnabled)
                                rollbackData.put("VFMODULE", "rollbackCreateNetworkPoliciesAAI", "true")
                                rollbackData.put("VFMODULE", "contrailNetworkPolicyFqdn" + i, fqdn)
                                execution.setVariable("rollbackData", rollbackData)

                            } else {
                                // aai all errors
                                String putErrorMessage = "Unable to add network-policy to AAI createNetworkPoliciesInAAI - " + returnCodePut
                                logDebug(putErrorMessage, isDebugLogEnabled)
                                exceptionUtil.buildAndThrowWorkflowException(execution, 2500, putErrorMessage)
                            }

                        } else {
                            if (aaiResponseAsString.contains("RESTFault")) {
                                WorkflowException exceptionObject = exceptionUtil.MapAAIExceptionToWorkflowException(aaiResponseAsString, execution)
                                execution.setVariable("WorkflowException", exceptionObject)
                                throw new BpmnError("MSOWorkflowException")

                            } else {
                                // aai all errors
                                String dataErrorMessage = "Unexpected Response from createNetworkPoliciesInAAI - " + returnCode
                                logDebug(dataErrorMessage, isDebugLogEnabled)
                                exceptionUtil.buildAndThrowWorkflowException(execution, 2500, dataErrorMessage)

                            }
                        }
                    }

                } // end loop


            } else {
                logDebug("No contrail network policies to query/create", isDebugLogEnabled)

            }

        } catch (BpmnError e) {
            throw e;

        } catch (Exception ex) {
            String exceptionMessage = "Bpmn error encountered in DoCreateVfModule flow. createNetworkPoliciesInAAI() - " + ex.getMessage()
            logDebug(exceptionMessage, isDebugLogEnabled)
            exceptionUtil.buildAndThrowWorkflowException(execution, 7000, exceptionMessage)
        }

    }

    /**
     * Prepare a Request for invoking the UpdateAAIGenericVnf subflow.
     *
     * @param execution The flow's execution instance.
     */
    public void prepUpdateAAIGenericVnf(Execution execution) {
        def method = getClass().getSimpleName() + '.prepUpdateAAIGenericVnf(' +
                'execution=' + execution.getId() +
                ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        try {
            def rollbackData = execution.getVariable("rollbackData")
            def vnfId = execution.getVariable('DCVFMMV_vnfId')
            def oamManagementV4Address = execution.getVariable("DCVFMMV_oamManagementV4Address")
            def oamManagementV6Address = execution.getVariable("DCVFMMV_oamManagementV6Address")
            def ipv4OamAddressElement = ''
            def managementV6AddressElement = ''

            if (oamManagementV4Address != null && !oamManagementV4Address.isEmpty()) {
                ipv4OamAddressElement = '<ipv4-oam-address>' + oamManagementV4Address + '</ipv4-oam-address>'
            }

            if (oamManagementV6Address != null && !oamManagementV6Address.isEmpty()) {
                managementV6AddressElement = '<management-v6-address>' + oamManagementV6Address + '</management-v6-address>'
            }

            rollbackData.put("VFMODULE", "oamManagementV4Address", oamManagementV4Address)


            String updateAAIGenericVnfRequest = """
                    <UpdateAAIGenericVnfRequest>
                        <vnf-id>${vnfId}</vnf-id>
                        ${ipv4OamAddressElement}
                        ${managementV6AddressElement}
                    </UpdateAAIGenericVnfRequest>
                """
            updateAAIGenericVnfRequest = utils.formatXml(updateAAIGenericVnfRequest)
            execution.setVariable('DCVM_updateAAIGenericVnfRequest', updateAAIGenericVnfRequest)
            utils.logAudit("updateAAIGenericVnfRequest : " + updateAAIGenericVnfRequest)
            logDebug('Request for UpdateAAIGenericVnf:\n' + updateAAIGenericVnfRequest, isDebugLogEnabled)


            logDebug('Exited ' + method, isDebugLogEnabled)
        } catch (BpmnError e) {
            throw e;
        } catch (Exception e) {
            logError('Caught exception in ' + method, e)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, 'Error in prepUpdateAAIGenericVnf(): ' + e.getMessage())
        }
    }

    /**
     * Post process a result from invoking the UpdateAAIGenericVnf subflow.
     *
     * @param execution The flow's execution instance.
     */
    public void postProcessUpdateAAIGenericVnf(Execution execution) {
        def method = getClass().getSimpleName() + '.postProcessUpdateAAIGenericVnf(' +
                'execution=' + execution.getId() +
                ')'
        def isDebugLogEnabled = execution.getVariable('isDebugLogEnabled')
        logDebug('Entered ' + method, isDebugLogEnabled)

        try {
            def rollbackData = execution.getVariable("RollbackData")

            rollbackData.put("VFMODULE", "rollbackUpdateVnfAAI", "true")

            def vnfId = execution.getVariable('DCVFMMV_vnfId')
            def oamManagementV4Address = execution.getVariable("DCVFMMV_oamManagementV4Address")
            def oamManagementV6Address = execution.getVariable("DCVFMMV_oamManagementV6Address")
            def ipv4OamAddressElement = ''
            def managementV6AddressElement = ''

            if (oamManagementV4Address != null && !oamManagementV4Address.isEmpty()) {
                rollbackData.put("VFMODULE", "oamManagementV4Address", oamManagementV4Address)
            }

            if (oamManagementV6Address != null && !oamManagementV6Address.isEmpty()) {
                rollbackData.put("VFMODULE", "oamManagementV6Address", oamManagementV6Address)
            }

            execution.setVariable("RollbackData", rollbackData)

            logDebug('Exited ' + method, isDebugLogEnabled)
        } catch (BpmnError e) {
            throw e;
        } catch (Exception e) {
            logError('Caught exception in ' + method, e)
            exceptionUtil.buildAndThrowWorkflowException(execution, 1002, 'Error in postProcessUpdateAAIGenericVnf(): ' + e.getMessage())
        }
    }

    public void preProcessRollback(Execution execution) {
        def isDebugEnabled = execution.getVariable("isDebugLogEnabled")
        utils.log("DEBUG", " ***** preProcessRollback ***** ", isDebugEnabled)
        try {

            Object workflowException = execution.getVariable("WorkflowException");

            if (workflowException instanceof WorkflowException) {
                utils.log("DEBUG", "Prev workflowException: " + workflowException.getErrorMessage(), isDebugEnabled)
                execution.setVariable("prevWorkflowException", workflowException);
                //execution.setVariable("WorkflowException", null);
            }
        } catch (BpmnError e) {
            utils.log("DEBUG", "BPMN Error during preProcessRollback", isDebugEnabled)
        } catch (Exception ex) {
            String msg = "Exception in preProcessRollback. " + ex.getMessage()
            utils.log("DEBUG", msg, isDebugEnabled)
        }
        utils.log("DEBUG", " *** Exit preProcessRollback *** ", isDebugEnabled)
    }

    public void postProcessRollback(Execution execution) {
        def isDebugEnabled = execution.getVariable("isDebugLogEnabled")
        utils.log("DEBUG", " ***** postProcessRollback ***** ", isDebugEnabled)
        String msg = ""
        try {
            Object workflowException = execution.getVariable("prevWorkflowException");
            if (workflowException instanceof WorkflowException) {
                utils.log("DEBUG", "Setting prevException to WorkflowException: ", isDebugEnabled)
                execution.setVariable("WorkflowException", workflowException);
            }
            execution.setVariable("rollbackData", null)
        } catch (BpmnError b) {
            utils.log("DEBUG", "BPMN Error during postProcessRollback", isDebugEnabled)
            throw b;
        } catch (Exception ex) {
            msg = "Exception in postProcessRollback. " + ex.getMessage()
            utils.log("DEBUG", msg, isDebugEnabled)
        }
        utils.log("DEBUG", " *** Exit postProcessRollback *** ", isDebugEnabled)
    }

    /*
	 * Parses VNF parameters passed in on the incoming requests and SDNC parameters returned from SDNC get response
	 * and puts them into the format expected by VNF adapter.
	 * @param vnfParamsMap -  map of VNF parameters passed in the request body
	 * @param sdncGetResponse - response string from SDNC GET topology request
	 * @param vnfId
	 * @param vnfName
	 * @param vfModuleId
	 * @param vfModuleName
	 * @param vfModuleIndex - can be null
	 * @return a String of key/value entries for vfModuleParams
	 */


    private Map<String, Object> buildVfModuleParam(Map<String, String> vnfParamsMap, String sdncGetResponse, String vnfId, String vnfName,
                                                   String vfModuleId, String vfModuleName, String vfModuleIndex) {

        //Get SDNC Response Data

        String data = utils.getNodeXml(sdncGetResponse, "response-data")
        data = data.replaceAll("&lt;", "<")
        data = data.replaceAll("&gt;", ">")

        String serviceData = utils.getNodeXml(data, "service-data")
        serviceData = utils.removeXmlPreamble(serviceData)
        serviceData = utils.removeXmlNamespaces(serviceData)
        String vnfRequestInfo = utils.getNodeXml(serviceData, "vnf-request-information")
        String oldVnfId = utils.getNodeXml(vnfRequestInfo, "vnf-id")
        oldVnfId = utils.removeXmlPreamble(oldVnfId)
        oldVnfId = utils.removeXmlNamespaces(oldVnfId)
        serviceData = serviceData.replace(oldVnfId, "")
        def vnfId1 = utils.getNodeText1(serviceData, "vnf-id")

        Map<String, Object> paramsMap = new HashMap<String, Object>()

        InputSource source = new InputSource(new StringReader(data));
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true)
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder()
        Document responseXml = docBuilder.parse(source)

        // Availability Zones Data

        NodeList aZonesList = responseXml.getElementsByTagNameNS("*", "availability-zones")
        String aZonePosition = "0"
        for (int z = 0; z < aZonesList.getLength(); z++) {
            Node node = aZonesList.item(z)
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node
                String aZoneValue = utils.getElementText(eElement, "availability-zone")
                aZonePosition = z.toString()
                paramsMap.put("availability_zone_${aZonePosition}".toString(), mapObject(aZoneValue))
            }
        }

        // VNF Networks Data

        StringBuilder sbNet = new StringBuilder()

        NodeList vnfNetworkList = responseXml.getElementsByTagNameNS("*", "vnf-networks")
        for (int x = 0; x < vnfNetworkList.getLength(); x++) {
            Node node = vnfNetworkList.item(x)
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node
                String vnfNetworkKey = utils.getElementText(eElement, "network-role")
                String vnfNetworkNeutronIdValue = utils.getElementText(eElement, "neutron-id")
                String vnfNetworkNetNameValue = utils.getElementText(eElement, "network-name")
                String vnfNetworkSubNetIdValue = utils.getElementText(eElement, "subnet-id")
                String vnfNetworkV6SubNetIdValue = utils.getElementText(eElement, "ipv6-subnet-id")
                String vnfNetworkNetFqdnValue = utils.getElementText(eElement, "contrail-network-fqdn")
                paramsMap.put("${vnfNetworkKey}_net_id".toString(), mapObject(vnfNetworkNeutronIdValue))
                paramsMap.put("${vnfNetworkKey}_net_name".toString(), mapObject(vnfNetworkNetNameValue))
                paramsMap.put("${vnfNetworkKey}_subnet_id".toString(), mapObject(vnfNetworkSubNetIdValue))
                paramsMap.put("${vnfNetworkKey}_v6_subnet_id".toString(), mapObject(vnfNetworkV6SubNetIdValue))
                paramsMap.put("${vnfNetworkKey}_net_fqdn".toString(), mapObject(vnfNetworkNetFqdnValue))

                NodeList sriovVlanFilterList = eElement.getElementsByTagNameNS("*", "sriov-vlan-filter-list")
                StringBuffer sriovFilterBuf = new StringBuffer()
                String values = ""
                for (int i = 0; i < sriovVlanFilterList.getLength(); i++) {
                    Node node1 = sriovVlanFilterList.item(i)
                    if (node1.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement1 = (Element) node1
                        String value = utils.getElementText(eElement1, "sriov-vlan-filter")
                        if (i != sriovVlanFilterList.getLength() - 1) {
                            values = sriovFilterBuf.append(value + ",")
                        } else {
                            values = sriovFilterBuf.append(value);
                        }
                    }
                }
                if (!values.isEmpty()) {
                    paramsMap.put("${vnfNetworkKey}_ATT_VF_VLAN_FILTER".toString(), mapObject(values))
                }
            }
        }

        // VNF-VMS Data

        def key
        def value
        def networkKey
        def networkValue
        def floatingIPKey
        def floatingIPKeyValue
        def floatingIPV6Key
        def floatingIPV6KeyValue
        StringBuilder sb = new StringBuilder()

        NodeList vmsList = responseXml.getElementsByTagNameNS("*", "vnf-vms")
        for (int x = 0; x < vmsList.getLength(); x++) {
            Node node = vmsList.item(x)
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node
                key = utils.getElementText(eElement, "vm-type")
                String values
                String position = "0"
                StringBuilder sb1 = new StringBuilder()
                NodeList valueList = eElement.getElementsByTagNameNS("*", "vm-names")
                NodeList vmNetworksList = eElement.getElementsByTagNameNS("*", "vm-networks")
                for (int i = 0; i < valueList.getLength(); i++) {
                    Node node1 = valueList.item(i)
                    if (node1.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement1 = (Element) node1
                        value = utils.getElementText(eElement1, "vm-name")
                        if (i != valueList.getLength() - 1) {
                            values = sb1.append(value + ",")
                        } else {
                            values = sb1.append(value);
                        }
                        position = i.toString()
                        paramsMap.put("${key}_name_${position}".toString(), mapObject(value))
                    }
                }
                for (int n = 0; n < vmNetworksList.getLength(); n++) {
                    String floatingIpKeyValueStr = ""
                    String floatingIpV6KeyValueStr = ""
                    Node nodeNetworkKey = vmNetworksList.item(n)
                    if (nodeNetworkKey.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElementNetworkKey = (Element) nodeNetworkKey
                        String ipAddressValues
                        String ipV6AddressValues
                        String networkPosition = "0"
                        StringBuilder sb2 = new StringBuilder()
                        StringBuilder sb3 = new StringBuilder()
                        StringBuilder sb4 = new StringBuilder()
                        networkKey = utils.getElementText(eElementNetworkKey, "network-role")
                        floatingIPKey = key + '_' + networkKey + '_floating_ip'
                        floatingIPKeyValue = utils.getElementText(eElementNetworkKey, "floating-ip")
                        if (!floatingIPKeyValue.isEmpty()) {
                            paramsMap.put(floatingIPKey, mapObject(floatingIPKeyValue))
                        }
                        floatingIPV6Key = key + '_' + networkKey + '_floating_v6_ip'
                        floatingIPV6KeyValue = utils.getElementText(eElementNetworkKey, "floating-ip-v6")
                        if (!floatingIPV6KeyValue.isEmpty()) {
                            paramsMap.put(floatingIPV6Key, mapObject(floatingIPV6KeyValue))
                        }
                        NodeList networkIpsList = eElementNetworkKey.getElementsByTagNameNS("*", "network-ips")
                        for (int a = 0; a < networkIpsList.getLength(); a++) {
                            Node ipAddress = networkIpsList.item(a)
                            if (ipAddress.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElementIpAddress = (Element) ipAddress
                                String ipAddressValue = utils.getElementText(eElementIpAddress, "ip-address")
                                if (a != networkIpsList.getLength() - 1) {
                                    ipAddressValues = sb2.append(ipAddressValue + ",")
                                } else {
                                    ipAddressValues = sb2.append(ipAddressValue);
                                }
                                networkPosition = a.toString()
                                paramsMap.put("${key}_${networkKey}_ip_${networkPosition}".toString(), mapObject(ipAddressValue))
                            }
                        }

                        paramsMap.put("${key}_${networkKey}_ips".toString(), mapObject(ipAddressValues))

                        NodeList interfaceRoutePrefixesList = eElementNetworkKey.getElementsByTagNameNS("*", "interface-route-prefixes")
                        String interfaceRoutePrefixValues = sb3.append("[")

                        for (int a = 0; a < interfaceRoutePrefixesList.getLength(); a++) {
                            Node interfaceRoutePrefix = interfaceRoutePrefixesList.item(a)
                            if (interfaceRoutePrefix.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElementInterfaceRoutePrefix = (Element) interfaceRoutePrefix
                                String interfaceRoutePrefixValue = utils.getElementText(eElementInterfaceRoutePrefix, "interface-route-prefix-cidr")
                                if (interfaceRoutePrefixValue == null || interfaceRoutePrefixValue.isEmpty()) {
                                    interfaceRoutePrefixValue = utils.getElementText(eElementInterfaceRoutePrefix, "interface-route-prefix")
                                }
                                if (a != interfaceRoutePrefixesList.getLength() - 1) {
                                    interfaceRoutePrefixValues = sb3.append("{\"interface_route_table_routes_route_prefix\": \"" + interfaceRoutePrefixValue + "\"}" + ",")
                                } else {
                                    interfaceRoutePrefixValues = sb3.append("{\"interface_route_table_routes_route_prefix\": \"" + interfaceRoutePrefixValue + "\"}")
                                }
                            }
                        }
                        interfaceRoutePrefixValues = sb3.append("]")
                        if (interfaceRoutePrefixesList.getLength() > 0) {
                            paramsMap.put("${key}_${networkKey}_route_prefixes".toString(), mapObject(interfaceRoutePrefixValues))
                        }

                        NodeList networkIpsV6List = eElementNetworkKey.getElementsByTagNameNS("*", "network-ips-v6")
                        for (int a = 0; a < networkIpsV6List.getLength(); a++) {
                            Node ipV6Address = networkIpsV6List.item(a)
                            if (ipV6Address.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElementIpV6Address = (Element) ipV6Address
                                String ipV6AddressValue = utils.getElementText(eElementIpV6Address, "ip-address-ipv6")
                                if (a != networkIpsV6List.getLength() - 1) {
                                    ipV6AddressValues = sb4.append(ipV6AddressValue + ",")
                                } else {
                                    ipV6AddressValues = sb4.append(ipV6AddressValue);
                                }
                                networkPosition = a.toString()
                                paramsMap.put("${key}_${networkKey}_v6_ip_${networkPosition}".toString(), mapObject(ipV6AddressValue))
                            }
                        }
                        paramsMap.put("${key}_${networkKey}_v6_ips".toString(), mapObject(ipV6AddressValues))
                    }
                }
                paramsMap.put("${key}_names".toString(), mapObject(values))
            }
        }
        //SDNC Response Params
        String sdncResponseParams = ""
        List<String> sdncResponseParamsToSkip = ["vnf_id", "vf_module_id", "vnf_name", "vf_module_name"]
        String vnfParamsChildNodes = utils.getChildNodes(data, "vnf-parameters")
        if (vnfParamsChildNodes == null || vnfParamsChildNodes.length() < 1) {
            // No SDNC params
        } else {
            NodeList paramsList = responseXml.getElementsByTagNameNS("*", "vnf-parameters")
            for (int z = 0; z < paramsList.getLength(); z++) {
                Node node = paramsList.item(z)
                Element eElement = (Element) node
                String vnfParameterName = utils.getElementText(eElement, "vnf-parameter-name")
                if (!sdncResponseParamsToSkip.contains(vnfParameterName)) {
                    String vnfParameterValue = utils.getElementText(eElement, "vnf-parameter-value")
                    paramsMap.put(vnfParameterName, mapObject(vnfParameterValue))
                }
            }
        }

        // Parameters received from the request should overwrite any parameters received from SDNC
        if (vnfParamsMap != null) {
            for (Map.Entry<String, String> entry : vnfParamsMap.entrySet()) {
                paramsMap.put(entry.getKey(), mapObject(entry.getValue()))
            }
        }

        return paramsMap

    }

    /*
			 * Parses VNF parameters passed in on the incoming requests and SDNC parameters returned from SDNC get response
			 * for both VNF and VF Module
			 * and puts them into the format expected by VNF adapter.
			 * @param vnfParamsMap -  map of VNF parameters passed in the request body
			 * @param vnfSdncGetResponse - response string from SDNC GET VNF topology request
			 * @param vfmoduleSdncGetResponse - response string from SDNC GET VF Module topology request
			 * @param vnfId
			 * @param vnfName
			 * @param vfModuleId
			 * @param vfModuleName
			 * @param vfModuleIndex - can be null
			 * @return a String of key/value entries for vfModuleParams
			 */

    private Map<String, Object> buildVfModuleParamFromCombinedTopologies(Map<String, String> vnfParamsMap, String vnfSdncGetResponse, String vfmoduleSdncGetResponse, String vnfId, String vnfName,
                                                                         String vfModuleId, String vfModuleName, String vfModuleIndex) {

        // Set up initial parameters

        Map<String, Object> paramsMap = new HashMap<String, Object>()

        if (vfModuleIndex != null) {
            paramsMap.put("vf_module_index", mapObject(vfModuleIndex))
        }

        // Add-on data
        paramsMap.put("vnf_id", mapObject(vnfId))
        paramsMap.put("vnf_name", mapObject(vnfName))
        paramsMap.put("vf_module_id", mapObject(vfModuleId))
        paramsMap.put("vf_module_name", mapObject(vfModuleName))

        //Get SDNC Response Data for VNF

        String vnfData = utils.getNodeXml(vnfSdncGetResponse, "response-data")
        vnfData = vnfData.replaceAll("&lt;", "<")
        vnfData = vnfData.replaceAll("&gt;", ">")

        String vnfTopology = utils.getNodeXml(vnfData, "vnf-topology")
        vnfTopology = utils.removeXmlPreamble(vnfTopology)
        vnfTopology = utils.removeXmlNamespaces(vnfTopology)

        InputSource sourceVnf = new InputSource(new StringReader(vnfData));
        DocumentBuilderFactory docFactoryVnf = DocumentBuilderFactory.newInstance();
        docFactoryVnf.setNamespaceAware(true)
        DocumentBuilder docBuilderVnf = docFactoryVnf.newDocumentBuilder()
        Document responseXmlVnf = docBuilderVnf.parse(sourceVnf)

        // Availability Zones Data

        NodeList aZonesList = responseXmlVnf.getElementsByTagNameNS("*", "availability-zones")
        String aZonePosition = "0"
        for (int z = 0; z < aZonesList.getLength(); z++) {
            Node node = aZonesList.item(z)
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node
                String aZoneValue = utils.getElementText(eElement, "availability-zone")
                aZonePosition = z.toString()
                paramsMap.put("availability_zone_${aZonePosition}".toString(), mapObject(aZoneValue))
            }
        }

        // VNF Networks Data

        StringBuilder sbNet = new StringBuilder()

        NodeList vnfNetworkList = responseXmlVnf.getElementsByTagNameNS("*", "vnf-networks")
        for (int x = 0; x < vnfNetworkList.getLength(); x++) {
            Node node = vnfNetworkList.item(x)
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node
                String vnfNetworkKey = utils.getElementText(eElement, "network-role")
                String vnfNetworkNeutronIdValue = utils.getElementText(eElement, "neutron-id")
                String vnfNetworkNetNameValue = utils.getElementText(eElement, "network-name")
                String vnfNetworkSubNetIdValue = utils.getElementText(eElement, "subnet-id")
                String vnfNetworkV6SubNetIdValue = utils.getElementText(eElement, "ipv6-subnet-id")
                String vnfNetworkNetFqdnValue = utils.getElementText(eElement, "contrail-network-fqdn")
                paramsMap.put("${vnfNetworkKey}_net_id".toString(), mapObject(vnfNetworkNeutronIdValue))
                paramsMap.put("${vnfNetworkKey}_net_name".toString(), mapObject(vnfNetworkNetNameValue))
                paramsMap.put("${vnfNetworkKey}_subnet_id".toString(), mapObject(vnfNetworkSubNetIdValue))
                paramsMap.put("${vnfNetworkKey}_v6_subnet_id".toString(), mapObject(vnfNetworkV6SubNetIdValue))
                paramsMap.put("${vnfNetworkKey}_net_fqdn".toString(), mapObject(vnfNetworkNetFqdnValue))

                NodeList sriovVlanFilterList = eElement.getElementsByTagNameNS("*", "sriov-vlan-filter-list")
                StringBuffer sriovFilterBuf = new StringBuffer()
                String values = ""
                for (int i = 0; i < sriovVlanFilterList.getLength(); i++) {
                    Node node1 = sriovVlanFilterList.item(i)
                    if (node1.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement1 = (Element) node1
                        String value = utils.getElementText(eElement1, "sriov-vlan-filter")
                        if (i != sriovVlanFilterList.getLength() - 1) {
                            values = sriovFilterBuf.append(value + ",")
                        } else {
                            values = sriovFilterBuf.append(value);
                        }
                    }
                }
                if (!values.isEmpty()) {
                    paramsMap.put("${vnfNetworkKey}_ATT_VF_VLAN_FILTER".toString(), mapObject(values))
                }
            }
        }

        //Get SDNC Response Data for VF Module

        String vfModuleData = utils.getNodeXml(vfmoduleSdncGetResponse, "response-data")
        vfModuleData = vfModuleData.replaceAll("&lt;", "<")
        vfModuleData = vfModuleData.replaceAll("&gt;", ">")

        String vfModuleTopology = utils.getNodeXml(vfModuleData, "vf-module-topology")
        vfModuleTopology = utils.removeXmlPreamble(vfModuleTopology)
        vfModuleTopology = utils.removeXmlNamespaces(vfModuleTopology)
        String vfModuleTopologyIdentifier = utils.getNodeXml(vfModuleTopology, "vf-module-topology-identifier")

        InputSource sourceVfModule = new InputSource(new StringReader(vfModuleData));
        DocumentBuilderFactory docFactoryVfModule = DocumentBuilderFactory.newInstance();
        docFactoryVfModule.setNamespaceAware(true)
        DocumentBuilder docBuilderVfModule = docFactoryVfModule.newDocumentBuilder()
        Document responseXmlVfModule = docBuilderVfModule.parse(sourceVfModule)

        // VMS Data

        def key
        def value
        def networkKey
        def networkValue
        def floatingIPKey
        def floatingIPKeyValue
        def floatingIPV6Key
        def floatingIPV6KeyValue
        StringBuilder sb = new StringBuilder()

        NodeList vmsList = responseXmlVfModule.getElementsByTagNameNS("*", "vm")
        for (int x = 0; x < vmsList.getLength(); x++) {
            Node node = vmsList.item(x)
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node
                key = utils.getElementText(eElement, "vm-type")
                String values
                String position = "0"
                StringBuilder sb1 = new StringBuilder()
                NodeList valueList = eElement.getElementsByTagNameNS("*", "vm-names")
                NodeList vmNetworksList = eElement.getElementsByTagNameNS("*", "vm-networks")
                for (int i = 0; i < valueList.getLength(); i++) {
                    Node node1 = valueList.item(i)
                    if (node1.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement1 = (Element) node1
                        value = utils.getElementText(eElement1, "vm-name")
                        if (i != valueList.getLength() - 1) {
                            values = sb1.append(value + ",")
                        } else {
                            values = sb1.append(value);
                        }
                        position = i.toString()
                        paramsMap.put("${key}_name_${position}".toString(), mapObject(value))
                    }
                }
                for (int n = 0; n < vmNetworksList.getLength(); n++) {
                    String floatingIpKeyValueStr = ""
                    String floatingIpV6KeyValueStr = ""
                    Node nodeNetworkKey = vmNetworksList.item(n)
                    if (nodeNetworkKey.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElementNetworkKey = (Element) nodeNetworkKey
                        String ipAddressValues
                        String ipV6AddressValues
                        String networkPosition = "0"
                        StringBuilder sb2 = new StringBuilder()
                        StringBuilder sb3 = new StringBuilder()
                        StringBuilder sb4 = new StringBuilder()
                        networkKey = utils.getElementText(eElementNetworkKey, "network-role")
                        floatingIPKey = key + '_' + networkKey + '_floating_ip'
                        floatingIPKeyValue = utils.getElementText(eElementNetworkKey, "floating-ip")
                        if (!floatingIPKeyValue.isEmpty()) {
                            paramsMap.put(floatingIPKey, mapObject(floatingIPKeyValue))
                        }
                        floatingIPV6Key = key + '_' + networkKey + '_floating_v6_ip'
                        floatingIPV6KeyValue = utils.getElementText(eElementNetworkKey, "floating-ip-v6")
                        if (!floatingIPV6KeyValue.isEmpty()) {
                            paramsMap.put(floatingIPV6Key, mapObject(floatingIPV6KeyValue))
                        }
                        NodeList networkIpsList = eElementNetworkKey.getElementsByTagNameNS("*", "network-ips")
                        for (int a = 0; a < networkIpsList.getLength(); a++) {
                            Node ipAddress = networkIpsList.item(a)
                            if (ipAddress.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElementIpAddress = (Element) ipAddress
                                String ipAddressValue = utils.getElementText(eElementIpAddress, "ip-address")
                                if (a != networkIpsList.getLength() - 1) {
                                    ipAddressValues = sb2.append(ipAddressValue + ",")
                                } else {
                                    ipAddressValues = sb2.append(ipAddressValue);
                                }
                                networkPosition = a.toString()
                                paramsMap.put("${key}_${networkKey}_ip_${networkPosition}".toString(), mapObject(ipAddressValue))
                            }
                        }

                        paramsMap.put("${key}_${networkKey}_ips".toString(), mapObject(ipAddressValues))

                        NodeList interfaceRoutePrefixesList = eElementNetworkKey.getElementsByTagNameNS("*", "interface-route-prefixes")
                        String interfaceRoutePrefixValues = sb3.append("[")

                        for (int a = 0; a < interfaceRoutePrefixesList.getLength(); a++) {
                            Node interfaceRoutePrefix = interfaceRoutePrefixesList.item(a)
                            if (interfaceRoutePrefix.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElementInterfaceRoutePrefix = (Element) interfaceRoutePrefix
                                String interfaceRoutePrefixValue = utils.getElementText(eElementInterfaceRoutePrefix, "interface-route-prefix-cidr")
                                if (interfaceRoutePrefixValue == null || interfaceRoutePrefixValue.isEmpty()) {
                                    interfaceRoutePrefixValue = utils.getElementText(eElementInterfaceRoutePrefix, "interface-route-prefix")
                                }
                                if (a != interfaceRoutePrefixesList.getLength() - 1) {
                                    interfaceRoutePrefixValues = sb3.append("{\"interface_route_table_routes_route_prefix\": \"" + interfaceRoutePrefixValue + "\"}" + ",")
                                } else {
                                    interfaceRoutePrefixValues = sb3.append("{\"interface_route_table_routes_route_prefix\": \"" + interfaceRoutePrefixValue + "\"}")
                                }
                            }
                        }
                        interfaceRoutePrefixValues = sb3.append("]")
                        if (interfaceRoutePrefixesList.getLength() > 0) {
                            paramsMap.put("${key}_${networkKey}_route_prefixes".toString(), mapObject(interfaceRoutePrefixValues))
                        }

                        NodeList networkIpsV6List = eElementNetworkKey.getElementsByTagNameNS("*", "network-ips-v6")
                        for (int a = 0; a < networkIpsV6List.getLength(); a++) {
                            Node ipV6Address = networkIpsV6List.item(a)
                            if (ipV6Address.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElementIpV6Address = (Element) ipV6Address
                                String ipV6AddressValue = utils.getElementText(eElementIpV6Address, "ip-address-ipv6")
                                if (a != networkIpsV6List.getLength() - 1) {
                                    ipV6AddressValues = sb4.append(ipV6AddressValue + ",")
                                } else {
                                    ipV6AddressValues = sb4.append(ipV6AddressValue);
                                }
                                networkPosition = a.toString()
                                paramsMap.put("${key}_${networkKey}_v6_ip_${networkPosition}".toString(), mapObject(ipV6AddressValue))
                            }
                        }
                        paramsMap.put("${key}_${networkKey}_v6_ips".toString(), mapObject(ipV6AddressValues))
                    }
                }
                paramsMap.put("${key}_names".toString(), mapObject(values))
            }
        }
        //SDNC Response Params
        List<String> sdncResponseParamsToSkip = ["vnf_id", "vf_module_id", "vnf_name", "vf_module_name"]

        String vnfParamsChildNodes = utils.getChildNodes(vnfData, "param")
        if (vnfParamsChildNodes == null || vnfParamsChildNodes.length() < 1) {
            // No SDNC params for VNF
        } else {
            NodeList paramsList = responseXmlVnf.getElementsByTagNameNS("*", "param")
            for (int z = 0; z < paramsList.getLength(); z++) {
                Node node = paramsList.item(z)
                Element eElement = (Element) node
                String vnfParameterName = utils.getElementText(eElement, "name")
                if (!sdncResponseParamsToSkip.contains(vnfParameterName)) {
                    String vnfParameterValue = utils.getElementText(eElement, "value")
                    paramsMap.put(vnfParameterName, mapObject(vnfParameterValue))
                }
            }
        }

        String vfModuleParamsChildNodes = utils.getChildNodes(vfModuleData, "param")
        if (vfModuleParamsChildNodes == null || vfModuleParamsChildNodes.length() < 1) {
            // No SDNC params for VF Module
        } else {
            NodeList paramsList = responseXmlVfModule.getElementsByTagNameNS("*", "param")
            for (int z = 0; z < paramsList.getLength(); z++) {
                Node node = paramsList.item(z)
                Element eElement = (Element) node
                String vnfParameterName = utils.getElementText(eElement, "name")
                if (!sdncResponseParamsToSkip.contains(vnfParameterName)) {
                    String vnfParameterValue = utils.getElementText(eElement, "value")
                    paramsMap.put(vnfParameterName, mapObject(vnfParameterValue))
                }
            }
        }

        // Parameters received from the request should overwrite any parameters received from SDNC
        if (vnfParamsMap != null) {
            for (Map.Entry<String, String> entry : vnfParamsMap.entrySet()) {
                paramsMap.put(entry.getKey(), mapObject(entry.getValue()))
            }
        }

        return paramsMap

    }

    private static Object mapObject(String value) {
        if (NumberUtils.isNumber(value)) {
            return NumberUtils.createNumber(value)
        } else if (BooleanUtils.toBooleanObject(value) != null) {
            return BooleanUtils.toBooleanObject(value)
        }

        return value
    }

}

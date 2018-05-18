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

package org.openecomp.mso.bpmn.infrastructure;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.openecomp.mso.bpmn.common.WorkflowTest;
import org.openecomp.mso.bpmn.mock.FileUtil;
import org.openecomp.mso.bpmn.mock.StubHAService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.openecomp.mso.bpmn.common.BPMNUtil.getRawVariable;
import static org.openecomp.mso.bpmn.mock.StubMultiVimAdapter.mockMultiVimPost;
import static org.openecomp.mso.bpmn.mock.StubResponseAAI.*;
import static org.openecomp.mso.bpmn.mock.StubResponseDatabase.MockGetServiceResourcesCatalogData;
import static org.openecomp.mso.bpmn.mock.StubResponseDatabase.mockUpdateRequestDB;
import static org.openecomp.mso.bpmn.mock.StubResponseESR.mockCloudRegionESR;
import static org.openecomp.mso.bpmn.mock.StubResponseSDNCAdapter.mockSDNCAdapter;

/**
 * Unit tests for DoCreateVfModuleTest.bpmn.
 */
public class DoCreateVfModuleMultiVimTest extends WorkflowTest {

    private static final String CLOUD_OWNER = "ATT";
    private static final String CLOUD_REGION_ID = "EastUS";
    private final CallbackSet callbacks = new CallbackSet();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(28090);

    public DoCreateVfModuleMultiVimTest() {
        callbacks.put("assign", FileUtil.readResourceFile(
                "__files/VfModularity/SDNCTopologyAssignCallback.xml"));
        callbacks.put("query", FileUtil.readResourceFile(
                "__files/VfModularity/SDNCTopologyQueryCallback.xml"));
        callbacks.put("queryVnf", FileUtil.readResourceFile(
                "__files/VfModularity/SDNCTopologyQueryCallbackVnf.xml"));
        callbacks.put("queryModuleNoVnf", FileUtil.readResourceFile(
                "__files/VfModularity/SDNCTopologyQueryCallbackVfModuleNoVnf.xml"));
        callbacks.put("queryModule", FileUtil.readResourceFile(
                "__files/VfModularity/SDNCTopologyQueryCallbackVfModule.xml"));
        callbacks.put("activate", FileUtil.readResourceFile(
                "__files/VfModularity/SDNCTopologyActivateCallback.xml"));
        callbacks.put("vnfCreate", FileUtil.readResourceFile(
                "__files/VfModularity/MultiVimAdapterRestCreateCallback.xml"));
        callbacks.put("SOLUTION_FOUND", JSON, "HAS_SolutionResponse", FileUtil.readResourceFile("__files/notifyHASolution.json"));
    }

    /**
     * Test the sunny day scenario.
     */
    @Test
    @Deployment(resources = {
            "subprocess/DoCreateVfModuleMultiVim.bpmn",
            "subprocess/MultiVimAdapter.bpmn",
            "subprocess/ReceiveWorkflowMessage.bpmn",
            "subprocess/SDNCAdapterV1.bpmn",
            "subprocess/GenericGetVnf.bpmn",
            "subprocess/ConfirmVolumeGroupTenant.bpmn",
            "subprocess/ConfirmVolumeGroupName.bpmn",
            "subprocess/CreateAAIVfModule.bpmn",
            "subprocess/UpdateAAIVfModule.bpmn",
            "subprocess/CreateAAIVfModuleVolumeGroup.bpmn",
            "subprocess/UpdateAAIGenericVnf.bpmn",
            "subprocess/ESRGetVimById.bpmn",
            "subprocess/HAService.bpmn"
    })
    public void sunnyDay() {

        logStart();

        mockCloudRegionESR(200, CLOUD_OWNER, CLOUD_REGION_ID, "esrGetVIMResponse.xml");

        StubHAService.mockHASPost(202);

        MockAAIVfModule();
        MockPatchGenericVnf("skask");
        MockPatchVfModuleId("skask", ".*");
        mockSDNCAdapter("VfModularity/StandardSDNCSynchResponse.xml");
        mockMultiVimPost(202, "skask");
        mockUpdateRequestDB(200, "Database/DBUpdateResponse.xml");
        MockGetServiceResourcesCatalogData("aa5256d2-5a33-55df-13ab-12abad84e7ff", "1", "getCatalogServiceResourcesData.json");

        String businessKey = UUID.randomUUID().toString();

        Map<String, Object> variables = setupVariablesSunnyDayBuildingBlocks();

        invokeSubProcess("DoCreateVfModuleMultiVim", businessKey, variables);

        injectWorkflowMessages(callbacks, "SOLUTION_FOUND");

        injectSDNCCallbacks(callbacks, "queryVnf");
        injectSDNCCallbacks(callbacks, "assign, queryModuleNoVnf");
        injectMultiVimRestCallbacks(callbacks, "vnfCreate");
        injectSDNCCallbacks(callbacks, "activate");

        waitForProcessEnd(businessKey, 10000);

        Assert.assertTrue(isProcessEnded(businessKey));
        Assert.assertTrue((boolean) getRawVariable(processEngineRule, "DoCreateVfModuleMultiVim", "DCVFMMV_SuccessIndicator"));

        logEnd();
    }

    /**
     * Test the sunny day scenario with 1702 SDNC interaction.
     */
    @Test
    @Deployment(resources = {
            "subprocess/DoCreateVfModuleMultiVim.bpmn",
            "subprocess/MultiVimAdapter.bpmn",
            "subprocess/ReceiveWorkflowMessage.bpmn",
            "subprocess/GenericGetVnf.bpmn",
            "subprocess/SDNCAdapterV1.bpmn",
            "subprocess/ConfirmVolumeGroupTenant.bpmn",
            "subprocess/ConfirmVolumeGroupName.bpmn",
            "subprocess/CreateAAIVfModule.bpmn",
            "subprocess/UpdateAAIVfModule.bpmn",
            "subprocess/CreateAAIVfModuleVolumeGroup.bpmn",
            "subprocess/UpdateAAIGenericVnf.bpmn",
            "subprocess/ESRGetVimById.bpmn",
            "subprocess/HAService.bpmn"
    })
    public void sunnyDay_1702() throws IOException {

        logStart();

        mockCloudRegionESR(200, CLOUD_OWNER, CLOUD_REGION_ID, "esrGetVIMResponse.xml");

        StubHAService.mockHASPost(202);

        MockGetGenericVnfByIdWithPriority("skask", ".*", 200, "VfModularity/VfModule-new.xml", 5);
        MockGetGenericVnfByIdWithDepth("skask", 1, "VfModularity/GenericVnf.xml");
        MockPutVfModuleIdNoResponse("skask", "PCRF", ".*");
        MockPutNetwork(".*", "VfModularity/AddNetworkPolicy_AAIResponse_Success.xml", 200);
        MockPutGenericVnf("skask");
        mockSDNCAdapter("/SDNCAdapter", "vnf-type>STMTN", 200, "VfModularity/StandardSDNCSynchResponse.xml");
        mockSDNCAdapter("/SDNCAdapter", "SvcAction>query", 200, "VfModularity/StandardSDNCSynchResponse.xml");
        mockMultiVimPost(202, "skask");
        mockUpdateRequestDB(200, "Database/DBUpdateResponse.xml");
        MockPatchGenericVnf("skask");
        MockPatchVfModuleId("skask", ".*");
        MockGetServiceResourcesCatalogData("aa5256d2-5a33-55df-13ab-12abad84e7ff", "1", "getCatalogServiceResourcesData.json");

        String businessKey = UUID.randomUUID().toString();
        //RuntimeService runtimeService = processEngineRule.getRuntimeService();

        Map<String, Object> variables = setupVariablesSunnyDayBuildingBlocks();
        variables.put("sdncVersion", "1702");
        //runtimeService.startProcessInstanceByKey("DoCreateVfModule", variables);
        invokeSubProcess("DoCreateVfModuleMultiVim", businessKey, variables);

        injectWorkflowMessages(callbacks, "SOLUTION_FOUND");

        injectSDNCCallbacks(callbacks, "assign, queryModule");
        injectMultiVimRestCallbacks(callbacks, "vnfCreate");
        injectSDNCCallbacks(callbacks, "activate");

        waitForProcessEnd(businessKey, 10000);

        Assert.assertTrue(isProcessEnded(businessKey));
        Assert.assertTrue((boolean) getRawVariable(processEngineRule, "DoCreateVfModuleMultiVim", "DCVFMMV_SuccessIndicator"));

        logEnd();
    }

    /**
     * Test the sunny day scenario.
     */
    @Test
    @Deployment(resources = {
            "subprocess/DoCreateVfModuleMultiVim.bpmn",
            "subprocess/MultiVimAdapter.bpmn",
            "subprocess/ReceiveWorkflowMessage.bpmn",
            "subprocess/GenerateVfModuleName.bpmn",
            "subprocess/GenericGetVnf.bpmn",
            "subprocess/SDNCAdapterV1.bpmn",
            "subprocess/ConfirmVolumeGroupTenant.bpmn",
            "subprocess/ConfirmVolumeGroupName.bpmn",
            "subprocess/CreateAAIVfModule.bpmn",
            "subprocess/UpdateAAIVfModule.bpmn",
            "subprocess/CreateAAIVfModuleVolumeGroup.bpmn",
            "subprocess/UpdateAAIGenericVnf.bpmn",
            "subprocess/ESRGetVimById.bpmn",
            "subprocess/HAService.bpmn"
    })
    public void sunnyDay_withVfModuleNameGeneration() throws IOException {

        logStart();

        mockCloudRegionESR(200, CLOUD_OWNER, CLOUD_REGION_ID, "esrGetVIMResponse.xml");

        StubHAService.mockHASPost(202);

        MockGetGenericVnfByIdWithPriority("skask", ".*", 200, "VfModularity/VfModule-new.xml", 5);
        MockGetGenericVnfByIdWithDepth("skask", 1, "VfModularity/GenericVnf.xml");
        MockPutVfModuleIdNoResponse("skask", "PCRF", ".*");
        MockPutNetwork(".*", "VfModularity/AddNetworkPolicy_AAIResponse_Success.xml", 200);
        MockPutGenericVnf("skask");
        MockAAIVfModule();
        mockSDNCAdapter("/SDNCAdapter", "vnf-type>STMTN", 200, "VfModularity/StandardSDNCSynchResponse.xml");
        mockSDNCAdapter("/SDNCAdapter", "SvcAction>query", 200, "VfModularity/StandardSDNCSynchResponse.xml");
        mockMultiVimPost(202, "skask");
        mockUpdateRequestDB(200, "Database/DBUpdateResponse.xml");
        MockPatchGenericVnf("skask");
        MockPatchVfModuleId("skask", ".*");
        MockGetServiceResourcesCatalogData("aa5256d2-5a33-55df-13ab-12abad84e7ff", "1", "getCatalogServiceResourcesData.json");

        String businessKey = UUID.randomUUID().toString();
        //RuntimeService runtimeService = processEngineRule.getRuntimeService();

        Map<String, Object> variables = setupVariablesSunnyDayBuildingBlocks();
        variables.put("vfModuleName", null);
        variables.put("vfModuleLabel", "MODULELABEL");
        variables.put("sdncVersion", "1702");
        //runtimeService.startProcessInstanceByKey("DoCreateVfModule", variables);
        invokeSubProcess("DoCreateVfModuleMultiVim", businessKey, variables);

        injectWorkflowMessages(callbacks, "SOLUTION_FOUND");

        injectSDNCCallbacks(callbacks, "assign, query");
        injectMultiVimRestCallbacks(callbacks, "vnfCreate");
        injectSDNCCallbacks(callbacks, "activate");

        waitForProcessEnd(businessKey, 10000);

        Assert.assertTrue(isProcessEnded(businessKey));
        Assert.assertTrue((boolean) getRawVariable(processEngineRule, "DoCreateVfModuleMultiVim", "DCVFMMV_SuccessIndicator"));

        logEnd();
    }


    private Map<String, Object> setupVariablesSunnyDayBuildingBlocks() {

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("mso-request-id", "testRequestId");

        variables.put("msoRequestId", "testRequestId");
        variables.put("isBaseVfModule", false);
        variables.put("isDebugLogEnabled", "true");
        variables.put("disableRollback", "true");
        //variables.put("recipeTimeout", "0");
        //variables.put("requestAction", "CREATE_VF_MODULE");
        variables.put("serviceInstanceId", "f70e927b-6087-4974-9ef8-c5e4d5847ca4");
        variables.put("vnfId", "skask");
        variables.put("vnfName", "vnfname");
        variables.put("vfModuleName", "PCRF::module-0-2");
        variables.put("vnfType", "vSAMP12");
        variables.put("vfModuleId", "");
        variables.put("volumeGroupId", "");
        variables.put("serviceType", "MOG");
        variables.put("vfModuleType", "");
        variables.put("isVidRequest", "true");
        variables.put("asdcServiceModelVersion", "1.0");
        variables.put("usePreload", true);
        variables.put("cloudType", "azure");

        String vfModuleModelInfo = "{ " + "\"modelType\": \"vfModule\"," +
                "\"modelInvariantUuid\": \"ff5256d2-5a33-55df-13ab-12abad84e7ff\"," +
                "\"modelUuid\": \"fe6478e5-ea33-3346-ac12-ab121484a3fe\"," +
                "\"modelName\": \"STMTN5MMSC21-MMSC::model-1-0\"," +
                "\"modelVersion\": \"1\"," +
                "\"modelCustomizationUuid\": \"MODEL-123\"" + "}";
        variables.put("vfModuleModelInfo", vfModuleModelInfo);

        variables.put("sdncVersion", "1707");

        variables.put("lcpCloudRegionId", "MDTWNJ21");
        variables.put("tenantId", "fba1bd1e195a404cacb9ce17a9b2b421");

        String serviceModelInfo = "{ " + "\"modelType\": \"service\"," +
                "\"modelInvariantUuid\": \"aa5256d2-5a33-55df-13ab-12abad84e7ff\"," +
                "\"modelUuid\": \"bb6478e5-ea33-3346-ac12-ab121484a3fe\"," +
                "\"modelName\": \"SVC-STMTN5MMSC21-MMSC::model-1-0\"," +
                "\"modelVersion\": \"1\"," +
                "}";
        variables.put("serviceModelInfo", serviceModelInfo);

        String vnfModelInfo = "{ " + "\"modelType\": \"vnf\"," +
                "\"modelInvariantUuid\": \"445256d2-5a33-55df-13ab-12abad84e7ff\"," +
                "\"modelUuid\": \"f26478e5-ea33-3346-ac12-ab121484a3fe\"," +
                "\"modelName\": \"VNF-STMTN5MMSC21-MMSC::model-1-0\"," +
                "\"modelVersion\": \"1\"," +
                "\"modelCustomizationUuid\": \"VNF-MODEL-123\"" + "}";
        variables.put("vnfModelInfo", vnfModelInfo);

        variables.put("vnfQueryPath", "/restconf/vnfQueryPath");

        return variables;

    }
}
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

package org.openecomp.mso.bpmn.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openecomp.mso.bpmn.common.workflow.service.WorkflowAsyncResource;
import org.openecomp.mso.bpmn.common.workflow.service.WorkflowResource;
import org.openecomp.mso.bpmn.common.workflow.service.WorkflowResponse;

/**
 * Set of utility methods used for Unit testing
 *
 */
public class BPMNUtil {

	public static String getVariable(ProcessEngineServices processEngineServices, String processDefinitionID, String name) {
		String pID = getProcessInstanceId(processEngineServices,
				processDefinitionID);
		assertProcessInstanceFinished(processEngineServices, pID);
		HistoricVariableInstance responseData = processEngineServices.getHistoryService()
			    .createHistoricVariableInstanceQuery().processInstanceId(pID)
			    .variableName(name)
			    .singleResult();
		
		if (responseData != null) {
			return (responseData.getValue() != null ? responseData.getValue().toString(): null); 
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Object> T getRawVariable(ProcessEngineServices processEngineServices, String processDefinitionID, String name) {
		String pID = getProcessInstanceId(processEngineServices,
				processDefinitionID);
		assertProcessInstanceFinished(processEngineServices, pID);
		Object responseData = processEngineServices.getHistoryService()
			    .createHistoricVariableInstanceQuery().processInstanceId(pID)
			    .variableName(name)
			    .singleResult()
			    .getValue();
		return (T) responseData;
	}

	
	public static void assertAnyProcessInstanceFinished(ProcessEngineServices processEngineServices, String processDefinitionID) {
		String pID = getProcessInstanceId(processEngineServices,
				processDefinitionID);
		assertNotNull(pID);
	    assertTrue(processEngineServices.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(pID).finished().count() > 0);
	}
	
	public static void assertNoProcessInstance(ProcessEngineServices processEngineServices, String processDefinitionID) {
		assertNull(getProcessInstanceId(processEngineServices, processDefinitionID));
	}
	
	public static void assertProcessInstanceFinished(ProcessEngineServices processEngineServices, String pid) {
	    assertEquals(1, processEngineServices.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(pid).finished().count());
	}
	
	public static void assertProcessInstanceNotFinished(ProcessEngineServices processEngineServices, String processDefinitionID) {
		String pID = getProcessInstanceId(processEngineServices,
				processDefinitionID);		
	    assertEquals(0, processEngineServices.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(pID).finished().count());
	}

	private static String getProcessInstanceId(
			ProcessEngineServices processEngineServices, String processDefinitionID) {
		List<HistoricProcessInstance> historyList =  processEngineServices.getHistoryService().createHistoricProcessInstanceQuery().list();
		String pID = null;
		for (HistoricProcessInstance hInstance: historyList) {
			if (hInstance.getProcessDefinitionKey().equals(processDefinitionID)) {
				pID = hInstance.getId();
				break;
			}
		}
		return pID;
	}

	public static boolean isProcessInstanceFinished(ProcessEngineServices processEngineServices, String pid) {
	    return processEngineServices.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(pid).finished().count() == 1 ? true: false;
	}

	
	private static void buildVariable(String key, String value, Map<String,Object> variableValueType) {
		Map<String, Object> host = new HashMap<String, Object>();
		host.put("value", value);
		host.put("type", "String");
		variableValueType.put(key, host);
	}
	
	public static WorkflowResponse executeWorkFlow(ProcessEngineServices processEngineServices, String processKey, Map<String,String> variables) {
		WorkflowResource workflowResource = new WorkflowResource();
		VariableMapImpl variableMap = new VariableMapImpl();

		Map<String, Object> variableValueType = new HashMap<String, Object>();
		for (String key : variables.keySet()) {
			buildVariable(key, variables.get(key), variableValueType);
		}
		buildVariable("mso-service-request-timeout","600", variableValueType);
		variableMap.put("variables", variableValueType);
		
		workflowResource.setProcessEngineServices4junit(processEngineServices);
		Response response = workflowResource.startProcessInstanceByKey(
					processKey, variableMap);
		WorkflowResponse workflowResponse = (WorkflowResponse) response.getEntity();
		return workflowResponse;
	}

	//Check the runtime service to see whether the process is completed
	public static void waitForWorkflowToFinish(ProcessEngineServices processEngineServices, String pid) throws InterruptedException {
		// Don't wait forever
		long waitTime = 120000;
		long endTime = System.currentTimeMillis() + waitTime;

		while (true) {
			if (processEngineServices.getRuntimeService().createProcessInstanceQuery().processInstanceId(pid).singleResult() == null) {
				break;
			}

			if (System.currentTimeMillis() >= endTime) {
				fail("Process " + pid + " did not finish in " + waitTime + "ms");
			}

			Thread.sleep(200);
		}
	}
	
	/**
	 * Executes the Asynchronous workflow in synchronous fashion and returns the WorkflowResponse object
	 * @param processEngineServices
	 * @param processKey
	 * @param variables
	 * @return
	 * @throws InterruptedException
	 */
	public static WorkflowResponse executeAsyncWorkflow(ProcessEngineServices processEngineServices, String processKey, Map<String,String> variables) throws InterruptedException {
		ProcessThread pthread = new ProcessThread(processKey, processEngineServices, variables);
		pthread.start();
		BPMNUtil.assertProcessInstanceNotFinished(processEngineServices, processKey);
		String pid = getProcessInstanceId(processEngineServices, processKey);
		//Caution: If there is a problem with workflow, this may wait for ever
		while (true) {
			pid = getProcessInstanceId(processEngineServices, processKey);
			if (!isProcessInstanceFinished(processEngineServices,pid)) {
				Thread.sleep(200);
			} else{
				break;
			}
		}
		//need to retrieve for second time ?
		pid = getProcessInstanceId(processEngineServices, processKey);
		waitForWorkflowToFinish(processEngineServices, pid);
		return pthread.workflowResponse;
	}

	/**
	 * Execute workflow using async resource
	 * @param processEngineServices
	 * @param processKey
	 * @param asyncResponse
	 * @param variables
	 */
	private static void executeAsyncFlow(ProcessEngineServices processEngineServices, String processKey, AsynchronousResponse asyncResponse, Map<String,String> variables) {
		WorkflowAsyncResource workflowResource = new WorkflowAsyncResource();
		VariableMapImpl variableMap = new VariableMapImpl();

		Map<String, Object> variableValueType = new HashMap<String, Object>();
		for (String key : variables.keySet()) {
			buildVariable(key, variables.get(key), variableValueType);
		}
		buildVariable("mso-service-request-timeout","600", variableValueType);
		variableMap.put("variables", variableValueType);
		
		workflowResource.setProcessEngineServices4junit(processEngineServices);
		workflowResource.startProcessInstanceByKey(asyncResponse, processKey, variableMap);
	}
	
	/**
	 * Helper class which executes workflow in a thread
	 *
	 */
	static class ProcessThread extends Thread {
		
		public WorkflowResponse workflowResponse = null;
		public String processKey;
		public AsynchronousResponse asyncResponse = spy(AsynchronousResponse.class);
		public boolean started;
		public ProcessEngineServices processEngineServices;
		public Map<String,String> variables;
		
		public ProcessThread(String processKey, ProcessEngineServices processEngineServices, Map<String,String> variables) {
			this.processKey = processKey;
			this.processEngineServices = processEngineServices;
			this.variables = variables;
		}
		
		public void run() {
			started = true;
			doAnswer(new Answer<Void>() {
			    public Void answer(InvocationOnMock invocation) {
			      Response response = (Response) invocation.getArguments()[0];
			      workflowResponse = (WorkflowResponse) response.getEntity();
			      return null;
			    }
			}).when(asyncResponse).setResponse(any(Response.class));		
			executeAsyncFlow(processEngineServices, processKey, asyncResponse, variables);
		}
	}
}

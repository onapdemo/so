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

package org.openecomp.mso.bpmn.common.scripts

import org.junit.Assert
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


import org.junit.Ignore;

import static org.mockito.Mockito.*

import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.internal.debugging.MockitoDebuggerImpl
import org.mockito.runners.MockitoJUnitRunner
import org.openecomp.mso.bpmn.common.scripts.ExceptionUtil;
@RunWith(MockitoJUnitRunner.class)
import org.junit.Test



class ExceptionUtilTest {


	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this)
	}

	@Test
	public void testMapErrorCode5010(){
		String msg = "Connect to njcdtl20ew2988:8070  failed: Connection refused: connect"
		ExceptionUtil util = new ExceptionUtil()
		Assert.assertEquals("5010",util.MapErrorCode(msg))
	}

	@Test
	public void testMapErrorCode5020(){
		String msg = "Connection timed out"
		ExceptionUtil util = new ExceptionUtil()
		Assert.assertEquals("5020",util.MapErrorCode(msg))
	}
}

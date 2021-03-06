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

package org.openecomp.mso.apihandlerinfra;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openecomp.mso.apihandlerinfra.vnfbeans.VnfRequest;
import org.openecomp.mso.requestsdb.InfraRequests;

public class VnfInfoHandlerTest {

	VnfInfoHandler handler = new VnfInfoHandler();
	
	@Test
	public void fillVnfRequestTest(){
		VnfRequest qr = new VnfRequest();
		InfraRequests ar = new InfraRequests();
		ar.setVnfId("2990102");
		ar.setVnfParams("test");
		handler.fillVnfRequest(qr, ar, "v3");
		String vnfid = (String)qr.getVnfParams();
		assertTrue(vnfid.equals("test"));
	}
	
	@Test
	public void fillVnfRequestTestV2(){
		VnfRequest qr = new VnfRequest();
		InfraRequests ar = new InfraRequests();
		ar.setVnfId("2990102");
		ar.setVnfParams("test");
		handler.fillVnfRequest(qr, ar, "v2");
		String vnfid = (String)qr.getVnfParams();
		assertTrue(vnfid.equals("test"));
	}
}

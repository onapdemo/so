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

package org.openecomp.mso.adapters.nwrest;



import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.annotations.providers.NoJackson;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("rollbackNetworkResponse")
@XmlRootElement(name = "rollbackNetworkResponse")
@NoJackson
public class RollbackNetworkResponse extends NetworkResponseCommon {

	private Boolean networkRolledBack;

	public RollbackNetworkResponse() {
		super();
	}
	
	public RollbackNetworkResponse(Boolean networkRolledBack, String messageId) {
		super(messageId);
		this.networkRolledBack = networkRolledBack;
	}

	public Boolean getNetworkRolledBack() {
		return networkRolledBack;
	}

	public void setNetworkRolledBack(Boolean networkRolledBack) {
		this.networkRolledBack = networkRolledBack;
	}
}

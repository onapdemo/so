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

package org.openecomp.mso.adapters.vnfrest;


import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.annotations.providers.NoJackson;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("rollbackVfModuleResponse")
@XmlRootElement(name = "rollbackVfModuleResponse")
@NoJackson
public class RollbackVfModuleResponse extends VfResponseCommon {
	private Boolean vfModuleRolledback;

	public RollbackVfModuleResponse() {
		super();
	}

	public RollbackVfModuleResponse(Boolean vfModuleRolledback, String messageId) {
		super(messageId);
		this.vfModuleRolledback = vfModuleRolledback;
	}

	public Boolean getVfModuleRolledback() {
		return vfModuleRolledback;
	}

	public void setVfModuleRolledback(Boolean vfModuleRolledback) {
		this.vfModuleRolledback = vfModuleRolledback;
	}
}

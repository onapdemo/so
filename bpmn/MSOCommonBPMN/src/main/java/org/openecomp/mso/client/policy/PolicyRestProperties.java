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

package org.openecomp.mso.client.policy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.openecomp.mso.bpmn.core.PropertyConfiguration;
import org.openecomp.mso.client.RestProperties;

public class PolicyRestProperties implements RestProperties {

	
	final Map<String, String> props;
	public PolicyRestProperties() {
		this.props = PropertyConfiguration.getInstance().getProperties("mso.bpmn.urn.properties");

	}
	@Override
	public URL getEndpoint() {
		try {
			return new URL(props.getOrDefault("policy.endpoint", ""));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public String getSystemName() {
		return "MSO";
	}
	
	public String getClientAuth() {
		return props.get("policy.client.auth");
	}
	
	public String getAuth() {
		return props.get("policy.auth");
	}
	
	public String getEnvironment() {
		return props.get("policy.environment");
	}

}

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

package org.openecomp.mso.bpmn.core.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("homingSolution")
public class HomingSolution  extends JsonWrapper implements Serializable  {

	private static final long serialVersionUID = 1L;

	private InventoryType inventoryType;
	private String serviceInstanceId;
	private String vnfHostname;
	private String cloudOwner;
	private String cloudRegionId;
	private String aicClli;
	private String aicVersion;
	private String ucpeId; //TODO Remove?
	private List<String> entitlementPoolList;
	private List<String> licenseKeyGroupList;


	public InventoryType getInventoryType(){
		return inventoryType;
	}

	public void setInventoryType(InventoryType inventoryType){
		this.inventoryType = inventoryType;
	}

	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public void setServiceInstanceId(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	public String getVnfHostname(){
		return vnfHostname;
	}

	public void setVnfHostname(String vnfHostname){
		this.vnfHostname = vnfHostname;
	}


	public String getCloudOwner(){
		return cloudOwner;
	}


	public void setCloudOwner(String cloudOwner){
		this.cloudOwner = cloudOwner;
	}

	public String getCloudRegionId() {
		return cloudRegionId;
	}

	public void setCloudRegionId(String cloudRegionId) {
		this.cloudRegionId = cloudRegionId;
	}


	public String getAicClli(){
		return aicClli;
	}


	public void setAicClli(String aicClli){
		this.aicClli = aicClli;
	}


	public String getAicVersion(){
		return aicVersion;
	}


	public void setAicVersion(String aicVersion){
		this.aicVersion = aicVersion;
	}

	public String getUcpeId(){
		return ucpeId;
	}

	public void setUcpeId(String ucpeId){
		this.ucpeId = ucpeId;
	}

	public List<String> getEntitlementPoolList(){
		return entitlementPoolList;
	}

	public void setEntitlementPoolList(List<String> entitlementPoolList){
		this.entitlementPoolList = entitlementPoolList;
	}

	public List<String> getLicenseKeyGroupList(){
		return licenseKeyGroupList;
	}

	public void setLicenseKeyGroupList(List<String> licenseKeyGroupList){
		this.licenseKeyGroupList = licenseKeyGroupList;
	}
}
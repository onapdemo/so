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

package org.openecomp.mso.db.catalog.beans;


import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.openecomp.mso.db.catalog.utils.MavenLikeVersioning;

public class VnfResource extends MavenLikeVersioning implements Serializable {
	
	private static final long serialVersionUID = 768026109321305392L;

	private String modelUuid;
	private String modelInvariantUuid;
	private String modelName;
    private String toscaNodeType;
    private String description;
    private String orchestrationMode;
    private String aicVersionMin;
    private String aicVersionMax;
    private String heatTemplateArtifactUUId;
    private Timestamp created;
    private String modelVersion;
    private Set<VnfResourceCustomization> vnfResourceCustomizations;
    private Set<VfModule> vfModules;
    private List<VfModule> vfModuleList;
    private List<VfModuleCustomization> vfModuleCustomizations;

    public VnfResource () { }

    public String getOrchestrationMode () {
        return orchestrationMode;
    }

    public void setOrchestrationMode (String orchestrationMode) {
        this.orchestrationMode = orchestrationMode;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getTemplateId () {
        return heatTemplateArtifactUUId;
    }

    public void setTemplateId (String heatTemplateArtifactUUId) {
        this.heatTemplateArtifactUUId = heatTemplateArtifactUUId;
    }
    public String getHeatTemplateArtifactUUId () {
        return heatTemplateArtifactUUId;
    }

    public void setHeatTemplateArtifactUUId (String heatTemplateArtifactUUId) {
        this.heatTemplateArtifactUUId = heatTemplateArtifactUUId;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public String getAicVersionMin() {
		return this.aicVersionMin;
	}

	public void setAicVersionMin(String aicVersionMin) {
		this.aicVersionMin = aicVersionMin;
	}

	public String getAicVersionMax() {
		return this.aicVersionMax;
	}

	public void setAicVersionMax(String aicVersionMax) {
		this.aicVersionMax = aicVersionMax;
	}

	public String getModelInvariantUuid() {
		return this.modelInvariantUuid;
	}

	public void setModelInvariantUuid(String modelInvariantUuid) {
		this.modelInvariantUuid = modelInvariantUuid;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelUuid() {
		return modelUuid;
	}

	public void setModelUuid(String modelUuid) {
		this.modelUuid = modelUuid;
	}

	public String getModelInvariantId() {
		return this.modelInvariantUuid;
	}

	public String getToscaNodeType() {
		return toscaNodeType;
	}

	public void setToscaNodeType(String toscaNodeType) {
		this.toscaNodeType = toscaNodeType;
	}

	public Set<VnfResourceCustomization> getVnfResourceCustomizations() {
		return vnfResourceCustomizations;
	}

	public void setVnfResourceCustomizations(Set<VnfResourceCustomization> vnfResourceCustomizations) {
		this.vnfResourceCustomizations = vnfResourceCustomizations;
	}

	public Set<VfModule> getVfModules() {
		return vfModules;
	}

	public void setVfModules(Set<VfModule> vfModules) {
		this.vfModules = vfModules;
	}

	public List<VfModuleCustomization> getVfModuleCustomizations() {
		return this.vfModuleCustomizations == null ? new ArrayList<VfModuleCustomization>() : this.vfModuleCustomizations;
	}
	public void setVfModuleCustomizations(ArrayList<VfModuleCustomization> vfModuleCustomizations) {
		this.vfModuleCustomizations = vfModuleCustomizations;
	}
	public void addVfModuleCustomization(VfModuleCustomization vfmc) {
		if (vfmc != null) {
			if (this.vfModuleCustomizations != null) {
				this.vfModuleCustomizations.add(vfmc);
			} else {
				this.vfModuleCustomizations = new ArrayList<VfModuleCustomization>();
				this.vfModuleCustomizations.add(vfmc);
			}
	}
	}

	public void addVfModule(VfModule vfm) {
		if (vfm != null) {
			if (this.vfModules != null) {
				this.vfModules.add(vfm);
			} else {
				this.vfModules = new HashSet<VfModule>();
				this.vfModules.add(vfm);
			}
		}
	}
	public ArrayList<VfModule> getVfModuleList() {
		if (this.vfModules == null || this.vfModules.size() < 1) {
			return null;
	}
		ArrayList<VfModule> list = new ArrayList<VfModule>();
		for (VfModule vfm : this.vfModules) {
			list.add(vfm);
	}
		return list;
	}
	
	public String getModelVersion() {
		return modelVersion;
	}

	public void setModelVersion(String modelVersion) {
		this.modelVersion = modelVersion;
	}

	@Override
	public String toString () {
		StringBuffer buf = new StringBuffer();

		buf.append("VNF=");
		buf.append(",modelVersion=");
		buf.append(modelVersion);
		buf.append(",mode=");
		buf.append(orchestrationMode);
		buf.append(",heatTemplateArtifactUUId=");
		buf.append(heatTemplateArtifactUUId);
		buf.append(",envtId=");
		buf.append(",asdcUuid=");
		buf.append(",aicVersionMin=");
		buf.append(this.aicVersionMin);
		buf.append(",aicVersionMax=");
		buf.append(this.aicVersionMax);
        buf.append(",modelInvariantUuid=");
        buf.append(this.modelInvariantUuid);
        buf.append(",modelVersion=");
        buf.append(",modelCustomizationName=");
        buf.append(",modelName=");
        buf.append(this.modelName);
        buf.append(",serviceModelInvariantUUID=");
		buf.append(",modelCustomizationUuid=");
        buf.append(",toscaNodeType=");
        buf.append(toscaNodeType);

		if (created != null) {
			buf.append(",created=");
			buf.append(DateFormat.getInstance().format(created));
		}
		
		for(VnfResourceCustomization vrc : vnfResourceCustomizations) {
			buf.append("/n" + vrc.toString());
			}
		
		for(VfModule vfm : vfModules) {
			buf.append("/n" + vfm.toString());
		}
		return buf.toString();
    }

}

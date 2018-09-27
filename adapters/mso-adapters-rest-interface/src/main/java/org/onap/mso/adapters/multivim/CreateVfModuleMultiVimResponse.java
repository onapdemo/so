package org.onap.mso.adapters.multivim;

import org.codehaus.jackson.map.annotate.JsonRootName;
import org.jboss.resteasy.annotations.providers.NoJackson;
import org.openecomp.mso.adapters.vnfrest.VfResponseCommon;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

@JsonRootName("CreateVfModuleMultiVimResponse")
@XmlRootElement(name = "CreateVfModuleMultiVimResponse")
@NoJackson
public class CreateVfModuleMultiVimResponse extends VfResponseCommon {

  private String vnfId;
  private String vfModuleStackId;
  private Boolean vfModuleCreated;
  private Map<String,String> vfModuleOutputs = new HashMap<>();

  public CreateVfModuleMultiVimResponse() {
    super();
  }

  public CreateVfModuleMultiVimResponse(String vnfId, String vfModuleStackId,
                                        Boolean vfModuleCreated,
                                        Map<String, String> vfModuleOutputs, String messageId) {
    super(messageId);
    this.vnfId = vnfId;
    this.vfModuleStackId = vfModuleStackId;
    this.vfModuleCreated = vfModuleCreated;
    this.vfModuleOutputs = vfModuleOutputs;
  }

  public String getVnfId() {
    return vnfId;
  }

  public void setVnfId(String vnfId) {
    this.vnfId = vnfId;
  }

  public String getVfModuleStackId() {
    return vfModuleStackId;
  }

  public void setVfModuleStackId(String vfModuleStackId) {
    this.vfModuleStackId = vfModuleStackId;
  }

  public Boolean getVfModuleCreated() {
    return vfModuleCreated;
  }

  public void setVfModuleCreated(Boolean vfModuleCreated) {
    this.vfModuleCreated = vfModuleCreated;
  }

  public Map<String, String> getVfModuleOutputs() {
    return vfModuleOutputs;
  }

  public void setVfModuleOutputs(Map<String, String> vfModuleOutputs) {
    this.vfModuleOutputs = vfModuleOutputs;
  }
}

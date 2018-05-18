package org.onap.mso.adapters.multivim;

import org.codehaus.jackson.map.annotate.JsonRootName;
import org.jboss.resteasy.annotations.providers.NoJackson;
import org.openecomp.mso.adapters.vnfrest.VfRequestCommon;
import org.openecomp.mso.entity.MsoRequest;

import javax.xml.bind.annotation.XmlRootElement;

/* README
* 1) Used JAXB/Jettison - see @NoJackson annotation on class to get RootElements REad by RestEasy
 * 2) to output json see toJSonString method below which required the @JsonRootName annotation and the WRAP_ROOT feature enabled
 * 3) Trying to work with RESTEASY JACKSON and JAXB/JETTISON to conform to Json input/output specs
*/
@JsonRootName("createVfModuleMultiVimRequest")
@XmlRootElement(name = "createVfModuleMultiVimRequest")
@NoJackson
public class CreateVfModuleMultiVimRequest extends VfRequestCommon {

  private String vnfId;
  private String vnfModelName;
  private String vfModuleName;
  private String multicloudName;
  private CloudType cloudType;
  private String toscaCsarArtifactUuid;
  private String inputs;

  private MsoRequest msoRequest = new MsoRequest();

  public String getVnfId() {
    return vnfId;
  }

  public void setVnfId(String vnfId) {
    this.vnfId = vnfId;
  }

  public String getInputs() {
    return inputs;
  }

  public void setInputs(String inputs) {
    this.inputs = inputs;
  }

  public String getVfModuleName() {
    return vfModuleName;
  }

  public void setVfModuleName(String vfModuleName) {
    this.vfModuleName = vfModuleName;
  }

  public String getMulticloudName() {
    return multicloudName;
  }

  public void setMulticloudName(String multicloudName) {
    this.multicloudName = multicloudName;
  }

  public CloudType getCloudType() {
    return cloudType;
  }

  public void setCloudType(CloudType cloudType) {
    this.cloudType = cloudType;
  }

  public String getToscaCsarArtifactUuid() {
    return toscaCsarArtifactUuid;
  }

  public void setToscaCsarArtifactUuid(String toscaCsarArtifactUuid) {
    this.toscaCsarArtifactUuid = toscaCsarArtifactUuid;
  }

  public String getVnfModelName() {
    return vnfModelName;
  }

  public void setVnfModelName(String vnfModelName) {
    this.vnfModelName = vnfModelName;
  }
}

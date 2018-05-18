package org.onap.mso.adapters.multivim;


import org.onap.mso.adapters.rest.RESTException;
import org.openecomp.mso.properties.MsoPropertiesException;

import javax.xml.ws.Holder;
import java.util.Map;

public interface MultiVimAdapter {

    public void createVfModule(CloudType cloudType, String vnfId, String vnfModelName, String vfModuleName, String multicloudName, String toscaCsarArtifactUuid,
                               String inputs, Boolean isSync, Holder<String> vfModuleStackId,
                               Holder<Map<String, String>> outputs, Holder<Boolean> vfModuleCreated) throws Exception;

    public String getStack(String stackId, String multicloudName) throws MsoPropertiesException, RESTException;
}

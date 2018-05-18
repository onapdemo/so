package org.onap.mso.adapters.multivim.util;

import org.onap.mso.adapters.multivim.MultiVimConstants;
import org.onap.mso.adapters.rest.APIResponse;
import org.onap.mso.adapters.rest.HttpHeader;
import org.onap.mso.adapters.rest.RESTClient;
import org.onap.mso.adapters.rest.RESTException;
import org.openecomp.mso.properties.MsoJavaProperties;
import org.openecomp.mso.properties.MsoPropertiesException;
import org.openecomp.mso.properties.MsoPropertiesFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiVimUtils {
    private static final Map<String, String> tokenCache = new ConcurrentHashMap<>();
    private static final MsoPropertiesFactory msoPropertiesFactory = new MsoPropertiesFactory();

    public String getToken(String projectId,  String multicloudName) {
        if (tokenCache.containsKey(projectId)) {
            return tokenCache.get(projectId);
        } else {
            String token = getAuthToken(projectId, multicloudName);
            if (token != null) {
                tokenCache.put(projectId, token);
                return token;
            }
        }
        return null;
    }

    // The cache key is "tenantId:cloudId"
    private String getAuthToken(final String projectId,  String multicloudName) {
        String body = "{\"auth\":{\"scope\":{\"project\":{\"id\":\"?\"}}," +
                "\"identity\":{\"password\":{\"user\":{\"domain\":{\"name\":\"Default\"},\"password\":\"vmware\",\"name\":\"admin\"}}," +
                "\"methods\":[\"password\"]}}}";

        try {
            final MsoJavaProperties msoJavaProperties = msoPropertiesFactory.getMsoJavaProperties(MultiVimConstants.MSO_PROP_MULTIVIM_ADAPTER);
            final String msb_endpoint_url = msoJavaProperties.getProperty(MultiVimConstants.MSB_ENDPOINT_URL, null);
            final RESTClient client = new RESTClient(msb_endpoint_url + "/" + multicloudName + "/identity/v3/auth/tokens");
            APIResponse restResponse = client
                    .setHeader("Content-Type", "application/json")
                    .httpPost(body);

            if (restResponse.getStatusCode() == 200 || restResponse.getStatusCode() == 201) {
                for (HttpHeader a : restResponse.getAllHeaders()) {
                    if (a.getName().equals("x-subject-token")) {
                        return a.getValue();
                    }
                }

            }
        } catch (MsoPropertiesException | RESTException e) {
            e.printStackTrace();
        }

        return null;
    }
}
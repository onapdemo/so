package org.openecomp.mso.bpmn.mock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class StubResponseESR {

    public static void mockCloudRegionESR(int statusCode, String cloudOwner, String cloudRegionId, String responseBodyFile) {
        stubFor(get(urlEqualTo("/aai/v9/cloud-infrastructure/cloud-regions/cloud-region/" + cloudOwner + "/" + cloudRegionId + "?depth=all"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("content-type", "application/xml")
                        .withBodyFile(responseBodyFile)));
    }
}

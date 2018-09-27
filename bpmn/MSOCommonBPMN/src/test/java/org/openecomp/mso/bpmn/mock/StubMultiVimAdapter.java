package org.openecomp.mso.bpmn.mock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class StubMultiVimAdapter {

    public static void mockMultiVimPost(int statusCode, String vnfId ) {
        stubFor(post(urlEqualTo("/org/onap/mso/adapters/multivim/rest/v1/vnf/" + vnfId + "/vf-modules"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("content-type", "application/xml")));
    }
}

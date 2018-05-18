package org.openecomp.mso.bpmn.mock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class StubHAService {

    public static void mockHASPost(int statusCode) {
        stubFor(post(urlEqualTo("/api/oof/v1/placement"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("content-type", "text/plain")));
    }
}

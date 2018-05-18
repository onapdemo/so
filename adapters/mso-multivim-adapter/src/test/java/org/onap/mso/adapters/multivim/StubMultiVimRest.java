package org.onap.mso.adapters.multivim;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class StubMultiVimRest {

    public static void mockCreateVfmoduleHttpPost(final String multicloudName, final int statusCode, final String vfModuleName,
                                                  final String responseBody) {
        stubFor(post(urlEqualTo("/api/multicloud-azure/v0/" + multicloudName + "/multipart/createStack/" + vfModuleName))
                .willReturn(aResponse()
                        .withBody(responseBody)
                        .withStatus(statusCode)
                        .withHeader("content-type", "application/json   ")));

    }

    public static void mockGetStackGet(final String multicloudName, final int statusCode, final String responseBody) {
        stubFor(get(urlEqualTo("/api/multicloud-azure/v0/" + multicloudName + "/stacks/10"))
                .willReturn(aResponse()
                        .withBody(responseBody)
                        .withStatus(statusCode)
                        .withHeader("content-type", "application/json   ")));
    }

    public static void mockGetStackBaReq(final String multicloudName, final int statusCode, final String responseBody) {
        stubFor(get(urlEqualTo("/api/multicloud-azure/v0/" + multicloudName + "/stacks/11"))
                .willReturn(aResponse()
                        .withBody(responseBody)
                        .withStatus(statusCode)
                        .withHeader("content-type", "application/json   ")));
    }

    public static void mockBpel(int status) {
        stubFor(post(urlEqualTo("/bpel/test"))
                .willReturn(aResponse()
                        .withBody("")
                        .withStatus(status)
                        .withHeader("content-type", "application/json   ")));
    }

    public static void mockNotification(int status, String body) {
        stubFor(post(urlEqualTo("/notif"))
                .willReturn(aResponse()
                        .withBody(body)
                        .withStatus(status)
                        .withHeader("content-type", "application/json   ")));
    }

    public static void verifyNotification(int responseCode, String bodyContains) {
        verify(postRequestedFor(urlEqualTo("/notif"))
            .withRequestBody(containing(bodyContains)));
    }
}
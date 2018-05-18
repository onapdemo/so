package org.onap.mso.adapters.multivim;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openecomp.mso.properties.MsoPropertiesException;
import org.openecomp.mso.properties.MsoPropertiesFactory;

import static org.junit.Assert.*;

public class BpelRestClientTest {

    private BpelRestClient client = new BpelRestClient();
    private String uri = "http://localhost:28090/bpel/test";
    private String request = "";

    private static MsoPropertiesFactory msoPropertiesFactory = new MsoPropertiesFactory();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(28090);

    @BeforeClass
    public static void setUp() throws MsoPropertiesException {

        ClassLoader classLoader = MultiVimAdapterRestTest.class.getClassLoader();
        String path = classLoader.getResource("mso.multivim.properties").toString().substring(5);

        System.setProperty("mso.config.path", "src/test/resources/");

        msoPropertiesFactory.initializeMsoProperties("MSO_PROP_MULTIVIM_ADAPTER", path);
    }

    @AfterClass
    public static void tearDown() {
        msoPropertiesFactory.removeAllMsoProperties();
    }

    @Test
    public void bpelPostSuccess() {
        StubMultiVimRest.mockBpel(200);
        final boolean success = client.bpelPost(request, uri, false);
        assertTrue(success);
        assertEquals(200, client.getLastResponseCode());
        assertEquals("", client.getLastResponse());
    }

    @Test
    public void bpelPostRetryFail() {
        StubMultiVimRest.mockBpel(500);
        final boolean success = client.bpelPost(request, uri, false);
        assertFalse(success);
        assertEquals(500, client.getLastResponseCode());
        assertEquals("", client.getLastResponse());
    }
}
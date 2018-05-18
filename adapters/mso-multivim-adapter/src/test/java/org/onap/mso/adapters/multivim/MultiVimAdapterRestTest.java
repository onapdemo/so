package org.onap.mso.adapters.multivim;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import mockit.Mock;
import mockit.MockUp;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openecomp.mso.db.catalog.CatalogDatabase;
import org.openecomp.mso.db.catalog.beans.ToscaCsar;
import org.openecomp.mso.entity.MsoRequest;
import org.openecomp.mso.properties.MsoPropertiesException;
import org.openecomp.mso.properties.MsoPropertiesFactory;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;


public class MultiVimAdapterRestTest {

    private static final String vfModuleName = "CB_BASE_VF";
    private static final String amdocsAzureVfw = "AmdocsAzureVfw";
    private static final String ariaResponseCreateVfModule1 = "{\"id\":10}";
    private static final String ariaResponseGetStack1 = "{\"execution_id\":\"10\",\"service_name\":\"test15\",\"service_template_name\":\"test15-template\",\"status\":\"succeeded\",\"workflow_name\":\"start\"}";
    private static MsoPropertiesFactory msoPropertiesFactory = new MsoPropertiesFactory();
    private final String vnfId = "7d2e2469-8708-47c3-a0d4-73fa28a8a50c";
    private final String msoRequestId = "466ec94f-f615-4a97-a7f8-df21d4997321";
    private final String serviceInstanceId = "466ec94f-f615-4a97-a7f8-df21d499755";
    private final String toscaCsarArtifactUuid = "aaaa5198-b70a-49db-a5eb-fa080cbfc3b7";
    private final int badResponseCode = 400;
    private final int responseCode = 200;
    private final String badResponseBody = "{\"status\":\"badrequest\"}";

    private final String multicloudName = "ATT_EastUS";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(28090);
    private String notif = "http://localhost:28090/notif";

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
    public void sunnyDay() {

        MockUp mockUp = new MockUp<CatalogDatabase>() {

            @Mock
            public ToscaCsar getToscaCsarByUuid(String artifactUuid) {
                ToscaCsar toscaCsar = new ToscaCsar();
                toscaCsar.setName("service-AmdocsAzureVfwService-csar.csar");
                return toscaCsar;
            }
        };

        StubMultiVimRest.mockCreateVfmoduleHttpPost(multicloudName, 202, vfModuleName, ariaResponseCreateVfModule1);
        StubMultiVimRest.mockGetStackGet(multicloudName, responseCode, ariaResponseGetStack1);

        MsoRequest msoRequest = new MsoRequest();
        msoRequest.setRequestId(msoRequestId);
        msoRequest.setServiceInstanceId(serviceInstanceId);

        CreateVfModuleMultiVimRequest cVfMMVRequest = getVimRequest();
        cVfMMVRequest.setNotificationUrl(null);

        MultiVimAdapterRest multiVimAdapterRest = new MultiVimAdapterRest();
        Response response = multiVimAdapterRest.createVfModule(vnfId, cVfMMVRequest);

        assertNotNull(response);
        assertEquals(202, response.getStatus());
        final CreateVfModuleMultiVimResponse entity = (CreateVfModuleMultiVimResponse) response.getEntity();
        assertNotNull(entity);
        assertTrue(entity.getVfModuleCreated());

        mockUp.tearDown();
    }

    @Test
    public void sunnyDayAsync() {

        MockUp mockUp = new MockUp<CatalogDatabase>() {

            @Mock
            public ToscaCsar getToscaCsarByUuid(String artifactUuid) {
                ToscaCsar toscaCsar = new ToscaCsar();
                toscaCsar.setName("service-AmdocsAzureVfwService-csar.csar");
                return toscaCsar;
            }
        };

        StubMultiVimRest.mockCreateVfmoduleHttpPost(multicloudName, 202, vfModuleName, ariaResponseCreateVfModule1);
        StubMultiVimRest.mockGetStackGet(multicloudName, responseCode, ariaResponseGetStack1);
        StubMultiVimRest.mockNotification(responseCode, ariaResponseGetStack1);

        MsoRequest msoRequest = new MsoRequest();
        msoRequest.setRequestId(msoRequestId);
        msoRequest.setServiceInstanceId(serviceInstanceId);

        CreateVfModuleMultiVimRequest cVfMMVRequest = getVimRequest();

        MultiVimAdapterRest multiVimAdapterRest = new MultiVimAdapterRest();
        Response response = multiVimAdapterRest.createVfModule(vnfId, cVfMMVRequest);

        assertNotNull(response);
        assertEquals(202, response.getStatus());

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ignored) {
        }
        StubMultiVimRest.verifyNotification(responseCode, "<vfModuleCreated>true</vfModuleCreated>");

        mockUp.tearDown();
    }

    @Test
    public void createVfModuleNoCsarFail() {

        MockUp mockUp = new MockUp<CatalogDatabase>() {

            @Mock
            public ToscaCsar getToscaCsarByUuid(String artifactUuid) {
                ToscaCsar toscaCsar = new ToscaCsar();
                toscaCsar.setName("Invalid-Csar.csar");
                return toscaCsar;
            }
        };

        StubMultiVimRest.mockCreateVfmoduleHttpPost(multicloudName, 202, vfModuleName, ariaResponseCreateVfModule1);
        StubMultiVimRest.mockGetStackGet(multicloudName, responseCode, ariaResponseGetStack1);

        MsoRequest msoRequest = new MsoRequest();
        msoRequest.setRequestId(msoRequestId);
        msoRequest.setServiceInstanceId(serviceInstanceId);

        CreateVfModuleMultiVimRequest cVfMMVRequest = getVimRequest();
        cVfMMVRequest.setNotificationUrl(null);

        MultiVimAdapterRest multiVimAdapterRest = new MultiVimAdapterRest();
        Response response = multiVimAdapterRest.createVfModule(vnfId, cVfMMVRequest);

        assertNotNull(response);
        assertEquals(500, response.getStatus());

        mockUp.tearDown();
    }

    @Test
    public void badRequest() {

        MsoRequest msoRequest = new MsoRequest();
        msoRequest.setRequestId(msoRequestId);
        msoRequest.setServiceInstanceId(serviceInstanceId);

        CreateVfModuleMultiVimRequest cVfMMVRequest = getVimRequest();

        MultiVimAdapterRest multiVimAdapterRest = new MultiVimAdapterRest();
        Response response = multiVimAdapterRest.createVfModule("SomeJunk_vnfId", cVfMMVRequest);
        assertNotNull(response);
        assertEquals(badResponseCode, response.getStatus());
        String badRequestResponse = response.getEntity().toString();
        System.out.print("\n\n\tbadRequestResponse = " + badRequestResponse + "\n\n");
        assertTrue(badRequestResponse.contains("does not match content"));
    }

    @Test
    public void getStack() {

        StubMultiVimRest.mockGetStackGet(multicloudName, responseCode, ariaResponseGetStack1);

        MultiVimAdapterRest multiVimAdapterRest = new MultiVimAdapterRest();
        Response response = multiVimAdapterRest.getStacks(multicloudName, "10");

        assertNotNull(response);
        assertEquals(responseCode, response.getStatus());
        String responseStr = response.getEntity().toString();
        assertTrue(responseStr.contains("\"status\":\"succeeded\""));
    }

    @Test
    public void getInvalidStackIdStack() {

        StubMultiVimRest.mockGetStackBaReq(multicloudName, badResponseCode, badResponseBody);
        MultiVimAdapterRest multiVimAdapterRest = new MultiVimAdapterRest();
        final Response response = multiVimAdapterRest.getStacks(multicloudName, "11");
        assertNotNull(response);
        assertEquals(badResponseCode, response.getStatus());
        assertEquals(badResponseBody, response.getEntity().toString());
    }

    private CreateVfModuleMultiVimRequest getVimRequest(){

        CreateVfModuleMultiVimRequest cVfMMVRequest = new CreateVfModuleMultiVimRequest();
        cVfMMVRequest.setVnfId(vnfId);
        cVfMMVRequest.setVnfModelName(amdocsAzureVfw);
        cVfMMVRequest.setVfModuleName(vfModuleName);
        cVfMMVRequest.setCloudType(CloudType.AZURE);
        cVfMMVRequest.setMulticloudName(multicloudName);
        cVfMMVRequest.setMulticloudName(multicloudName);
        cVfMMVRequest.setToscaCsarArtifactUuid(toscaCsarArtifactUuid);
        cVfMMVRequest.setNotificationUrl(notif);
        cVfMMVRequest.setInputs(getInputs());

        return cVfMMVRequest;
    }

    private String getInputs() {

        JSONObject jsonObj = new JSONObject();
        JSONObject inputsObject = new JSONObject();
        jsonObj.put("inputs", inputsObject);

        inputsObject.put("subscription_id", "asdf");
        inputsObject.put("client_id", "asdfsd");
        inputsObject.put("tenant_id", "tenantId");
        inputsObject.put("client_secret", "asdfs");
        inputsObject.put("location", "eastus");
        inputsObject.put("azure_resource_group_name", "asdfs");
        inputsObject.put("retry_after", 30);
        inputsObject.put("azure_domain_name_label", "sdfsdf");
        inputsObject.put("azure_domain_fqdn", "sdfgdfsg");
        inputsObject.put("vm_name", "chorus-helloworld");
        inputsObject.put("vm_size", "Standard_D2s_v3");
        inputsObject.put("vm_os_family", "linux");
        inputsObject.put("vm_image_publisher", "Canonical");
        inputsObject.put("vm_image_offer", "UbuntuServer");
        inputsObject.put("vm_image_sku", "16.04-LTS");
        inputsObject.put("vm_image_version", "latest");
        inputsObject.put("vm_os_username", "sdfg");
        inputsObject.put("vm_os_password", "dfgdfsg");
        inputsObject.put("ssh_username", "dsfgdf");
        inputsObject.put("private_key_path", "gdsfgdfs");
        inputsObject.put("vm_public_key_data", "dfgdfs");
        inputsObject.put("vm_public_key_path", "/home/chorus/.ssh/authorized_keys");
        inputsObject.put("vm_public_key_auth_only", true);
        inputsObject.put("use_existing_security_group", false);
        inputsObject.put("use_existing_vm", false);

        return jsonObj.toString();
    }

}

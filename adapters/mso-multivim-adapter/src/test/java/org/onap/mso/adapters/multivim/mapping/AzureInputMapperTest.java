package org.onap.mso.adapters.multivim.mapping;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.mso.adapters.multivim.mapping.input.AzureInputMapper;

public class AzureInputMapperTest {

    private String inputs = "{" +
            "\"inputs\": {" +
            "\"image_name\": \"image_name\"," +
            "\"flavor_name\": \"m1.medium\"," +
            "\"public_net_id\": \"public_net_id\"," +
            "\"vfw_private_ip_0\": 123" +
            "}" +
            "}";
    @BeforeClass
    public static void setUp() {

        ClassLoader classLoader = AzureInputMapperTest.class.getClassLoader();
        String path = classLoader.getResource("azure_key_mapping.json").toString().substring(5);
        path = path.substring(0, path.lastIndexOf("/") + 1);

        System.setProperty("mso.config.path", path);
    }

    @Test
    public void test() throws Exception {

        AzureInputMapper azureMapper = null;
        azureMapper = new AzureInputMapper();

        Assert.assertNotNull(azureMapper);
        String newInputs = azureMapper.mapInputs(inputs);

        Assert.assertTrue(newInputs.contains("\"vm_size\":\"Standard_D2\""));
    }
}

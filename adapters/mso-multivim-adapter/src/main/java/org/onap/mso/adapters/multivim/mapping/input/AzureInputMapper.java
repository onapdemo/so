package org.onap.mso.adapters.multivim.mapping.input;

import java.io.IOException;

public class AzureInputMapper extends InputMapper {


    public AzureInputMapper() throws IOException {
    }

    @Override
    protected void initMapperProperties() throws IOException {

        keyMapperProperties = loadKeyMapperProperties();

        valueMapperProperties = loadValueMapperProperties();
    }

    private MapperProperties loadKeyMapperProperties() throws IOException {

        String filePath = InputsMapperContsants.CONFIG_DIR +
                InputsMapperContsants.AZURE_INPUTS_KEY_MAPING_FILE;

        return MapperPropertiesFactory.getMapperProperties(filePath);
    }

    private MapperProperties loadValueMapperProperties() throws IOException {

        String filePath = InputsMapperContsants.CONFIG_DIR +
                InputsMapperContsants.AZURE_INPUTS_VALUE_MAPING_FILE;

        return MapperPropertiesFactory.getMapperProperties(filePath);
    }
}

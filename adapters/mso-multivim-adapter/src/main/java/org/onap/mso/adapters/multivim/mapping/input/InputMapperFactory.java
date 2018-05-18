package org.onap.mso.adapters.multivim.mapping.input;


import org.onap.mso.adapters.multivim.CloudType;

public class InputMapperFactory {

    public static InputMapper getInputMapper(final CloudType cloudType) throws Exception {

        switch (cloudType) {

            case AZURE:
                return new AzureInputMapper();

            default:
                throw new Exception("No Mapper is supported for cloudType \"" + cloudType + "\"");
        }
    }
}

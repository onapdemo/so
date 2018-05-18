package org.onap.mso.adapters.multivim.mapping.input;

import java.io.IOException;

public class MapperPropertiesFactory {

    private static boolean isJsonFile(final String propertiesFilePath) {

        return propertiesFilePath.endsWith(".json");
    }


    private static boolean isJavaPropertiesFile (final String propertiesFilePath) {
        return propertiesFilePath.endsWith(".properties");
    }

    public static MapperProperties getMapperProperties(final String propertiesFilePath) throws IOException {

        if(isJsonFile(propertiesFilePath)){

            return new JsonMapperProperties(propertiesFilePath);
        }

        if(isJavaPropertiesFile(propertiesFilePath)){

            return new JavaMapperProperties(propertiesFilePath);
        }

        throw new RuntimeException("Unsupported file type - " + propertiesFilePath);
    }
}

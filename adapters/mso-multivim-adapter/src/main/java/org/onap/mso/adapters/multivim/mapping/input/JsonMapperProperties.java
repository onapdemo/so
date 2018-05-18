package org.onap.mso.adapters.multivim.mapping.input;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openecomp.mso.logger.MessageEnum;
import org.openecomp.mso.logger.MsoLogger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class JsonMapperProperties extends MapperProperties {

    private static final MsoLogger LOGGER = MsoLogger.getMsoLogger(MsoLogger.Catalog.RA);

    private Map<String, String> keyMapping;

    public JsonMapperProperties(String propFilename) throws IOException{
        super(propFilename);
    }

    @Override
    protected synchronized void loadProperties(String propFilename) throws IOException{

        ObjectMapper objectMapper = new ObjectMapper();

        try (FileReader fileReader = new FileReader(propFilename)) {

            keyMapping = objectMapper.readValue(fileReader, new TypeReference<Map<String, String>>() {
            });

        } catch (IOException e) {

            LOGGER.error(MessageEnum.RA_CREATE_VNF_ERR, "", "createVfModule",
                    MsoLogger.ErrorCode.BusinessProcesssError, "Failed to load inputs mapping property - "
                            + propFilename, e);

            throw e;
        }
    }

    @Override
    public String getProperty(String propKey) {
        return keyMapping == null ? null : keyMapping.get(propKey);
    }

}

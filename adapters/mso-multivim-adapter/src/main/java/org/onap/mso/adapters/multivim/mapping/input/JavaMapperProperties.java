package org.onap.mso.adapters.multivim.mapping.input;

import org.openecomp.mso.logger.MessageEnum;
import org.openecomp.mso.logger.MsoLogger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class JavaMapperProperties extends MapperProperties {

    private static final MsoLogger LOGGER = MsoLogger.getMsoLogger(MsoLogger.Catalog.RA);

    private Properties mProperties = new Properties();

    public JavaMapperProperties(String propFilename) throws IOException {
        super(propFilename);
    }

    @Override
    protected synchronized void loadProperties(final String propFilename) throws IOException {

        mProperties.clear();

        try (FileReader fileReader = new FileReader(propFilename)) {
            mProperties.load(fileReader);
        } catch (IOException e) {
            LOGGER.error(MessageEnum.RA_CREATE_VNF_ERR, "", "createVfModule",
                    MsoLogger.ErrorCode.BusinessProcesssError, "Failed to load inputs mapping property - "
                            + propFilename, e);
            throw e;
        }
    }

    @Override
    public String getProperty(final String propKey) {
        return mProperties.getProperty(propKey);
    }

}

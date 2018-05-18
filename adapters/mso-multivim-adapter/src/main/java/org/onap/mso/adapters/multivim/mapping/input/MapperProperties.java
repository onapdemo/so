package org.onap.mso.adapters.multivim.mapping.input;

import java.io.IOException;

public abstract class MapperProperties {

    protected final String propFilename;

    protected MapperProperties(final String propFilename) throws IOException {

        this.propFilename = propFilename;
        loadProperties(propFilename);
    }

    protected abstract void loadProperties(final String propFilename) throws IOException;

    public abstract String getProperty(final String propKey);

    public String getPropertyOrDefault(String key, String defaultValue) {

        String value = getProperty(key);
        return value == null ? defaultValue : value;
    }
}

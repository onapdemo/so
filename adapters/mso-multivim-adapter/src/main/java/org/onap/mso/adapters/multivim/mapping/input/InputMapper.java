package org.onap.mso.adapters.multivim.mapping.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.openecomp.mso.logger.MessageEnum;
import org.openecomp.mso.logger.MsoLogger;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

public abstract class InputMapper {

    private static final MsoLogger LOGGER = MsoLogger.getMsoLogger(MsoLogger.Catalog.RA);

    protected MapperProperties keyMapperProperties;
    protected MapperProperties valueMapperProperties;

    protected InputMapper() throws IOException {

        initMapperProperties();
    }

    /**
     * This method should load & configure mapper properties for Keys & Values.
     * @throws IOException
     */
    protected abstract void initMapperProperties() throws IOException;

    protected String getNewKey(final String oldKey) {

        return keyMapperProperties.getProperty(oldKey);
    }

    protected String getNewKeyOrDefault(String oldKey, String defaultValue) {
        return keyMapperProperties.getPropertyOrDefault(oldKey, defaultValue);
    }

    protected String getNewValue(final String oldValue) {

        return valueMapperProperties.getProperty(oldValue);
    }

    protected String getNewValueOrDefault(String oldValue, String defaultValue) {
        return valueMapperProperties.getPropertyOrDefault(oldValue, defaultValue);
    }

    public String mapInputs(final String inputs) throws IOException, ParseException {

        JSONObject inputJsonObj = null;
        try {
            inputJsonObj = new JSONObject(inputs);
        } catch (ParseException pe) {
            LOGGER.error(MessageEnum.RA_CREATE_VNF_ERR, "", "mapInputs",
                    MsoLogger.ErrorCode.BusinessProcesssError, "Failed to convert inputs to JSON", pe);
            throw pe;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> inputMap = null;
        try {
            inputMap = objectMapper.readValue(inputJsonObj.getJSONObject("inputs").toString(), Map.class);
        } catch (IOException ioe) {
            LOGGER.error(MessageEnum.RA_CREATE_VNF_ERR, "", "mapInputs",
                    MsoLogger.ErrorCode.BusinessProcesssError, "Failed to Map inputs", ioe);
            throw ioe;
        }

        JSONObject jsonObj = new JSONObject("{\"inputs\":{}}");
        JSONObject mappingInputsObject = jsonObj.getJSONObject("inputs");

        for (Map.Entry<String, Object> entry : inputMap.entrySet()) {

            String newKey = getNewKeyOrDefault(entry.getKey(), entry.getKey());

            Object oldValue = entry.getValue();
            Object newValue = oldValue;
            if (oldValue != null && oldValue instanceof String) {
                newValue = getNewValueOrDefault(oldValue.toString(), oldValue.toString());
            }

            mappingInputsObject.put(newKey, newValue);
        }

        String mappedInputs = jsonObj.toString();

        LOGGER.debug("mapInputs(), mappedInputs = " + mappedInputs);

        return mappedInputs;

    }

}

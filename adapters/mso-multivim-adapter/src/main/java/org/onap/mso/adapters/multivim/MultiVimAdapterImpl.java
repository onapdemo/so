package org.onap.mso.adapters.multivim;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.onap.mso.adapters.multivim.mapping.input.InputMapper;
import org.onap.mso.adapters.multivim.mapping.input.InputMapperFactory;
import org.onap.mso.adapters.rest.APIResponse;
import org.onap.mso.adapters.rest.RESTClient;
import org.onap.mso.adapters.rest.RESTException;
import org.openecomp.mso.db.catalog.CatalogDatabase;
import org.openecomp.mso.db.catalog.beans.ToscaCsar;
import org.openecomp.mso.logger.MessageEnum;
import org.openecomp.mso.logger.MsoLogger;
import org.openecomp.mso.properties.MsoJavaProperties;
import org.openecomp.mso.properties.MsoPropertiesException;
import org.openecomp.mso.properties.MsoPropertiesFactory;

import javax.xml.ws.Holder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.onap.mso.adapters.multivim.MultiVimConstants.*;

public class MultiVimAdapterImpl implements MultiVimAdapter {

    private static final MsoPropertiesFactory msoPropertiesFactory = new MsoPropertiesFactory();
    private static final MsoLogger LOGGER = MsoLogger.getMsoLogger(MsoLogger.Catalog.RA);
    private static final int CREATE_POLL_INTERVAL_DEFAULT = 15;
    private static final int TIMEOUT_MINUTES_DEFAULT = 20;
    public final static Pattern DASH_PATTERN = Pattern.compile("[-]+");
    public final static Pattern UNDERSCORE_PATTERN = Pattern.compile("[_]+");
    public final static Pattern PLUS_PATTERN = Pattern.compile("[+]+");
    public final static Pattern SPACE_PATTERN = Pattern.compile("[ ]+");
    public final static Pattern DOT_PATTERN = Pattern.compile("[\\.]+");
    private final MsoJavaProperties msoJavaProperties;

    public MultiVimAdapterImpl() {
        try {
            msoJavaProperties = msoPropertiesFactory.getMsoJavaProperties(MSO_PROP_MULTIVIM_ADAPTER);
        } catch (MsoPropertiesException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createVfModule(CloudType cloudType, String vnfId, String vnfModelName, String vfModuleName, String multicloudName, String toscaCsarArtifactUuid,
                               String inputs, Boolean isSync, Holder<String> vfModuleStackId,
                               Holder<Map<String, String>> outputs, Holder<Boolean> vfModuleCreated) throws Exception {

        LOGGER.debug("Entering createVfModule of MultiVimAdapterImpl");
        final Path csarPath = getCsarPath(toscaCsarArtifactUuid);
        LOGGER.debug("Csar Path: " + Objects.toString(csarPath));
        if (csarPath == null || !Files.exists(csarPath)) {
            final String errorDesc = "Csar not found: location - " + Objects.toString(csarPath) + ", toscaCsarArtifactUuid - " + toscaCsarArtifactUuid;
            LOGGER.error(MessageEnum.RA_CREATE_VNF_ERR, "", "createVfModule", MsoLogger.ErrorCode.BusinessProcesssError, errorDesc);
            throw new RESTException(500, errorDesc);
        }


        InputMapper inputMapper = InputMapperFactory.getInputMapper(cloudType);
        String mappedInputs = inputMapper.mapInputs(inputs);

        File csarFile = null;
        File inputFile = null;
        try {
            csarFile = getInnerCsarFile(csarPath, vfModuleName, vnfModelName);
            inputFile = dumpInputs(mappedInputs, vfModuleName + "_input_values.json");

            final String msbEndpointUrl = msoJavaProperties.getProperty(MSB_ENDPOINT_URL, null);
            LOGGER.debug("msbEndpointUrl : " + msbEndpointUrl);
            LOGGER.debug("multicloudName : " + multicloudName);
            LOGGER.debug("vfModuleName : " + vfModuleName);

            String ariaURL = msbEndpointUrl + "/" + multicloudName + "/multipart/createStack/" + vfModuleName;
            LOGGER.debug("Aria URL : " + ariaURL);
            final RESTClient client = new RESTClient(ariaURL);
            final MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .addBinaryBody("service_template", csarFile)
                    .addBinaryBody("input_values", inputFile);

            final APIResponse restResponse = client
                    .setHeader("X-Auth-Token", "TBD")
                    .setHeader("File-Type", "csar")
                    .httpPost(builder);

            LOGGER.debug("Received response from MultiVim, code=" + restResponse.getStatusCode()
                    + ", resp=" + restResponse.getResponseBodyAsString());

            if (restResponse.getStatusCode() == 202) {
                final Map<String, String> responseMap = parseResponse(restResponse.getResponseBodyAsString());

                if (responseMap.containsKey("id")) {
                    final String executionId = responseMap.get("id");
                    if (isSync) {
                        LOGGER.debug("Inside Sync response ");
                        outputs.value = parseResponse(getStack(executionId, multicloudName));
                        vfModuleStackId.value = responseMap.get("id");
                        vfModuleCreated.value = true;
                        LOGGER.debug("Exiting createVfModule of MultiVimAdapterImpl ");
                    } else {
                        //pollForCompletion(executionId);
                        // Set a time limit on overall polling.
                        // Use the resource (template) timeout  (expressed in minutes)
                        // and add one poll interval to give azure a chance to fail on its own.
                        poll(vfModuleStackId, outputs, vfModuleCreated, executionId, multicloudName);
                    }
                } else {
                    final String msg = "Received unexpected response from Aria, missing id. Response body: " + restResponse.getResponseBodyAsString();
                    throw new RESTException(msg);
                }
            } else {
                final String msg = "failed: " + restResponse.getResponseBodyAsString();
                throw new RESTException(msg);
            }
        } catch (Exception e) {
            LOGGER.error(MessageEnum.RA_CREATE_VNF_ERR, "", "createVfModule",
                    MsoLogger.ErrorCode.BusinessProcesssError, "Failed to create VNF", e);
            throw e;
        } finally {
            cleanupTempFiles(csarFile, inputFile);
            LOGGER.debug("Exiting createVfModule of MultiVimAdapterImpl ");
        }
    }

    @Override
    public String getStack(String stackId, String multicloudName) throws MsoPropertiesException, RESTException {
        try {
            final MsoJavaProperties msoJavaProperties = msoPropertiesFactory.getMsoJavaProperties(MSO_PROP_MULTIVIM_ADAPTER);
            final String msbEndpointUrl = msoJavaProperties.getProperty(MSB_ENDPOINT_URL, null);
            final RESTClient client = new RESTClient(msbEndpointUrl + "/" + multicloudName + "/stacks/" + stackId);
            final APIResponse restResponse = client
//          .setHeader("Content-Type", "application/xml")
                    .setHeader("X-Auth-Token", "TBD")
                    .httpGet();
            if (restResponse.getStatusCode() == 200) {
                return restResponse.getResponseBodyAsString();
            }
            throw new RESTException(restResponse.getStatusCode(), restResponse.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error(MessageEnum.RA_QUERY_VNF_ERR, "", "getStack", MsoLogger.ErrorCode.BusinessProcesssError, "Exception - getStack", e);
            throw e;
        }
    }

    private void poll(Holder<String> vfModuleStackId, Holder<Map<String, String>> outputs, Holder<Boolean> vfModuleCreated, String executionId, String multicloudName)
            throws JSONException, MsoPropertiesException, RESTException {
        LOGGER.debug("Inside PollForCompletion for Async response");
        final int createPollInterval = msoJavaProperties.getIntProperty(POLL_INTERVAL, CREATE_POLL_INTERVAL_DEFAULT);
        final int timeoutMinutes = msoJavaProperties.getIntProperty(POLL_TIMEOUT, TIMEOUT_MINUTES_DEFAULT);
        LOGGER.debug("createPollInterval : " + createPollInterval);
        boolean createTimedOut = false;
        int pollTimeout = (timeoutMinutes * 60) + createPollInterval;
        LOGGER.debug("pollTimeout : " + pollTimeout);
        while (true) {
            Map<String, String> getStack = parseResponse(getStack(executionId, multicloudName));
            if ("started".equals(getStack.get("status"))) {
                // VM creation is still running.
                // Sleep and try again unless timeout has been reached
                if (pollTimeout <= 0) {
                    // Note that this should not occur, since there is a timeout specified
                    createTimedOut = true;
                    break;
                }
                try {
                    Thread.sleep(createPollInterval * 1000L);
                } catch (InterruptedException ignored) {
                }

                pollTimeout -= createPollInterval;
            } else if ("succeeded".equals(getStack.get("status"))) {
                LOGGER.debug("Received Success response");
                outputs.value = getStack;
                vfModuleStackId.value = executionId;
                vfModuleCreated.value = true;
                break;
            } else {
                final String msg = "failed: VM creation status: " + getStack.get("status");
                LOGGER.debug(msg);
                throw new RESTException(msg);
            }
        }
        if (createTimedOut) {
            final String msg = "failed: VM creation status: Timedout";
            LOGGER.debug(msg);
            throw new RESTException(msg);
        }
    }

    private File dumpInputs(String inputs, String name) throws IOException {
        File inputFile = new File(name);
        if (!inputFile.exists()) {
            inputFile.createNewFile();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(inputFile))) {
            bufferedWriter.write(inputs);
            bufferedWriter.flush();
        }

        return inputFile;
    }

    private void cleanupTempFiles(File csarFile, File inputFile) {
        if (csarFile != null && csarFile.exists()) {
            csarFile.delete();
        }
        if (inputFile != null && inputFile.exists()) {
            inputFile.delete();
        }
    }

    private File getInnerCsarFile(Path csarPath, String vfModuleName, String vnfModelName) throws Exception {
        final File file = new File(CSAR_DIR + vfModuleName + ".csar");
        try (ZipFile zipFile = new ZipFile(csarPath.toFile())) {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                //TODO - check for actual logic to find corect csar from parent csar.
                if (entry.getName().endsWith(".csar") && entry.getName().toLowerCase().contains(normalizeName(vnfModelName))) {
                    final InputStream csar = zipFile.getInputStream(entry);
                    FileUtils.copyInputStreamToFile(csar, file);
                }
            }
        }

        return file;
    }

    private String normalizeName(String strIn) {
        String str = DASH_PATTERN.matcher(strIn).replaceAll("");
        str = UNDERSCORE_PATTERN.matcher(str).replaceAll("");
        str = PLUS_PATTERN.matcher(str).replaceAll("");
        str = SPACE_PATTERN.matcher(str).replaceAll("");
        str = DOT_PATTERN.matcher(str).replaceAll("");
        str = str.toLowerCase();
        return str;
    }

    private Path getCsarPath(String toscaCsarArtifactUuid) {
        try (final CatalogDatabase db = CatalogDatabase.getInstance()) {
            final ToscaCsar result = db.getToscaCsarByUuid(toscaCsarArtifactUuid);
            if (result != null) {
                return Paths.get(CSAR_DIR, result.getName());
            }
        }

        return null;
    }

    private Map<String, String> parseResponse(String responseBodyAsString) throws JSONException {
        final Map<String, String> responseMap = new HashMap<>();
        final JSONObject json = new JSONObject(responseBodyAsString);
        final Iterator itr = json.keys();
        while (itr.hasNext()) {
            final String key = (String) itr.next();
            responseMap.put(key, json.get(key).toString());
        }

        return responseMap;
    }
}

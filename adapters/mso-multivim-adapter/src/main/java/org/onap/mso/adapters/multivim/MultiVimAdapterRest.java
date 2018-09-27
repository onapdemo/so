package org.onap.mso.adapters.multivim;

import org.apache.http.HttpStatus;
import org.onap.mso.adapters.rest.RESTException;
import org.openecomp.mso.adapters.vnfrest.VfModuleExceptionResponse;
import org.openecomp.mso.logger.MessageEnum;
import org.openecomp.mso.logger.MsoLogger;
import org.openecomp.mso.openstack.exceptions.MsoExceptionCategory;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.Holder;
import java.util.Map;

/**
 * This class services calls to the REST interface for Multi-Vim Modules (http://host:port/multivim/rest/v1/vnf)
 * Both XML and JSON can be produced/consumed.  Set Accept: and Content-Type: headers appropriately.  XML is the default.
 */
@Path("/v1/vnf")
public class MultiVimAdapterRest {

    private static MsoLogger LOGGER = MsoLogger.getMsoLogger(MsoLogger.Catalog.RA);
    private final MultiVimAdapter multiVimAdapter = new MultiVimAdapterImpl();

    @HEAD
    @GET
    @Path("/healthcheck")
    @Produces(MediaType.TEXT_HTML)
    public Response healthcheck() {
        String CHECK_HTML = "<!DOCTYPE html><html><head><meta charset=\"ISO-8859-1\"><title>Health Check</title></head><body>Application ready</body></html>";
        return Response.ok().entity(CHECK_HTML).build();
    }

    /*URL: http://localhost:8080/multivim/rest/v1/vnf/<aaivnfid>/vf-modules
       *REQUEST:
      <createVfModuleMultiVimRequest>
        <vnfId>test_14</vnfId>
        <vfModuleName>test_14</vfModuleName>
        <multicloudName>ATT_EastUS</multicloudName>
	    <cloudType>azure</cloudType>
        <toscaCsarArtifactUuid>aaaa5198-b70a-49db-a5eb-fa080cbfc3b7</toscaCsarArtifactUuid>
        <inputs>{
        "inputs":{
        }
      }</inputs>
      <notificationUrl>http://localhost:8446/multivim/callback</notificationUrl>
        <msoRequest>
          <requestId>7aff92f5-75c6-4e94-980f-73c63f207b11</requestId>
          <serviceInstanceId>4827566d-8066-4118-ba2e-34eb82120e6b</serviceInstanceId>
        </msoRequest>
      </createVfModuleMultiVimRequest>
    */
    @POST
    @Path("{aaiVnfId}/vf-modules")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createVfModule(@PathParam("aaiVnfId") String aaiVnfId, final CreateVfModuleMultiVimRequest req) {
        LOGGER.debug("Create VfModule enter inside MultiVimAdapterRest: " + req.toJsonString());
        if (aaiVnfId == null || !aaiVnfId.equals(req.getVnfId())) {
            LOGGER.debug("Req rejected - aaiVnfId not provided or doesn't match URL");
            return Response
                    .status(HttpStatus.SC_BAD_REQUEST)
                    .type(MediaType.TEXT_PLAIN)
                    .entity("vnfid in URL does not match content")
                    .build();
        }

        CreateVfModuleTask task = new CreateVfModuleTask(req);
        if (req.isSynchronous()) {
            // This is a synchronous request
            task.run();
            return Response
                    .status(task.getStatusCode())
                    .entity(task.getGenericEntityResponse())
                    .build();
        } else {
            // This is an asynchronous request
            try {
                Thread t1 = new Thread(task);
                t1.start();
            } catch (Exception e) {
                // problem handling create, send generic failure as sync resp to caller
                LOGGER.error(MessageEnum.RA_CREATE_VNF_ERR, "", "createVfModule", MsoLogger.ErrorCode.BusinessProcesssError, "Exception - createVfModule", e);
                return Response.serverError().build();
            }
            // send sync response (ACK) to caller
            LOGGER.debug("createVfModule exit");
            return Response.status(HttpStatus.SC_ACCEPTED).build();
        }
    }

    @GET
    @Path("stacks/{multicloudName}/{stackId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getStacks(@PathParam("multicloudName") String multicloudName, @PathParam("stackId") String stackId) {
        LOGGER.debug("Entering get stack for ID: " + stackId);
        String response = null;
        try {
            response = multiVimAdapter.getStack(stackId, multicloudName);
            LOGGER.debug("getStacks exit: response=" + response);
            return Response
                    .status(HttpStatus.SC_OK)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(response)
                    .build();
        } catch (RESTException e) {
            return Response
                    .status(e.getStatusCode())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(e.getErrorMessage())
                    .build();
        } catch (Exception e) {
            return Response
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(e.getMessage())
                    .build();
        }
    }

    private class CreateVfModuleTask implements Runnable {
        private final CreateVfModuleMultiVimRequest req;
        private CreateVfModuleMultiVimResponse response = null;
        private VfModuleExceptionResponse eresponse = null;

        CreateVfModuleTask(CreateVfModuleMultiVimRequest req) {
            this.req = req;
        }

        int getStatusCode() {
            return (response != null)
                    ? HttpStatus.SC_ACCEPTED
                    : eresponse.getCategory() == MsoExceptionCategory.USERDATA
                        ? HttpStatus.SC_BAD_REQUEST
                        : HttpStatus.SC_INTERNAL_SERVER_ERROR;
        }

        Object getGenericEntityResponse() {
            return (response != null) ? new GenericEntity<CreateVfModuleMultiVimResponse>(response) {
            } : null;
        }

        private String getResponse() {
            return response != null ? response.toXmlString() : eresponse.toXmlString();
        }

        @Override
        public void run() {
            LOGGER.debug("CreateVfModuleTask start");
            Holder<String> vfModuleStackId = new Holder<>();
            Holder<Map<String, String>> outputs = new Holder<>();
            Holder<Boolean> vfModuleCreated = new Holder<>();

            try {
                multiVimAdapter.createVfModule(req.getCloudType(), req.getVnfId(), req.getVfModuleModelName(),
                        req.getVfModuleName(), req.getMulticloudName(), req.getToscaCsarArtifactUuid(),
                        req.getInputs(), req.isSynchronous(), vfModuleStackId, outputs, vfModuleCreated);

                response = new CreateVfModuleMultiVimResponse(req.getVnfId(), vfModuleStackId.value,
                        vfModuleCreated.value, outputs.value, req.getMessageId());
            } catch (Exception e) {
                eresponse = new VfModuleExceptionResponse(e.getMessage(), MsoExceptionCategory.INTERNAL, false, req.getMessageId());
            }

            if (!req.isSynchronous()) {
                // Asynchronous Web Service Outputs
                BpelRestClient bpelClient = new BpelRestClient();
                bpelClient.bpelPost(getResponse(), req.getNotificationUrl(), true);
            }
            LOGGER.debug("CreateVfModuleTask exit: code=" + getStatusCode() + ", resp=" + getResponse());
        }
    }

}

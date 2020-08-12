package org.seariver.kanbanboard.write.adapter.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.seariver.kanbanboard.commom.exception.ResponseError;
import org.seariver.kanbanboard.commom.observable.ServiceBus;
import org.seariver.kanbanboard.write.application.service.CreateBucketCommand;
import org.seariver.kanbanboard.write.application.service.MoveBucketCommand;
import org.seariver.kanbanboard.write.application.service.UpdateBucketCommand;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CREATED;

@ApplicationScoped
@Path("buckets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "bucket")
public class WriteBucketRest {

    final static Logger logger = Logger.getLogger(WriteBucketRest.class);

    private final ServiceBus serviceBus;

    public WriteBucketRest(ServiceBus serviceBus) {
        this.serviceBus = serviceBus;
    }

    @POST
    @APIResponse(responseCode = "201", description = "Bucket created successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response create(BucketInput input) {

        logger.infov("ENTRYPOINT:HTTP:Bucket Creation:{0}", input.bucketExternalId);

        var command = new CreateBucketCommand(input.bucketExternalId, input.position, input.name);
        serviceBus.execute(command);

        return Response.status(CREATED).build();
    }

    @PUT
    @Path("{bucketExternalId}")
    @APIResponse(responseCode = "201", description = "Bucket update successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response update(@PathParam("bucketExternalId") String bucketExternalId, BucketInput input) {

        var command = new UpdateBucketCommand(bucketExternalId, input.name);
        serviceBus.execute(command);

        return Response.noContent().build();
    }

    @PUT
    @Path("{bucketExternalId}/move")
    @APIResponse(responseCode = "201", description = "Bucket moved successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response move(@PathParam("bucketExternalId") String bucketExternalId, BucketInput input) {

        var command = new MoveBucketCommand(bucketExternalId, input.position);
        serviceBus.execute(command);

        return Response.noContent().build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class BucketInput {
        @JsonProperty("bucketId")
        public String bucketExternalId;
        public double position;
        public String name;
    }
}

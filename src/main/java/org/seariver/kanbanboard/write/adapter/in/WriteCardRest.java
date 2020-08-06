package org.seariver.kanbanboard.write.adapter.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.seariver.kanbanboard.commom.exception.ResponseError;
import org.seariver.kanbanboard.commom.observable.ServiceBus;
import org.seariver.kanbanboard.write.domain.application.CreateCardCommand;
import org.seariver.kanbanboard.write.domain.application.UpdateCardCommand;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@ApplicationScoped
@Path("/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WriteCardRest {

    private ServiceBus serviceBus;

    public WriteCardRest(ServiceBus serviceBus) {
        this.serviceBus = serviceBus;
    }

    @POST
    @Path("buckets/{bucketId}/cards")
    @APIResponse(responseCode = "201", description = "Card created successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response create(@PathParam("bucketId") String bucketExternalId, CardInput input) {

        var command = new CreateCardCommand(bucketExternalId,
                input.externalId,
                input.position,
                input.name);

        serviceBus.execute(command);

        return Response.status(CREATED).build();
    }

    @PUT
    @Path("cards/{id}")
    @APIResponse(responseCode = "201", description = "Card created successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response update(@PathParam("id") String externalId, CardInput input) {

        var command = new UpdateCardCommand(externalId, input.name, input.description);

        serviceBus.execute(command);

        return Response.status(NO_CONTENT).build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class CardInput {
        @JsonProperty("id")
        public String externalId;
        @JsonProperty("bucket")
        public String bucketExternalId;
        public double position;
        public String name;
        public String description;
    }
}

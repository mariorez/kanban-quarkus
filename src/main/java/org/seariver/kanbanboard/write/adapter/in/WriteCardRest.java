package org.seariver.kanbanboard.write.adapter.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.seariver.kanbanboard.commom.exception.ResponseError;
import org.seariver.kanbanboard.commom.observable.ServiceBus;
import org.seariver.kanbanboard.write.application.service.CreateCardCommand;
import org.seariver.kanbanboard.write.application.service.MoveCardCommand;
import org.seariver.kanbanboard.write.application.service.UpdateCardCommand;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@ApplicationScoped
@Path("cards")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "card")
public class WriteCardRest {

    private final ServiceBus serviceBus;

    public WriteCardRest(ServiceBus serviceBus) {
        this.serviceBus = serviceBus;
    }

    @POST
    @APIResponse(responseCode = "201", description = "Card created successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response create(CardInput input) {

        var command = new CreateCardCommand(
                input.bucketExternalId,
                input.cardExternalId,
                input.position,
                input.name);

        serviceBus.execute(command);

        return Response.status(CREATED).build();
    }

    @PATCH
    @Path("{cardExternalId}")
    @APIResponse(responseCode = "201", description = "Card created successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response update(@PathParam("cardExternalId") String cardExternalId, CardInput input) {

        var command = new UpdateCardCommand(cardExternalId, input.name, input.description);

        serviceBus.execute(command);

        return Response.status(NO_CONTENT).build();
    }

    @PATCH
    @Path("{cardExternalId}/move")
    @APIResponse(responseCode = "201", description = "Card moved successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response move(@PathParam("cardExternalId") String cardExternalId, CardInput input) {

        var command = new MoveCardCommand(cardExternalId, input.position);

        serviceBus.execute(command);

        return Response.status(NO_CONTENT).build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class CardInput {
        @JsonProperty("bucketId")
        public String bucketExternalId;
        @JsonProperty("cardId")
        public String cardExternalId;
        public double position;
        public String name;
        public String description;
    }
}

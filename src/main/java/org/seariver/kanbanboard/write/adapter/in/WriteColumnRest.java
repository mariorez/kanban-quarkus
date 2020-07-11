package org.seariver.kanbanboard.write.adapter.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.seariver.kanbanboard.commom.exception.ResponseError;
import org.seariver.kanbanboard.write.domain.application.CreateColumnCommand;
import org.seariver.kanbanboard.write.domain.application.CreateColumnCommandHandler;
import org.seariver.kanbanboard.write.domain.application.MoveColumnCommand;
import org.seariver.kanbanboard.write.domain.application.MoveColumnCommandHandler;
import org.seariver.kanbanboard.write.domain.application.UpdateColumnCommand;
import org.seariver.kanbanboard.write.domain.application.UpdateColumnCommandHandler;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;

@Path("v1/columns")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "columns")
public class WriteColumnRest {

    public static final String UUID_FORMAT = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$";
    public static final String INVALID_UUID = "invalid uuid format";
    @Inject
    private CreateColumnCommandHandler createHandler;
    @Inject
    private UpdateColumnCommandHandler updateHandler;
    @Inject
    private MoveColumnCommandHandler moveHandler;

    @POST
    @APIResponse(responseCode = "201", description = "Bucket created successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response create(@Valid CreateInput input) {

        var command = new CreateColumnCommand(UUID.fromString(input.externalId), input.position, input.name);
        createHandler.handle(command);

        return Response.created(URI.create(String.format("v1/columns/%s", input.externalId))).build();
    }

    @PUT
    @Path("{uuid}")
    @APIResponse(responseCode = "201", description = "Bucket update successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response update(
        @Valid
        @Pattern(regexp = UUID_FORMAT, message = INVALID_UUID)
        @PathParam("uuid") String uuid,
        @Valid UpdateInput input) {

        var command = new UpdateColumnCommand(UUID.fromString(uuid), input.name);
        updateHandler.handle(command);

        return Response.noContent().build();
    }

    @PUT
    @Path("{uuid}/move")
    @APIResponse(responseCode = "201", description = "Bucket moved successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response move(
        @Valid
        @Pattern(regexp = UUID_FORMAT, message = INVALID_UUID)
        @PathParam("uuid") String uuid,
        @Valid MoveInput input) {

        var command = new MoveColumnCommand(UUID.fromString(uuid), input.position);
        moveHandler.handle(command);

        return Response.noContent().build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class CreateInput {
        @NotBlank
        @Pattern(regexp = UUID_FORMAT, message = INVALID_UUID)
        @JsonProperty("id")
        public String externalId;
        @Positive
        public double position;
        @NotBlank
        @Size(min = 1, max = 100)
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class UpdateInput {
        @NotBlank
        @Size(min = 1, max = 100)
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class MoveInput {
        @Positive
        public double position;
    }
}

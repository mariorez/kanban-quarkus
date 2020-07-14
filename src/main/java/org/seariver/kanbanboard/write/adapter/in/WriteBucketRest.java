package org.seariver.kanbanboard.write.adapter.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.seariver.kanbanboard.commom.exception.ResponseError;
import org.seariver.kanbanboard.write.domain.application.CreateBucketCommand;
import org.seariver.kanbanboard.write.domain.application.CreateBucketCommandHandler;
import org.seariver.kanbanboard.write.domain.application.MoveBucketCommand;
import org.seariver.kanbanboard.write.domain.application.MoveBucketCommandHandler;
import org.seariver.kanbanboard.write.domain.application.UpdateBucketCommand;
import org.seariver.kanbanboard.write.domain.application.UpdateBucketCommandHandler;

import javax.enterprise.context.ApplicationScoped;
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
import java.util.UUID;

import static javax.ws.rs.core.Response.Status.CREATED;

@ApplicationScoped
@Path("v1/buckets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "bucket")
public class WriteBucketRest {

    public static final String UUID_FORMAT = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$";
    public static final String INVALID_UUID = "invalid UUID format";

    private CreateBucketCommandHandler createHandler;
    private UpdateBucketCommandHandler updateHandler;
    private MoveBucketCommandHandler moveHandler;

    public WriteBucketRest(CreateBucketCommandHandler createHandler,
                           UpdateBucketCommandHandler updateHandler,
                           MoveBucketCommandHandler moveHandler) {
        this.createHandler = createHandler;
        this.updateHandler = updateHandler;
        this.moveHandler = moveHandler;
    }

    @POST
    @APIResponse(responseCode = "201", description = "Bucket created successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response create(@Valid CreateInput input) {

        var command = new CreateBucketCommand(UUID.fromString(input.externalId), input.position, input.name);
        createHandler.handle(command);

        return Response.status(CREATED).build();
    }

    @PUT
    @Path("{id}")
    @APIResponse(responseCode = "201", description = "Bucket update successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response update(
        @Valid
        @Pattern(regexp = UUID_FORMAT, message = INVALID_UUID)
        @PathParam("id") String externalId,
        @Valid UpdateInput input) {

        var command = new UpdateBucketCommand(UUID.fromString(externalId), input.name);
        updateHandler.handle(command);

        return Response.noContent().build();
    }

    @PUT
    @Path("{id}/move")
    @APIResponse(responseCode = "201", description = "Bucket moved successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response move(
        @Valid
        @Pattern(regexp = UUID_FORMAT, message = INVALID_UUID)
        @PathParam("id") String externalId,
        @Valid MoveInput input) {

        var command = new MoveBucketCommand(UUID.fromString(externalId), input.position);
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

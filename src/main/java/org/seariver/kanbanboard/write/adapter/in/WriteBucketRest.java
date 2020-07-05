package org.seariver.kanbanboard.write.adapter.in;

import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.seariver.kanbanboard.write.domain.application.CreateBucketCommand;
import org.seariver.kanbanboard.write.domain.application.CreateBucketCommandHandler;
import org.seariver.kanbanboard.write.domain.application.UpdateBucketCommand;
import org.seariver.kanbanboard.write.domain.application.UpdateBucketCommandHandler;

import javax.inject.Inject;
import javax.transaction.Transactional;
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

@Transactional
@Path("v1/buckets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "bucket")
public class WriteBucketRest {

    @Inject
    private CreateBucketCommandHandler createHandler;
    @Inject
    private UpdateBucketCommandHandler updateHandler;

    @POST
    @APIResponse(responseCode = "201", description = "Bucket created successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    public Response create(@Valid CreateInput input) {

        var command = new CreateBucketCommand(UUID.fromString(input.uuid), input.position, input.name);
        createHandler.handle(command);

        return Response.created(URI.create(String.format("v1/buckets/%s", input.uuid))).build();
    }

    @PUT
    @Path("{uuid}")
    @APIResponse(responseCode = "204", description = "Bucket updated successful")
    @APIResponse(responseCode = "400", content = @Content(schema = @Schema(allOf = ResponseError.class)))
    public Response update(
        @Valid
        @NotBlank
        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$",
            message = "invalid uuid format")
        @PathParam String uuid,
        @Valid UpdateInput input) {

        var command = new UpdateBucketCommand(UUID.fromString(uuid), input.position, input.name);
        updateHandler.handle(command);

        return Response.noContent().build();
    }

    static class CreateInput {
        @NotBlank
        @Pattern(
            regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$",
            message = "invalid uuid format")
        public String uuid;
        @Positive
        public double position;
        @NotBlank
        @Size(min = 1, max = 100)
        public String name;
    }

    static class UpdateInput {
        @Positive
        public double position;
        @NotBlank
        @Size(min = 1, max = 100)
        public String name;
    }
}

package org.seariver.kanbanboard.write.adapter.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.seariver.kanbanboard.write.domain.application.CreateBucketCommand;
import org.seariver.kanbanboard.write.domain.application.CreateBucketCommandHandler;
import org.seariver.kanbanboard.write.domain.application.UpdateBucketCommand;
import org.seariver.kanbanboard.write.domain.application.UpdateBucketCommandHandler;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
    public Response create(@Valid CreateInput input) {

        var command = new CreateBucketCommand(input.uuid, input.position, input.name);
        createHandler.handle(command);

        return Response.created(URI.create(String.format("v1/buckets/%s", input.uuid))).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") UUID uuid, @Valid UpdateInput input) {

        var command = new UpdateBucketCommand(uuid, input.position, input.name);
        updateHandler.handle(command);

        return Response.noContent().build();
    }

    static class CreateInput {
        @NotNull
        @JsonProperty("id")
        public UUID uuid;
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
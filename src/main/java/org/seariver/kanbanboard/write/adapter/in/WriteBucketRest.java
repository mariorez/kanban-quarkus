package org.seariver.kanbanboard.write.adapter.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.seariver.kanbanboard.write.domain.application.CreateBucketCommand;
import org.seariver.kanbanboard.write.domain.application.CreateBucketCommandHandler;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;

@Transactional
@Path("v1/buckets")
public class WriteBucketRest {

    @Inject
    private CreateBucketCommandHandler handler;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(@Valid BucketInput input) {

        var command = new CreateBucketCommand(input.uuid, input.position, input.name);
        handler.handle(command);

        return Response.created(URI.create(String.format("v1/buckets/%s", input.uuid))).build();
    }

    static class BucketInput {
        @NotNull
        @JsonProperty("id")
        public UUID uuid;
        @Positive
        public double position;
        @NotBlank
        @Size(min = 1, max = 100)
        public String name;
    }
}
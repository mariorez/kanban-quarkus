package org.seariver.kanbanboard.write.adapter.in;

import org.seariver.kanbanboard.write.domain.application.CreateBucketCommand;
import org.seariver.kanbanboard.write.domain.application.CreateBucketCommandHandler;

import javax.inject.Inject;
import javax.transaction.Transactional;
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
    public Response create(BucketInput input) {

        var command = new CreateBucketCommand(input.uuid, input.position, input.name);
        handler.handle(command);

        return Response.created(URI.create(String.format("v1/buckets/%s", input.uuid))).build();
    }

    static class BucketInput {
        public UUID uuid;
        public double position;
        public String name;
    }
}
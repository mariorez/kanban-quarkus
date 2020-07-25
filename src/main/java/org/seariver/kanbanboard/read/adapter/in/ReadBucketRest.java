package org.seariver.kanbanboard.read.adapter.in;

import org.seariver.kanbanboard.commom.observable.ServiceBus;
import org.seariver.kanbanboard.read.application.ListAllBucketQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("v1/buckets")
@Produces(MediaType.APPLICATION_JSON)
public class ReadBucketRest {

    private ServiceBus serviceBus;

    public ReadBucketRest(ServiceBus serviceBus) {
        this.serviceBus = serviceBus;
    }

    @GET
    public Response listAll() {

        var query = new ListAllBucketQuery();

        serviceBus.execute(query);

        return Response.ok(query.getResult()).build();
    }
}

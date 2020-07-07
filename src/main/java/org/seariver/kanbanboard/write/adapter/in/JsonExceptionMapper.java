package org.seariver.kanbanboard.write.adapter.in;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

@Provider
public class JsonExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    @Override
    public Response toResponse(JsonProcessingException exception) {

        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(new ResponseError("Malformed JSON", Collections.emptyList()))
            .build();
    }
}

package org.seariver.kanbanboard.commom.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public class JsonExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    public static final String MALFORMED_JSON_MESSAGE = "Malformed JSON";

    @Override
    public Response toResponse(JsonProcessingException exception) {

        return Response
            .status(BAD_REQUEST)
            .entity(new ResponseError(MALFORMED_JSON_MESSAGE, Collections.emptyList()))
            .build();
    }
}

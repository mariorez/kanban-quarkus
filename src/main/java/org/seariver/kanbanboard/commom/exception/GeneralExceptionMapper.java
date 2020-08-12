package org.seariver.kanbanboard.commom.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {

    public static final String SERVER_ERROR_MESSAGE = "Internal Server Error";

    @Override
    public Response toResponse(Exception exception) {

        return Response
                .status(INTERNAL_SERVER_ERROR)
                .entity(new ResponseError(
                        SERVER_ERROR_MESSAGE,
                        List.of(new ResponseError.ErrorField("code", "500"))))
                .build();
    }
}

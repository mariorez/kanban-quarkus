package org.seariver.kanbanboard.commom.exception;

import org.seariver.kanbanboard.commom.exception.ResponseError.ErrorField;
import org.seariver.kanbanboard.write.domain.exception.BucketNotExistentException;
import org.seariver.kanbanboard.write.domain.exception.WriteException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Provider
public class WriteDomainExceptionMapper implements ExceptionMapper<WriteException> {

    public static final String INVALID_PARAMETER_MESSAGE = "Invalid parameter";

    @Override
    public Response toResponse(WriteException exception) {

        var statusCode = BAD_REQUEST;
        var errorMessage = INVALID_PARAMETER_MESSAGE;

        if (exception instanceof BucketNotExistentException) {
            statusCode = NOT_FOUND;
            errorMessage = NOT_FOUND.getReasonPhrase();
        }

        return Response
            .status(statusCode)
            .entity(new ResponseError(errorMessage,
                List.of(new ErrorField("code", String.valueOf(exception.getCode())))))
            .build();
    }
}

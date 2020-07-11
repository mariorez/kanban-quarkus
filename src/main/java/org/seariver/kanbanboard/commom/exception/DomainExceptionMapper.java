package org.seariver.kanbanboard.commom.exception;

import org.seariver.kanbanboard.commom.exception.ResponseError.ErrorField;
import org.seariver.kanbanboard.write.domain.exception.DomainException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

    public static final String INVALID_FIELD_MESSAGE = "Invalid field";

    @Override
    public Response toResponse(DomainException exception) {

        return Response
            .status(BAD_REQUEST)
            .entity(new ResponseError(
                INVALID_FIELD_MESSAGE,
                List.of(new ErrorField("code", String.valueOf(exception.getCode())))))
            .build();
    }
}

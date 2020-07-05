package org.seariver.kanbanboard.write.adapter.in;

import org.seariver.kanbanboard.write.adapter.in.ResponseError.ErrorField;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

@Provider
public class ConstraintExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    public static final String INVALID_FIELD_MESSAGE = "Invalid field";

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        var errors = exception
            .getConstraintViolations()
            .stream()
            .map(error -> {
                var fieldPath = error.getPropertyPath().toString();
                var fieldName = fieldPath.substring(fieldPath.lastIndexOf('.') + 1);
                return new ErrorField(fieldName, error.getMessage());
            })
            .collect(Collectors.toList());

        var errorResult = new ResponseError(INVALID_FIELD_MESSAGE, errors);

        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(errorResult)
            .build();
    }
}

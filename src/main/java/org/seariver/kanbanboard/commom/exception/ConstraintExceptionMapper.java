package org.seariver.kanbanboard.commom.exception;

import org.seariver.kanbanboard.commom.exception.ResponseError.ErrorField;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class ConstraintExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    public static final String INVALID_PARAMETER_MESSAGE = "Invalid parameter";

    private Map<String, String> fieldNameMapping = Map.ofEntries(
            Map.entry("bucketExternalId", "bucketId"),
            Map.entry("cardExternalId", "cardId")
    );

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        var errors = exception
            .getConstraintViolations()
            .stream()
            .map(error -> {
                var fieldPath = error.getPropertyPath().toString();
                var fieldName = fieldPath.substring(fieldPath.lastIndexOf('.') + 1);

                return new ErrorField(
                        fieldNameMapping.getOrDefault(fieldName, fieldName),
                        error.getMessage()
                );
            })
            .collect(Collectors.toList());

        var errorResult = new ResponseError(INVALID_PARAMETER_MESSAGE, errors);

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(errorResult)
                .build();
    }
}

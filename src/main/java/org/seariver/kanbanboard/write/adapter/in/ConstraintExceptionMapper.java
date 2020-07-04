package org.seariver.kanbanboard.write.adapter.in;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class ConstraintExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    public static final String INVALID_FIELD_MESSAGE = "Invalid field";

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        List<FieldValidationError> errors = exception
            .getConstraintViolations()
            .stream()
            .map(error -> {
                var fieldPath = error.getPropertyPath().toString();
                var fieldName = fieldPath.substring(fieldPath.lastIndexOf('.') + 1);
                return new FieldValidationError(fieldName, error.getMessage());
            })
            .collect(Collectors.toList());

        Map<String, Object> errorResult = new HashMap<>(Map.of("message", INVALID_FIELD_MESSAGE));
        errorResult.put("errors", errors);

        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(errorResult)
            .build();
    }

    static class FieldValidationError {

        private final String field;
        private final String detail;

        public FieldValidationError(String field, String detail) {
            this.field = field;
            this.detail = detail;
        }

        public String getField() {
            return field;
        }

        public String getDetail() {
            return detail;
        }
    }
}

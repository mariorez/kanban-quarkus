package org.seariver.kanbanboard.commom.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.seariver.kanbanboard.commom.exception.ResponseError.ErrorField;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public class JsonExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    public static final String INVALID_FORMAT_MESSAGE = "Invalid format";
    public static final String MALFORMED_JSON_MESSAGE = "Malformed JSON";

    @Override
    public Response toResponse(JsonProcessingException exception) {

        var message = MALFORMED_JSON_MESSAGE;
        List<ErrorField> errors = Collections.emptyList();

        if (exception instanceof InvalidFormatException) {
            message = INVALID_FORMAT_MESSAGE;
            InvalidFormatException invalidException = (InvalidFormatException) exception;
            errors = invalidException.getPath()
                    .stream()
                    .map(path -> new ErrorField(
                            path.getFieldName(),
                            String.valueOf(invalidException.getTargetType())))
                    .collect(Collectors.toList());
        }

        return Response
                .status(BAD_REQUEST)
                .entity(new ResponseError(message, errors))
                .build();
    }
}

package org.seariver.kanbanboard.commom.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.seariver.kanbanboard.commom.exception.ResponseError.ErrorField;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public class JsonInvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {

    public static final String INVALID_FORMAT_MESSAGE = "Invalid format";

    @Override
    public Response toResponse(InvalidFormatException exception) {

        List<ErrorField> errors = exception.getPath()
            .stream()
            .map(path -> new ErrorField(path.getFieldName(), String.valueOf(exception.getTargetType())))
            .collect(Collectors.toList());

        return Response
            .status(BAD_REQUEST)
            .entity(new ResponseError(INVALID_FORMAT_MESSAGE, errors))
            .build();
    }
}

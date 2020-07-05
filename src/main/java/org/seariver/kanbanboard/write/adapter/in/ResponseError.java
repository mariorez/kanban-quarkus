package org.seariver.kanbanboard.write.adapter.in;

import java.util.List;

public class ResponseError {

    private final String message;
    private final List<ErrorField> errors;

    public ResponseError(String message, List<ErrorField> errors) {
        this.message = message;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public List<ErrorField> getErrors() {
        return errors;
    }

    public static class ErrorField {

        private final String field;
        private final String detail;

        public ErrorField(String field, String detail) {
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

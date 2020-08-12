package org.seariver.kanbanboard.write.application.exception;

public class DuplicatedDataException extends WriteException {

    public DuplicatedDataException(Error error, Throwable throwable) {
        super(error, throwable);
    }
}

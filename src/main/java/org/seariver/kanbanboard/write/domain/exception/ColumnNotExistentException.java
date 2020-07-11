package org.seariver.kanbanboard.write.domain.exception;

public class ColumnNotExistentException extends DomainException {

    public ColumnNotExistentException(Error error) {
        super(error);
    }
}

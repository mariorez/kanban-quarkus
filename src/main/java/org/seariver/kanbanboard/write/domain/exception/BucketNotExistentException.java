package org.seariver.kanbanboard.write.domain.exception;

public class BucketNotExistentException extends WriteException {

    public BucketNotExistentException(Error error) {
        super(error);
    }
}

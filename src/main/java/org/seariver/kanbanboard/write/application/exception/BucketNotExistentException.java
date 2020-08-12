package org.seariver.kanbanboard.write.application.exception;

public class BucketNotExistentException extends WriteException {

    public BucketNotExistentException(Error error) {
        super(error);
    }
}

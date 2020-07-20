package org.seariver.kanbanboard.write.domain.application;

import org.seariver.kanbanboard.write.domain.core.WriteBucketRepository;
import org.seariver.kanbanboard.write.domain.exception.BucketNotExistentException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import static org.seariver.kanbanboard.write.domain.exception.DomainException.Error.BUCKET_NOT_EXIST;

@Named
@ApplicationScoped
public class MoveBucketHandler implements Handler<MoveBucketCommand> {

    private final WriteBucketRepository repository;

    public MoveBucketHandler(WriteBucketRepository repository) {
        this.repository = repository;
    }

    public void handle(MoveBucketCommand command) {

        var optionalBucket = repository.findByExternalId(command.getExternalId());

        if (optionalBucket.isEmpty()) {
            throw new BucketNotExistentException(BUCKET_NOT_EXIST);
        }

        var bucket = optionalBucket.get();

        bucket.setPosition(command.getPosition());

        repository.update(bucket);
    }
}

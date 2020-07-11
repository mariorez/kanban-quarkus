package org.seariver.kanbanboard.write.domain.application;

import org.seariver.kanbanboard.write.domain.core.WriteColumnRepository;
import org.seariver.kanbanboard.write.domain.exception.ColumnNotExistentException;

import javax.inject.Singleton;

import static org.seariver.kanbanboard.write.domain.exception.DomainException.Error.BUCKET_NOT_EXIST;

@Singleton
public class MoveColumnCommandHandler {

    private final WriteColumnRepository repository;

    public MoveColumnCommandHandler(WriteColumnRepository repository) {
        this.repository = repository;
    }

    public void handle(MoveColumnCommand command) {

        var optionalBucket = repository.findByExternalId(command.getUuid());

        if (optionalBucket.isEmpty()) {
            throw new ColumnNotExistentException(BUCKET_NOT_EXIST);
        }

        var bucket = optionalBucket.get();

        bucket.setPosition(command.getPosition());

        repository.update(bucket);
    }
}

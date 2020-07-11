package org.seariver.kanbanboard.write.domain.application;

import org.seariver.kanbanboard.write.domain.core.Column;
import org.seariver.kanbanboard.write.domain.core.WriteColumnRepository;
import org.seariver.kanbanboard.write.domain.exception.ColumnNotExistentException;

import javax.inject.Singleton;
import java.util.Optional;

import static org.seariver.kanbanboard.write.domain.exception.DomainException.Error.BUCKET_NOT_EXIST;

@Singleton
public class UpdateColumnCommandHandler implements Handler<UpdateColumnCommand> {

    private WriteColumnRepository repository;

    public UpdateColumnCommandHandler(WriteColumnRepository repository) {
        this.repository = repository;
    }

    public void handle(UpdateColumnCommand command) {

        Optional<Column> bucketOptional = repository.findByExternalId(command.getUuid());

        if (!bucketOptional.isPresent()) {
            throw new ColumnNotExistentException(BUCKET_NOT_EXIST);
        }

        var bucket = bucketOptional.get();
        bucket.setName(command.getName());

        repository.update(bucket);
    }
}


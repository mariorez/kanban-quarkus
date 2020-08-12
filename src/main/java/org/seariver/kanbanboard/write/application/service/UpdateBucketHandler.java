package org.seariver.kanbanboard.write.application.service;

import org.seariver.kanbanboard.write.application.domain.Bucket;
import org.seariver.kanbanboard.write.application.domain.WriteBucketRepository;
import org.seariver.kanbanboard.write.application.exception.BucketNotExistentException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Optional;

import static org.seariver.kanbanboard.write.application.exception.WriteException.Error.BUCKET_NOT_EXIST;

@Named
@ApplicationScoped
public class UpdateBucketHandler implements Handler<UpdateBucketCommand> {

    private final WriteBucketRepository repository;

    public UpdateBucketHandler(WriteBucketRepository repository) {
        this.repository = repository;
    }

    public void handle(UpdateBucketCommand command) {

        Optional<Bucket> bucketOptional = repository.findByExternalId(command.getBucketExternalId());

        if (!bucketOptional.isPresent()) {
            throw new BucketNotExistentException(BUCKET_NOT_EXIST);
        }

        var bucket = bucketOptional.get();
        bucket.setName(command.getName());

        repository.update(bucket);
    }
}


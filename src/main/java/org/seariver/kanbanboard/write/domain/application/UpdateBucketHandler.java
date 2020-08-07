package org.seariver.kanbanboard.write.domain.application;

import org.seariver.kanbanboard.write.domain.core.Bucket;
import org.seariver.kanbanboard.write.domain.core.WriteBucketRepository;
import org.seariver.kanbanboard.write.domain.exception.BucketNotExistentException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Optional;

import static org.seariver.kanbanboard.write.domain.exception.WriteException.Error.BUCKET_NOT_EXIST;

@Named
@ApplicationScoped
public class UpdateBucketHandler implements Handler<UpdateBucketCommand> {

    private WriteBucketRepository repository;

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


package org.seariver.kanbanboard.write.domain.application;

import org.seariver.kanbanboard.write.domain.core.Bucket;
import org.seariver.kanbanboard.write.domain.core.Card;
import org.seariver.kanbanboard.write.domain.core.WriteBucketRepository;
import org.seariver.kanbanboard.write.domain.core.WriteCardRepository;
import org.seariver.kanbanboard.write.domain.exception.BucketNotExistentException;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

import static org.seariver.kanbanboard.write.domain.exception.DomainException.Error.BUCKET_NOT_EXIST;

@ApplicationScoped
public class CreateCardCommandHandler implements Handler<CreateCardCommand> {

    private WriteBucketRepository bucketRepository;
    private WriteCardRepository cardRepository;

    public CreateCardCommandHandler(WriteBucketRepository bucketRepository, WriteCardRepository cardRepository) {
        this.bucketRepository = bucketRepository;
        this.cardRepository = cardRepository;
    }

    public void handle(CreateCardCommand command) {

        Optional<Bucket> bucketOptional = bucketRepository.findByExternalId(command.getBucketExternalId());

        if (bucketOptional.isEmpty()) {
            throw new BucketNotExistentException(BUCKET_NOT_EXIST);
        }

        var bucket = bucketOptional.get();

        var card = new Card()
                .setBucketId(bucket.getId())
                .setExternalId(command.getExternalId())
                .setPosition(command.getPosition())
                .setName(command.getName());

        cardRepository.create(card);
    }
}

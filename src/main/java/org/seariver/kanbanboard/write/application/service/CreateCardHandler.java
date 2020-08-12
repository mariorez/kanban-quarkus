package org.seariver.kanbanboard.write.application.service;

import org.seariver.kanbanboard.write.application.domain.Bucket;
import org.seariver.kanbanboard.write.application.domain.Card;
import org.seariver.kanbanboard.write.application.domain.WriteBucketRepository;
import org.seariver.kanbanboard.write.application.domain.WriteCardRepository;
import org.seariver.kanbanboard.write.application.exception.BucketNotExistentException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Optional;

import static org.seariver.kanbanboard.write.application.exception.WriteException.Error.BUCKET_NOT_EXIST;

@Named
@ApplicationScoped
public class CreateCardHandler implements Handler<CreateCardCommand> {

    private final WriteBucketRepository bucketRepository;
    private final WriteCardRepository cardRepository;

    public CreateCardHandler(WriteBucketRepository bucketRepository, WriteCardRepository cardRepository) {
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
                .setCardExternalId(command.getCardExternalId())
                .setPosition(command.getPosition())
                .setName(command.getName());

        cardRepository.create(card);
    }
}

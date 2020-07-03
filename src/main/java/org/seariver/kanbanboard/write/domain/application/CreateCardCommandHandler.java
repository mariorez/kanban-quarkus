package org.seariver.kanbanboard.write.domain.application;

import org.seariver.kanbanboard.write.domain.core.Bucket;
import org.seariver.kanbanboard.write.domain.core.Card;
import org.seariver.kanbanboard.write.domain.core.WriteBucketRepository;
import org.seariver.kanbanboard.write.domain.core.WriteCardRepository;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class CreateCardCommandHandler implements Handler<CreateCardCommand> {

    private WriteBucketRepository bucketRepository;
    private WriteCardRepository cardRepository;

    public CreateCardCommandHandler(WriteBucketRepository bucketRepository, WriteCardRepository cardRepository) {
        this.bucketRepository = bucketRepository;
        this.cardRepository = cardRepository;
    }

    public void handle(CreateCardCommand command) {

        Optional<Bucket> bucketOptional = bucketRepository.findByUuid(command.getBucketId());
        var bucket = bucketOptional.get();

        var card = new Card()
            .setBucketId(bucket.getId())
            .setUuid(command.getUuid())
            .setPosition(command.getPosition())
            .setName(command.getName());

        cardRepository.create(card);
    }
}

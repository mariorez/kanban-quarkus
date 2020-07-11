package org.seariver.kanbanboard.write.domain.application;

import org.seariver.kanbanboard.write.domain.core.Column;
import org.seariver.kanbanboard.write.domain.core.Card;
import org.seariver.kanbanboard.write.domain.core.WriteColumnRepository;
import org.seariver.kanbanboard.write.domain.core.WriteCardRepository;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class CreateCardCommandHandler implements Handler<CreateCardCommand> {

    private WriteColumnRepository bucketRepository;
    private WriteCardRepository cardRepository;

    public CreateCardCommandHandler(WriteColumnRepository bucketRepository, WriteCardRepository cardRepository) {
        this.bucketRepository = bucketRepository;
        this.cardRepository = cardRepository;
    }

    public void handle(CreateCardCommand command) {

        Optional<Column> bucketOptional = bucketRepository.findByExternalId(command.getBucketId());
        var bucket = bucketOptional.get();

        var card = new Card()
            .setColumnId(bucket.getId())
            .setExternalId(command.getUuid())
            .setPosition(command.getPosition())
            .setName(command.getName());

        cardRepository.create(card);
    }
}

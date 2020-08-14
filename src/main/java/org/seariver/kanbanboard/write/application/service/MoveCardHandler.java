package org.seariver.kanbanboard.write.application.service;

import org.seariver.kanbanboard.write.application.domain.WriteBucketRepository;
import org.seariver.kanbanboard.write.application.domain.WriteCardRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class MoveCardHandler implements Handler<MoveCardCommand> {

    private final WriteBucketRepository bucketRepository;
    private final WriteCardRepository cardRepository;

    public MoveCardHandler(WriteBucketRepository bucketRepository, WriteCardRepository cardRepository) {
        this.bucketRepository = bucketRepository;
        this.cardRepository = cardRepository;
    }

    public void handle(MoveCardCommand command) {

        var card = cardRepository.findByExternalId(command.getCardExternalId()).get();
        var bucket = bucketRepository.findByExternalId(command.getBucketExternalId()).get();

        card
                .setBucketId(bucket.getId())
                .setPosition(command.getPosition());

        cardRepository.update(card);
    }
}

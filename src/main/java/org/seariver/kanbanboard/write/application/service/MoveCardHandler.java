package org.seariver.kanbanboard.write.application.service;

import org.seariver.kanbanboard.write.application.domain.WriteCardRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class MoveCardHandler implements Handler<MoveCardCommand> {

    private final WriteCardRepository repository;

    public MoveCardHandler(WriteCardRepository repository) {
        this.repository = repository;
    }

    public void handle(MoveCardCommand command) {

        var card = repository.findByExternalId(command.getCardExternalId()).get();

        card.setPosition(command.getPosition());

        repository.update(card);
    }
}

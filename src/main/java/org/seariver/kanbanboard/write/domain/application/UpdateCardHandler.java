package org.seariver.kanbanboard.write.domain.application;

import org.seariver.kanbanboard.write.domain.core.WriteCardRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class UpdateCardHandler implements Handler<UpdateCardCommand> {

    private WriteCardRepository repository;

    public UpdateCardHandler(WriteCardRepository repository) {
        this.repository = repository;
    }

    public void handle(UpdateCardCommand command) {

        var optionalCard = repository.findByExternalId(command.getCardExternalId());
        var card = optionalCard.get();

        card.setName(command.getName());
        card.setDescription(command.getDescription());

        repository.update(card);
    }
}

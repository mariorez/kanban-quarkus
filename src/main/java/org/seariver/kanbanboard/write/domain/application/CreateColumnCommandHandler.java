package org.seariver.kanbanboard.write.domain.application;

import org.seariver.kanbanboard.write.domain.core.Column;
import org.seariver.kanbanboard.write.domain.core.WriteColumnRepository;

import javax.inject.Singleton;

@Singleton
public class CreateColumnCommandHandler implements Handler<CreateColumnCommand> {

    private final WriteColumnRepository repository;

    public CreateColumnCommandHandler(WriteColumnRepository repository) {
        this.repository = repository;
    }

    public void handle(CreateColumnCommand command) {

        var bucket = new Column()
            .setExternalId(command.getUuid())
            .setPosition(command.getPosition())
            .setName(command.getName());

        repository.create(bucket);
    }
}

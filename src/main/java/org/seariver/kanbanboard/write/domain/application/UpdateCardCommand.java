package org.seariver.kanbanboard.write.domain.application;

import java.util.UUID;

public class UpdateCardCommand implements Command {

    private final String cardExternalId;
    private final String name;
    private String description;

    public UpdateCardCommand(String cardExternalId, String name, String description) {
        this.cardExternalId = cardExternalId;
        this.name = name;
        this.description = description;
    }

    public UUID getCardExternalId() {
        return UUID.fromString(cardExternalId);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

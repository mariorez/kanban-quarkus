package org.seariver.kanbanboard.write.domain.application;

import java.util.UUID;

public class UpdateCardCommand implements Command {

    private final String externalId;
    private final String name;
    private String description;

    public UpdateCardCommand(String externalId, String name, String description) {
        this.externalId = externalId;
        this.name = name;
        this.description = description;
    }

    public UUID getExternalId() {
        return UUID.fromString(externalId);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

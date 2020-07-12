package org.seariver.kanbanboard.write.domain.application;

import java.util.UUID;

public class UpdateBucketCommand implements Command {

    private final UUID externalId;
    private final String name;

    public UpdateBucketCommand(UUID externalId, String name) {
        this.externalId = externalId;
        this.name = name;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }
}

package org.seariver.kanbanboard.write.domain.application;

import java.util.UUID;

public class CreateBucketCommand implements Command {

    private final UUID externalId;
    private final double position;
    private final String name;

    public CreateBucketCommand(UUID externalId, double position, String name) {
        this.externalId = externalId;
        this.position = position;
        this.name = name;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public double getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}

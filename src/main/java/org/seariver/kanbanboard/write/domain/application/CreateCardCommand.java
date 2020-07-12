package org.seariver.kanbanboard.write.domain.application;

import java.util.UUID;

public class CreateCardCommand implements Command {

    private final UUID externalId;
    private final UUID bucketId;
    private final double position;
    private final String name;

    public CreateCardCommand(UUID externalId, UUID bucketId, double position, String name) {
        this.externalId = externalId;
        this.bucketId = bucketId;
        this.position = position;
        this.name = name;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public UUID getBucketId() {
        return bucketId;
    }

    public double getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}

package org.seariver.kanbanboard.write.domain.application;

import java.util.UUID;

public class CreateCardCommand implements Command {

    private final UUID bucketExternalId;
    private final UUID externalId;
    private final double position;
    private final String name;

    public CreateCardCommand(UUID bucketExternalId, UUID externalId, double position, String name) {
        this.bucketExternalId = bucketExternalId;
        this.externalId = externalId;
        this.position = position;
        this.name = name;
    }

    public UUID getBucketExternalId() {
        return bucketExternalId;
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

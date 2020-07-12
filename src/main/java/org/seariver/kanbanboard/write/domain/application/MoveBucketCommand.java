package org.seariver.kanbanboard.write.domain.application;

import java.util.UUID;

public class MoveBucketCommand {

    private final UUID externalId;
    private final double position;

    public MoveBucketCommand(UUID externalId, double position) {
        this.externalId = externalId;
        this.position = position;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public double getPosition() {
        return position;
    }
}

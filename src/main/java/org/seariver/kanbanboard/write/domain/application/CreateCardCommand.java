package org.seariver.kanbanboard.write.domain.application;

import java.util.UUID;

public class CreateCardCommand implements Command {

    private final UUID uuid;
    private final UUID bucketId;
    private final double position;
    private final String name;

    public CreateCardCommand(UUID uuid, UUID bucketId, double position, String name) {
        this.uuid = uuid;
        this.bucketId = bucketId;
        this.position = position;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
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

package org.seariver.kanbanboard.write.domain.application;

import java.util.UUID;

public class UpdateBucketCommand implements Command {

    private final UUID uuid;
    private final String name;

    public UpdateBucketCommand(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}

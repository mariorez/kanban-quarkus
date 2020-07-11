package org.seariver.kanbanboard.write.domain.application;

import java.util.UUID;

public class MoveColumnCommand {

    private final UUID uuid;
    private final double position;

    public MoveColumnCommand(UUID uuid, double position) {
        this.uuid = uuid;
        this.position = position;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getPosition() {
        return position;
    }
}

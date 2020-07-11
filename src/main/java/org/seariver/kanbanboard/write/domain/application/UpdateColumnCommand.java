package org.seariver.kanbanboard.write.domain.application;

import java.util.UUID;

public class UpdateColumnCommand implements Command {

    private final UUID uuid;
    private final String name;

    public UpdateColumnCommand(UUID uuid, String name) {
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

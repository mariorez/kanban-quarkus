package org.seariver.kanbanboard.write.application.service;

import java.util.UUID;

public class MoveCardCommand implements Command {

    private final String cardExternalId;
    private final double position;

    public MoveCardCommand(String cardExternalId, double position) {
        this.cardExternalId = cardExternalId;
        this.position = position;
    }

    public UUID getCardExternalId() {
        return UUID.fromString(cardExternalId);
    }

    public double getPosition() {
        return position;
    }
}

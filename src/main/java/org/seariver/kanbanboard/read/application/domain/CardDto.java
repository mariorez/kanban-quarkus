package org.seariver.kanbanboard.read.application.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class CardDto {

    @JsonProperty("id")
    private final UUID externalId;
    @JsonProperty("position")
    private final double position;
    @JsonProperty("name")
    private final String name;

    public CardDto(UUID externalId, double position, String name) {
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

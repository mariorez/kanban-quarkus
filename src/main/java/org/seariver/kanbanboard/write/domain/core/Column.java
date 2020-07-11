package org.seariver.kanbanboard.write.domain.core;

import java.time.LocalDateTime;
import java.util.UUID;

public class Column {

    private Long id;
    private UUID externalId;
    private double position;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public Column setId(Long id) {
        this.id = id;
        return this;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public Column setExternalId(UUID externalId) {
        this.externalId = externalId;
        return this;
    }

    public double getPosition() {
        return position;
    }

    public Column setPosition(double position) {
        this.position = position;
        return this;
    }

    public String getName() {
        return name;
    }

    public Column setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Column setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Column setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}

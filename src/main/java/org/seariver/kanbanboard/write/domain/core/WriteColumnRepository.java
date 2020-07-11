package org.seariver.kanbanboard.write.domain.core;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WriteColumnRepository {

    void create(Column column);

    void update(Column column);

    Optional<Column> findByExternalId(UUID uuid);

    List<Column> findByExternalIdOrPosition(UUID uuid, double position);
}

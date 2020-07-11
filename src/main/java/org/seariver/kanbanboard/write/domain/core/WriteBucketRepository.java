package org.seariver.kanbanboard.write.domain.core;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WriteBucketRepository {

    void create(Bucket bucket);

    void update(Bucket bucket);

    Optional<Bucket> findByExternalId(UUID uuid);

    List<Bucket> findByExternalIdOrPosition(UUID uuid, double position);
}

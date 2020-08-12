package org.seariver.kanbanboard.write.application.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WriteBucketRepository {

    void create(Bucket bucket);

    void update(Bucket bucket);

    Optional<Bucket> findByExternalId(UUID externalId);

    List<Bucket> findByExternalIdOrPosition(UUID externalId, double position);
}

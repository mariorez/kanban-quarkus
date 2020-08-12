package org.seariver.kanbanboard.read.application.domain;

import java.util.List;

public interface ReadBucketRepository {

    List<BucketDto> findAll();
}

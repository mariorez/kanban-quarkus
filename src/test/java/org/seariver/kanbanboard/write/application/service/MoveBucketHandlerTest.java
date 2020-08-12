package org.seariver.kanbanboard.write.application.service;

import helper.TestHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.seariver.kanbanboard.write.application.domain.Bucket;
import org.seariver.kanbanboard.write.application.domain.WriteBucketRepository;
import org.seariver.kanbanboard.write.application.exception.BucketNotExistentException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
public class MoveBucketHandlerTest extends TestHelper {

    @Test
    void GIVEN_ValidPosition_MUST_UpdateBucketPosition() {

        // given
        var bucketExternalId = UUID.randomUUID();
        var position = faker.number().randomDouble(3, 1, 10);
        var command = new MoveBucketCommand(bucketExternalId.toString(), position);
        var repository = mock(WriteBucketRepository.class);
        var bucket = new Bucket().setBucketExternalId(bucketExternalId).setPosition(123);
        when(repository.findByExternalId(bucketExternalId)).thenReturn(Optional.of(bucket));

        // when
        var handler = new MoveBucketHandler(repository);
        handler.handle(command);

        // then
        verify(repository).findByExternalId(bucketExternalId);
        verify(repository).update(bucket);
        assertThat(bucket.getBucketExternalId()).isEqualTo(bucketExternalId);
        assertThat(bucket.getPosition()).isEqualTo(position);
    }

    @Test
    void GIVEN_NotExistentUuid_MUST_ThrowException() {

        // given
        var bucketExternalId = UUID.randomUUID();
        var position = faker.number().randomDouble(3, 1, 10);
        var command = new MoveBucketCommand(bucketExternalId.toString(), position);
        var repository = mock(WriteBucketRepository.class);
        when(repository.findByExternalId(bucketExternalId)).thenReturn(Optional.empty());

        // when
        var handler = new MoveBucketHandler(repository);
        var exception = assertThrows(
                BucketNotExistentException.class, () -> handler.handle(command));

        // then
        verify(repository).findByExternalId(bucketExternalId);
        assertThat(exception.getMessage()).isEqualTo("Bucket not exist");
    }
}

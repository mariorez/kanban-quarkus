package org.seariver.kanbanboard.write.domain.application;

import helper.TestHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.seariver.kanbanboard.write.domain.core.Bucket;
import org.seariver.kanbanboard.write.domain.core.WriteBucketRepository;
import org.seariver.kanbanboard.write.domain.exception.BucketNotExistentException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class UpdateBucketHandlerTest extends TestHelper {

    @Test
    void GIVEN_ValidCommand_MUST_UpdateBucketInDatabase() {

        // given
        var externalId = UUID.fromString("6d9db741-ef57-4d5a-ac0f-34f68fb0ab5e");
        var name = faker.pokemon().name();
        var command = new UpdateBucketCommand(externalId.toString(), name);
        var repository = mock(WriteBucketRepository.class);
        var bucket = new Bucket().setExternalId(externalId).setPosition(123).setName("FOOBAR");
        when(repository.findByExternalId(externalId)).thenReturn(Optional.of(bucket));

        // when
        var handler = new UpdateBucketHandler(repository);
        handler.handle(command);

        // then
        verify(repository).findByExternalId(externalId);
        verify(repository).update(bucket);
        assertThat(bucket.getExternalId()).isEqualTo(externalId);
        assertThat(bucket.getName()).isEqualTo(name);
    }

    @Test
    void GIVEN_NotExistentBucket_MUST_ThrowException() {

        // given
        var notExistentExternalId = UUID.fromString("019641f6-6e9e-4dd9-ab02-e864a3dfa016");
        var command = new UpdateBucketCommand(notExistentExternalId.toString(), "WHATEVER");
        var repository = mock(WriteBucketRepository.class);
        when(repository.findByExternalId(notExistentExternalId)).thenReturn(Optional.empty());

        // when
        var handler = new UpdateBucketHandler(repository);
        var exception = assertThrows(BucketNotExistentException.class, () -> handler.handle(command));

        // then
        verify(repository).findByExternalId(notExistentExternalId);
        assertThat(exception.getMessage()).isEqualTo("Bucket not exist");
    }
}

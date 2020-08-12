package org.seariver.kanbanboard.write.application.service;

import helper.TestHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.seariver.kanbanboard.write.application.domain.Bucket;
import org.seariver.kanbanboard.write.application.domain.WriteBucketRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Tag("unit")
public class CreateBucketHandlerTest extends TestHelper {

    private final ArgumentCaptor<Bucket> captor = ArgumentCaptor.forClass(Bucket.class);

    @Test
    void GIVEN_ValidCommand_MUST_CreateBucketInDatabase() {

        // given
        var bucketExternalId = UUID.randomUUID();
        var position = faker.number().randomDouble(3, 1, 10);
        var name = faker.pokemon().name();
        var command = new CreateBucketCommand(bucketExternalId.toString(), position, name);
        var repository = mock(WriteBucketRepository.class);

        // when
        var handler = new CreateBucketHandler(repository);
        handler.handle(command);

        // then
        verify(repository).create(captor.capture());
        var bucket = captor.getValue();
        assertThat(bucket.getBucketExternalId()).isEqualTo(bucketExternalId);
        assertThat(bucket.getPosition()).isEqualTo(position);
        assertThat(bucket.getName()).isEqualTo(name);
    }
}

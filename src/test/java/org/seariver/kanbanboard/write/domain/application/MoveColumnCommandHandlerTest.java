package org.seariver.kanbanboard.write.domain.application;

import helper.TestHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.seariver.kanbanboard.write.domain.core.Column;
import org.seariver.kanbanboard.write.domain.core.WriteColumnRepository;
import org.seariver.kanbanboard.write.domain.exception.ColumnNotExistentException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
public class MoveColumnCommandHandlerTest extends TestHelper {

    @Test
    void GIVEN_ValidPosition_MUST_UpdateBucketPosition() {

        // given
        var uuid = UUID.randomUUID();
        var position = faker.number().randomDouble(3, 1, 10);
        var command = new MoveColumnCommand(uuid, position);
        var repository = mock(WriteColumnRepository.class);
        var bucket = new Column().setExternalId(uuid).setPosition(123);
        when(repository.findByExternalId(uuid)).thenReturn(Optional.of(bucket));

        // when
        var handler = new MoveColumnCommandHandler(repository);
        handler.handle(command);

        // then
        verify(repository).findByExternalId(uuid);
        verify(repository).update(bucket);
        assertThat(bucket.getExternalId()).isEqualTo(uuid);
        assertThat(bucket.getPosition()).isEqualTo(position);
    }

    @Test
    void GIVEN_NotExistentUuid_MUST_ThrowException() {

        // given
        var uuid = UUID.randomUUID();
        var position = faker.number().randomDouble(3, 1, 10);
        var command = new MoveColumnCommand(uuid, position);
        var repository = mock(WriteColumnRepository.class);
        when(repository.findByExternalId(uuid)).thenReturn(Optional.empty());

        // when
        var handler = new MoveColumnCommandHandler(repository);
        var exception = assertThrows(
            ColumnNotExistentException.class, () -> handler.handle(command));

        // then
        verify(repository).findByExternalId(uuid);
        assertThat(exception.getMessage()).isEqualTo("Bucket not exist");
    }
}

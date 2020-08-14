package org.seariver.kanbanboard.write.application.service;

import helper.TestHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.seariver.kanbanboard.write.application.domain.Bucket;
import org.seariver.kanbanboard.write.application.domain.Card;
import org.seariver.kanbanboard.write.application.domain.WriteBucketRepository;
import org.seariver.kanbanboard.write.application.domain.WriteCardRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
public class MoveCardHandlerTest extends TestHelper {

    @Test
    void GIVEN_ValidPosition_MUST_UpdateBucketPosition() {

        // given
        long bucketId = 2L;
        var bucketExternalId = UUID.randomUUID();
        var bucketRepository = mock(WriteBucketRepository.class);
        var bucket = new Bucket()
                .setId(bucketId)
                .setBucketExternalId(bucketExternalId);
        when(bucketRepository.findByExternalId(bucketExternalId)).thenReturn(Optional.of(bucket));

        var cardExternalId = UUID.randomUUID();
        var position = faker.number().randomDouble(3, 1, 10);
        var cardRepository = mock(WriteCardRepository.class);
        var card = new Card()
                .setBucketId(1L)
                .setCardExternalId(cardExternalId)
                .setPosition(123);
        when(cardRepository.findByExternalId(cardExternalId)).thenReturn(Optional.of(card));

        var command = new MoveCardCommand(bucketExternalId.toString(), cardExternalId.toString(), position);

        // when
        var handler = new MoveCardHandler(bucketRepository, cardRepository);
        handler.handle(command);

        // then
        verify(cardRepository).findByExternalId(cardExternalId);
        verify(bucketRepository).findByExternalId(bucketExternalId);
        verify(cardRepository).update(card);
        assertThat(card.getBucketId()).isEqualTo(bucketId);
        assertThat(card.getPosition()).isEqualTo(position);
    }
}

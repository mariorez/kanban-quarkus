package org.seariver.kanbanboard.write.application.service;

import helper.TestHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.seariver.kanbanboard.write.application.domain.Card;
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
        var cardExternalId = UUID.randomUUID();
        var position = faker.number().randomDouble(3, 1, 10);
        var command = new MoveCardCommand(cardExternalId.toString(), position);
        var repository = mock(WriteCardRepository.class);
        var card = new Card().setCardExternalId(cardExternalId).setPosition(123);
        when(repository.findByExternalId(cardExternalId)).thenReturn(Optional.of(card));

        // when
        var handler = new MoveCardHandler(repository);
        handler.handle(command);

        // then
        verify(repository).findByExternalId(cardExternalId);
        verify(repository).update(card);
        assertThat(card.getCardExternalId()).isEqualTo(cardExternalId);
        assertThat(card.getPosition()).isEqualTo(position);
    }
}

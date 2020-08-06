package org.seariver.kanbanboard.write.domain.application;

import helper.TestHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.seariver.kanbanboard.write.domain.core.Card;
import org.seariver.kanbanboard.write.domain.core.WriteCardRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
public class UpdateCardHandlerTest extends TestHelper {

    @Test
    void GIVEN_ValidCommand_MUST_UpdateCard() {

        // given
        var externalId = UUID.randomUUID();
        var name = faker.pokemon().name();
        var description = faker.lorem().paragraph();
        var command = new UpdateCardCommand(externalId.toString(), name, description);

        var repository = mock(WriteCardRepository.class);
        Card card = new Card();
        when(repository.findByExternalId(externalId)).thenReturn(Optional.of(card));

        // when
        var handler = new UpdateCardHandler(repository);
        handler.handle(command);

        // then
        verify(repository).findByExternalId(externalId);
        verify(repository).update(card);
        assertThat(card.getName()).isEqualTo(name);
        assertThat(card.getDescription()).isEqualTo(description);
    }
}

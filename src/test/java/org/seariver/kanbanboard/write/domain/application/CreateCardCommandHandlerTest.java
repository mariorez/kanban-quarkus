package org.seariver.kanbanboard.write.domain.application;

import helper.TestHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.seariver.kanbanboard.write.domain.core.Bucket;
import org.seariver.kanbanboard.write.domain.core.Card;
import org.seariver.kanbanboard.write.domain.core.WriteBucketRepository;
import org.seariver.kanbanboard.write.domain.core.WriteCardRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
public class CreateCardCommandHandlerTest extends TestHelper {

    private ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);

    @Test
    void GIVEN_ValidCommand_MUST_CreateCard() {

        // given
        var bucketId = 100L;
        var bucketExternalId = UUID.randomUUID();
        var externalId = UUID.randomUUID();
        var position = faker.number().randomDouble(3, 1, 10);
        var name = faker.pokemon().name();
        CreateCardCommand command = new CreateCardCommand(bucketExternalId, externalId, position, name);
        var bucketRepository = mock(WriteBucketRepository.class);
        var cardRepository = mock(WriteCardRepository.class);
        when(bucketRepository.findByExternalId(bucketExternalId)).thenReturn(
            Optional.of(new Bucket().setId(bucketId).setExternalId(bucketExternalId)));

        // when
        CreateCardCommandHandler handler = new CreateCardCommandHandler(bucketRepository, cardRepository);
        handler.handle(command);

        // then
        verify(bucketRepository).findByExternalId(bucketExternalId);
        verify(cardRepository).create(captor.capture());
        var card = captor.getValue();
        assertThat(card.getBucketId()).isEqualTo(bucketId);
        assertThat(card.getExternalId()).isEqualTo(externalId);
        assertThat(card.getPosition()).isEqualTo(position);
        assertThat(card.getName()).isEqualTo(name);
    }
}

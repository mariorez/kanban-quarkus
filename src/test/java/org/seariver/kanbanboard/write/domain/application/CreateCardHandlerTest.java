package org.seariver.kanbanboard.write.domain.application;

import helper.TestHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.seariver.kanbanboard.write.domain.core.Bucket;
import org.seariver.kanbanboard.write.domain.core.Card;
import org.seariver.kanbanboard.write.domain.core.WriteBucketRepository;
import org.seariver.kanbanboard.write.domain.core.WriteCardRepository;
import org.seariver.kanbanboard.write.domain.exception.BucketNotExistentException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
public class CreateCardHandlerTest extends TestHelper {

    private ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);

    @Test
    void GIVEN_ValidCommand_MUST_CreateCard() {

        // given
        var bucketId = 100L;
        var bucketExternalId = UUID.randomUUID();
        var cardExternalId = UUID.randomUUID();
        var position = faker.number().randomDouble(3, 1, 10);
        var name = faker.pokemon().name();
        var command = new CreateCardCommand(bucketExternalId.toString(), cardExternalId.toString(), position, name);
        var bucketRepository = mock(WriteBucketRepository.class);
        var cardRepository = mock(WriteCardRepository.class);
        when(bucketRepository.findByExternalId(bucketExternalId)).thenReturn(
                Optional.of(new Bucket().setId(bucketId).setBucketExternalId(bucketExternalId)));

        // when
        CreateCardHandler handler = new CreateCardHandler(bucketRepository, cardRepository);
        handler.handle(command);

        // then
        verify(bucketRepository).findByExternalId(bucketExternalId);
        verify(cardRepository).create(captor.capture());
        var card = captor.getValue();
        assertThat(card.getBucketId()).isEqualTo(bucketId);
        assertThat(card.getCardExternalId()).isEqualTo(cardExternalId);
        assertThat(card.getPosition()).isEqualTo(position);
        assertThat(card.getName()).isEqualTo(name);
    }

    @Test
    void GIVEN_NotExistentBucket_MUST_ThrowException() {

        // given
        var notExistentBucketExternalId = UUID.fromString("019641f6-6e9e-4dd9-ab02-e864a3dfa016");
        var command = new CreateCardCommand(notExistentBucketExternalId.toString(), UUID.randomUUID().toString(), 1.3, "WHATEVER");
        var bucketRepository = mock(WriteBucketRepository.class);
        var cardRepository = mock(WriteCardRepository.class);
        when(bucketRepository.findByExternalId(notExistentBucketExternalId)).thenReturn(Optional.empty());

        // when
        var handler = new CreateCardHandler(bucketRepository, cardRepository);
        var exception = assertThrows(BucketNotExistentException.class, () -> handler.handle(command));

        // then
        verify(bucketRepository).findByExternalId(notExistentBucketExternalId);
        assertThat(exception.getMessage()).isEqualTo("Bucket not exist");
    }
}

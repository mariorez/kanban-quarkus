package org.seariver.kanbanboard.write.adapter.out;

import helper.DataSourceMock;
import helper.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.seariver.kanbanboard.write.domain.core.Card;
import org.seariver.kanbanboard.write.domain.core.WriteCardRepository;
import org.seariver.kanbanboard.write.domain.exception.DuplicatedDataException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Tag("unit")
class WriteCardRepositoryImplTest extends TestHelper {

    private WriteCardRepositoryImpl repository;

    @BeforeEach
    void setup() {
        repository = new WriteCardRepositoryImpl(new DataSourceMock());
    }

    @Test
    void MUST_ImplementInterface() {
        assertThat(repository).isInstanceOf(WriteCardRepository.class);
    }

    @Test
    void WHEN_CreatingCard_GIVEN_ValidData_MUST_PersistOnDatabase() {

        // given
        var bucketId = 1L;
        var cardExternalId = UUID.randomUUID();
        var position = faker.number().randomDouble(3, 1, 10);
        var name = faker.pokemon().name();
        var expected = new Card()
                .setBucketId(bucketId)
                .setCardExternalId(cardExternalId)
                .setPosition(position)
                .setName(name);

        // when
        repository.create(expected);

        // then
        var actualOptional = repository.findByExternalId(cardExternalId);
        Card actual = actualOptional.get();
        assertThat(actual.getBucketId()).isEqualTo(bucketId);
        assertThat(actual.getCardExternalId()).isEqualTo(expected.getCardExternalId());
        assertThat(actual.getPosition()).isEqualTo(expected.getPosition());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(actual.getUpdatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @ParameterizedTest
    @MethodSource("creatingWithDuplicatedDataProvider")
    void WHEN_CreatingCard_GIVEN_AlreadyExistentKey_MUST_ThrowException(UUID cardExternalId,
                                                                        double position,
                                                                        Map<String, Object> expectedError) {

        // given
        var bucketId = 1L;
        var name = faker.pokemon().name();
        var expected = new Card()
                .setBucketId(bucketId)
                .setCardExternalId(cardExternalId)
                .setPosition(position)
                .setName(name);

        // then
        DuplicatedDataException exception = assertThrows(
                DuplicatedDataException.class, () -> repository.create(expected));

        // when
        assertThat(exception.getMessage()).isEqualTo("Invalid duplicated data");
        assertThat(exception.getCode()).isEqualTo(1000);
        assertThat(exception.getErrors()).containsExactlyInAnyOrderEntriesOf(expectedError);
    }

    @Test
    void WHEN_UpdatingCard_WITH_ValidData_MUST_SaveOnDatabase() {

        // given
        var cardExternalId = UUID.fromString("021944cd-f516-4432-ba8d-44a312267c7d");
        var card = repository.findByExternalId(cardExternalId).get();
        var newName = faker.pokemon().name();
        var newDescription = faker.lorem().paragraph();
        var newPosition = faker.number().randomDouble(2, 1, 5);

        // when
        card.setPosition(newPosition);
        card.setName(newName);
        card.setDescription(newDescription);
        repository.update(card);

        // then
        var actualCard = repository.findByExternalId(cardExternalId).get();
        assertThat(actualCard.getName()).isEqualTo(newName);
        assertThat(actualCard.getPosition()).isEqualTo(newPosition);
        assertThat(actualCard.getDescription()).isEqualTo(newDescription);
    }

    private static Stream<Arguments> creatingWithDuplicatedDataProvider() {

        var existentCardExternalId = UUID.fromString("021944cd-f516-4432-ba8d-44a312267c7d");
        var existentPositionSameRegister = 200.01;
        var existentPositionAnotherRegister = 100.01;
        var notInUsePosition = faker.number().randomDouble(3, 1, 10);

        return Stream.of(
                arguments(existentCardExternalId, notInUsePosition, Map.of("id", existentCardExternalId)),
                arguments(UUID.randomUUID(), existentPositionSameRegister, Map.of("position", existentPositionSameRegister)),
                arguments(existentCardExternalId, existentPositionSameRegister, Map.of("id", existentCardExternalId, "position", existentPositionSameRegister)),
                arguments(existentCardExternalId, existentPositionAnotherRegister, Map.of("id", existentCardExternalId, "position", existentPositionAnotherRegister))
        );
    }
}
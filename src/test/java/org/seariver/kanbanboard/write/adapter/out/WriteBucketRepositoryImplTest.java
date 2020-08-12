package org.seariver.kanbanboard.write.adapter.out;

import helper.DataSourceMock;
import helper.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.seariver.kanbanboard.write.application.domain.Bucket;
import org.seariver.kanbanboard.write.application.domain.WriteBucketRepository;
import org.seariver.kanbanboard.write.application.exception.DuplicatedDataException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Tag("unit")
class WriteBucketRepositoryImplTest extends TestHelper {

    private WriteBucketRepositoryImpl repository;

    @BeforeEach
    void setup() {
        repository = new WriteBucketRepositoryImpl(new DataSourceMock());
    }

    @Test
    void MUST_ImplementInterface() {
        assertThat(repository).isInstanceOf(WriteBucketRepository.class);
    }

    @ParameterizedTest
    @MethodSource("validDataProvider")
    void WHEN_CreatingBucket_GIVEN_ValidData_MUST_PersistOnDatabase(UUID bucketExternalId,
                                                                    double position,
                                                                    String name) {
        // given
        var expected = new Bucket()
                .setBucketExternalId(bucketExternalId)
                .setPosition(position)
                .setName(name);

        // when
        repository.create(expected);

        // then
        var actualOptional = repository.findByExternalId(bucketExternalId);
        var actual = actualOptional.get();
        assertThat(actual.getId()).isPositive();
        assertThat(actual.getBucketExternalId()).isEqualTo(expected.getBucketExternalId());
        assertThat(actual.getPosition()).isEqualTo(expected.getPosition());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(actual.getUpdatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @ParameterizedTest
    @MethodSource("creatingWithDuplicatedDataProvider")
    void WHEN_CreatingBucket_GIVEN_AlreadyExistentKey_MUST_ThrowException(UUID bucketExternalId,
                                                                          double position,
                                                                          Map<String, Object> expectedError) {
        // given
        var expected = new Bucket()
                .setBucketExternalId(bucketExternalId)
                .setPosition(position)
                .setName("WHATEVER");

        // then
        DuplicatedDataException exception = assertThrows(
                DuplicatedDataException.class, () -> repository.create(expected));

        // when
        assertThat(exception.getMessage()).isEqualTo("Invalid duplicated data");
        assertThat(exception.getCode()).isEqualTo(1000);
        assertThat(exception.getErrors()).containsExactlyInAnyOrderEntriesOf(expectedError);
    }

    @Test
    void WHEN_UpdatingBucket_WITH_ValidData_MUST_SaveOnDatabase() {

        // given
        var bucketExternalId = UUID.fromString("3731c747-ea27-42e5-a52b-1dfbfa9617db");
        var actualBucket = repository.findByExternalId(bucketExternalId).get();

        var position = faker.number().randomDouble(3, 1, 10);
        var name = faker.pokemon().name();
        actualBucket.setPosition(position).setName(name);

        // when
        repository.update(actualBucket);

        // then
        var expectedBucket = repository.findByExternalId(bucketExternalId).get();
        assertThat(expectedBucket.getBucketExternalId()).isEqualTo(bucketExternalId);
        assertThat(expectedBucket.getPosition()).isEqualTo(position);
        assertThat(expectedBucket.getName()).isEqualTo(name);
        assertThat(expectedBucket.getCreatedAt()).isNotNull();
        assertThat(expectedBucket.getUpdatedAt()).isNotNull();
    }

    @Test
    void WHEN_UpdatingBucket_GIVEN_AlreadyExistentKey_MUST_ThrowException() {

        // given
        double alreadyExistentPosition = 100.15;
        var expected = new Bucket()
                .setBucketExternalId(UUID.fromString("3731c747-ea27-42e5-a52b-1dfbfa9617db"))
                .setPosition(alreadyExistentPosition)
                .setName("WHATEVER");

        // then
        DuplicatedDataException exception = assertThrows(
                DuplicatedDataException.class, () -> repository.update(expected));

        // when
        assertThat(exception.getMessage()).isEqualTo("Invalid duplicated data");
        assertThat(exception.getCode()).isEqualTo(1000);
        assertThat(exception.getErrors()).containsExactlyInAnyOrderEntriesOf(Map.of("position", alreadyExistentPosition));
    }

    private static Stream<Arguments> validDataProvider() {

        var positionAsInteger = faker.number().randomDigitNotZero();
        var positionAsDouble = faker.number().randomDouble(3, 1, 10);
        var newName = faker.pokemon().name();
        var existentName = "FIRST-BUCKET";

        return Stream.of(
                arguments(UUID.randomUUID(), positionAsInteger, newName),
                arguments(UUID.randomUUID(), positionAsDouble, existentName)
        );
    }

    private static Stream<Arguments> creatingWithDuplicatedDataProvider() {

        var existentBucketExternalId = UUID.fromString("3731c747-ea27-42e5-a52b-1dfbfa9617db");
        var existentPositionSameRegister = 200.987;
        var existentPositionAnotherRegister = 100.15;
        var validPosition = faker.number().randomDouble(3, 1, 10);

        return Stream.of(
                arguments(existentBucketExternalId, validPosition, Map.of("id", existentBucketExternalId)),
                arguments(UUID.randomUUID(), existentPositionSameRegister, Map.of("position", existentPositionSameRegister)),
                arguments(existentBucketExternalId, existentPositionSameRegister, Map.of("id", existentBucketExternalId, "position", existentPositionSameRegister)),
                arguments(existentBucketExternalId, existentPositionAnotherRegister, Map.of("id", existentBucketExternalId, "position", existentPositionAnotherRegister))
        );
    }
}
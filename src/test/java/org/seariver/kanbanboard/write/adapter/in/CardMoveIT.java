package org.seariver.kanbanboard.write.adapter.in;

import com.github.jsontemplate.JsonTemplate;
import helper.IntegrationHelper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.seariver.kanbanboard.write.adapter.out.WriteCardRepositoryImpl;

import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@QuarkusTest
class CardMoveIT extends IntegrationHelper {

    public static final String RESOURCE_PATH = "/v1/cards/{cardExternalId}/move";

    @ParameterizedTest
    @MethodSource("provideValidMoveData")
    void GIVEN_ValidPayload_MUST_UpdateSuccessful(String existentBucketExternalId,
                                                  String existentCardExternalId,
                                                  double newPosition,
                                                  int bucketId) {
        // setup
        var template = "{" +
                "  bucketId : $bucketId," +
                "  position : $position" +
                "}";
        var payload = new JsonTemplate(template)
                .withVar("bucketId", existentBucketExternalId)
                .withVar("position", newPosition)
                .prettyString();

        // verify
        given()
                .contentType(JSON)
                .body(payload).log().body()
                .when()
                .patch(RESOURCE_PATH, existentCardExternalId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        var repository = new WriteCardRepositoryImpl(dataSource);
        var actualBucket = repository.findByExternalId(UUID.fromString(existentCardExternalId)).get();
        assertThat(actualBucket.getBucketId()).isEqualTo(bucketId);
        assertThat(actualBucket.getPosition()).isEqualTo(newPosition);
    }

    private static Stream<Arguments> provideValidMoveData() {

        var existentCardExternalId = "df5cf5b1-c2c7-4c02-b4d4-341d6772f193";
        var cardCurrentBucketExternalId = "6d9db741-ef57-4d5a-ac0f-34f68fb0ab5e";
        var cardCurrentBucketId = 2;
        var anotherBucketExternalId = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
        var anotherBucketId = 1;

        return Stream.of(
                arguments(cardCurrentBucketExternalId, existentCardExternalId, 1.12, cardCurrentBucketId),
                arguments(anotherBucketExternalId, existentCardExternalId, 2.0, anotherBucketId)
        );
    }
}

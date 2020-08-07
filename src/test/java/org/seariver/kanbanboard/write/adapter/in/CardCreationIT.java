package org.seariver.kanbanboard.write.adapter.in;

import com.github.jsontemplate.JsonTemplate;
import helper.BlankStringValueProducer;
import helper.IntegrationHelper;
import helper.UuidStringValueProducer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.seariver.kanbanboard.write.adapter.out.WriteCardRepositoryImpl;

import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@QuarkusTest
public class CardCreationIT extends IntegrationHelper {

    public static final String ENDPOINT_PATH = "/v1/cards";

    @Test
    void GIVEN_ValidPayload_MUST_ReturnCreated() {

        // setup
        var bucketExternalId = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
        var cardExternalId = UUID.randomUUID().toString();
        var position = faker.number().randomDouble(3, 1, 10);
        var name = faker.pokemon().name();

        var template = String.format("{" +
                "  bucketId : $bucketId," +
                "  cardId : $cardId," +
                "  position : %s," +
                "  name : $name" +
                "}", position);

        var payload = new JsonTemplate(template)
                .withVar("bucketId", bucketExternalId)
                .withVar("cardId", cardExternalId)
                .withVar("name", name)
                .prettyString();

        // verify
        given()
                .contentType(JSON)
                .body(payload).log().body()
                .when()
                .post(ENDPOINT_PATH)
                .then()
                .statusCode(CREATED.getStatusCode());

        var repository = new WriteCardRepositoryImpl(dataSource);
        var newCard = repository.findByExternalId(UUID.fromString(cardExternalId)).get();
        assertThat(newCard.getBucketId()).isEqualTo(1L);
        assertThat(newCard.getCardExternalId().toString()).isEqualTo(cardExternalId);
        assertThat(newCard.getName()).isEqualTo(name);
        assertThat(newCard.getPosition()).isEqualTo(position);
        assertThat(newCard.getDescription()).isNull();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidData")
    void GIVEN_InvalidData_MUST_ReturnBadRequest(String jsonTemplate,
                                                 String[] errorsFields,
                                                 String[] errorsDetails) {
        // setup
        var payload = new JsonTemplate(jsonTemplate)
                .withValueProducer(new UuidStringValueProducer())
                .withValueProducer(new BlankStringValueProducer())
                .prettyString();

        // verify
        given()
                .contentType(JSON)
                .body(payload).log().body()
                .when()
                .post(ENDPOINT_PATH)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(JSON)
                .assertThat()
                .log().body()
                .body("message", is("Invalid parameter"),
                        "errors.field", containsInAnyOrder(errorsFields),
                        "errors.detail", containsInAnyOrder(errorsDetails));
    }

    @Test
    void GIVEN_MalformedJson_MUST_ReturnBadRequest() {

        // setup
        var payload = "{ malformed JSON >:{P ";

        // verify
        given()
                .contentType(JSON)
                .body(payload).log().body()
                .when()
                .post(ENDPOINT_PATH)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(JSON)
                .assertThat()
                .log().body()
                .body("message", is("Malformed JSON"));
    }

    @Test
    void GIVEN_DuplicatedKey_MUST_ReturnBadRequest() {

        // setup
        var existentBucketExternalId = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
        var duplicatedCardExternalId = "df5cf5b1-c2c7-4c02-b4d4-341d6772f193";
        var duplicatedPosition = 200.01;

        var template = "{" +
                "  bucketId : $existentBucketExternalId," +
                "  cardId : $duplicatedCardExternalId," +
                "  position : $duplicatedPosition," +
                "  name : @s" +
                "}";

        var payload = new JsonTemplate(template)
                .withVar("existentBucketExternalId", existentBucketExternalId)
                .withVar("duplicatedCardExternalId", duplicatedCardExternalId)
                .withVar("duplicatedPosition", duplicatedPosition)
                .prettyString();

        // verify
        given()
                .contentType(JSON)
                .body(payload).log().body()
                .when()
                .post(ENDPOINT_PATH)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(JSON)
                .assertThat()
                .log().body()
                .body("message", is("Invalid parameter"),
                        "errors.field", containsInAnyOrder("code"),
                        "errors.detail", containsInAnyOrder("1000"));
    }

    private static Stream<Arguments> provideInvalidData() {

        return Stream.of(

                // invalid bucketId entries
                arguments(
                        "{notExistentField:@s, cardId:@uuid, position:@f, name:@s}",
                        args("bucketId"), args("must not be blank")),
                arguments(
                        "{bucketId:null, cardId:@uuid, position:@f, name:@s}",
                        args("bucketId"), args("must not be blank")),
                arguments(
                        "{bucketId:@s(length=0), cardId:@uuid, position:@f, name:@s}",
                        args("bucketId", "bucketId"), args("must not be blank", "invalid UUID format")),
                arguments(
                        "{bucketId:@s(foobar), cardId:@uuid, position:@f, name:@s}",
                        args("bucketId"), args("invalid UUID format")),

                // invalid cardId entries
                arguments(
                        "{bucketId:@uuid, notExistentField:@s, position:@f, name:@s}",
                        args("cardId"), args("must not be blank")),
                arguments(
                        "{bucketId:@uuid, cardId:null, position:@f, name:@s}",
                        args("cardId"), args("must not be blank")),
                arguments(
                        "{bucketId:@uuid, cardId:@s(length=0), position:@f, name:@s}",
                        args("cardId", "cardId"), args("must not be blank", "invalid UUID format")),
                arguments(
                        "{bucketId:@uuid, cardId:@s(foobar), position:@f, name:@s}",
                        args("cardId"), args("invalid UUID format")),

                // invalid position entries
                arguments(
                        "{bucketId:@uuid, cardId:@uuid, notExistentField:@f, name:@s}",
                        args("position"), args("must be greater than 0")),
                arguments(
                        "{bucketId:@uuid, cardId:@uuid, position:@f(-1), name:@s}",
                        args("position"), args("must be greater than 0")),
                arguments(
                        "{bucketId:@uuid, cardId:@uuid, position:@f(0), name:@s}",
                        args("position"), args("must be greater than 0")),

                // invalid NAME parameter entries
                arguments(
                        "{bucketId:@uuid, cardId:@uuid, position:@f, notExistentField:@s}",
                        args("name"), args("must not be blank")),
                arguments(
                        "{bucketId:@uuid, cardId:@uuid, position:@f, name:null}",
                        args("name"), args("must not be blank")),
                arguments(
                        "{bucketId:@uuid, cardId:@uuid, position:@f, name:@s(length=0)}",
                        args("name", "name"), args("must not be blank", "size must be between 1 and 100")),
                arguments(
                        "{bucketId:@uuid, cardId:@uuid, position:@f, name:@blank}",
                        args("name"), args("must not be blank")),
                arguments(
                        "{bucketId:@uuid, cardId:@uuid, position:@f, name:@s(length=101)}",
                        args("name"), args("size must be between 1 and 100"))
        );
    }
}

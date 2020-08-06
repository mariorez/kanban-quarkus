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

        // fixture
        var externalId = UUID.randomUUID().toString();
        var bucketExternalId = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
        var position = faker.number().randomDouble(3, 1, 10);
        var name = faker.pokemon().name();

        var template = String.format("{" +
                "  id : $id," +
                "  bucket : $bucket," +
                "  position : %s," +
                "  name : $name" +
                "}", position);

        var payload = new JsonTemplate(template)
                .withVar("id", externalId)
                .withVar("bucket", bucketExternalId)
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
        var newCard = repository.findByExternalId(UUID.fromString(externalId)).get();
        assertThat(newCard.getBucketId()).isEqualTo(1L);
        assertThat(newCard.getName()).isEqualTo(name);
        assertThat(newCard.getPosition()).isEqualTo(position);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidData")
    void GIVEN_InvalidData_MUST_ReturnBadRequest(String jsonTemplate,
                                                 String[] errorsFields,
                                                 String[] errorsDetails) {
        // fixture
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
        // fixture
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

        var bucketExternalId = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
        var duplicatedExternalId = "df5cf5b1-c2c7-4c02-b4d4-341d6772f193";
        var duplicatedPosition = 200.01;

        // given
        var template = "{" +
                "  id : $externalId," +
                "  bucket : $bucketExternalId," +
                "  position : $position," +
                "  name : @s" +
                "}";

        var payload = new JsonTemplate(template)
                .withVar("externalId", duplicatedExternalId)
                .withVar("bucketExternalId", bucketExternalId)
                .withVar("position", duplicatedPosition)
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
                //id
                arguments(
                        "{id:null, bucket:@uuid, position:@f, name:@s}",
                        args("id"), args("must not be blank")),
                arguments(
                        "{id:@s(length=0), bucket:@uuid, position:@f, name:@s}",
                        args("id", "id"), args("must not be blank", "invalid UUID format")),
                arguments(
                        "{id:@s(foobar), bucket:@uuid, position:@f, name:@s}",
                        args("id"), args("invalid UUID format")),
                arguments(
                        "{notExistentField:@s, bucket:@uuid, position:@f, name:@s}",
                        args("id"), args("must not be blank")),
                //bucket
                arguments(
                        "{id:@uuid, bucket:null, position:@f, name:@s}",
                        args("bucket"), args("must not be blank")),
                arguments(
                        "{id:@uuid, bucket:@s(length=0), position:@f, name:@s}",
                        args("bucket", "bucket"), args("must not be blank", "invalid UUID format")),
                arguments(
                        "{id:@uuid, bucket:@s(foobar), position:@f, name:@s}",
                        args("bucket"), args("invalid UUID format")),
                arguments(
                        "{id:@uuid, notExistentField:@s, position:@f, name:@s}",
                        args("bucket"), args("must not be blank")),
                //position
                arguments(
                        "{id:@uuid, bucket:@uuid, position:@f(-1), name:@s}",
                        args("position"), args("must be greater than 0")),
                arguments(
                        "{id:@uuid, bucket:@uuid, position:@f(0), name:@s}",
                        args("position"), args("must be greater than 0")),
                //name
                arguments(
                        "{id:@uuid, bucket:@uuid, position:@f, name:null}",
                        args("name"), args("must not be blank")),
                arguments(
                        "{id:@uuid, bucket:@uuid, position:@f, name:@s(length=0)}",
                        args("name", "name"), args("must not be blank", "size must be between 1 and 100")),
                arguments(
                        "{id:@uuid, bucket:@uuid, position:@f, name:@blank}",
                        args("name"), args("must not be blank")),
                arguments(
                        "{id:@uuid, bucket:@uuid, position:@f, name:@s(length=101)}",
                        args("name"), args("size must be between 1 and 100"))
        );
    }
}

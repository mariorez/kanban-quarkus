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
import org.seariver.kanbanboard.write.adapter.out.WriteBucketRepositoryImpl;

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
class BucketCreationIT extends IntegrationHelper {

    public static final String RESOURCE_PATH = "/v1/buckets";

    @Test
    void GIVEN_ValidPayload_MUST_ReturnCreated() {

        // setup
        var externalId = UUID.randomUUID().toString();
        var position = faker.number().randomDouble(3, 1, 10);
        var name = faker.pokemon().name();

        var template = String.format("{" +
                "  bucketId : $bucketId," +
                "  position : %s," +
                "  name : $name" +
                "}", position);

        var payload = new JsonTemplate(template)
                .withVar("bucketId", externalId)
                .withVar("name", name)
                .prettyString();

        // verify
        given()
                .contentType(JSON)
                .body(payload).log().body()
                .when()
                .post(RESOURCE_PATH)
                .then()
                .statusCode(CREATED.getStatusCode());

        var repository = new WriteBucketRepositoryImpl(dataSource);
        var newBucket = repository.findByExternalId(UUID.fromString(externalId)).get();
        assertThat(newBucket.getName()).isEqualTo(name);
        assertThat(newBucket.getPosition()).isEqualTo(position);
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
                .post(RESOURCE_PATH)
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
                .post(RESOURCE_PATH)
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
        var duplicatedExternalId = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
        var duplicatedPosition = 100.15;

        var template = "{" +
                "  bucketId : $bucketId," +
                "  position : $position," +
                "  name : @s" +
                "}";

        var payload = new JsonTemplate(template)
                .withVar("bucketId", duplicatedExternalId)
                .withVar("position", duplicatedPosition)
                .prettyString();

        // verify
        given()
                .contentType(JSON)
                .body(payload).log().body()
                .when()
                .post(RESOURCE_PATH)
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
                // invalid bucketId parameter entries
                arguments(
                        "{notExistentField:@s, position:@f, name:@s}",
                        args("bucketId"), args("must not be blank")),
                arguments(
                        "{bucketId:null, position:@f, name:@s}",
                        args("bucketId"), args("must not be blank")),
                arguments(
                        "{bucketId:@s(length=0), position:@f, name:@s}",
                        args("bucketId", "bucketId"), args("must not be blank", "invalid UUID format")),
                arguments(
                        "{bucketId:@s(foobar), position:@f, name:@s}",
                        args("bucketId"), args("invalid UUID format")),
                // invalid position parameter entries
                arguments(
                        "{bucketId:@uuid, notExistentField:@f, name:@s}",
                        args("position"), args("must be greater than 0")),
                arguments(
                        "{bucketId:@uuid, position:@f(-1), name:@s}",
                        args("position"), args("must be greater than 0")),
                arguments(
                        "{bucketId:@uuid, position:@f(0), name:@s}",
                        args("position"), args("must be greater than 0")),
                // invalid name parameter entries
                arguments(
                        "{bucketId:@uuid, position:@f, notExistentField:@s}",
                        args("name"), args("must not be blank")),
                arguments(
                        "{bucketId:@uuid, position:@f, name:null}",
                        args("name"), args("must not be blank")),
                arguments(
                        "{bucketId:@uuid, position:@f, name:@s(length=0)}",
                        args("name", "name"), args("must not be blank", "size must be between 1 and 100")),
                arguments(
                        "{bucketId:@uuid, position:@f, name:@blank}",
                        args("name"), args("must not be blank")),
                arguments(
                        "{bucketId:@uuid, position:@f, name:@s(length=101)}",
                        args("name"), args("size must be between 1 and 100"))
        );
    }
}

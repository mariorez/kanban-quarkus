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
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@QuarkusTest
class BucketUpdateIT extends IntegrationHelper {

    public static final String RESOURCE_PATH = "/v1/buckets/{bucketExternalId}";

    @Test
    void GIVEN_ValidPayload_MUST_UpdateSuccessful() {

        // setup
        var existentBucketExternalId = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
        var newName = "New Name";

        var template = "{" +
                "  name : $name" +
                "}";

        var payload = new JsonTemplate(template)
                .withVar("name", newName)
                .prettyString();

        // verify
        given()
                .contentType(JSON)
                .body(payload).log().body()
                .when()
                .patch(RESOURCE_PATH, existentBucketExternalId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        var repository = new WriteBucketRepositoryImpl(dataSource);
        var actualBucket = repository.findByExternalId(UUID.fromString(existentBucketExternalId)).get();
        assertThat(actualBucket.getName()).isEqualTo(newName);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidNames")
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
                .patch(RESOURCE_PATH, UUID.randomUUID().toString())
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
    void GIVEN_NotExistentExternalId_MUST_ReturnBadRequest() {

        // setup
        var notExistentBucketExternalId = UUID.randomUUID().toString();

        var template = "{" +
                "  name : @s" +
                "}";

        var payload = new JsonTemplate(template).prettyString();

        // verify
        given()
                .contentType(JSON)
                .body(payload).log().body()
                .when()
                .patch(RESOURCE_PATH, notExistentBucketExternalId)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .contentType(JSON)
                .assertThat()
                .log().body()
                .body("message", is(NOT_FOUND.getReasonPhrase()),
                        "errors.field", containsInAnyOrder("code"),
                        "errors.detail", containsInAnyOrder("1001"));
    }

    private static Stream<Arguments> provideInvalidNames() {

        return Stream.of(
                arguments("{name:null}",
                        args("name"), args("must not be blank")),
                arguments("{name:@s(length=0)}",
                        args("name", "name"), args("must not be blank", "size must be between 1 and 100")),
                arguments("{name:@blank}",
                        args("name"), args("must not be blank")),
                arguments("{notExistent:@s}",
                        args("name"), args("must not be blank"))
        );
    }
}

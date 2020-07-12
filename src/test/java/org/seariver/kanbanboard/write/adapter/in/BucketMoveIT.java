package org.seariver.kanbanboard.write.adapter.in;

import com.github.jsontemplate.JsonTemplate;
import helper.BlankStringValueProducer;
import helper.IntegrationHelper;
import helper.UuidStringValueProducer;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.seariver.kanbanboard.write.adapter.out.WriteBucketRepositoryImpl;

import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@QuarkusTest
class BucketMoveIT extends IntegrationHelper {

    public static final String ENDPOINT_PATH = "/v1/buckets/{id}/move";

    @Test
    void GIVEN_ValidPayload_MUST_UpdateSuccessful() {

        // fixture
        var existentExternalId = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
        var newPosition = 1.23;

        var template = String.format("{" +
            "  position : %s" +
            "}", newPosition);

        var payload = new JsonTemplate(template).prettyString();

        // verify
        given()
            .contentType(ContentType.JSON)
            .body(payload).log().body()
            .when().put(ENDPOINT_PATH, existentExternalId)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        var repository = new WriteBucketRepositoryImpl(dataSource);
        var actualBucket = repository.findByExternalId(UUID.fromString(existentExternalId)).get();
        assertThat(newPosition).isEqualTo(actualBucket.getPosition());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidPositions")
    void GIVEN_InvalidData_MUST_ReturnBadRequest(String jsonTemplate,
                                                 String errorMessage,
                                                 String[] errorsFields,
                                                 String[] errorsDetails) {
        // fixture
        String existentExternalId = "3731c747-ea27-42e5-a52b-1dfbfa9617db";

        var payload = new JsonTemplate(jsonTemplate)
            .withValueProducer(new UuidStringValueProducer())
            .withValueProducer(new BlankStringValueProducer())
            .prettyString();

        // verify
        given()
            .contentType(ContentType.JSON)
            .body(payload).log().body()
            .when().put(ENDPOINT_PATH, existentExternalId)
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .contentType(ContentType.JSON)
            .assertThat()
            .body("message", is(errorMessage))
            .and().body("errors.field", containsInAnyOrder(errorsFields))
            .and().body("errors.detail", containsInAnyOrder(errorsDetails))
            .log().body();
    }

    @Test
    void GIVEN_NotExistentKey_MUST_ReturnBadRequest() {

        // fixture
        var notExistentExternalId = "effce142-1a08-49d4-9fe6-3fe728b17a41";

        var template = "{" +
            "  position : @f" +
            "}";

        var payload = new JsonTemplate(template).prettyString();

        // verify
        given()
            .contentType(ContentType.JSON)
            .body(payload).log().body()
            .when().put(ENDPOINT_PATH, notExistentExternalId)
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .contentType(ContentType.JSON)
            .assertThat()
            .body("message", is("Invalid field"))
            .and().body("errors.field", containsInAnyOrder("code"))
            .and().body("errors.detail", containsInAnyOrder("1001"))
            .log().body();
    }

    @Test
    void GIVEN_DuplicatedPosition_MUST_ReturnBadRequest() {

        var existentExternalId = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
        var alreadyInUsePosition = 100.15;

        // given
        var template = String.format("{" +
            "  position : %s" +
            "}", alreadyInUsePosition);

        var payload = new JsonTemplate(template).prettyString();

        // verify
        given()
            .contentType(ContentType.JSON)
            .body(payload).log().body()
            .when().put(ENDPOINT_PATH, existentExternalId)
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .contentType(ContentType.JSON)
            .assertThat()
            .body("message", is("Invalid field"))
            .and().body("errors.field", containsInAnyOrder("code"))
            .and().body("errors.detail", containsInAnyOrder("1000"))
            .log().body();
    }

    private static Stream<Arguments> provideInvalidPositions() {

        return Stream.of(
            arguments("{position:null}", "Invalid field",
                args("position"), args("must be greater than 0")),
            arguments("{position:@s}", "Invalid format",
                args("position"), args("double")),
            arguments("{position:0}", "Invalid field",
                args("position"), args("must be greater than 0")),
            arguments("{position:-1}", "Invalid field",
                args("position"), args("must be greater than 0")),
            arguments("{notExistent:@f}", "Invalid field",
                args("position"), args("must be greater than 0"))
        );
    }
}

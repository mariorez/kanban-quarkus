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

import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@QuarkusTest
class BuckeCreationtIT extends IntegrationHelper {

    @Test
    void GIVEN_ValidPayload_MUST_ReturnCreated() {

        // fixture
        var uuid = UUID.randomUUID().toString();

        var template = "{" +
            "  uuid : $uuid," +
            "  position : @f," +
            "  name : @s" +
            "}";

        var payload = new JsonTemplate(template)
            .withVar("uuid", uuid)
            .prettyString();

        // verify
        given()
            .contentType(ContentType.JSON)
            .body(payload).log().body()
            .when().post("/v1/buckets")
            .then()
            .statusCode(CREATED.getStatusCode())
            .header("Location", containsString(String.format("/v1/buckets/%s", uuid)));
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
            .contentType(ContentType.JSON)
            .body(payload).log().body()
            .when().post("/v1/buckets")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .contentType(ContentType.JSON)
            .assertThat()
            .body("message", is("Invalid field"))
            .and().body("errors.field", containsInAnyOrder(errorsFields))
            .and().body("errors.detail", containsInAnyOrder(errorsDetails))
            .log().body();
    }

    @Test
    void GIVEN_MalformedJson_MUST_ReturnBadRequest() {
        // fixture
        var payload = "{ malformed JSON >:{P ";

        // verify
        given()
            .contentType(ContentType.JSON)
            .body(payload).log().body()
            .when().post("/v1/buckets")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .contentType(ContentType.JSON)
            .assertThat()
            .body("message", is("Malformed JSON"))
            .log().body();
    }

    @Test
    void GIVEN_DuplicatedKey_MUST_ReturnBadRequest() {

        var duplicatedUuid = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
        var duplicatedPosition = 100.15;

        // given
        var template = "{" +
            "  uuid : $uuid," +
            "  position : $position," +
            "  name : @s" +
            "}";

        var payload = new JsonTemplate(template)
            .withVar("uuid", duplicatedUuid)
            .withVar("position", duplicatedPosition)
            .prettyString();

        // verify
        given()
            .contentType(ContentType.JSON)
            .body(payload).log().body()
            .when().post("/v1/buckets")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .contentType(ContentType.JSON)
            .assertThat()
            .body("message", is("Invalid field"))
            .and().body("errors.field", containsInAnyOrder("code"))
            .and().body("errors.detail", containsInAnyOrder("1000"))
            .log().body();
    }

    private static Stream<Arguments> provideInvalidData() {

        return Stream.of(
            arguments(
                "{uuid:null, position:@f, name:@s}",
                args("uuid"), args("must not be blank")),
            arguments(
                "{uuid:@s(length=0), position:@f, name:@s}",
                args("uuid", "uuid"), args("must not be blank", "invalid uuid format")),
            arguments(
                "{uuid:@s(foobar), position:@f, name:@s}",
                args("uuid"), args("invalid uuid format")),
            arguments(
                "{notExistent:@s, position:@f, name:@s}",
                args("uuid"), args("must not be blank")),
            arguments(
                "{uuid:@uuid, position:@f(-1), name:@s}",
                args("position"), args("must be greater than 0")),
            arguments(
                "{uuid:@uuid, position:@f(0), name:@s}",
                args("position"), args("must be greater than 0")),
            arguments(
                "{uuid:@uuid, position:@f, name:null}",
                args("name"), args("must not be blank")),
            arguments(
                "{uuid:@uuid, position:@f, name:@s(length=0)}",
                args("name", "name"), args("must not be blank", "size must be between 1 and 100")),
            arguments(
                "{uuid:@uuid, position:@f, name:@blank}",
                args("name"), args("must not be blank")),
            arguments(
                "{uuid:@uuid, position:@f, name:@s(length=101)}",
                args("name"), args("size must be between 1 and 100"))
        );
    }
}

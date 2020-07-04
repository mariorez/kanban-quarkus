package org.seariver.kanbanboard.write.adapter.in;

import com.github.jsontemplate.JsonTemplate;
import helper.IntegrationHelper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.ws.rs.core.Response.Status;
import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@QuarkusTest
public class CreateBucketIT extends IntegrationHelper {

    @Test
    void GIVEN_ValidPayload_MUST_ReturnCreated() {

        // fixture
        var uuid = UUID.randomUUID().toString();
        var template = String.format("{" +
            "  id : %s," +
            "  position : @f," +
            "  name : @s" +
            "}", uuid);
        var payload = new JsonTemplate(template).prettyString();

        // verify
        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when().post("/v1/buckets")
            .then()
            .statusCode(Status.CREATED.getStatusCode())
            .header("Location", containsString(String.format("/v1/buckets/%s", uuid)));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidData")
    void GIVEN_InvalidData_MUST_ReturnBadRequest(String uuid,
                                                 double position,
                                                 String name,
                                                 String[] errorsFields,
                                                 String[] errorsDetails) {
        // fixture
        var template = String.format("{" +
            "  id : %s," +
            "  position : %s," +
            "  name : %s" +
            "}", uuid, position, name);
        var payload = new JsonTemplate(template).prettyString();

        // verify
        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when().post("/v1/buckets")
            .then()
            .statusCode(Status.BAD_REQUEST.getStatusCode())
            .contentType(ContentType.JSON)
            .assertThat()
            .body("message", is("Invalid field"))
            .and().body("errors", hasSize(1))
            .and().body("errors.field", containsInAnyOrder(errorsFields))
            .and().body("errors.detail", containsInAnyOrder(errorsDetails));
    }

    private static Stream<Arguments> provideInvalidData() {

        var validUuid = UUID.randomUUID().toString();
        var validPosition = faker.number().randomDouble(5, 1, 10);
        var validName = "WHATEVER";

        return Stream.of(
            arguments(null, validPosition, validName, args("uuid"), args("must not be null")),
            arguments("@s()", validPosition, validName, args("uuid"), args("must not be null")),
            arguments(validUuid, -1, validName, args("position"), args("must be greater than 0")),
            arguments(validUuid, 0, validName, args("position"), args("must be greater than 0")),
            arguments(validUuid, validPosition, null, args("name"), args("must not be blank")),
            arguments(validUuid, validPosition, "", args("name"), args("must not be blank")),
            arguments(validUuid, validPosition, "      ", args("name"), args("must not be blank")),
            arguments(validUuid, validPosition, "@s(length=101)", args("name"), args("size must be between 1 and 100"))
        );
    }
}

package org.seariver.kanbanboard.write.adapter.in;

import com.github.javafaker.Faker;
import com.github.jsontemplate.JsonTemplate;
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
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@QuarkusTest
public class WriteBucketIT {

    private static Faker faker = new Faker();

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
                                                 String name) {
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
            .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    private static Stream<Arguments> provideInvalidData() {

        var validUuid = UUID.randomUUID().toString();
        var validPosition = faker.number().randomDouble(5, 1, 10);
        var validName = "WHATEVER";

        return Stream.of(
            arguments(null, validPosition, validName),
            arguments("@s()", validPosition, validName),
            arguments(validUuid, -1, validName),
            arguments(validUuid, 0, validName),
            arguments(validUuid, validPosition, null),
            arguments(validUuid, validPosition, ""),
            arguments(validUuid, validPosition, "      "),
            arguments(validUuid, validPosition, "@s(length=101)")
        );
    }
}

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
import org.seariver.kanbanboard.write.adapter.out.WriteColumnRepositoryImpl;

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
class ColumnUpdateIT extends IntegrationHelper {

    public static final String ENDPOINT_PATH = "/v1/columns/{uuid}";

    @Test
    void GIVEN_ValidPayload_MUST_UpdateSuccessful() {

        // fixture
        var existentExternalId = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
        var name = "New Name";

        var template = "{" +
            "  name : $name" +
            "}";

        var payload = new JsonTemplate(template)
            .withVar("name", name)
            .prettyString();

        // verify
        given()
            .contentType(ContentType.JSON)
            .body(payload).log().body()
            .when().put(ENDPOINT_PATH, existentExternalId)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        var repository = new WriteColumnRepositoryImpl(dataSource);
        var actualBucket = repository.findByExternalId(UUID.fromString(existentExternalId)).get();
        assertThat(name).isEqualTo(actualBucket.getName());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidData")
    void GIVEN_InvalidData_MUST_ReturnBadRequest(String jsonTemplate,
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
            .body("message", is("Invalid field"))
            .and().body("errors.field", containsInAnyOrder(errorsFields))
            .and().body("errors.detail", containsInAnyOrder(errorsDetails))
            .log().body();
    }

    @Test
    void GIVEN_NotExistentKey_MUST_ReturnBadRequest() {

        // fixture
        var notExistentExternalId = UUID.randomUUID().toString();

        var template = "{" +
            "  name : @s" +
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

    private static Stream<Arguments> provideInvalidData() {

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

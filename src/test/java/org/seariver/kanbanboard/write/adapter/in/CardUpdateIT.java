package org.seariver.kanbanboard.write.adapter.in;

import com.github.jsontemplate.JsonTemplate;
import helper.BlankStringValueProducer;
import helper.IntegrationHelper;
import helper.UuidStringValueProducer;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.seariver.kanbanboard.write.adapter.out.WriteCardRepositoryImpl;

import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@QuarkusTest
public class CardUpdateIT extends IntegrationHelper {

    public static final String RESOURCE_PATH = "/v1/cards/{cardExternalId}";

    @ParameterizedTest
    @MethodSource("provideValidData")
    void GIVEN_ValidPayload_MUST_UpdateSuccessful(String jsonTemplate) {

        // setup
        var cardExternalId = "021944cd-f516-4432-ba8d-44a312267c7d";
        var payload = new JsonTemplate(jsonTemplate)
                .withValueProducer(new BlankStringValueProducer())
                .prettyString();

        // verify
        given()
                .contentType(JSON)
                .body(payload).log().body()
                .when()
                .patch(RESOURCE_PATH, cardExternalId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        var repository = new WriteCardRepositoryImpl(dataSource);
        var updatedCard = repository.findByExternalId(UUID.fromString(cardExternalId)).get();
        assertThat(updatedCard.getName()).isNotBlank();
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

    private static Stream<Arguments> provideValidData() {
        return Stream.of(
                arguments("{name:@s}"),
                arguments("{name:@s, notExistentField: @s}"),
                arguments("{name:@s, description: @s}"),
                arguments("{name:@s, description: null}"),
                arguments("{name:@s, description: @s(length=0)}"),
                arguments("{name:@s, description: @blank}")
        );
    }

    private static Stream<Arguments> provideInvalidData() {
        return Stream.of(

                // invalid NAME parameter entries
                arguments(
                        "{notExistentField:@s, description: @s}",
                        args("name"), args("must not be blank")),
                arguments(
                        "{name:null, description: @s}",
                        args("name"), args("must not be blank")),
                arguments(
                        "{name:@s(length=0), description: @s}",
                        args("name", "name"), args("must not be blank", "size must be between 1 and 100")),
                arguments(
                        "{name:@blank, description: @s}",
                        args("name"), args("must not be blank")),
                arguments(
                        "{name:@s(length=101), description: @s}",
                        args("name"), args("size must be between 1 and 100"))
        );
    }
}

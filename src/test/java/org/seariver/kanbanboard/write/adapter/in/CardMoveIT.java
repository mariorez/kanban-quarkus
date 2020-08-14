package org.seariver.kanbanboard.write.adapter.in;

import com.github.jsontemplate.JsonTemplate;
import helper.IntegrationHelper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.seariver.kanbanboard.write.adapter.out.WriteCardRepositoryImpl;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class CardMoveIT extends IntegrationHelper {

    public static final String ENDPOINT_PATH = "/v1/cards/{cardExternalId}/move";

    @Test
    void GIVEN_ValidPayload_MUST_UpdateSuccessful() {

        // setup
        var existentCardExternalId = "df5cf5b1-c2c7-4c02-b4d4-341d6772f193";
        var newPosition = 1.12;
        var template = "{" +
                "  position : $position" +
                "}";
        var payload = new JsonTemplate(template)
                .withVar("position", newPosition)
                .prettyString();

        // verify
        given()
                .contentType(JSON)
                .body(payload).log().body()
                .when()
                .patch(ENDPOINT_PATH, existentCardExternalId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        var repository = new WriteCardRepositoryImpl(dataSource);
        var actualBucket = repository.findByExternalId(UUID.fromString(existentCardExternalId)).get();
        assertThat(actualBucket.getPosition()).isEqualTo(newPosition);
    }
}

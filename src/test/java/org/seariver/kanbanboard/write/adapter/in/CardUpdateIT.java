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
public class CardUpdateIT extends IntegrationHelper {

    public static final String ENDPOINT_PATH = "/v1/cards/{cardExternalId}";

    @Test
    void GIVEN_ValidPayload_MUST_UpdateSuccessful() {

        // setup
        var cardExternalId = "021944cd-f516-4432-ba8d-44a312267c7d";
        var name = faker.pokemon().name();
        var description = faker.lorem().paragraph();

        var template = "{" +
                "  name : $name," +
                "  description : $description" +
                "}";

        var payload = new JsonTemplate(template)
                .withVar("name", name)
                .withVar("description", description)
                .prettyString();

        // verify
        given()
                .contentType(JSON)
                .body(payload).log().body()
                .when()
                .put(ENDPOINT_PATH, cardExternalId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        var repository = new WriteCardRepositoryImpl(dataSource);
        var updatedCard = repository.findByExternalId(UUID.fromString(cardExternalId)).get();
        assertThat(updatedCard.getName()).isEqualTo(name);
        assertThat(updatedCard.getDescription()).isEqualTo(description);
    }
}

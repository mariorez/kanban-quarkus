package org.seariver.kanbanboard.write.adapter.in;

import com.github.jsontemplate.JsonTemplate;
import helper.IntegrationHelper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.seariver.kanbanboard.write.adapter.out.WriteCardRepositoryImpl;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.CREATED;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class CardCreationIT extends IntegrationHelper {

    public static final String ENDPOINT_PATH = "/v1/buckets/{bucketExternalId}/cards";

    @Test
    void GIVEN_ValidPayload_MUST_ReturnCreated() {

        // fixture
        var bucketExternalId = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
        var externalId = UUID.randomUUID().toString();
        var position = faker.number().randomDouble(3, 1, 10);
        var name = faker.pokemon().name();

        var template = String.format("{" +
            "  id : $id," +
            "  position : %s," +
            "  name : $name" +
            "}", position);

        var payload = new JsonTemplate(template)
            .withVar("id", externalId)
            .withVar("name", name)
            .prettyString();

        // verify
        given()
            .contentType(ContentType.JSON)
            .body(payload).log().body()
            .when()
            .post(ENDPOINT_PATH, bucketExternalId)
            .then()
            .statusCode(CREATED.getStatusCode());

        var repository = new WriteCardRepositoryImpl(dataSource);
        var newCard = repository.findByExternalId(UUID.fromString(externalId)).get();
        assertThat(newCard.getBucketId()).isEqualTo(1L);
        assertThat(newCard.getName()).isEqualTo(name);
        assertThat(newCard.getPosition()).isEqualTo(position);
    }
}

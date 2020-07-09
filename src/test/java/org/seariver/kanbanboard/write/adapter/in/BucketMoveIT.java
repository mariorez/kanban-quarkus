package org.seariver.kanbanboard.write.adapter.in;

import com.github.jsontemplate.JsonTemplate;
import helper.IntegrationHelper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.seariver.kanbanboard.write.adapter.out.WriteBucketRepositoryImpl;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class BucketMoveIT extends IntegrationHelper {

    @Test
    void GIVEN_ValidPayload_MUST_UpdateSuccessful() {

        // fixture
        var uuid = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
        var position = 1.23;

        var template = String.format("{" +
            "  position : %s" +
            "}", position);

        var payload = new JsonTemplate(template).prettyString();

        // verify
        given()
            .contentType(ContentType.JSON)
            .body(payload).log().body()
            .when().put("/v1/buckets/{uuid}/move", uuid)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        var repository = new WriteBucketRepositoryImpl(dataSource);
        var actualBucket = repository.findByUuid(UUID.fromString(uuid)).get();
        assertThat(position).isEqualTo(actualBucket.getPosition());
    }
}

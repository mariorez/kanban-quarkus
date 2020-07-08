package org.seariver.kanbanboard.write.adapter.in;

import com.github.jsontemplate.JsonTemplate;
import helper.IntegrationHelper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.seariver.kanbanboard.write.adapter.out.WriteBucketRepositoryImpl;

import javax.ws.rs.core.Response.Status;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class BucketUpdateIT extends IntegrationHelper {

    @Test
    void GIVEN_ValidPayload_MUST_UpdateSuccessful() {

        // fixture
        var uuid = "3731c747-ea27-42e5-a52b-1dfbfa9617db";
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
            .when().put("/v1/buckets/{uuid}", uuid)
            .then()
            .statusCode(Status.NO_CONTENT.getStatusCode());

        var repository = new WriteBucketRepositoryImpl(dataSource);
        var actualBucket = repository.findByUuid(UUID.fromString(uuid)).get();
        assertThat(name).isEqualTo(actualBucket.getName());
    }
}

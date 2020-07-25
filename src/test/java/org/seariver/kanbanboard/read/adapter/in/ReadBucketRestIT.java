package org.seariver.kanbanboard.read.adapter.in;

import helper.IntegrationHelper;
import helper.ReadProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;

@QuarkusTest
@TestProfile(ReadProfile.class)
public class ReadBucketRestIT extends IntegrationHelper {

    private static final String ENDPOINT_PATH = "/v1/buckets";

    @Test
    void WHEN_GetAllBuckets_MUST_ListByPositionOrder() {

        var result = given()
                .when()
                .get(ENDPOINT_PATH)
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(JSON)
                .assertThat()
                .log().body()
                .and().body("size()", greaterThanOrEqualTo(2),
                        "id", hasItems("6d9db741-ef57-4d5a-ac0f-34f68fb0ab5e", "3731c747-ea27-42e5-a52b-1dfbfa9617db"),
                        "position", hasItems(100.15f, 200.987f),
                        "name", hasItems("FIRST-BUCKET", "SECOND-BUCKET"),
                        "cards[0].id", contains("df5cf5b1-c2c7-4c02-b4d4-341d6772f193"),
                        "cards[1].id", contains("021944cd-f516-4432-ba8d-44a312267c7d"));
    }
}

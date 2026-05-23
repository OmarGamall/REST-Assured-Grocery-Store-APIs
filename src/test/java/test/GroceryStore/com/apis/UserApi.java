package test.GroceryStore.com.apis;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class UserApi {
    public static final String BASE_URI = "https://simple-grocery-store-api.click";
    public static final String REGISTER_CLIENT_ENDPOINT = "/api-clients";

    public static Response registerClient(Object clientData) {
        return io.restassured.RestAssured.given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .body(clientData)
                .log().all()
        .when()
                .post(REGISTER_CLIENT_ENDPOINT)
        .then()
                .log().all()
                .extract().response();
    }
}

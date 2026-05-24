package test.GroceryStore.com.apis;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class UserApi {

    public static Response registerClient(Object clientData) {
        return io.restassured.RestAssured.given()
                .baseUri(Routes.BASE_URI)
                .contentType(ContentType.JSON)
                .body(clientData)
                .log().all()
        .when()
                .post(Routes.REGISTER_CLIENT_ENDPOINT)
        .then()
                .log().all()
                .extract().response();
    }
}

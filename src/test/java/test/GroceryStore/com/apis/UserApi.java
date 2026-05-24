package test.GroceryStore.com.apis;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class UserApi extends BaseApi {

    public static Response registerClient(Object clientData) {
        return given()
                .spec(requestSpec)
                .body(clientData)
                .log().all()
        .when()
                .post(Routes.REGISTER_CLIENT_ENDPOINT)
        .then()
                .log().all()
                .extract().response();
    }
}

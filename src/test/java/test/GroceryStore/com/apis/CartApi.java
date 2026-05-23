package test.GroceryStore.com.apis;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

public class CartApi {

    public static final String BASE_URI = "https://simple-grocery-store-api.click";
    public static final String CARTS_ENDPOINT = "/carts";

    public static Response createCart() {
        return given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .post(CARTS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }
}

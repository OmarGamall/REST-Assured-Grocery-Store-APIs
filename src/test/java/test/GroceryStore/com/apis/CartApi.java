package test.GroceryStore.com.apis;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import test.GroceryStore.com.models.CartItem;
import static io.restassured.RestAssured.*;

public class CartApi {

    public static final String BASE_URI = "https://simple-grocery-store-api.click";
    public static final String CARTS_ENDPOINT = "/carts";
    public static final String CART_ITEMS_ENDPOINT = "/carts/{cartId}/items";

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

    public static Response addItemToCart(CartItem cartItem) {
        return given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .pathParams("cartId", cartItem.getCartId())
                .body(cartItem)
                .log().all()
                .when()
                .post(CART_ITEMS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }
}

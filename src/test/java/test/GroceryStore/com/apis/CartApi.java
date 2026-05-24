package test.GroceryStore.com.apis;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import test.GroceryStore.com.models.CartItem;

import java.util.HashMap;
import java.util.Map;

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

    public static Response getCartById(String cartId) {
        return given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .pathParam("cartId", cartId)
                .log().all()
                .when()
                .get(CARTS_ENDPOINT + "/{cartId}")
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

    public static Response getCartItems(String cartId) {
        return given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .pathParam("cartId", cartId)
                .log().all()
                .when()
                .get(CART_ITEMS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response modifyCartItem(String cartId, String itemId, int quantity) {
        Map<String, Object> body = new HashMap<>();
        body.put("quantity", quantity);

        return given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .body(body)
                .log().all()
                .when()
                .patch(CART_ITEMS_ENDPOINT + "/{itemId}")
                .then()
                .log().all()
                .extract().response();
    }

    // Overloaded method to allow passing itemId as an Integer
    public static Response modifyCartItem(String cartId, Integer itemId, int quantity) {
        return modifyCartItem(cartId, String.valueOf(itemId), quantity);
    }
}

package test.GroceryStore.com.apis;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import test.GroceryStore.com.models.CartItem;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class CartApi {

    public static Response createCart() {
        return given()
                .baseUri(Routes.BASE_URI)
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .post(Routes.CARTS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response getCartById(String cartId) {
        return given()
                .baseUri(Routes.BASE_URI)
                .contentType(ContentType.JSON)
                .pathParam("cartId", cartId)
                .log().all()
                .when()
                .get(Routes.CART_BY_ID_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response addItemToCart(CartItem cartItem) {
        return given()
                .baseUri(Routes.BASE_URI)
                .contentType(ContentType.JSON)
                .pathParams("cartId", cartItem.getCartId())
                .body(cartItem)
                .log().all()
                .when()
                .post(Routes.CART_ITEMS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response getCartItems(String cartId) {
        return given()
                .baseUri(Routes.BASE_URI)
                .contentType(ContentType.JSON)
                .pathParam("cartId", cartId)
                .log().all()
                .when()
                .get(Routes.CART_ITEMS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response modifyCartItem(String cartId, String itemId, Integer quantity) {
        CartItem body = new CartItem();
        body.setQuantity(quantity);

        return given()
                .baseUri(Routes.BASE_URI)
                .contentType(ContentType.JSON)
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .body(body)
                .log().all()
                .when()
                .patch(Routes.CART_ITEM_BY_ID_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    // Overloaded method to allow passing itemId as an Integer
    public static Response modifyCartItem(String cartId, Integer itemId, Integer quantity) {
        return modifyCartItem(cartId, String.valueOf(itemId), quantity);
    }
}

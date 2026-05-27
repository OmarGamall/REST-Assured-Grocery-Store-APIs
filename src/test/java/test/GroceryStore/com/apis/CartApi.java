package test.GroceryStore.com.apis;

import io.restassured.response.Response;
import test.GroceryStore.com.models.cart.CartItem;


import static io.restassured.RestAssured.*;

public class CartApi extends BaseApi {

    public static Response createCart() {
        return given()
                .when()
                .post(Routes.CARTS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response getCartById(String cartId) {
        return given()
                .pathParam("cartId", cartId)
                .when()
                .get(Routes.CART_BY_ID_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response addItemToCart(CartItem cartItem) {
        return given()
                .pathParams("cartId", cartItem.getCartId())
                .body(cartItem)
                .when()
                .post(Routes.CART_ITEMS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response getCartItems(String cartId) {
        return given()
                .pathParam("cartId", cartId)
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
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .body(body)
                .when()
                .patch(Routes.CART_ITEM_BY_ID_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response replaceCartItem(String cartId, String itemId, CartItem cartItem) {
        return given()
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .body(cartItem)
                .when()
                .put(Routes.CART_ITEM_BY_ID_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response replaceCartItem(String cartId, String itemId, Integer productId, Integer quantity) {
        CartItem body = new CartItem();
        body.setProductId(productId);
        body.setQuantity(quantity);
        return replaceCartItem(cartId, itemId, body);
    }

    public static Response replaceCartItem(String cartId, String itemId, Integer productId) {
        CartItem body = new CartItem();
        body.setProductId(productId);
        return replaceCartItem(cartId, itemId, body);
    }

    public static Response deleteCartItem(String cartId, String itemId) {
        return given()
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .when()
                .delete(Routes.CART_ITEM_BY_ID_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }
}

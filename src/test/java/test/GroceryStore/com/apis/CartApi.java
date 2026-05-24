package test.GroceryStore.com.apis;

import io.restassured.response.Response;
import test.GroceryStore.com.models.CartItem;


import static io.restassured.RestAssured.*;

public class CartApi extends BaseApi {

    public static Response createCart() {
        return given()
                .spec(requestSpec)
                .when()
                .post(Routes.CARTS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response getCartById(String cartId) {
        return given()
                .spec(requestSpec)
                .pathParam("cartId", cartId)
                .when()
                .get(Routes.CART_BY_ID_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response addItemToCart(CartItem cartItem) {
        return given()
                .spec(requestSpec)
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
                .spec(requestSpec)
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
                .spec(requestSpec)
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .body(body)
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

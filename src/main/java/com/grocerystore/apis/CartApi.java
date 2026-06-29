package com.grocerystore.apis;

import io.restassured.response.Response;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.utils.RestHelper;
import io.qameta.allure.Step;

public class CartApi extends BaseApi {

    @Step("API: Create a new cart")
    public static Response createCart() {
        return RestHelper.build()
                .endpoint(Routes.CARTS_ENDPOINT)
                .post();
    }

    @Step("API: Get cart by ID: {cartId}")
    public static Response getCartById(String cartId) {
        return RestHelper.build()
                .endpoint(Routes.CART_BY_ID_ENDPOINT)
                .pathParam("cartId", cartId)
                .get();
    }

    @Step("API: Add item to cart ID: {cartItem.cartId}")
    public static Response addItemToCart(CartItem cartItem) {
        return RestHelper.build()
                .endpoint(Routes.CART_ITEMS_ENDPOINT)
                .pathParam("cartId", cartItem.getCartId())
                .body(cartItem)
                .post();
    }

    @Step("API: Add product {productId} (qty: {quantity}) to cart ID: {cartId}")
    public static Response addItemToCart(String cartId, Integer productId, Integer quantity) {
        CartItem cartItem = CartItem.builder()
                .cartId(cartId)
                .productId(productId)
                .quantity(quantity)
                .build();
        return addItemToCart(cartItem);
    }

    @Step("API: Get items in cart ID: {cartId}")
    public static Response getCartItems(String cartId) {
        return RestHelper.build()
                .endpoint(Routes.CART_ITEMS_ENDPOINT)
                .pathParam("cartId", cartId)
                .get();
    }

    @Step("API: Modify quantity of item {itemId} to {quantity} in cart ID: {cartId}")
    public static Response modifyCartItem(String cartId, String itemId, Integer quantity) {
        CartItem body = new CartItem();
        body.setQuantity(quantity);

        return RestHelper.build()
                .endpoint(Routes.CART_ITEM_BY_ID_ENDPOINT)
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .body(body)
                .patch();
    }

    @Step("API: Replace item {itemId} in cart ID: {cartId} with cartItem details")
    public static Response replaceCartItem(String cartId, String itemId, CartItem cartItem) {
        return RestHelper.build()
                .endpoint(Routes.CART_ITEM_BY_ID_ENDPOINT)
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .body(cartItem)
                .put();
    }

    @Step("API: Replace item {itemId} in cart ID: {cartId} with product {productId} (qty: {quantity})")
    public static Response replaceCartItem(String cartId, String itemId, Integer productId, Integer quantity) {
        CartItem body = new CartItem();
        body.setProductId(productId);
        body.setQuantity(quantity);
        return replaceCartItem(cartId, itemId, body);
    }

    @Step("API: Replace item {itemId} in cart ID: {cartId} with product {productId}")
    public static Response replaceCartItem(String cartId, String itemId, Integer productId) {
        CartItem body = new CartItem();
        body.setProductId(productId);
        return replaceCartItem(cartId, itemId, body);
    }

    @Step("API: Delete item {itemId} from cart ID: {cartId}")
    public static Response deleteCartItem(String cartId, String itemId) {
        return RestHelper.build()
                .endpoint(Routes.CART_ITEM_BY_ID_ENDPOINT)
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .delete();
    }
}


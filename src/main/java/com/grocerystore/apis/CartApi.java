package com.grocerystore.apis;

import io.restassured.response.Response;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.utils.RestHelper;

public class CartApi extends BaseApi {

    public static Response createCart() {
        return RestHelper.build()
                .endpoint(Routes.CARTS_ENDPOINT)
                .post();
    }

    public static Response getCartById(String cartId) {
        return RestHelper.build()
                .endpoint(Routes.CART_BY_ID_ENDPOINT)
                .pathParam("cartId", cartId)
                .get();
    }

    public static Response addItemToCart(CartItem cartItem) {
        return RestHelper.build()
                .endpoint(Routes.CART_ITEMS_ENDPOINT)
                .pathParam("cartId", cartItem.getCartId())
                .body(cartItem)
                .post();
    }

    public static Response addItemToCart(String cartId, Integer productId, Integer quantity) {
        CartItem cartItem = CartItem.builder()
                .cartId(cartId)
                .productId(productId)
                .quantity(quantity)
                .build();
        return addItemToCart(cartItem);
    }

    public static Response getCartItems(String cartId) {
        return RestHelper.build()
                .endpoint(Routes.CART_ITEMS_ENDPOINT)
                .pathParam("cartId", cartId)
                .get();
    }

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

    public static Response replaceCartItem(String cartId, String itemId, CartItem cartItem) {
        return RestHelper.build()
                .endpoint(Routes.CART_ITEM_BY_ID_ENDPOINT)
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .body(cartItem)
                .put();
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
        return RestHelper.build()
                .endpoint(Routes.CART_ITEM_BY_ID_ENDPOINT)
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemId)
                .delete();
    }
}


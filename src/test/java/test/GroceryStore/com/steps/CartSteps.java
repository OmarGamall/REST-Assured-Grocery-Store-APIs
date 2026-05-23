package test.GroceryStore.com.steps;

import io.restassured.response.Response;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.CartItem;
import test.GroceryStore.com.models.CartItemResponse;
import test.GroceryStore.com.models.CartResponse;

import static org.testng.Assert.*;

public class CartSteps {

    /**
     * Creates a cart and returns its valid ID.
     */
    public static String createCartAndGetId() {
        Response response = CartApi.createCart();
        assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful cart creation");
        CartResponse cartResponse = response.as(CartResponse.class);
        assertNotNull(cartResponse.getCartId(), "Cart ID should not be null");
        return cartResponse.getCartId();
    }

    /**
     * Adds an item to the cart and returns the response metadata.
     */
    public static CartItemResponse addItemToCart(String cartId, String productId, int quantity) {
        CartItem cartItem = new CartItem(cartId, productId, quantity);
        Response response = CartApi.addItemToCart(cartItem);
        assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful item addition");
        
        CartItemResponse responseBody = response.as(CartItemResponse.class);
        assertTrue(responseBody.getCreated(), "Expected 'created' to be true in response");
        assertNotNull(responseBody.getItemId(), "Expected 'itemId' to be returned");
        return responseBody;
    }

    /**
     * Fetches the list of items inside a cart.
     */
    public static CartItem[] getCartItems(String cartId) {
        Response response = CartApi.getCartItems(cartId);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for retrieving cart items");
        return response.as(CartItem[].class);
    }
}

package test.GroceryStore.com;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.CartItem;
import test.GroceryStore.com.models.CartResponse;

import static org.testng.Assert.*;

public class CartTest {
    @Test
    public void testCreateCart() {
        Response response = CartApi.createCart();
        // Verify the response status code
        assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful cart creation");
        // Deserialize the response to a CartResponse object
        CartResponse cartResponse = response.as(CartResponse.class);
        // Verify the response body contains the expected fields
        assertNotNull(cartResponse.getCartId());
        // Verify the created field is true
        assertTrue(cartResponse.getCreated(), "Expected 'created' field to be true");
    }
    @Test
    public void testAddItemToCart() {
        // First create a cart to get a valid cartId
        Response createCartResponse = CartApi.createCart();
        assertEquals(createCartResponse.getStatusCode(), 201, "Expected status code 201 for successful cart creation");
        CartResponse cartResponse = createCartResponse.as(CartResponse.class);
        String cartId = cartResponse.getCartId();
        // Create a CartItem to add to the cart
        CartItem cartItem = new CartItem(cartId, "1709", 2);
        // Add the item to the cart
        Response addItemResponse = CartApi.addItemToCart(cartItem);
        // Verify the response status code
        assertEquals(addItemResponse.getStatusCode(), 201, "Expected status code 201 for successful item addition");
    }

}

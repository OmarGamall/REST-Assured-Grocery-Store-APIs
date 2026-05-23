package test.GroceryStore.com.testcases;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.CartItem;
import test.GroceryStore.com.models.CartItemResponse;
import test.GroceryStore.com.models.CartResponse;
import test.GroceryStore.com.models.Product;
import test.GroceryStore.com.services.ProductService;

import java.util.concurrent.ThreadLocalRandom;

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
        // 1. Fetch a random available product using ProductService
        Product detailedProduct = ProductService.getRandomAvailableProduct();

        // 2. Generate a random quantity based on available stock
        int currentStock = (detailedProduct.getCurrentStock() != null) ? detailedProduct.getCurrentStock() : 1;
        int randomQuantity = (currentStock > 0)
                ? ThreadLocalRandom.current().nextInt(1, Math.min(currentStock, 10) + 1)
                : 1;

        // 3. Create a cart to get a valid cartId
        Response createCartResponse = CartApi.createCart();
        assertEquals(createCartResponse.getStatusCode(), 201, "Expected status code 201 for successful cart creation");
        CartResponse cartResponse = createCartResponse.as(CartResponse.class);
        String cartId = cartResponse.getCartId();

        // 4. Create a CartItem to add to the cart with dynamic values
        CartItem cartItem = new CartItem(cartId, String.valueOf(detailedProduct.getId()), randomQuantity);

        // 5. Add the item to the cart
        Response addItemResponse = CartApi.addItemToCart(cartItem);

        // 6. Verify the response status code
        assertEquals(addItemResponse.getStatusCode(), 201, "Expected status code 201 for successful item addition");

        // 7. Deserialize response and assert body properties
        CartItemResponse cartItemResponse = addItemResponse.as(CartItemResponse.class);
        assertTrue(cartItemResponse.getCreated(), "Expected 'created' property to be true in response");
        assertNotNull(cartItemResponse.getItemId(), "Expected non-null 'itemId' in response");

        // 8. Retrieve the cart items and verify the added item details
        Response getItemsResponse = CartApi.getCartItems(cartId);
        assertEquals(getItemsResponse.getStatusCode(), 200, "Expected status code 200 for retrieving cart items");

        CartItem[] cartItems = getItemsResponse.as(CartItem[].class);
        assertTrue(cartItems.length > 0, "Expected the cart to contain items");

        // Assert the item details match what we added
        assertEquals(cartItems[0].getProductId(), String.valueOf(detailedProduct.getId()), "Product ID mismatch in cart");
        assertEquals(cartItems[0].getQuantity(), Integer.valueOf(randomQuantity), "Quantity mismatch in cart");
        assertEquals(cartItems[0].getId(), cartItemResponse.getItemId(), "Item ID (itemId) mismatch in cart");
    }

}

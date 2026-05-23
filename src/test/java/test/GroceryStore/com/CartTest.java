package test.GroceryStore.com;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.CartItem;
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
    }

}

package test.GroceryStore.com;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
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

}

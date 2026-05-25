package test.GroceryStore.com.testcases.cart.create;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.cart.CartResponse;
import test.GroceryStore.com.models.cart.CartItem;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.testcases.BaseTest;

import static org.testng.Assert.*;

public class CreateCartTest extends BaseTest {

    @Test
    public void testCreateCart() {
        // 1. Act (Create the Cart)
        Response createResponse = CartApi.createCart();

        // 2. Assert (Verify creation response)
        assertEquals(createResponse.getStatusCode(), 201, "Expected status code 201 for successful cart creation");
        CartResponse cartResponse = createResponse.as(CartResponse.class);
        assertNotNull(cartResponse.getCartId(), "Expected non-null cartId");
        assertTrue(cartResponse.getCreated(), "Expected 'created' field to be true");

        // 3. Act (Retrieve the created Cart by ID)
        Response getResponse = CartApi.getCartById(cartResponse.getCartId());

        // 4. Assert (Verify retrieved cart payload details)
        assertEquals(getResponse.getStatusCode(), 200, "Expected status code 200 for retrieving cart by ID");
        assertNotNull(getResponse.jsonPath().getString("created"), "Expected 'created' timestamp to be present");
        assertTrue(getResponse.jsonPath().getList("items").isEmpty(), "Expected new cart to have an empty items list");
    }

    @Test
    public void testGetCartItemsForEmptyCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();

        // 2. Act
        CartItem[] cartItems = CartSteps.getCartItems(cartId);

        // 3. Assert
        assertNotNull(cartItems, "Expected non-null array of cart items");
        assertEquals(cartItems.length, 0, "Expected no items in the cart");
    }
}

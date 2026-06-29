package com.grocerystore.testcases.cart.create;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Allure;
import com.grocerystore.apis.CartApi;
import com.grocerystore.models.cart.CartResponse;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;

import static org.testng.Assert.*;

@Test(groups = {"cart", "happy-path"})
public class CreateCartTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"smoke"}, description = "TC_CART_001: Verify that POST /carts returns 201 Created and cartId when creating a new cart, and GET /carts/{cartId} returns 200 OK and empty items list")
    public void testCreateCart() {
        // 1. Act (Create the Cart)
        Response createResponse = CartApi.createCart();

        // 2. Assert (Verify creation response)
        CartResponse cartResponse = Allure.step("Assert: Verify cart creation response is 201 and contains cart ID", () -> {
            assertEquals(createResponse.getStatusCode(), 201, "Expected status code 201 for successful cart creation");
            assertResponseSchema(createResponse, "schemas/cart-created-schema.json");
            CartResponse res = createResponse.as(CartResponse.class);
            assertNotNull(res.getCartId(), "Expected non-null cartId");
            assertTrue(res.getCreated(), "Expected 'created' field to be true");
            return res;
        });

        // 3. Act (Retrieve the created Cart by ID)
        Response getResponse = CartApi.getCartById(cartResponse.getCartId());

        // 4. Assert (Verify retrieved cart payload details)
        Allure.step("Assert: Verify retrieved cart details match empty cart state", () -> {
            assertEquals(getResponse.getStatusCode(), 200, "Expected status code 200 for retrieving cart by ID");
            assertResponseSchema(getResponse, "schemas/cart-schema.json");
            assertNotNull(getResponse.jsonPath().getString("created"), "Expected 'created' timestamp to be present");
            assertTrue(getResponse.jsonPath().getList("items").isEmpty(), "Expected new cart to have an empty items list");
        });
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_002: Verify that GET /carts/{cartId}/items returns 200 OK and an empty items list when a new empty cart is requested")
    public void testGetCartItemsForEmptyCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();

        // 2. Act
        CartItem[] cartItems = CartSteps.getCartItems(cartId);

        // 3. Assert
        Allure.step("Assert: Verify that the items list in the empty cart is non-null and empty", () -> {
            assertNotNull(cartItems, "Expected non-null array of cart items");
            assertEquals(cartItems.length, 0, "Expected no items in the cart");
        });
    }
}

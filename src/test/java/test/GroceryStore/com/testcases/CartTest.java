package test.GroceryStore.com.testcases;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.CartItem;
import test.GroceryStore.com.models.CartItemResponse;
import test.GroceryStore.com.models.CartResponse;
import test.GroceryStore.com.models.Product;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;

import static org.testng.Assert.*;

public class CartTest {

    @Test
    public void testCreateCart() {
        Response response = CartApi.createCart();
        assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful cart creation");
        CartResponse cartResponse = response.as(CartResponse.class);
        assertNotNull(cartResponse.getCartId(), "Expected non-null cartId");
        assertTrue(cartResponse.getCreated(), "Expected 'created' field to be true");
    }
    
    @Test
    public void testAddItemToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = ProductService.getRandomQuantity(product);

        // 2. Act
        CartItemResponse addResponse = CartSteps.addItemToCart(cartId, String.valueOf(product.getId()), quantity);

        // 3. Assert
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), String.valueOf(product.getId()), "Product ID mismatch in cart");
        assertEquals(cartItems[0].getQuantity(), Integer.valueOf(quantity), "Quantity mismatch in cart");
        assertEquals(cartItems[0].getId(), addResponse.getItemId(), "Item ID mismatch in cart");
    }
}

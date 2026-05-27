package test.GroceryStore.com.testcases.cart.add_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.cart.CartItem;
import test.GroceryStore.com.models.cart.CartItemResponse;
import test.GroceryStore.com.models.product.Product;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.testcases.BaseTest;

import static org.testng.Assert.*;
import static test.GroceryStore.com.services.ProductService.isProductAlreadySelected;

public class AddItemHappyPathTest extends BaseTest {

    @Test
    public void testAddItemToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = ProductService.getRandomQuantity(product);

        // 2. Act
        CartItem cartItem = CartItem.builder()
                .cartId(cartId)
                .productId(product.getId())
                .quantity(quantity)
                .build();
        Response response = CartApi.addItemToCart(cartItem);

        // 3. Assert
        assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful item addition");
        CartItemResponse addResponse = response.as(CartItemResponse.class);
        assertTrue(addResponse.getCreated(), "Expected 'created' to be true in response");
        assertNotNull(addResponse.getItemId(), "Expected 'itemId' to be returned");

        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), product.getId(), "Product ID mismatch in cart");
        assertEquals(cartItems[0].getQuantity(), Integer.valueOf(quantity), "Quantity mismatch in cart");
        assertEquals(cartItems[0].getItemId(), addResponse.getItemId(), "Item ID mismatch in cart");
    }

    @Test
    public void testAddItemToCartWithQuantitySameAsStock() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = product.getCurrentStock(); // Use the current stock as the quantity

        // 2. Act
        CartItem cartItem = CartItem.builder()
                .cartId(cartId)
                .productId(product.getId())
                .quantity(quantity)
                .build();
        Response response = CartApi.addItemToCart(cartItem);

        // 3. Assert
        assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful item addition");
        CartItemResponse addResponse = response.as(CartItemResponse.class);
        assertTrue(addResponse.getCreated(), "Expected 'created' to be true in response");
        assertNotNull(addResponse.getItemId(), "Expected 'itemId' to be returned");

        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), product.getId(), "Product ID mismatch in cart");
        assertEquals(cartItems[0].getQuantity(), Integer.valueOf(quantity), "Quantity mismatch in cart");
        assertEquals(cartItems[0].getItemId(), addResponse.getItemId(), "Item ID mismatch in cart");
    }

    @Test
    public void testAddMultipleItemsToCartNoDuplicates() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        int numberOfItemsToAdd = 10;
        Product[] products = new Product[numberOfItemsToAdd];
        Product productToAdd = null;
        int[] quantities = new int[numberOfItemsToAdd];
        for (int i = 0; i < numberOfItemsToAdd; i++) {
            do {
                productToAdd = ProductService.getRandomAvailableProduct();
            } while (isProductAlreadySelected(products, productToAdd));
            products[i] = productToAdd;
            quantities[i] = ProductService.getRandomQuantity(products[i]);
        }

        // 2. Act
        for (int i = 0; i < numberOfItemsToAdd; i++) {
            CartItem cartItem = CartItem.builder()
                    .cartId(cartId)
                    .productId(products[i].getId())
                    .quantity(quantities[i])
                    .build();
            Response response = CartApi.addItemToCart(cartItem);
            assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful item addition at index " + (i + 1));
        }

        // 3. Assert
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, numberOfItemsToAdd, "Expected exactly " + numberOfItemsToAdd + " items in the cart");

        for (int i = 0; i < numberOfItemsToAdd; i++) {
            assertEquals(cartItems[i].getProductId(), products[i].getId(), "Product ID mismatch for item " + i);
            assertEquals(cartItems[i].getQuantity(), Integer.valueOf(quantities[i]), "Quantity mismatch for item " + i);
        }
    }
}

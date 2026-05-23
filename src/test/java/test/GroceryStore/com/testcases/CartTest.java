package test.GroceryStore.com.testcases;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.*;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;

import static org.testng.Assert.*;
import static test.GroceryStore.com.services.ProductService.isProductAlreadySelected;

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

    @Test
    public void testAddMultipleItemsToCartNoDuplicates() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        int numberOfItemsToAdd = 10;
        // Use arrays for better scalability and maintainability
        Product[] products = new Product[numberOfItemsToAdd];
        Product productToAdd = null;
        int[] quantities = new int[numberOfItemsToAdd];
        for (int i = 0; i < numberOfItemsToAdd; i++) {
            // Ensure we don't add the same product multiple times to the cart
            do {
                productToAdd = ProductService.getRandomAvailableProduct();
            } while (isProductAlreadySelected(products, productToAdd));
            products[i] = productToAdd;
            quantities[i] = ProductService.getRandomQuantity(products[i]);
        }

        // 2. Act
        for (int i = 0; i < numberOfItemsToAdd; i++) {
            CartSteps.addItemToCart(cartId, String.valueOf(products[i].getId()), quantities[i]);
        }

        // 3. Assert
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, numberOfItemsToAdd, "Expected exactly " + numberOfItemsToAdd + " items in the cart");

        for (int i = 0; i < numberOfItemsToAdd; i++) {
            assertEquals(cartItems[i].getProductId(), String.valueOf(products[i].getId()), "Product ID mismatch for item " + i);
            assertEquals(cartItems[i].getQuantity(), Integer.valueOf(quantities[i]), "Quantity mismatch for item " + i);
        }
    }

    @Test
    public void testAddDuplicateItemToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = ProductService.getRandomQuantity(product);

        // 2. Act
        CartSteps.addItemToCart(cartId, String.valueOf(product.getId()), quantity);
        Response duplicateAddResponse = CartApi.addItemToCart(new CartItem(cartId, String.valueOf(product.getId()), quantity));


        // 3. Assert
        assertEquals(duplicateAddResponse.getStatusCode(), 400, "Expected status code 400 for adding duplicate item to cart");
        ErrorResponse errorResponse = duplicateAddResponse.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("This product has already been added to cart"), "Expected error message to indicate duplicate item");
    }

    @Test
    public void testAddNonAvailableProductToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product nonAvailableProduct = ProductService.getRandomNonAvailableProduct();
        int quantity = 1; // Quantity doesn't matter since the product is not available

        // 2. Act
        Response response = CartApi.addItemToCart(new CartItem(cartId, String.valueOf(nonAvailableProduct.getId()), quantity));

        // 3. Assert
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for adding non-available product to cart");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("This product is not in stock and cannot be ordered"), "Expected error message to indicate product is not available");
    }

    @Test
    public void testAddQuantityExceedingStockToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantityExceedingStock =  product.getCurrentStock() + 1;

        // 2. Act
        Response response = CartApi.addItemToCart(new CartItem(cartId, String.valueOf(product.getId()), quantityExceedingStock));

        // 3. Assert
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for adding quantity exceeding stock to cart");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("The quantity requested exceeds the current stock"), "Expected error message to indicate quantity exceeds stock");
    }
}

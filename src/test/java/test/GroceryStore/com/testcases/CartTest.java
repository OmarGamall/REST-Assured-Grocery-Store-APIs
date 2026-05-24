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
    public void testAddItemToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = ProductService.getRandomQuantity(product);

        // 2. Act
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, String.valueOf(product.getId()), quantity);

        // 3. Assert
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), String.valueOf(product.getId()), "Product ID mismatch in cart");
        assertEquals(cartItems[0].getQuantity(), Integer.valueOf(quantity), "Quantity mismatch in cart");
        assertEquals(cartItems[0].getId(), addResponse.getItemId(), "Item ID mismatch in cart");
    }

    @Test
    public void testAddItemToCartWithQuantitySameAsStock() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = product.getCurrentStock(); // Use the current stock as the quantity

        // 2. Act
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, String.valueOf(product.getId()), quantity);

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
            CartSteps.addItemToCartAndGetResponse(cartId, String.valueOf(products[i].getId()), quantities[i]);
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
        CartSteps.addItemToCartAndGetResponse(cartId, String.valueOf(product.getId()), quantity);
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

    @Test
    public void testAddItemWithZeroQuantityToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int zeroQuantity = 0;

        // 2. Act
        Response response = CartApi.addItemToCart(new CartItem(cartId, String.valueOf(product.getId()), zeroQuantity));

        // 3. Assert
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for adding item with zero quantity to cart");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("Quantity must be at least 1"), "Expected error message to indicate quantity must be at least 1");
    }

    @Test
    public void testAddItemWithNegativeQuantityToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int negativeQuantity = -5;

        // 2. Act
        Response response = CartApi.addItemToCart(new CartItem(cartId, String.valueOf(product.getId()), negativeQuantity));

        // 3. Assert
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for adding item with negative quantity to cart");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("Quantity must be at least 1"), "Expected error message to indicate quantity must be at least 1");
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

    @Test
    public void testAddItemWithInvalidProductIdToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        String invalidProductId = "999999"; // Assuming this product ID does not exist
        int quantity = 1;

        // 2. Act
        Response response = CartApi.addItemToCart(new CartItem(cartId, invalidProductId, quantity));

        // 3. Assert
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for adding item with invalid product ID to cart");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("Invalid or missing productId"), "Expected error message to indicate product not found");
    }

    @Test
    public void testAddItemWithInvalidCartId() {
        // 1. Arrange
        String invalidCartId = "invalid-cart-id";
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = 1;

        // 2. Act
        Response response = CartApi.addItemToCart(new CartItem(invalidCartId, String.valueOf(product.getId()), quantity));

        // 3. Assert
        assertEquals(response.getStatusCode(), 404, "Expected status code 400 for adding item with invalid cart ID");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("No cart with id"), "Expected error message to indicate invalid cart ID");
    }

    @Test
    public void testModifyCartItemQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = null;
        // Ensure we select a product with at least 2 in stock so we can modify the quantity to 2
        do {
            product = ProductService.getRandomAvailableProduct();
        } while (product.getCurrentStock() != null && product.getCurrentStock() < 2);
        
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, String.valueOf(product.getId()), 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, addResponse.getItemId(), 2);

        // 3. Assert
        assertEquals(response.getStatusCode(), 204, "Expected status code 204 for successful item modification");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), String.valueOf(product.getId()), "Product ID mismatch in cart");
        assertEquals(cartItems[0].getQuantity(), Integer.valueOf(2), "Expected updated quantity to be 2");
    }

    @Test
    public void testModifyCartItemQuantityExceedingStock() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = null;
        // Ensure we select a product with at least 2 in stock so we can modify the quantity to 2
        do {
            product = ProductService.getRandomAvailableProduct();
        } while (product.getCurrentStock() != null && product.getCurrentStock() < 2);

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, String.valueOf(product.getId()), 1);
        int quantityExceedingStock = product.getCurrentStock() + 1;

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, addResponse.getItemId(), quantityExceedingStock);

        // 3. Assert
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for modifying item quantity exceeding stock");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("The quantity requested is not available in stock"), "Expected error message to indicate quantity exceeds stock");
    }

    @Test
    public void testModifyCartItemQuantityToZero() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, String.valueOf(product.getId()), 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, addResponse.getItemId(), 0);

        // 3. Assert
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for modifying item quantity to zero");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("Invalid or missing quantity"), "Expected error message to indicate quantity must be at least 1");
    }

    @Test
    public void testModifyCartItemQuantityToNegative() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, String.valueOf(product.getId()), 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, addResponse.getItemId(), -5);

        // 3. Assert
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for modifying item quantity to negative");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("Invalid or missing quantity"), "Expected error message to indicate quantity must be at least 1");
    }

}

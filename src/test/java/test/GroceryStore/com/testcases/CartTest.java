package test.GroceryStore.com.testcases;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.*;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;

import java.util.Objects;

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
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), quantity);

        // 3. Assert
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), product.getId(), "Product ID mismatch in cart");
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
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), quantity);

        // 3. Assert
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), product.getId(), "Product ID mismatch in cart");
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
            CartSteps.addItemToCartAndGetResponse(cartId, products[i].getId(), quantities[i]);
        }

        // 3. Assert
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, numberOfItemsToAdd, "Expected exactly " + numberOfItemsToAdd + " items in the cart");

        for (int i = 0; i < numberOfItemsToAdd; i++) {
            assertEquals(cartItems[i].getProductId(), products[i].getId(), "Product ID mismatch for item " + i);
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
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), quantity);
        Response duplicateAddResponse = CartApi.addItemToCart(new CartItem(cartId, product.getId(), quantity));


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
        Response response = CartApi.addItemToCart(new CartItem(cartId, nonAvailableProduct.getId(), quantity));

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
        int quantityExceedingStock = product.getCurrentStock() + 1;

        // 2. Act
        Response response = CartApi.addItemToCart(new CartItem(cartId, product.getId(), quantityExceedingStock));

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
        Response response = CartApi.addItemToCart(new CartItem(cartId, product.getId(), zeroQuantity));

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
        Response response = CartApi.addItemToCart(new CartItem(cartId, product.getId(), negativeQuantity));

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
        Integer invalidProductId = 999999; // Assuming this product ID does not exist
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
        Response response = CartApi.addItemToCart(new CartItem(invalidCartId, product.getId(), quantity));

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

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, String.valueOf(addResponse.getItemId()), 2);

        // 3. Assert
        assertEquals(response.getStatusCode(), 204, "Expected status code 204 for successful item modification");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), product.getId(), "Product ID mismatch in cart");
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

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);
        int quantityExceedingStock = product.getCurrentStock() + 1;

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, String.valueOf(addResponse.getItemId()), quantityExceedingStock);

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
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, String.valueOf(addResponse.getItemId()), 0);

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
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, String.valueOf(addResponse.getItemId()), -5);

        // 3. Assert
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for modifying item quantity to negative");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("Invalid or missing quantity"), "Expected error message to indicate quantity must be at least 1");
    }

    @Test
    public void testModifyCartItemToSameQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, String.valueOf(addResponse.getItemId()), 1); // Modify to the same quantity

        // 3. Assert
        assertEquals(response.getStatusCode(), 204, "Expected status code 204 for modifying item to the same quantity");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), product.getId(), "Product ID mismatch in cart");
        assertEquals(cartItems[0].getQuantity(), Integer.valueOf(1), "Expected quantity to remain unchanged at 1");
    }

    @Test
    public void testModifyCartItemWithInvalidItemId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);
        String invalidItemId = "invalid-item-id";

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, invalidItemId, 2);

        // 3. Assert
        assertEquals(response.getStatusCode(), 404, "Expected status code 404 for modifying item with invalid item ID");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("No item with id"), "Expected error message to indicate invalid item ID");
    }

    @Test
    public void testModifyCartItemWithInvalidCartId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);
        String invalidCartId = "invalid-cart-id";

        // 2. Act
        Response response = CartApi.modifyCartItem(invalidCartId, String.valueOf(addResponse.getItemId()), 2);

        // 3. Assert
        assertEquals(response.getStatusCode(), 404, "Expected status code 404 for modifying item with invalid cart ID");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("No cart with id"), "Expected error message to indicate invalid cart ID");
    }

    @Test
    public void testModifyCartItemWithMissingQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, String.valueOf(addResponse.getItemId()), null); // Pass null for quantity

        // 3. Assert
        assertEquals(response.getStatusCode(), 404, "Expected status code 404 for modifying item with missing quantity");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("Invalid or missing quantity"), "Expected error message to indicate quantity is required");
    }

    @Test
    public void testModifyCartItemWithMismatchedCartAndItemId() {
        // 1. Arrange
        String cartAId = CartSteps.createCartAndGetId();
        String cartBId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartAId, product.getId(), 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartBId, String.valueOf(addResponse.getItemId()), 2);

        // 3. Assert
        assertEquals(response.getStatusCode(), 404, "Expected status code 404 for mismatched cart and item ID");
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("No item with id"), "Expected error message to indicate item not found in cart");
    }

    @Test
    public void testReplaceCartItemProductAndQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        Product replacementProduct = null;
        do {
            replacementProduct = ProductService.getRandomAvailableProduct();
        } while (Objects.equals(replacementProduct.getId(), initialProduct.getId()));

        int initialQuantity = 1;
        int replacementQuantity = 3;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), initialQuantity);
        String itemId = addResponse.getItemId();

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, String.valueOf(itemId), replacementProduct.getId(), replacementQuantity);

        // 3. Assert
        assertEquals(replaceResponse.getStatusCode(), 204, "Expected status code 204 for successful item replacement");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), replacementProduct.getId(), "Product ID mismatch after replacement");
        assertEquals(cartItems[0].getQuantity(), Integer.valueOf(replacementQuantity), "Quantity mismatch after replacement");
        assertEquals(cartItems[0].getId(), itemId, "Item ID mismatch after replacement");
    }

    @Test
    public void testReplaceCartItemWithSameProductIdButDifferentQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int initialQuantity = 1;
        int replacementQuantity = 3;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), initialQuantity);
        String itemId = addResponse.getItemId();

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, String.valueOf(itemId), product.getId(), replacementQuantity);

        // 3. Assert
        assertEquals(replaceResponse.getStatusCode(), 204, "Expected status code 204 for successful item replacement");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), product.getId(), "Product ID mismatch after replacement");
        assertEquals(cartItems[0].getQuantity(), Integer.valueOf(replacementQuantity), "Quantity mismatch after replacement");
        assertEquals(cartItems[0].getId(), itemId, "Item ID mismatch after replacement");
    }

    @Test
    public void testReplaceCartItemProductAndKeepSameQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        Product replacementProduct = null;
        do {
            replacementProduct = ProductService.getRandomAvailableProduct();
        } while (Objects.equals(replacementProduct.getId(), initialProduct.getId()));

        int quantity = 2;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), quantity);
        String itemId = addResponse.getItemId();

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, String.valueOf(itemId), replacementProduct.getId(), quantity);

        // 3. Assert
        assertEquals(replaceResponse.getStatusCode(), 204, "Expected status code 204 for successful item replacement");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), replacementProduct.getId(), "Product ID mismatch after replacement");
        assertEquals(cartItems[0].getQuantity(), Integer.valueOf(quantity), "Quantity mismatch after replacement");
        assertEquals(cartItems[0].getId(), itemId, "Item ID mismatch after replacement");
    }

    @Test
    public void testReplaceCartItemProductAndMissingQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        Product replacementProduct = null;
        do {
            replacementProduct = ProductService.getRandomAvailableProduct();
        } while (Objects.equals(replacementProduct.getId(), initialProduct.getId()));

        int quantity = 2;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), quantity);
        String itemId = addResponse.getItemId();

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, String.valueOf(itemId), replacementProduct.getId()); // Pass null for quantity
        // 3. Assert
        assertEquals(replaceResponse.getStatusCode(), 204, "Expected status code 204 for successful item replacement");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), replacementProduct.getId(), "Product ID mismatch after replacement");
        assertEquals(cartItems[0].getQuantity(), Integer.valueOf(quantity), "Quantity mismatch after replacement");
        assertEquals(cartItems[0].getId(), itemId, "Item ID mismatch after replacement");
    }

    @Test
    public void testReplaceCartItemWithInvalidProductId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        int quantity = 2;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), quantity);
        String itemId = addResponse.getItemId();
        Integer invalidProductId = 999999; // Assuming this product ID does not exist

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, String.valueOf(itemId), invalidProductId, quantity);

        // 3. Assert
        assertEquals(replaceResponse.getStatusCode(), 400, "Expected status code 400 for replacing item with invalid product ID");
        ErrorResponse errorResponse = replaceResponse.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("Invalid or missing productId"), "Expected error message to indicate product not found");
    }

     @Test
    public void testReplaceCartItemWithInvalidCartId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        int quantity = 2;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), quantity);
        String itemId = addResponse.getItemId();
        String invalidCartId = "invalid-cart-id";

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(invalidCartId, String.valueOf(itemId), initialProduct.getId(), quantity);

        // 3. Assert
        assertEquals(replaceResponse.getStatusCode(), 404, "Expected status code 404 for replacing item with invalid cart ID");
        ErrorResponse errorResponse = replaceResponse.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("No cart with id"), "Expected error message to indicate invalid cart ID");
     }

     @Test
    public void testReplaceCartItemWithInvalidItemId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        int quantity = 2;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), quantity);
        String invalidItemId = "invalid-item-id";

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, invalidItemId, initialProduct.getId(), quantity);

        // 3. Assert
        assertEquals(replaceResponse.getStatusCode(), 404, "Expected status code 404 for replacing item with invalid item ID");
        ErrorResponse errorResponse = replaceResponse.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("No item with id"), "Expected error message to indicate invalid item ID");
     }

     @Test
    public void testReplaceCartItemWithNonAvailableProduct() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        Product nonAvailableProduct = ProductService.getRandomNonAvailableProduct();
        int quantity = 2;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), quantity);
        String itemId = addResponse.getItemId();

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, String.valueOf(itemId), nonAvailableProduct.getId(), quantity);

        // 3. Assert
        assertEquals(replaceResponse.getStatusCode(), 400, "Expected status code 400 for replacing item with non-available product");
        ErrorResponse errorResponse = replaceResponse.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("The quantity requested is not available in stock"), "Expected error message to indicate product is not available");
     }

     @Test
    public void testReplaceCartItemWithQuantityExceedingStock() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        int initialQuantity = 1;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), initialQuantity);
        String itemId = addResponse.getItemId();
        int quantityExceedingStock = initialProduct.getCurrentStock() + 1;

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, String.valueOf(itemId), initialProduct.getId(), quantityExceedingStock);

        // 3. Assert
        assertEquals(replaceResponse.getStatusCode(), 400, "Expected status code 400 for replacing item with quantity exceeding stock");
        ErrorResponse errorResponse = replaceResponse.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("The quantity requested is not available in stock"), "Expected error message to indicate quantity exceeds stock");
     }

     @Test
    public void testReplaceCartItemWithNegativeQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        int initialQuantity = 1;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), initialQuantity);
        String itemId = addResponse.getItemId();
        int negativeQuantity = -5;

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, String.valueOf(itemId), initialProduct.getId(), negativeQuantity);

        // 3. Assert
        assertEquals(replaceResponse.getStatusCode(), 400, "Expected status code 400 for replacing item with negative quantity");
        ErrorResponse errorResponse = replaceResponse.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("Invalid or missing quantity"), "Expected error message to indicate quantity must be at least 1");
     }

     @Test
    public void testReplaceCartItemWithZeroQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        int initialQuantity = 1;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), initialQuantity);
        String itemId = addResponse.getItemId();
        int zeroQuantity = 0;

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, String.valueOf(itemId), initialProduct.getId(), zeroQuantity);

        // 3. Assert
        assertEquals(replaceResponse.getStatusCode(), 400, "Expected status code 400 for replacing item with zero quantity");
        ErrorResponse errorResponse = replaceResponse.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("Invalid or missing quantity"), "Expected error message to indicate quantity must be at least 1");
     }

     // replace an item in Cart B using Cart A's item ID - should return 404 since the item ID from Cart A does not exist in Cart B
     @Test
     public void testReplaceCartItemWithMismatchedCartAndItemId() {
         // 1. Arrange
         String cartAId = CartSteps.createCartAndGetId();
         String cartBId = CartSteps.createCartAndGetId();
         Product productA = ProductService.getRandomAvailableProduct();
         Product productB = null;
         do {
             productB = ProductService.getRandomAvailableProduct();
         } while (Objects.equals(productB.getId(), productA.getId()));

         CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartAId, productA.getId(), 1);
         String itemId = addResponse.getItemId();

         // 2. Act
         Response response = CartApi.replaceCartItem(cartBId, String.valueOf(itemId), productB.getId(), 2);

         // 3. Assert
         assertEquals(response.getStatusCode(), 404, "Expected status code 404 for mismatched cart and item ID during replacement");
         ErrorResponse errorResponse = response.as(ErrorResponse.class);
         assertTrue(errorResponse.getError().contains("No item with id"), "Expected error message to indicate item not found in cart");
     }

     @Test
    public void testDeleteCartItem() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);
        String itemId = addResponse.getItemId();

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(cartId, String.valueOf(itemId));

        // 3. Assert
        assertEquals(deleteResponse.getStatusCode(), 204, "Expected status code 204 for successful item deletion");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 0, "Expected no items in the cart after deletion");
     }

     @Test
    public void testDeleteSameCartItemTwice() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);
        String itemId = addResponse.getItemId();

        // 2. Act
        Response firstDeleteResponse = CartApi.deleteCartItem(cartId, String.valueOf(itemId));
        Response secondDeleteResponse = CartApi.deleteCartItem(cartId, String.valueOf(itemId));

        // 3. Assert
        assertEquals(firstDeleteResponse.getStatusCode(), 204, "Expected status code 204 for successful item deletion");
        assertEquals(secondDeleteResponse.getStatusCode(), 404, "Expected status code 404 for deleting the same item twice");
        ErrorResponse errorResponse = secondDeleteResponse.as(ErrorResponse.class);
        assertTrue(errorResponse.getError().contains("No item with id"), "Expected error message to indicate item not found in cart");
     }

     @Test
    public void testDeleteMoreThanOneItemFromCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product1 = ProductService.getRandomAvailableProduct();
        Product product2 = null;
        do {
            product2 = ProductService.getRandomAvailableProduct();
        } while (Objects.equals(product2.getId(), product1.getId()));

        CartItemResponse addResponse1 = CartSteps.addItemToCartAndGetResponse(cartId, product1.getId(), 1);
        CartItemResponse addResponse2 = CartSteps.addItemToCartAndGetResponse(cartId, product2.getId(), 1);
        String itemId1 = addResponse1.getItemId();
        String itemId2 = addResponse2.getItemId();

        // 2. Act
        Response firstDeleteResponse = CartApi.deleteCartItem(cartId, String.valueOf(itemId1));
        Response secondDeleteResponse = CartApi.deleteCartItem(cartId, String.valueOf(itemId2));

        // 3. Assert
        assertEquals(firstDeleteResponse.getStatusCode(), 204, "Expected status code 204 for successful deletion of first item");
        assertEquals(secondDeleteResponse.getStatusCode(), 204, "Expected status code 204 for successful deletion of second item");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 0, "Expected no items in the cart after deleting both items");
     }

}

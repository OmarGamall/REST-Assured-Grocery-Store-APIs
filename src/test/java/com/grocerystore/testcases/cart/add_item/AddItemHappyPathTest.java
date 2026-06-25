package com.grocerystore.testcases.cart.add_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.grocerystore.apis.CartApi;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.cart.CartItemResponse;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;

import static org.testng.Assert.*;
import static com.grocerystore.services.ProductService.isProductAlreadySelected;

@Test(groups = {"cart", "happy-path"})
public class AddItemHappyPathTest extends BaseTest {

    @Test(groups = {"smoke"}, description = "TC_CART_003: Verify adding an item to cart successfully")
    public void testAddItemToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = ProductService.getRandomQuantity(product);

        // 2. Act
        Response response = CartApi.addItemToCart(cartId, product.getId(), quantity);

        // 3. Assert
        assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful item addition");
        CartItemResponse addResponse = response.as(CartItemResponse.class);
        
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(addResponse.getCreated(), "Expected 'created' to be true in response");
        softAssert.assertNotNull(addResponse.getItemId(), "Expected 'itemId' to be returned");

        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        
        if (cartItems.length == 1) {
            softAssert.assertEquals(cartItems[0].getProductId(), product.getId(), "Product ID mismatch in cart");
            softAssert.assertEquals(cartItems[0].getQuantity(), Integer.valueOf(quantity), "Quantity mismatch in cart");
            softAssert.assertEquals(cartItems[0].getItemId(), addResponse.getItemId(), "Item ID mismatch in cart");
        }
        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, description = "TC_CART_004: Verify adding item with quantity equal to stock")
    public void testAddItemToCartWithQuantitySameAsStock() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = product.getCurrentStock(); // Use the current stock as the quantity

        // 2. Act
        Response response = CartApi.addItemToCart(cartId, product.getId(), quantity);

        // 3. Assert
        assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful item addition");
        CartItemResponse addResponse = response.as(CartItemResponse.class);
        
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(addResponse.getCreated(), "Expected 'created' to be true in response");
        softAssert.assertNotNull(addResponse.getItemId(), "Expected 'itemId' to be returned");

        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        
        if (cartItems.length == 1) {
            softAssert.assertEquals(cartItems[0].getProductId(), product.getId(), "Product ID mismatch in cart");
            softAssert.assertEquals(cartItems[0].getQuantity(), Integer.valueOf(quantity), "Quantity mismatch in cart");
            softAssert.assertEquals(cartItems[0].getItemId(), addResponse.getItemId(), "Item ID mismatch in cart");
        }
        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, description = "TC_CART_005: Verify adding multiple unique products to cart")
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
            Response response = CartApi.addItemToCart(cartId, products[i].getId(), quantities[i]);
            assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful item addition at index " + (i + 1));
        }

        // 3. Assert
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, numberOfItemsToAdd, "Expected exactly " + numberOfItemsToAdd + " items in the cart");

        SoftAssert softAssert = new SoftAssert();
        if (cartItems.length == numberOfItemsToAdd) {
            for (int i = 0; i < numberOfItemsToAdd; i++) {
                softAssert.assertEquals(cartItems[i].getProductId(), products[i].getId(), "Product ID mismatch for item " + i);
                softAssert.assertEquals(cartItems[i].getQuantity(), Integer.valueOf(quantities[i]), "Quantity mismatch for item " + i);
            }
        }
        softAssert.assertAll();
    }
}

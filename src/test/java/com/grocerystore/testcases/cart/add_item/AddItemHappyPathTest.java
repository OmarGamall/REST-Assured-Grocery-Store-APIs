package com.grocerystore.testcases.cart.add_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Allure;
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

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"smoke"}, description = "TC_CART_003: Verify that POST /carts/{cartId}/items returns 201 Created and successfully adds an item when a valid productId and quantity are provided")
    public void testAddItemToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = ProductService.getRandomQuantity(product);

        // 2. Act
        Response response = Allure.step("Act: Add product ID " + product.getId() + " (qty: " + quantity + ") to cart", () -> {
            return CartApi.addItemToCart(cartId, product.getId(), quantity);
        });

        // 3. Assert
        Allure.step("Assert: Verify response status code is 201 and the item is in the cart details", () -> {
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
        });
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_004: Verify that POST /carts/{cartId}/items returns 201 Created and adds the item when quantity is exactly equal to the product's current stock")
    public void testAddItemToCartWithQuantitySameAsStock() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = product.getCurrentStock(); // Use the current stock as the quantity

        // 2. Act
        Response response = Allure.step("Act: Add product ID " + product.getId() + " with quantity same as stock (" + quantity + ") to cart", () -> {
            return CartApi.addItemToCart(cartId, product.getId(), quantity);
        });

        // 3. Assert
        Allure.step("Assert: Verify status code is 201 and item is added successfully", () -> {
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
        });
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_CART_005: Verify that POST /carts/{cartId}/items returns 201 Created for multiple unique products added sequentially to the same cart")
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
        Allure.step("Act: Add " + numberOfItemsToAdd + " unique products sequentially to cart ID: " + cartId, () -> {
            for (int i = 0; i < numberOfItemsToAdd; i++) {
                Response response = CartApi.addItemToCart(cartId, products[i].getId(), quantities[i]);
                assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful item addition at index " + (i + 1));
            }
        });

        // 3. Assert
        Allure.step("Assert: Verify all " + numberOfItemsToAdd + " products are present in the cart details", () -> {
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
        });
    }
}

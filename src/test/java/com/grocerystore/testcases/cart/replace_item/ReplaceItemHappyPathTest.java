package com.grocerystore.testcases.cart.replace_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Allure;
import com.grocerystore.apis.CartApi;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.cart.CartItemResponse;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;

import java.util.Objects;

import static org.testng.Assert.*;

@Test(groups = {"cart", "happy-path"})
public class ReplaceItemHappyPathTest extends BaseTest {

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_029: Verify that PUT /carts/{cartId}/items/{itemId} returns 204 No Content and successfully replaces both the product and its quantity")
    public void testReplaceCartItemProductAndQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        Product replacementProduct = ProductService.getRandomAvailableProductDifferentFromWithStock(initialProduct.getId(), 2);

        int initialQuantity = 1;
        int replacementQuantity = 2;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), initialQuantity);
        String itemId = addResponse.getItemId();

        // 2. Act
        Response replaceResponse = Allure.step("Act: Replace cart item with new product ID: " + replacementProduct.getId() + " and quantity: " + replacementQuantity, () -> {
            return CartApi.replaceCartItem(cartId, String.valueOf(itemId), replacementProduct.getId(), replacementQuantity);
        });

        // 3. Assert
        Allure.step("Assert: Verify response status code is 204 and item in cart is updated with new product and quantity", () -> {
            assertEquals(replaceResponse.getStatusCode(), 204, "Expected status code 204 for successful item replacement");
            CartItem[] cartItems = CartSteps.getCartItems(cartId);
            assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
            assertEquals(cartItems[0].getProductId(), replacementProduct.getId(), "Product ID mismatch after replacement");
            assertEquals(cartItems[0].getQuantity(), Integer.valueOf(replacementQuantity), "Quantity mismatch after replacement");
            assertEquals(cartItems[0].getItemId(), itemId, "Item ID mismatch after replacement");
        });
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_030: Verify that PUT /carts/{cartId}/items/{itemId} returns 204 No Content and updates the quantity when the productId is unchanged but the quantity is modified")
    public void testReplaceCartItemWithSameProductIdButDifferentQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        // Ensure we select a product with at least 2 in stock so we can modify the quantity to 2
        Product product = ProductService.getRandomAvailableProductWithStock(2);

        int initialQuantity = 1;
        int replacementQuantity = 2;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), initialQuantity);
        String itemId = addResponse.getItemId();

        // 2. Act
        Response replaceResponse = Allure.step("Act: Replace cart item with same product and new quantity: " + replacementQuantity, () -> {
            return CartApi.replaceCartItem(cartId, String.valueOf(itemId), product.getId(), replacementQuantity);
        });

        // 3. Assert
        Allure.step("Assert: Verify response status code is 204 and quantity in cart is updated to 2", () -> {
            assertEquals(replaceResponse.getStatusCode(), 204, "Expected status code 204 for successful item replacement");
            CartItem[] cartItems = CartSteps.getCartItems(cartId);
            assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
            assertEquals(cartItems[0].getProductId(), product.getId(), "Product ID mismatch after replacement");
            assertEquals(cartItems[0].getQuantity(), Integer.valueOf(replacementQuantity), "Quantity mismatch after replacement");
            assertEquals(cartItems[0].getItemId(), itemId, "Item ID mismatch after replacement");
        });
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_031: Verify that PUT /carts/{cartId}/items/{itemId} returns 204 No Content and replaces the product while keeping the quantity unchanged")
    public void testReplaceCartItemProductAndKeepSameQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        Product replacementProduct = ProductService.getRandomAvailableProductDifferentFrom(initialProduct.getId());

        int quantity = 2;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), quantity);
        String itemId = addResponse.getItemId();

        // 2. Act
        Response replaceResponse = Allure.step("Act: Replace cart item with new product ID: " + replacementProduct.getId() + " and same quantity", () -> {
            return CartApi.replaceCartItem(cartId, String.valueOf(itemId), replacementProduct.getId(), quantity);
        });

        // 3. Assert
        Allure.step("Assert: Verify response status code is 204 and product ID in cart is updated", () -> {
            assertEquals(replaceResponse.getStatusCode(), 204, "Expected status code 204 for successful item replacement");
            CartItem[] cartItems = CartSteps.getCartItems(cartId);
            assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
            assertEquals(cartItems[0].getProductId(), replacementProduct.getId(), "Product ID mismatch after replacement");
            assertEquals(cartItems[0].getQuantity(), Integer.valueOf(quantity), "Quantity mismatch after replacement");
            assertEquals(cartItems[0].getItemId(), itemId, "Item ID mismatch after replacement");
        });
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_032: Verify that PUT /carts/{cartId}/items/{itemId} returns 204 No Content and replaces the product while keeping the previous quantity when the quantity parameter is omitted")
    public void testReplaceCartItemProductAndMissingQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        Product replacementProduct = ProductService.getRandomAvailableProductDifferentFrom(initialProduct.getId());

        int quantity = 2;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), quantity);
        String itemId = addResponse.getItemId();

        // 2. Act
        Response replaceResponse = Allure.step("Act: Replace cart item with new product and missing quantity parameter", () -> {
            return CartApi.replaceCartItem(cartId, String.valueOf(itemId), replacementProduct.getId()); // Pass null for quantity
        });

        // 3. Assert
        Allure.step("Assert: Verify response status code is 204 and product ID is updated while quantity remains 2", () -> {
            assertEquals(replaceResponse.getStatusCode(), 204, "Expected status code 204 for successful item replacement");
            CartItem[] cartItems = CartSteps.getCartItems(cartId);
            assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
            assertEquals(cartItems[0].getProductId(), replacementProduct.getId(), "Product ID mismatch after replacement");
            assertEquals(cartItems[0].getQuantity(), Integer.valueOf(quantity), "Quantity mismatch after replacement");
            assertEquals(cartItems[0].getItemId(), itemId, "Item ID mismatch after replacement");
        });
    }
}

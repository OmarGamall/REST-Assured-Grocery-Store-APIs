package com.grocerystore.testcases.cart.replace_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import com.grocerystore.apis.CartApi;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.cart.CartItemResponse;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;

import java.util.Objects;

import static org.testng.Assert.*;

public class ReplaceItemHappyPathTest extends BaseTest {

    @Test(description = "TC_CART_029: Verify replacing both product and quantity")
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
        assertEquals(cartItems[0].getItemId(), itemId, "Item ID mismatch after replacement");
    }

    @Test(description = "TC_CART_030: Verify replacing quantity with same productId")
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
        assertEquals(cartItems[0].getItemId(), itemId, "Item ID mismatch after replacement");
    }

    @Test(description = "TC_CART_031: Verify replacing product and keeping same quantity")
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
        assertEquals(cartItems[0].getItemId(), itemId, "Item ID mismatch after replacement");
    }

    @Test(description = "TC_CART_032: Verify replacing product with missing quantity parameter")
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
        assertEquals(cartItems[0].getItemId(), itemId, "Item ID mismatch after replacement");
    }
}

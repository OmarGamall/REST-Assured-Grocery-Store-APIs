package com.grocerystore.testcases.cart.add_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import com.grocerystore.apis.CartApi;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;

public class AddItemValidationTest extends BaseTest {

    @Test(description = "TC_CART_006: Verify error when adding duplicate product to cart")
    public void testAddDuplicateItemToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = ProductService.getRandomQuantity(product);

        // 2. Act
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), quantity);
        Response duplicateAddResponse = CartApi.addItemToCart(CartItem.builder()
                .cartId(cartId)
                .productId(product.getId())
                .quantity(quantity)
                .build());

        // 3. Assert
        assertErrorResponse(duplicateAddResponse, 400, "This product has already been added to cart");
    }

    @Test(description = "TC_CART_007: Verify error when adding out-of-stock product")
    public void testAddNonAvailableProductToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product nonAvailableProduct = ProductService.getRandomNonAvailableProduct();
        int quantity = 1;

        // 2. Act
        Response response = CartApi.addItemToCart(CartItem.builder()
                .cartId(cartId)
                .productId(nonAvailableProduct.getId())
                .quantity(quantity)
                .build());

        // 3. Assert
        assertErrorResponse(response, 400, "This product is not in stock and cannot be ordered");
    }

    @Test(description = "TC_CART_008: Verify error when quantity exceeds product stock")
    public void testAddQuantityExceedingStockToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantityExceedingStock = product.getCurrentStock() + 1;

        // 2. Act
        Response response = CartApi.addItemToCart(CartItem.builder()
                .cartId(cartId)
                .productId(product.getId())
                .quantity(quantityExceedingStock)
                .build());

        // 3. Assert
        assertErrorResponse(response, 400, "The quantity requested exceeds the current stock");
    }

    @Test(description = "TC_CART_009: Verify error when adding item with quantity 0")
    public void testAddItemWithZeroQuantityToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int zeroQuantity = 0;

        // 2. Act
        Response response = CartApi.addItemToCart(CartItem.builder()
                .cartId(cartId)
                .productId(product.getId())
                .quantity(zeroQuantity)
                .build());

        // 3. Assert
        assertErrorResponse(response, 400, "Quantity must be at least 1");
    }

    @Test(description = "TC_CART_010: Verify error when adding item with negative quantity")
    public void testAddItemWithNegativeQuantityToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int negativeQuantity = -5;

        // 2. Act
        Response response = CartApi.addItemToCart(CartItem.builder()
                .cartId(cartId)
                .productId(product.getId())
                .quantity(negativeQuantity)
                .build());

        // 3. Assert
        assertErrorResponse(response, 400, "Quantity must be at least 1");
    }

    @Test(description = "TC_CART_011: Verify error when adding invalid productId")
    public void testAddItemWithInvalidProductIdToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Integer invalidProductId = 999999;
        int quantity = 1;

        // 2. Act
        Response response = CartApi.addItemToCart(CartItem.builder()
                .cartId(cartId)
                .productId(invalidProductId)
                .quantity(quantity)
                .build());

        // 3. Assert
        assertErrorResponse(response, 400, "Invalid or missing productId");
    }

    @Test(description = "TC_CART_012: Verify error when adding item with invalid cartId")
    public void testAddItemWithInvalidCartId() {
        // 1. Arrange
        String invalidCartId = "invalid-cart-id";
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = 1;

        // 2. Act
        Response response = CartApi.addItemToCart(CartItem.builder()
                .cartId(invalidCartId)
                .productId(product.getId())
                .quantity(quantity)
                .build());

        // 3. Assert
        assertErrorResponse(response, 404, "No cart with id");
    }
}

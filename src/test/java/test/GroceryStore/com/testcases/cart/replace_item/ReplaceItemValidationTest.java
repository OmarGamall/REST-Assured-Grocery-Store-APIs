package test.GroceryStore.com.testcases.cart.replace_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.CartItemResponse;
import test.GroceryStore.com.models.Product;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.testcases.BaseTest;

import java.util.Objects;

public class ReplaceItemValidationTest extends BaseTest {

    @Test
    public void testReplaceCartItemWithInvalidProductId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        int quantity = 2;

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, initialProduct.getId(), quantity);
        String itemId = addResponse.getItemId();
        Integer invalidProductId = 999999;

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, String.valueOf(itemId), invalidProductId, quantity);

        // 3. Assert
        assertErrorResponse(replaceResponse, 400, "Invalid or missing productId");
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
        assertErrorResponse(replaceResponse, 404, "No cart with id");
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
        assertErrorResponse(replaceResponse, 404, "No item with id");
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
        assertErrorResponse(replaceResponse, 400, "The quantity requested is not available in stock");
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
        assertErrorResponse(replaceResponse, 400, "The quantity requested is not available in stock");
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
        assertErrorResponse(replaceResponse, 400, "Invalid or missing quantity");
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
        assertErrorResponse(replaceResponse, 400, "Invalid or missing quantity");
    }

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
        assertErrorResponse(response, 404, "No item with id");
    }
}

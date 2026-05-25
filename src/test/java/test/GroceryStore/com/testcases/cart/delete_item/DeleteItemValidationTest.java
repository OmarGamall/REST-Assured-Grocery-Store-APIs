package test.GroceryStore.com.testcases.cart.delete_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.cart.CartItemResponse;
import test.GroceryStore.com.models.product.Product;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.testcases.BaseTest;

import java.util.Objects;

import static org.testng.Assert.assertEquals;

public class DeleteItemValidationTest extends BaseTest {

    @Test
    public void testDeleteSameCartItemTwice() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);
        String itemId = addResponse.getItemId();

        // 2. Act
        Response firstDeleteResponse = CartApi.deleteCartItem(cartId, itemId);
        Response secondDeleteResponse = CartApi.deleteCartItem(cartId, itemId);

        // 3. Assert
        assertEquals(firstDeleteResponse.getStatusCode(), 204, "Expected status code 204 for successful item deletion");
        assertErrorResponse(secondDeleteResponse, 404, "No item with id");
    }

    @Test
    public void testDeleteCartItemWithInvalidCartId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);
        String itemId = addResponse.getItemId();
        String invalidCartId = "invalid-cart-id";

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(invalidCartId, itemId);

        // 3. Assert
        assertErrorResponse(deleteResponse, 404, "No cart with id");
    }

    @Test
    public void testDeleteCartItemWithInvalidItemId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);
        String invalidItemId = "invalid-item-id";

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(cartId, invalidItemId);

        // 3. Assert
        assertErrorResponse(deleteResponse, 404, "No item with id");
    }

    @Test
    public void testDeleteCartItemFromEmptyCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        String itemId = "non-existent-item-id";

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(cartId, itemId);

        // 3. Assert
        assertErrorResponse(deleteResponse, 404, "No item with id");
    }

    @Test
    public void testDeleteCartItemWithMismatchedCartAndItemId() {
        // 1. Arrange
        String cartAId = CartSteps.createCartAndGetId();
        String cartBId = CartSteps.createCartAndGetId();
        Product productA = ProductService.getRandomAvailableProduct();
        Product productB = null;
        do {
            productB = ProductService.getRandomAvailableProduct();
        } while (Objects.equals(productB.getId(), productA.getId()));

        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartAId, productA.getId(), 1);
        String itemIdA = addResponse.getItemId();

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(cartBId, itemIdA);

        // 3. Assert
        assertErrorResponse(deleteResponse, 404, "No item with id");
    }
}

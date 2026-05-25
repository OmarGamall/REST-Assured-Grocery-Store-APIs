package test.GroceryStore.com.testcases.cart.modify_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.cart.CartItemResponse;
import test.GroceryStore.com.models.product.Product;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.testcases.BaseTest;

public class ModifyItemValidationTest extends BaseTest {

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
        assertErrorResponse(response, 400, "The quantity requested is not available in stock");
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
        assertErrorResponse(response, 400, "Invalid or missing quantity");
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
        assertErrorResponse(response, 400, "Invalid or missing quantity");
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
        assertErrorResponse(response, 404, "No item with id");
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
        assertErrorResponse(response, 404, "No cart with id");
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
        assertErrorResponse(response, 404, "Invalid or missing quantity");
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
        assertErrorResponse(response, 404, "No item with id");
    }
}

package com.grocerystore.testcases.cart.modify_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import com.grocerystore.apis.CartApi;
import com.grocerystore.models.cart.CartItemResponse;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;

public class ModifyItemValidationTest extends BaseTest {

    @Test(description = "TC_CART_022: Verify error when modified quantity exceeds stock")
    public void testModifyCartItemQuantityExceedingStock() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = null;
        // Ensure we select a product with at least 2 in stock so we can modify the quantity to 2
        do {
            product = ProductService.getRandomAvailableProduct();
        } while (product.getCurrentStock() != null && product.getCurrentStock() < 2);

        CartItem cartItem = CartSteps.addItemToCart(cartId, product.getId(), 1);
        int quantityExceedingStock = product.getCurrentStock() + 1;

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, cartItem.getItemId(), quantityExceedingStock);

        // 3. Assert
        assertErrorResponse(response, 400, "The quantity requested is not available in stock");
    }

    @Test(description = "TC_CART_023: Verify error when modifying quantity to 0")
    public void testModifyCartItemQuantityToZero() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, cartItem.getItemId(), 0);

        // 3. Assert
        assertErrorResponse(response, 400, "Invalid or missing quantity");
    }

    @Test(description = "TC_CART_024: Verify error when modifying quantity to negative")
    public void testModifyCartItemQuantityToNegative() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, cartItem.getItemId(), -5);

        // 3. Assert
        assertErrorResponse(response, 400, "Invalid or missing quantity");
    }

    @Test(description = "TC_CART_025: Verify error when modifying with invalid itemId")
    public void testModifyCartItemWithInvalidItemId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId, 1);
        String invalidItemId = "invalid-item-id";

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, invalidItemId, 2);

        // 3. Assert
        assertErrorResponse(response, 404, "No item with id");
    }

    @Test(description = "TC_CART_026: Verify error when modifying with invalid cartId")
    public void testModifyCartItemWithInvalidCartId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);
        String invalidCartId = "invalid-cart-id";

        // 2. Act
        Response response = CartApi.modifyCartItem(invalidCartId, cartItem.getItemId(), 2);

        // 3. Assert
        assertErrorResponse(response, 404, "No cart with id");
    }

    @Test(description = "TC_CART_027: Verify error when modified quantity is missing or null")
    public void testModifyCartItemWithMissingQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, cartItem.getItemId(), null); // Pass null for quantity

        // 3. Assert
        assertErrorResponse(response, 404, "Invalid or missing quantity");
    }

    @Test(description = "TC_CART_028: Verify error when modifying item with mismatched cartId")
    public void testModifyCartItemWithMismatchedCartAndItemId() {
        // 1. Arrange
        String cartAId = CartSteps.createCartAndGetId();
        String cartBId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartAId, 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartBId, cartItem.getItemId(), 2);

        // 3. Assert
        assertErrorResponse(response, 404, "No item with id");
    }
}

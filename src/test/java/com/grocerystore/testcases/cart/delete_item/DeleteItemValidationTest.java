package com.grocerystore.testcases.cart.delete_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import com.grocerystore.apis.CartApi;
import com.grocerystore.models.cart.CartItemResponse;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;

import java.util.Objects;

import static org.testng.Assert.assertEquals;

public class DeleteItemValidationTest extends BaseTest {

    @Test(description = "TC_CART_015: Verify error when deleting same cart item twice")
    public void testDeleteSameCartItemTwice() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);
        String itemId = cartItem.getItemId();

        // 2. Act
        Response firstDeleteResponse = CartApi.deleteCartItem(cartId, itemId);
        Response secondDeleteResponse = CartApi.deleteCartItem(cartId, itemId);

        // 3. Assert
        assertEquals(firstDeleteResponse.getStatusCode(), 204, "Expected status code 204 for successful item deletion");
        assertErrorResponse(secondDeleteResponse, 404, "No item with id");
    }

    @Test(description = "TC_CART_016: Verify error when deleting with invalid cartId")
    public void testDeleteCartItemWithInvalidCartId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);
        String itemId = cartItem.getItemId();
        String invalidCartId = "invalid-cart-id";

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(invalidCartId, itemId);

        // 3. Assert
        assertErrorResponse(deleteResponse, 404, "No cart with id");
    }

    @Test(description = "TC_CART_017: Verify error when deleting with invalid itemId")
    public void testDeleteCartItemWithInvalidItemId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId, 1);
        String invalidItemId = "invalid-item-id";

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(cartId, invalidItemId);

        // 3. Assert
        assertErrorResponse(deleteResponse, 404, "No item with id");
    }

    @Test(description = "TC_CART_018: Verify error when deleting item from empty cart")
    public void testDeleteCartItemFromEmptyCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        String itemId = "non-existent-item-id";

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(cartId, itemId);

        // 3. Assert
        assertErrorResponse(deleteResponse, 404, "No item with id");
    }

    @Test(description = "TC_CART_019: Verify error when deleting item using mismatched cartId")
    public void testDeleteCartItemWithMismatchedCartAndItemId() {
        // 1. Arrange
        String cartAId = CartSteps.createCartAndGetId();
        String cartBId = CartSteps.createCartAndGetId();
        CartItem cartItemA = CartSteps.addRandomItemToCart(cartAId, 1);
        String itemIdA = cartItemA.getItemId();

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(cartBId, itemIdA);

        // 3. Assert
        assertErrorResponse(deleteResponse, 404, "No item with id");
    }
}

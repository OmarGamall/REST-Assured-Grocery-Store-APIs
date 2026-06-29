package com.grocerystore.testcases.cart.delete_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import com.grocerystore.apis.CartApi;
import com.grocerystore.models.cart.CartItemResponse;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;
import static com.grocerystore.constants.ErrorMessages.*;

import java.util.Objects;

import static org.testng.Assert.assertEquals;

@Test(groups = {"cart", "validation"})
public class DeleteItemValidationTest extends BaseTest {

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_015: Verify that DELETE /carts/{cartId}/items/{itemId} returns 404 Not Found and validation error when attempting to delete the same item a second time")
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
        assertErrorResponse(secondDeleteResponse, 404, NO_ITEM_WITH_ID);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_016: Verify that DELETE /carts/{cartId}/items/{itemId} returns 404 Not Found and validation error when the cartId is invalid or non-existent")
    public void testDeleteCartItemWithInvalidCartId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);
        String itemId = cartItem.getItemId();
        String invalidCartId = "invalid-cart-id";

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(invalidCartId, itemId);

        // 3. Assert
        assertErrorResponse(deleteResponse, 404, NO_CART_WITH_ID);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_017: Verify that DELETE /carts/{cartId}/items/{itemId} returns 404 Not Found and validation error when the itemId is invalid or non-existent")
    public void testDeleteCartItemWithInvalidItemId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId, 1);
        String invalidItemId = "invalid-item-id";

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(cartId, invalidItemId);

        // 3. Assert
        assertErrorResponse(deleteResponse, 404, NO_ITEM_WITH_ID);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_018: Verify that DELETE /carts/{cartId}/items/{itemId} returns 404 Not Found and validation error when attempting to delete an item from an empty cart")
    public void testDeleteCartItemFromEmptyCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        String itemId = "non-existent-item-id";

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(cartId, itemId);

        // 3. Assert
        assertErrorResponse(deleteResponse, 404, NO_ITEM_WITH_ID);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_019: Verify that DELETE /carts/{cartId}/items/{itemId} returns 404 Not Found and validation error when attempting to delete an item using a different cartId than the one it belongs to")
    public void testDeleteCartItemWithMismatchedCartAndItemId() {
        // 1. Arrange
        String cartAId = CartSteps.createCartAndGetId();
        String cartBId = CartSteps.createCartAndGetId();
        CartItem cartItemA = CartSteps.addRandomItemToCart(cartAId, 1);
        String itemIdA = cartItemA.getItemId();

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(cartBId, itemIdA);

        // 3. Assert
        assertErrorResponse(deleteResponse, 404, NO_ITEM_WITH_ID);
    }
}

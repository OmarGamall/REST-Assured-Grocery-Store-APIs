package com.grocerystore.testcases.cart.modify_item;

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

@Test(groups = {"cart", "validation"})
public class ModifyItemValidationTest extends BaseTest {

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_022: Verify that PATCH /carts/{cartId}/items/{itemId} returns 400 Bad Request and validation error when the modified quantity exceeds the product's current stock")
    public void testModifyCartItemQuantityExceedingStock() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        // Ensure we select a product with at least 2 in stock so we can modify the quantity to 2
        Product product = ProductService.getRandomAvailableProductWithStock(2);

        CartItem cartItem = CartSteps.addItemToCart(cartId, product.getId(), 1);
        int quantityExceedingStock = product.getCurrentStock() + 1;

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, cartItem.getItemId(), quantityExceedingStock);

        // 3. Assert
        assertErrorResponse(response, 400, QUANTITY_NOT_AVAILABLE);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_023: Verify that PATCH /carts/{cartId}/items/{itemId} returns 400 Bad Request and validation error when modifying the quantity of an item to 0")
    public void testModifyCartItemQuantityToZero() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, cartItem.getItemId(), 0);

        // 3. Assert
        assertErrorResponse(response, 400, INVALID_QUANTITY);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_024: Verify that PATCH /carts/{cartId}/items/{itemId} returns 400 Bad Request and validation error when modifying the quantity of an item to a negative number")
    public void testModifyCartItemQuantityToNegative() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, cartItem.getItemId(), -5);

        // 3. Assert
        assertErrorResponse(response, 400, INVALID_QUANTITY);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_025: Verify that PATCH /carts/{cartId}/items/{itemId} returns 404 Not Found and validation error when the itemId is invalid or non-existent")
    public void testModifyCartItemWithInvalidItemId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId, 1);
        String invalidItemId = "invalid-item-id";

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, invalidItemId, 2);

        // 3. Assert
        assertErrorResponse(response, 404, NO_ITEM_WITH_ID);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_026: Verify that PATCH /carts/{cartId}/items/{itemId} returns 404 Not Found and validation error when the cartId is invalid or non-existent")
    public void testModifyCartItemWithInvalidCartId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);
        String invalidCartId = "invalid-cart-id";

        // 2. Act
        Response response = CartApi.modifyCartItem(invalidCartId, cartItem.getItemId(), 2);

        // 3. Assert
        assertErrorResponse(response, 404, NO_CART_WITH_ID);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_027: Verify that PATCH /carts/{cartId}/items/{itemId} returns 404 Not Found and validation error when the quantity parameter is missing or null")
    public void testModifyCartItemWithMissingQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, cartItem.getItemId(), null); // Pass null for quantity

        // 3. Assert
        assertErrorResponse(response, 404, INVALID_QUANTITY);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_028: Verify that PATCH /carts/{cartId}/items/{itemId} returns 404 Not Found and validation error when attempting to modify an item using a different cartId than the one it belongs to")
    public void testModifyCartItemWithMismatchedCartAndItemId() {
        // 1. Arrange
        String cartAId = CartSteps.createCartAndGetId();
        String cartBId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartAId, 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartBId, cartItem.getItemId(), 2);

        // 3. Assert
        assertErrorResponse(response, 404, NO_ITEM_WITH_ID);
    }
}

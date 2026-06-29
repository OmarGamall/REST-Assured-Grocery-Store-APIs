package com.grocerystore.testcases.cart.replace_item;

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

@Test(groups = {"cart", "validation"})
public class ReplaceItemValidationTest extends BaseTest {

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_033: Verify that PUT /carts/{cartId}/items/{itemId} returns 400 Bad Request and validation error when replacing an item with an invalid or non-existent productId")
    public void testReplaceCartItemWithInvalidProductId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);
        Integer invalidProductId = 999999;

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, cartItem.getItemId(), invalidProductId, 1);

        // 3. Assert
        assertErrorResponse(replaceResponse, 400, INVALID_PRODUCT_ID);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_034: Verify that PUT /carts/{cartId}/items/{itemId} returns 404 Not Found and validation error when the cartId is invalid or non-existent")
    public void testReplaceCartItemWithInvalidCartId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);
        String invalidCartId = "invalid-cart-id";

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(invalidCartId, cartItem.getItemId(), cartItem.getProductId(), 1);

        // 3. Assert
        assertErrorResponse(replaceResponse, 404, NO_CART_WITH_ID);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_035: Verify that PUT /carts/{cartId}/items/{itemId} returns 404 Not Found and validation error when the itemId is invalid or non-existent")
    public void testReplaceCartItemWithInvalidItemId() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);
        String invalidItemId = "invalid-item-id";

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, invalidItemId, cartItem.getProductId(), 1);

        // 3. Assert
        assertErrorResponse(replaceResponse, 404, NO_ITEM_WITH_ID);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_036: Verify that PUT /carts/{cartId}/items/{itemId} returns 400 Bad Request and validation error when replacing an item with an out-of-stock product")
    public void testReplaceCartItemWithNonAvailableProduct() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);
        Product nonAvailableProduct = ProductService.getRandomNonAvailableProduct();

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, cartItem.getItemId(), nonAvailableProduct.getId(), 1);

        // 3. Assert
        assertErrorResponse(replaceResponse, 400, QUANTITY_NOT_AVAILABLE);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_037: Verify that PUT /carts/{cartId}/items/{itemId} returns 400 Bad Request and validation error when the replacement quantity exceeds the product's current stock")
    public void testReplaceCartItemWithQuantityExceedingStock() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product initialProduct = ProductService.getRandomAvailableProduct();
        int initialQuantity = 1;

        CartItem cartItem = CartSteps.addItemToCart(cartId, initialProduct.getId(), initialQuantity);
        int quantityExceedingStock = initialProduct.getCurrentStock() + 1;

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, cartItem.getItemId(), initialProduct.getId(), quantityExceedingStock);

        // 3. Assert
        assertErrorResponse(replaceResponse, 400, QUANTITY_NOT_AVAILABLE);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_038: Verify that PUT /carts/{cartId}/items/{itemId} returns 400 Bad Request and validation error when replacing an item with a negative quantity")
    public void testReplaceCartItemWithNegativeQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, cartItem.getItemId(), cartItem.getProductId(), -5);

        // 3. Assert
        assertErrorResponse(replaceResponse, 400, INVALID_QUANTITY);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_039: Verify that PUT /carts/{cartId}/items/{itemId} returns 400 Bad Request and validation error when replacing an item with a quantity of 0")
    public void testReplaceCartItemWithZeroQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);

        // 2. Act
        Response replaceResponse = CartApi.replaceCartItem(cartId, cartItem.getItemId(), cartItem.getProductId(), 0);

        // 3. Assert
        assertErrorResponse(replaceResponse, 400, INVALID_QUANTITY);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_040: Verify that PUT /carts/{cartId}/items/{itemId} returns 404 Not Found and validation error when attempting to replace an item using a different cartId than the one it belongs to")
    public void testReplaceCartItemWithMismatchedCartAndItemId() {
        // 1. Arrange
        String cartAId = CartSteps.createCartAndGetId();
        String cartBId = CartSteps.createCartAndGetId();
        CartItem cartItemA = CartSteps.addRandomItemToCart(cartAId, 1);
        Product productB = ProductService.getRandomAvailableProductDifferentFrom(cartItemA.getProductId());

        // 2. Act
        Response response = CartApi.replaceCartItem(cartBId, cartItemA.getItemId(), productB.getId(), 1);

        // 3. Assert
        assertErrorResponse(response, 404, NO_ITEM_WITH_ID);
    }
}

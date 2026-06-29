package com.grocerystore.testcases.cart.delete_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import com.grocerystore.apis.CartApi;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.cart.CartItemResponse;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;

import java.util.Objects;

import static org.testng.Assert.*;

@Test(groups = {"cart", "happy-path"})
public class DeleteItemHappyPathTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_CART_013: Verify that DELETE /carts/{cartId}/items/{itemId} returns 204 No Content and successfully deletes the item from the cart")
    public void testDeleteCartItem() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId, 1);
        String itemId = cartItem.getItemId();

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(cartId, itemId);

        // 3. Assert
        assertEquals(deleteResponse.getStatusCode(), 204, "Expected status code 204 for successful item deletion");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 0, "Expected no items in the cart after deletion");
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_CART_014: Verify that DELETE /carts/{cartId}/items/{itemId} returns 204 No Content for sequential deletions of multiple items in the same cart")
    public void testDeleteMoreThanOneItemFromCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem1 = CartSteps.addRandomItemToCart(cartId, 1);

        Product product2 = ProductService.getRandomAvailableProductDifferentFrom(cartItem1.getProductId());

        CartItem cartItem2 = CartSteps.addItemToCart(cartId, product2.getId(), 1);

        // 2. Act
        Response firstDeleteResponse = CartApi.deleteCartItem(cartId, cartItem1.getItemId());
        Response secondDeleteResponse = CartApi.deleteCartItem(cartId, cartItem2.getItemId());

        // 3. Assert
        assertEquals(firstDeleteResponse.getStatusCode(), 204, "Expected status code 204 for successful deletion of first item");
        assertEquals(secondDeleteResponse.getStatusCode(), 204, "Expected status code 204 for successful deletion of second item");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 0, "Expected no items in the cart after deleting both items");
    }
}

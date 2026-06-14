package com.grocerystore.testcases.cart.modify_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import com.grocerystore.apis.CartApi;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.cart.CartItemResponse;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;

import static org.testng.Assert.*;

@Test(groups = {"cart", "happy-path"})
public class ModifyItemHappyPathTest extends BaseTest {

    @Test(groups = {"regression"}, description = "TC_CART_020: Verify modifying cart item quantity")
    public void testModifyCartItemQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        // Ensure we select a product with at least 2 in stock so we can modify the quantity to 2
        Product product = ProductService.getRandomAvailableProductWithStock(2);

        CartItem cartItem = CartSteps.addItemToCart(cartId, product.getId(), 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, cartItem.getItemId(), 2);

        // 3. Assert
        assertEquals(response.getStatusCode(), 204, "Expected status code 204 for successful item modification");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), product.getId(), "Product ID mismatch in cart");
        assertEquals(cartItems[0].getQuantity(), Integer.valueOf(2), "Expected updated quantity to be 2");
    }

    @Test(groups = {"regression"}, description = "TC_CART_021: Verify modifying cart item to the same quantity")
    public void testModifyCartItemToSameQuantity() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItem cartItem = CartSteps.addItemToCart(cartId, product.getId(), 1);

        // 2. Act
        Response response = CartApi.modifyCartItem(cartId, cartItem.getItemId(), 1); // Modify to the same quantity

        // 3. Assert
        assertEquals(response.getStatusCode(), 204, "Expected status code 204 for modifying item to the same quantity");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 1, "Expected exactly 1 item in the cart");
        assertEquals(cartItems[0].getProductId(), product.getId(), "Product ID mismatch in cart");
        assertEquals(cartItems[0].getQuantity(), Integer.valueOf(1), "Expected quantity to remain unchanged at 1");
    }
}

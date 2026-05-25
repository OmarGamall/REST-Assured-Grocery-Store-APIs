package test.GroceryStore.com.testcases.cart.delete_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.CartItem;
import test.GroceryStore.com.models.CartItemResponse;
import test.GroceryStore.com.models.Product;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.testcases.BaseTest;

import java.util.Objects;

import static org.testng.Assert.*;

public class DeleteItemHappyPathTest extends BaseTest {

    @Test
    public void testDeleteCartItem() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartItemResponse addResponse = CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);
        String itemId = addResponse.getItemId();

        // 2. Act
        Response deleteResponse = CartApi.deleteCartItem(cartId, itemId);

        // 3. Assert
        assertEquals(deleteResponse.getStatusCode(), 204, "Expected status code 204 for successful item deletion");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 0, "Expected no items in the cart after deletion");
    }

    @Test
    public void testDeleteMoreThanOneItemFromCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product1 = ProductService.getRandomAvailableProduct();
        Product product2 = null;
        do {
            product2 = ProductService.getRandomAvailableProduct();
        } while (Objects.equals(product2.getId(), product1.getId()));

        CartItemResponse addResponse1 = CartSteps.addItemToCartAndGetResponse(cartId, product1.getId(), 1);
        CartItemResponse addResponse2 = CartSteps.addItemToCartAndGetResponse(cartId, product2.getId(), 1);
        String itemId1 = addResponse1.getItemId();
        String itemId2 = addResponse2.getItemId();

        // 2. Act
        Response firstDeleteResponse = CartApi.deleteCartItem(cartId, itemId1);
        Response secondDeleteResponse = CartApi.deleteCartItem(cartId, itemId2);

        // 3. Assert
        assertEquals(firstDeleteResponse.getStatusCode(), 204, "Expected status code 204 for successful deletion of first item");
        assertEquals(secondDeleteResponse.getStatusCode(), 204, "Expected status code 204 for successful deletion of second item");
        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        assertEquals(cartItems.length, 0, "Expected no items in the cart after deleting both items");
    }
}

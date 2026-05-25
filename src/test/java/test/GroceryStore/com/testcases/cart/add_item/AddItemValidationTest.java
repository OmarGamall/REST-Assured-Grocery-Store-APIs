package test.GroceryStore.com.testcases.cart.add_item;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.cart.CartItem;
import test.GroceryStore.com.models.product.Product;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.testcases.BaseTest;

public class AddItemValidationTest extends BaseTest {

    @Test
    public void testAddDuplicateItemToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = ProductService.getRandomQuantity(product);

        // 2. Act
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), quantity);
        Response duplicateAddResponse = CartApi.addItemToCart(new CartItem(cartId, product.getId(), quantity));

        // 3. Assert
        assertErrorResponse(duplicateAddResponse, 400, "This product has already been added to cart");
    }

    @Test
    public void testAddNonAvailableProductToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product nonAvailableProduct = ProductService.getRandomNonAvailableProduct();
        int quantity = 1;

        // 2. Act
        Response response = CartApi.addItemToCart(new CartItem(cartId, nonAvailableProduct.getId(), quantity));

        // 3. Assert
        assertErrorResponse(response, 400, "This product is not in stock and cannot be ordered");
    }

    @Test
    public void testAddQuantityExceedingStockToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantityExceedingStock = product.getCurrentStock() + 1;

        // 2. Act
        Response response = CartApi.addItemToCart(new CartItem(cartId, product.getId(), quantityExceedingStock));

        // 3. Assert
        assertErrorResponse(response, 400, "The quantity requested exceeds the current stock");
    }

    @Test
    public void testAddItemWithZeroQuantityToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int zeroQuantity = 0;

        // 2. Act
        Response response = CartApi.addItemToCart(new CartItem(cartId, product.getId(), zeroQuantity));

        // 3. Assert
        assertErrorResponse(response, 400, "Quantity must be at least 1");
    }

    @Test
    public void testAddItemWithNegativeQuantityToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int negativeQuantity = -5;

        // 2. Act
        Response response = CartApi.addItemToCart(new CartItem(cartId, product.getId(), negativeQuantity));

        // 3. Assert
        assertErrorResponse(response, 400, "Quantity must be at least 1");
    }

    @Test
    public void testAddItemWithInvalidProductIdToCart() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Integer invalidProductId = 999999;
        int quantity = 1;

        // 2. Act
        Response response = CartApi.addItemToCart(new CartItem(cartId, invalidProductId, quantity));

        // 3. Assert
        assertErrorResponse(response, 400, "Invalid or missing productId");
    }

    @Test
    public void testAddItemWithInvalidCartId() {
        // 1. Arrange
        String invalidCartId = "invalid-cart-id";
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = 1;

        // 2. Act
        Response response = CartApi.addItemToCart(new CartItem(invalidCartId, product.getId(), quantity));

        // 3. Assert
        assertErrorResponse(response, 404, "No cart with id");
    }
}

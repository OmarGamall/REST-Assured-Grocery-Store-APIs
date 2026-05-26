package test.GroceryStore.com.testcases.order.create;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.OrdersApi;
import test.GroceryStore.com.models.cart.CartItem;
import test.GroceryStore.com.models.order.OrderRequest;
import test.GroceryStore.com.models.product.Product;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.testcases.BaseTest;

import static org.testng.Assert.assertEquals;

public class CreateOrderValidationTest extends BaseTest {

    @Test
    public void testCreateOrderWithInvalidToken() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId);

        OrderRequest orderRequest = new OrderRequest(cartId, "Omar Validation");

        // Act
        Response response = OrdersApi.createOrder("invalid_token_12345", orderRequest);

        // Assert
        assertErrorResponse(response, 401, "Invalid bearer token");
    }

    @Test
    public void testCreateOrderWithEmptyCart() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        OrderRequest orderRequest = new OrderRequest(cartId, "Omar Validation");

        // Act
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert
        assertErrorResponse(response, 400, "cart is empty");
    }

    @Test
    public void testCreateOrderWithInvalidCartId() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest("non_existent_cart_id_12345", "Omar Validation");

        // Act
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert
        assertErrorResponse(response, 400, "cartId");
    }

    @Test
    public void testCreateOrderWithoutCustomerName() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        OrderRequest orderRequest = new OrderRequest(cartId, null);

        // Act
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert
        assertErrorResponse(response, 400, "customer name");
    }

    @Test
    public void testCreateDuplicateOrder() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        OrderRequest orderRequest = new OrderRequest(cartId, "Omar Validation");

        // Act - First order creation
        Response firstResponse = OrdersApi.createOrder(getToken(), orderRequest);
        assertEquals(firstResponse.getStatusCode(), 201, "Expected 201 status code for first order creation");

        // Act - Attempt to create a duplicate order with the same cart
        Response secondResponse = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert
        assertErrorResponse(secondResponse, 400, "Invalid or missing cartId");
    }

    @Test
    public void testCreateOrderWithInvalidCustomerName() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        OrderRequest orderRequest = new OrderRequest(cartId, ""); // Empty customer name

        // Act
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert
        assertErrorResponse(response, 400, "customer name");
    }

    @Test
    public void testCreateOrderWithExcessiveCommentLength() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        String longComment = FAKER.lorem().characters(10000); // Assuming the comment length limit is 1000 characters
        OrderRequest orderRequest = new OrderRequest(cartId, "Omar Validation", longComment);

        // Act
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert
        assertErrorResponse(response, 400, "comment");
    }
}

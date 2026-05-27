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

    @Test(description = "TC_ORDER_003: Verify error when creating order with invalid token")
    public void testCreateOrderWithInvalidToken() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId);

        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName("Omar Validation")
                .build();

        // Act
        Response response = OrdersApi.createOrder("invalid_token_12345", orderRequest);

        // Assert
        assertErrorResponse(response, 401, "Invalid bearer token");
    }

    @Test(description = "TC_ORDER_004: Verify error when placing order with empty cart")
    public void testCreateOrderWithEmptyCart() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName("Omar Validation")
                .build();

        // Act
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert
        assertErrorResponse(response, 400, "cart is empty");
    }

    @Test(description = "TC_ORDER_005: Verify error when placing order with invalid cartId")
    public void testCreateOrderWithInvalidCartId() {
        // Arrange
        OrderRequest orderRequest = OrderRequest.builder()
                .cartId("non_existent_cart_id_12345")
                .customerName("Omar Validation")
                .build();

        // Act
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert
        assertErrorResponse(response, 400, "cartId");
    }

    @Test(description = "TC_ORDER_006: Verify error when customerName is missing or null")
    public void testCreateOrderWithoutCustomerName() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .build();

        // Act
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert
        assertErrorResponse(response, 400, "customer name");
    }

    @Test(description = "TC_ORDER_007: Verify error when placing duplicate order using same cartId")
    public void testCreateDuplicateOrder() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName("Omar Validation")
                .build();

        // Act - First order creation
        Response firstResponse = OrdersApi.createOrder(getToken(), orderRequest);
        assertEquals(firstResponse.getStatusCode(), 201, "Expected 201 status code for first order creation");

        // Act - Attempt to create a duplicate order with the same cart
        Response secondResponse = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert
        assertErrorResponse(secondResponse, 400, "Invalid or missing cartId");
    }

    @Test(description = "TC_ORDER_008: Verify error when customerName is empty")
    public void testCreateOrderWithInvalidCustomerName() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName("")
                .build();

        // Act
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert
        assertErrorResponse(response, 400, "customer name");
    }

    @Test(description = "TC_ORDER_009: Verify error when comment is excessively long")
    public void testCreateOrderWithExcessiveCommentLength() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        String longComment = FAKER.lorem().characters(10000); // Assuming the comment length limit is 1000 characters
        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName("Omar Validation")
                .comment(longComment)
                .build();

        // Act
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert
        assertErrorResponse(response, 400, "comment");
    }
}

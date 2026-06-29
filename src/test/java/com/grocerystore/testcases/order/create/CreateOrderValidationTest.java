package com.grocerystore.testcases.order.create;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import com.grocerystore.apis.OrdersApi;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.order.OrderRequest;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;
import static com.grocerystore.constants.ErrorMessages.*;

import static org.testng.Assert.assertEquals;

@Test(groups = {"orders", "validation"})
public class CreateOrderValidationTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_ORDER_003: Verify that POST /orders returns 401 Unauthorized and validation error when an invalid bearer token is used")
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
        assertErrorResponse(response, 401, INVALID_BEARER_TOKEN);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_ORDER_004: Verify that POST /orders returns 400 Bad Request and validation error when attempting to place an order with an empty cart")
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
        assertErrorResponse(response, 400, CART_IS_EMPTY);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_ORDER_005: Verify that POST /orders returns 400 Bad Request and validation error when placing an order with an invalid or non-existent cartId")
    public void testCreateOrderWithInvalidCartId() {
        // Arrange
        OrderRequest orderRequest = OrderRequest.builder()
                .cartId("non_existent_cart_id_12345")
                .customerName("Omar Validation")
                .build();

        // Act
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert
        assertErrorResponse(response, 400, CART_ID_REQUIRED);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_ORDER_006: Verify that POST /orders returns 400 Bad Request and validation error when customerName parameter is missing or null")
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
        assertErrorResponse(response, 400, CUSTOMER_NAME_REQUIRED);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_ORDER_007: Verify that POST /orders returns 400 Bad Request and validation error when attempting to place a duplicate order using the same cartId")
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
        assertErrorResponse(secondResponse, 400, INVALID_OR_MISSING_CART_ID);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_ORDER_008: Verify that POST /orders returns 400 Bad Request and validation error when customerName is empty")
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
        assertErrorResponse(response, 400, CUSTOMER_NAME_REQUIRED);
    }

    @Severity(SeverityLevel.MINOR)
    @Test(groups = {"regression"}, description = "TC_ORDER_009: Verify that POST /orders returns 400 Bad Request and validation error when the comment length is excessively long")
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
        assertErrorResponse(response, 400, COMMENT_LIMIT);
    }
}

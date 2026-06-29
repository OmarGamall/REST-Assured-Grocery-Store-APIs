package com.grocerystore.testcases.order.update;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import com.grocerystore.apis.OrdersApi;
import com.grocerystore.models.order.Order;
import com.grocerystore.models.order.OrderRequest;
import com.grocerystore.models.order.OrderResponse;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.steps.ClientSteps;
import com.grocerystore.steps.OrderSteps;
import com.grocerystore.testcases.BaseTest;
import static com.grocerystore.constants.ErrorMessages.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = {"orders", "validation"})
public class UpdateOrderValidationTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_ORDER_024: Verify that PATCH /orders/{orderId} returns 401 Unauthorized and validation error when an invalid bearer token is used")
    public void testUpdateOrderWithInvalidToken() {
        // Arrange
        Order order = OrderSteps.createOrderAndGetOrderDetails();

        // Act
        OrderRequest updateRequest = OrderRequest.builder()
                .customerName("Updated Name")
                .comment("Updated Comment")
                .build();
        Response response = OrdersApi.updateOrder("invalid_token_12345", order.getId(), updateRequest);

        // Assert
        assertErrorResponse(response, 401, BEARER_TOKEN);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_ORDER_025: Verify that PATCH /orders/{orderId} returns 404 Not Found and validation error when the orderId is invalid or non-existent")
    public void testUpdateOrderWithNonExistentId() {
        // Arrange
        OrderRequest updateRequest = OrderRequest.builder()
                .customerName("Validation Name")
                .comment("Updated Comment")
                .build();

        // Act
        Response response = OrdersApi.updateOrder(getToken(), "non_existent_order_id_12345", updateRequest);

        // Assert
        assertErrorResponse(response, 404, NO_ORDER_WITH_ID);
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_ORDER_026: Verify that PATCH /orders/{orderId} returns 404 Not Found and validation error when attempting to update an order belonging to a different API client")
    public void testUpdateOrderBelongingToDifferentClient() {
        // Arrange - Register other client and place an order
        String firstClientToken = ClientSteps.registerClientAndGetToken();

        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        Order order = OrderSteps.createOrderAndGetOrderDetails(firstClientToken, cartId, "Original Name");
        String orderId = order.getId();

        // Prepare update body
        OrderRequest updateRequest = OrderRequest.builder()
                .customerName("Attempted Hack Name")
                .comment("Attempted Hack Comment")
                .build();
        String secondClientToken = ClientSteps.registerClientAndGetToken(); // Using our token, not the first client's token

        // Act - Attempt to update other client's order using our token
        Response response = OrdersApi.updateOrder(secondClientToken, orderId, updateRequest);

        // Assert - Should return 404 (No order with id)
        assertErrorResponse(response, 404, NO_ORDER_WITH_ID);

    }
}

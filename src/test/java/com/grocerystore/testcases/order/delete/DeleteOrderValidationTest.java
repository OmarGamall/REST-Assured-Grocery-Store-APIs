package com.grocerystore.testcases.order.delete;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Allure;
import com.grocerystore.apis.OrdersApi;
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

@Test(groups = {"orders", "validation"})
public class DeleteOrderValidationTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_ORDER_011: Verify that DELETE /orders/{orderId} returns 401 Unauthorized and validation error when an invalid bearer token is used")
    public void testDeleteOrderWithInvalidToken() {
        // Act
        Response response = OrdersApi.deleteOrder("invalid_token_12345", "some-order-id");

        // Assert
        assertErrorResponse(response, 401, BEARER_TOKEN);
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_ORDER_012: Verify that DELETE /orders/{orderId} returns 401 Unauthorized and validation error when bearer token is missing or null")
    public void testDeleteOrderWithMissingToken() {
        // Act
        Response response = OrdersApi.deleteOrder(null, "some-order-id");
        // Assert
        assertErrorResponse(response, 401, BEARER_TOKEN);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_ORDER_013: Verify that DELETE /orders/{orderId} returns 404 Not Found and validation error when attempting to delete the same order a second time")
    public void testDeleteSameOrderMultipleTimes() {
        // Arrange - Create an order to delete
        String orderId = OrderSteps.createRandomOrderAndGetId();

        // Act - First deletion attempt
        Response firstDeleteResponse = OrdersApi.deleteOrder(getToken(), orderId);
        Allure.step("Assert: Verify first deletion is successful with 204", () -> {
            assertEquals(firstDeleteResponse.getStatusCode(), 204, "Expected 204 status code for first deletion");
        });

        // Act - Second deletion attempt on the same order
        Response secondDeleteResponse = OrdersApi.deleteOrder(getToken(), orderId);

        // Assert - Should return 404 (No order with id) since it's already deleted
        assertErrorResponse(secondDeleteResponse, 404, NO_ORDER_WITH_ID + " " + orderId);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_ORDER_014: Verify that DELETE /orders/{orderId} returns 404 Not Found and validation error when the orderId is invalid or non-existent")
    public void testDeleteOrderWithInvalidOrderId() {
        // Act
        Response response = OrdersApi.deleteOrder(getToken(), "non_existent_order_id_12345");

        // Assert
        assertErrorResponse(response, 404, NO_ORDER_WITH_ID);
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_ORDER_015: Verify that DELETE /orders/{orderId} returns 404 Not Found and validation error when attempting to delete an order belonging to a different API client")
    public void testDeleteOrderBelongingToDifferentClient() {
        // Arrange - Register other client and place an order
        String firstClientToken = ClientSteps.registerClientAndGetToken();
        
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId);

        com.grocerystore.models.order.Order order = OrderSteps.createOrderAndGetOrderDetails(firstClientToken, cartId, "Original Name");
        String orderId = order.getId();

        // Act - Attempt to delete First client's order using other client's token
        String secondClientToken = ClientSteps.registerClientAndGetToken();
        Response response = OrdersApi.deleteOrder(secondClientToken, orderId);

        // Assert - Should return 404 (No order with id)
        assertErrorResponse(response, 404, NO_ORDER_WITH_ID);
    }
}

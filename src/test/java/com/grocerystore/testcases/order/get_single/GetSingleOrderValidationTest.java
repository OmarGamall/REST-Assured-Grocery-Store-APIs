package com.grocerystore.testcases.order.get_single;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
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
public class GetSingleOrderValidationTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_ORDER_020: Verify that GET /orders/{orderId} returns 401 Unauthorized and validation error when an invalid bearer token is used")
    public void testGetSingleOrderWithInvalidToken() {
        // Act
        Response response = OrdersApi.getOrderById("invalid_token_12345", "some-order-id");

        // Assert
        assertErrorResponse(response, 401, BEARER_TOKEN);
    }    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_ORDER_021: Verify that GET /orders/{orderId} returns 404 Not Found and validation error when the orderId is invalid or non-existent")
    public void testGetSingleOrderWithInvalidOrderId() {
        // Act
        Response response = OrdersApi.getOrderById(getToken(), "non_existent_order_id_12345");

        // Assert
        assertErrorResponse(response, 404, NO_ORDER_WITH_ID);
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_ORDER_022: Verify that GET /orders/{orderId} returns 404 Not Found and validation error when attempting to retrieve an order belonging to a different API client")
    public void testGetSingleOrderBelongingToDifferentClient() {
        // Arrange - Register another client and place an order under their account
        String firstClientToken = ClientSteps.registerClientAndGetToken();
        
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId);

        com.grocerystore.models.order.Order order = OrderSteps.createOrderAndGetOrderDetails(firstClientToken, cartId, "Other Customer");
        String orderId = order.getId();

        // Act - Attempt to retrieve other client's order using our main token
        String secondClientToken = ClientSteps.registerClientAndGetToken();
        Response response = OrdersApi.getOrderById(secondClientToken, orderId);

        // Assert - Should return 404 Not Found (or 404 No order with id)
        assertErrorResponse(response, 404, NO_ORDER_WITH_ID);
    }
}

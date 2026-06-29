package com.grocerystore.testcases.order.delete;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import com.grocerystore.apis.OrdersApi;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.order.OrderRequest;
import com.grocerystore.models.order.OrderResponse;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.steps.OrderSteps;
import com.grocerystore.testcases.BaseTest;

import static org.testng.Assert.assertEquals;

@Test(groups = {"orders", "happy-path"})
public class DeleteOrderHappyPathTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_ORDER_010: Verify that DELETE /orders/{orderId} returns 204 No Content and successfully deletes the order")
    public void testDeleteOrderSuccessfully() {
        // Arrange
        String orderId = OrderSteps.createRandomOrderAndGetId();

        // Act
        Response deleteResponse = OrdersApi.deleteOrder(getToken(), orderId);

        // Assert
        assertEquals(deleteResponse.getStatusCode(), 204, "Expected 204 status code for order deletion");

        // Verify deletion by attempting to retrieve it
        Response getResponse = OrdersApi.getOrderById(getToken(), orderId);
        assertErrorResponse(getResponse, 404, "No order with id " + orderId);
    }

}

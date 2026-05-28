package com.grocerystore.testcases.order.update;

import io.restassured.response.Response;
import org.testng.annotations.Test;
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UpdateOrderValidationTest extends BaseTest {

    @Test(description = "TC_ORDER_024: Verify error when updating order with invalid token")
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
        assertErrorResponse(response, 401, "bearer token");
    }

    @Test(description = "TC_ORDER_025: Verify error when updating order with non-existent orderId")
    public void testUpdateOrderWithNonExistentId() {
        // Arrange
        OrderRequest updateRequest = OrderRequest.builder()
                .customerName("Validation Name")
                .comment("Updated Comment")
                .build();

        // Act
        Response response = OrdersApi.updateOrder(getToken(), "non_existent_order_id_12345", updateRequest);

        // Assert
        assertErrorResponse(response, 404, "No order with id");
    }

    @Test(description = "TC_ORDER_026: Verify error when updating order belonging to another client")
    public void testUpdateOrderBelongingToDifferentClient() {
        // Arrange - Register other client and place an order
        String firstClientToken = ClientSteps.registerClientAndGetToken();

        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName("Original Name")
                .build();
        Response createResponse = OrdersApi.createOrder(firstClientToken, orderRequest);
        assertEquals(createResponse.getStatusCode(), 201);
        String orderId = createResponse.as(OrderResponse.class).getOrderId();

        // Prepare update body
        OrderRequest updateRequest = OrderRequest.builder()
                .customerName("Attempted Hack Name")
                .comment("Attempted Hack Comment")
                .build();
        String secondClientToken = ClientSteps.registerClientAndGetToken(); // Using our token, not the first client's token

        // Act - Attempt to update other client's order using our token
        Response response = OrdersApi.updateOrder(secondClientToken, orderId, updateRequest);

        // Assert - Should return 404 (No order with id)
        assertErrorResponse(response, 404, "No order with id");

    }
}

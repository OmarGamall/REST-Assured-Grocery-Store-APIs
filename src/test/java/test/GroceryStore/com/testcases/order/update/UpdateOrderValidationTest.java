package test.GroceryStore.com.testcases.order.update;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.OrdersApi;
import test.GroceryStore.com.models.order.OrderRequest;
import test.GroceryStore.com.models.order.OrderResponse;
import test.GroceryStore.com.models.product.Product;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.steps.ClientSteps;
import test.GroceryStore.com.testcases.BaseTest;

import static org.testng.Assert.assertEquals;

public class UpdateOrderValidationTest extends BaseTest {

    @Test
    public void testUpdateOrderWithInvalidToken() {
        // Arrange
        OrderRequest updateRequest = new OrderRequest();
        updateRequest.setCustomerName("Validation Name");

        // Act
        Response response = OrdersApi.updateOrder("invalid_token_12345", "some-order-id", updateRequest);

        // Assert
        assertErrorResponse(response, 401, "bearer token");
    }

    @Test
    public void testUpdateOrderWithNonExistentId() {
        // Arrange
        OrderRequest updateRequest = new OrderRequest();
        updateRequest.setCustomerName("Validation Name");

        // Act
        Response response = OrdersApi.updateOrder(getToken(), "non_existent_order_id_12345", updateRequest);

        // Assert
        assertErrorResponse(response, 404, "No order with id");
    }

    @Test
    public void testUpdateOrderBelongingToDifferentClient() {
        // Arrange - Register other client and place an order
        String otherToken = ClientSteps.registerClientAndGetToken();
        
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        OrderRequest orderRequest = new OrderRequest(cartId, "Original Name");
        Response createResponse = OrdersApi.createOrder(otherToken, orderRequest);
        assertEquals(createResponse.getStatusCode(), 201);
        String orderId = createResponse.as(OrderResponse.class).getOrderId();

        // Prepare update body
        OrderRequest updateRequest = new OrderRequest();
        updateRequest.setCustomerName("Attempted Hack Name");

        // Act - Attempt to update other client's order using our token
        Response response = OrdersApi.updateOrder(getToken(), orderId, updateRequest);

        // Assert - Should return 404 (No order with id)
        assertErrorResponse(response, 404, "No order with id");

        // Clean up other client's order
        OrdersApi.deleteOrder(otherToken, orderId);
    }
}

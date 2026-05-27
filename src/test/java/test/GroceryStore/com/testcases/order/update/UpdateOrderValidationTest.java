package test.GroceryStore.com.testcases.order.update;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.OrdersApi;
import test.GroceryStore.com.models.order.Order;
import test.GroceryStore.com.models.order.OrderRequest;
import test.GroceryStore.com.models.order.OrderResponse;
import test.GroceryStore.com.models.product.Product;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.steps.ClientSteps;
import test.GroceryStore.com.steps.OrderSteps;
import test.GroceryStore.com.testcases.BaseTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UpdateOrderValidationTest extends BaseTest {

    @Test
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

    @Test
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

    @Test
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

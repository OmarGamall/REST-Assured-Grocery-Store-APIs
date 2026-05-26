package test.GroceryStore.com.testcases.order.delete;

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

public class DeleteOrderValidationTest extends BaseTest {

    @Test
    public void testDeleteOrderWithInvalidToken() {
        // Act
        Response response = OrdersApi.deleteOrder("invalid_token_12345", "some-order-id");

        // Assert
        assertErrorResponse(response, 401, "bearer token");
    }

    @Test
    public void testDeleteOrderWithMissingToken() {
        // Act
        Response response = OrdersApi.deleteOrder(null, "some-order-id");
        // Assert
        assertErrorResponse(response, 401, "bearer token");
    }

    @Test
    public void testDeleteSameOrderMultipleTimes() {
        // Arrange - Create an order to delete
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId);

        OrderRequest orderRequest = new OrderRequest(cartId, "Omar Delete Twice");
        Response createResponse = OrdersApi.createOrder(getToken(), orderRequest);
        assertEquals(createResponse.getStatusCode(), 201);
        String orderId = createResponse.as(OrderResponse.class).getOrderId();

        // Act - First deletion attempt
        Response firstDeleteResponse = OrdersApi.deleteOrder(getToken(), orderId);
        assertEquals(firstDeleteResponse.getStatusCode(), 204, "Expected 204 status code for first deletion");

        // Act - Second deletion attempt on the same order
        Response secondDeleteResponse = OrdersApi.deleteOrder(getToken(), orderId);

        // Assert - Should return 404 (No order with id) since it's already deleted
        assertErrorResponse(secondDeleteResponse, 404, "No order with id " + orderId);
    }

    @Test
    public void testDeleteOrderWithInvalidOrderId() {
        // Act
        Response response = OrdersApi.deleteOrder(getToken(), "non_existent_order_id_12345");

        // Assert
        assertErrorResponse(response, 404, "No order with id");
    }

    @Test
    public void testDeleteOrderBelongingToDifferentClient() {
        // Arrange - Register other client and place an order
        String FirstClientToken = ClientSteps.registerClientAndGetToken();
        
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId);

        OrderRequest orderRequest = new OrderRequest(cartId, "Original Name");
        Response createResponse = OrdersApi.createOrder(FirstClientToken, orderRequest);
        assertEquals(createResponse.getStatusCode(), 201);
        // Extract Order Id
        String orderId = createResponse.as(OrderResponse.class).getOrderId();

        // Act - Attempt to delete First client's order using other client's token
        String SecondClientToken = ClientSteps.registerClientAndGetToken();
        Response response = OrdersApi.deleteOrder(SecondClientToken, orderId);

        // Assert - Should return 404 (No order with id)
        assertErrorResponse(response, 404, "No order with id");
    }
}

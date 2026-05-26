package test.GroceryStore.com.testcases.order.get_single;

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

public class GetSingleOrderValidationTest extends BaseTest {

    @Test
    public void testGetSingleOrderWithInvalidToken() {
        // Act
        Response response = OrdersApi.getOrderById("invalid_token_12345", "some-order-id");

        // Assert
        assertErrorResponse(response, 401, "bearer token");
    }

    @Test
    public void testGetSingleOrderWithInvalidOrderId() {
        // Act
        Response response = OrdersApi.getOrderById(getToken(), "non_existent_order_id_12345");

        // Assert
        assertErrorResponse(response, 404, "No order with id");
    }

    @Test
    public void testGetSingleOrderBelongingToDifferentClient() {
        // Arrange - Register another client and place an order under their account
        String FirstClientToken = ClientSteps.registerClientAndGetToken();
        
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId);

        OrderRequest orderRequest = new OrderRequest(cartId, "Other Customer");
        Response createResponse = OrdersApi.createOrder(FirstClientToken, orderRequest);
        assertEquals(createResponse.getStatusCode(), 201);
        // Extract the order ID from the creation response
        String orderId = createResponse.as(OrderResponse.class).getOrderId();

        // Act - Attempt to retrieve other client's order using our main token
        String SecondClientToken = ClientSteps.registerClientAndGetToken();
        Response response = OrdersApi.getOrderById(SecondClientToken, orderId);

        // Assert - Should return 404 Not Found (or 404 No order with id)
        assertErrorResponse(response, 404, "No order with id");
    }
}

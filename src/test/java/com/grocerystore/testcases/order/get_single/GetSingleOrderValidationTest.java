package com.grocerystore.testcases.order.get_single;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import com.grocerystore.apis.OrdersApi;
import com.grocerystore.models.order.OrderRequest;
import com.grocerystore.models.order.OrderResponse;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.steps.ClientSteps;
import com.grocerystore.testcases.BaseTest;

import static org.testng.Assert.assertEquals;

@Test(groups = {"orders", "validation"})
public class GetSingleOrderValidationTest extends BaseTest {

    @Test(groups = {"regression"}, description = "TC_ORDER_020: Verify error when retrieving single order with invalid token")
    public void testGetSingleOrderWithInvalidToken() {
        // Act
        Response response = OrdersApi.getOrderById("invalid_token_12345", "some-order-id");

        // Assert
        assertErrorResponse(response, 401, "bearer token");
    }

    @Test(groups = {"regression"}, description = "TC_ORDER_021: Verify error when retrieving order with non-existent orderId")
    public void testGetSingleOrderWithInvalidOrderId() {
        // Act
        Response response = OrdersApi.getOrderById(getToken(), "non_existent_order_id_12345");

        // Assert
        assertErrorResponse(response, 404, "No order with id");
    }

    @Test(groups = {"regression"}, description = "TC_ORDER_022: Verify error when retrieving order belonging to another client")
    public void testGetSingleOrderBelongingToDifferentClient() {
        // Arrange - Register another client and place an order under their account
        String FirstClientToken = ClientSteps.registerClientAndGetToken();
        
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId);

        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName("Other Customer")
                .build();
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

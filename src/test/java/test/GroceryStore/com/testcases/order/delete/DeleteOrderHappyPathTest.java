package test.GroceryStore.com.testcases.order.delete;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.OrdersApi;
import test.GroceryStore.com.models.order.OrderRequest;
import test.GroceryStore.com.models.order.OrderResponse;
import test.GroceryStore.com.models.product.Product;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.testcases.BaseTest;

import static org.testng.Assert.assertEquals;

public class DeleteOrderHappyPathTest extends BaseTest {

    @Test
    public void testDeleteOrderSuccessfully() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        OrderRequest orderRequest = new OrderRequest(cartId, "Omar Delete");
        Response createResponse = OrdersApi.createOrder(getToken(), orderRequest);
        assertEquals(createResponse.getStatusCode(), 201);
        String orderId = createResponse.as(OrderResponse.class).getOrderId();

        // Act
        Response response = OrdersApi.deleteOrder(getToken(), orderId);

        // Assert
        assertEquals(response.getStatusCode(), 204, "Expected 204 status code for order deletion");

        // Verify deletion by attempting to retrieve it
        Response getResponse = OrdersApi.getOrderById(getToken(), orderId);
        assertErrorResponse(getResponse, 404, "No order with id " + orderId);
    }
}

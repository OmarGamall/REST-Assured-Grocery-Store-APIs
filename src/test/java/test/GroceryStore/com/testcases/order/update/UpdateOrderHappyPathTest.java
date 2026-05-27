package test.GroceryStore.com.testcases.order.update;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.OrdersApi;
import test.GroceryStore.com.models.cart.CartItem;
import test.GroceryStore.com.models.order.Order;
import test.GroceryStore.com.models.order.OrderRequest;
import test.GroceryStore.com.models.order.OrderResponse;
import test.GroceryStore.com.models.product.Product;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.testcases.BaseTest;

import static org.testng.Assert.*;

public class UpdateOrderHappyPathTest extends BaseTest {

    @Test
    public void testUpdateOrderSuccessfully() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId);

        OrderRequest orderRequest = new OrderRequest(cartId, "Original Name", "Original Comment");
        Response createResponse = OrdersApi.createOrder(getToken(), orderRequest);
        assertEquals(createResponse.getStatusCode(), 201);
        String orderId = createResponse.as(OrderResponse.class).getOrderId();

        // Prepare update request
        String updatedName = "Updated Name";
        String updatedComment = "Updated Comment";
        OrderRequest updateRequest = new OrderRequest();
        updateRequest.setCustomerName(updatedName);
        updateRequest.setComment(updatedComment);

        // Act
        Response updateResponse = OrdersApi.updateOrder(getToken(), orderId, updateRequest);

        // Assert update status
        assertEquals(updateResponse.getStatusCode(), 204, "Expected 204 status code for order update");

        // Verify changes are persisted
        Response getResponse = OrdersApi.getOrderById(getToken(), orderId);
        assertEquals(getResponse.getStatusCode(), 200);
        Order updatedOrder = getResponse.as(Order.class);
        assertEquals(updatedOrder.getCustomerName(), updatedName, "Customer name was not updated");
        assertEquals(updatedOrder.getComment(), updatedComment, "Comment was not updated");

    }
}

package test.GroceryStore.com.testcases.order.get_all;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.OrdersApi;
import test.GroceryStore.com.models.order.Order;
import test.GroceryStore.com.models.order.OrderRequest;
import test.GroceryStore.com.models.order.OrderResponse;
import test.GroceryStore.com.models.product.Product;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.testcases.BaseTest;

import static org.testng.Assert.*;

public class GetAllOrdersHappyPathTest extends BaseTest {

    @Test
    public void testGetAllOrdersSuccessfully() {
        // Arrange - Ensure there is at least one order
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        OrderRequest orderRequest = new OrderRequest(cartId, "Omar GetAll", "Urgent delivery");
        Response createResponse = OrdersApi.createOrder(getToken(), orderRequest);
        assertEquals(createResponse.getStatusCode(), 201, "Expected 201 status code for order creation");
        String orderId = createResponse.as(OrderResponse.class).getOrderId();

        // Act
        Response response = OrdersApi.getAllOrders(getToken());

        // Assert
        assertEquals(response.getStatusCode(), 200, "Expected 200 status code for retrieving all orders");
        Order[] orders = response.as(Order[].class);
        assertNotNull(orders, "Expected non-null array of orders");
        assertTrue(orders.length > 0, "Expected list of orders to contain at least one order");

        // Verify our created order is present in the list
        boolean orderFound = false;
        for (Order order : orders) {
            if (orderId.equals(order.getId())) {
                orderFound = true;
                assertEquals(order.getCustomerName(), "Omar GetAll", "Customer name mismatch in listed order");
                break;
            }
        }
        assertTrue(orderFound, "Expected created order to be found in the orders list");

        // Clean up
        OrdersApi.deleteOrder(getToken(), orderId);
    }
}

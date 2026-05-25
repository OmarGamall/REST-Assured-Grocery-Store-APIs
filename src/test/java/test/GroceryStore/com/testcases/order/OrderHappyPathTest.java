package test.GroceryStore.com.testcases.order;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.OrdersApi;
import test.GroceryStore.com.models.order.*;
import test.GroceryStore.com.models.product.*;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.testcases.BaseTest;

import static org.testng.Assert.*;

public class OrderHappyPathTest extends BaseTest {

    @Test
    public void testCreateRetrieveAndDeleteOrder() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = 1;

        // Add item to cart so it is not empty
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), quantity);

        // 2. Act - Create Order
        OrderRequest orderRequest = new OrderRequest(cartId, "Omar Gamal", "Please pack carefully");
        Response createResponse = OrdersApi.createOrder(getToken(), orderRequest);


        // 3. Assert - Create Order
        assertEquals(createResponse.getStatusCode(), 201, "Expected 201 status code for order creation");
        OrderResponse orderResponse = createResponse.as(OrderResponse.class);
        assertTrue(orderResponse.getCreated(), "Expected 'created' flag to be true");
        String orderId = orderResponse.getOrderId();
        assertNotNull(orderId, "Expected non-null orderId");

        // 4. Act & Assert - Retrieve Single Order
        Response getResponse = OrdersApi.getOrderById(getToken(), orderId);
        assertEquals(getResponse.getStatusCode(), 200, "Expected 200 status code for retrieving order");
        Order order = getResponse.as(Order.class);
        assertEquals(order.getId(), orderId, "Order ID mismatch in response");
        assertEquals(order.getCustomerName(), "Omar Gamal", "Customer name mismatch in response");
        assertEquals(order.getComment(), "Please pack carefully", "Comment mismatch in response");
        assertNotNull(order.getCreated(), "Expected creation timestamp to be non-null");
        
        // Assert items in order
        assertNotNull(order.getItems(), "Expected order items to be present");
        assertEquals(order.getItems().size(), 1, "Expected exactly 1 item in order");
        assertEquals(order.getItems().get(0).getProductId(), product.getId(), "Product ID mismatch in order item");
        assertEquals(order.getItems().get(0).getQuantity(), Integer.valueOf(quantity), "Quantity mismatch in order item");

        // 5. Act & Assert - Retrieve All Orders
        Response listResponse = OrdersApi.getAllOrders(getToken());
        assertEquals(listResponse.getStatusCode(), 200, "Expected 200 status code for retrieving all orders");
        Order[] orders = listResponse.as(Order[].class);
        assertTrue(orders.length > 0, "Expected list of orders to contain at least one order");
        
        // Find our order in the list
        Order foundOrder = null;
        for (Order o : orders) {
            if (orderId.equals(o.getId())) {
                foundOrder = o;
                break;
            }
        }
        assertNotNull(foundOrder, "Expected created order to be found in orders list");
        assertEquals(foundOrder.getCustomerName(), "Omar Gamal", "Customer name mismatch in listed order");

        // 6. Act - Delete Order
        Response deleteResponse = OrdersApi.deleteOrder(getToken(), orderId);

        // 7. Assert - Delete Order
        assertEquals(deleteResponse.getStatusCode(), 204, "Expected 204 status code for successful deletion");

        // 8. Verify Deletion
        Response verifyGetResponse = OrdersApi.getOrderById(getToken(), orderId);
        assertErrorResponse(verifyGetResponse, 404, "No order with id " + orderId);
    }
}

package test.GroceryStore.com.testcases.order.get_all;

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

public class GetAllOrdersHappyPathTest extends BaseTest {

    @Test
    public void testGetAllOrdersForCustomerSuccessfully() {
        // Arrange - Ensure there is at least one order
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId);
        String customerName = FAKER.name().fullName();
        String comment = FAKER.lorem().sentence();

        OrderRequest orderRequest = new OrderRequest(cartId, customerName, comment);
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
                assertEquals(order.getCustomerName(), customerName, "Customer name mismatch in listed order");
                assertEquals(order.getComment(), comment, "Comment mismatch in listed order");
                assertEquals(order.getItems().get(0).getProductId(), cartItem.getProductId(), "Product ID mismatch in listed order");
                assertEquals(order.getItems().get(0).getQuantity(), cartItem.getQuantity(), "Product quantity mismatch in listed order");
                break;
            }
        }
        assertTrue(orderFound, "Expected created order to be found in the orders list");
    }
}

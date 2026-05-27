package test.GroceryStore.com.testcases.order.get_single;

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

public class GetSingleOrderHappyPathTest extends BaseTest {

    @Test
    public void testGetSingleOrderSuccessfully() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId);
        String customerName = FAKER.name().fullName();
        String comment = FAKER.lorem().sentence();
        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName(customerName)
                .comment(comment)
                .build();

        Response createResponse = OrdersApi.createOrder(getToken(), orderRequest);
        assertEquals(createResponse.getStatusCode(), 201);
        // Extract the order ID from the creation response
        String orderId = createResponse.as(OrderResponse.class).getOrderId();

        // Act
        Response response = OrdersApi.getOrderById(getToken(), orderId);

        // Assert
        assertEquals(response.getStatusCode(), 200, "Expected 200 status code for order lookup");
        Order order = response.as(Order.class);
        assertEquals(order.getId(), orderId, "Order ID mismatch in lookup");
        assertEquals(order.getCustomerName(), customerName, "Customer name mismatch");
        assertEquals(order.getComment(), comment, "Comment mismatch");
        assertNotNull(order.getCreated(), "Created timestamp should not be null");
        assertNotNull(order.getItems(), "Order items list should not be null");
        assertEquals(order.getItems().size(), 1);
        assertEquals(order.getItems().get(0).getProductId(), cartItem.getProductId(), "Expected product ID to match");
        assertEquals(order.getItems().get(0).getQuantity(), cartItem.getQuantity(), "Expected quantity to match");
    }

    @Test
    public void testGetSingleOrderInvoiceSuccessfully() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId);
        String customerName = FAKER.name().fullName();
        String comment = FAKER.lorem().sentence();
        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName(customerName)
                .comment(comment)
                .build();

        Response createResponse = OrdersApi.createOrder(getToken(), orderRequest);
        assertEquals(createResponse.getStatusCode(), 201);
        // Extract the order ID from the creation response
        String orderId = createResponse.as(OrderResponse.class).getOrderId();

        // Act
        Response response = OrdersApi.getOrderById(getToken(), orderId, true); // Pass 'true' to request invoice details

        // Assert
        assertEquals(response.getStatusCode(), 200, "Expected 200 status code for order lookup");
        Order order = response.as(Order.class);
        assertEquals(order.getId(), orderId, "Order ID mismatch in lookup");
        assertEquals(order.getCustomerName(), customerName, "Customer name mismatch");
        assertEquals(order.getComment(), comment, "Comment mismatch");
        assertNotNull(order.getCreated(), "Created timestamp should not be null");
        assertNotNull(order.getItems(), "Order items list should not be null");
        assertEquals(order.getItems().size(), 1);
        assertEquals(order.getItems().get(0).getProductId(), cartItem.getProductId(), "Expected product ID to match");
        assertEquals(order.getItems().get(0).getQuantity(), cartItem.getQuantity(), "Expected quantity to match");
        assertNotNull(order.getInvoice(), "Invoice details should be present when requested");
    }
}

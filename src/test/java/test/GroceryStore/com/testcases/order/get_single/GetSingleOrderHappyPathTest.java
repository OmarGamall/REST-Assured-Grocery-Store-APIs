package test.GroceryStore.com.testcases.order.get_single;

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

public class GetSingleOrderHappyPathTest extends BaseTest {

    @Test
    public void testGetSingleOrderSuccessfully() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = ProductService.getRandomQuantity(product);
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), quantity);

        OrderRequest orderRequest = new OrderRequest(cartId, "Omar GetSingle", "Fragile items");
        Response createResponse = OrdersApi.createOrder(getToken(), orderRequest);
        assertEquals(createResponse.getStatusCode(), 201);
        String orderId = createResponse.as(OrderResponse.class).getOrderId();

        // Act
        Response response = OrdersApi.getOrderById(getToken(), orderId);

        // Assert
        assertEquals(response.getStatusCode(), 200, "Expected 200 status code for order lookup");
        Order order = response.as(Order.class);
        assertEquals(order.getId(), orderId, "Order ID mismatch in lookup");
        assertEquals(order.getCustomerName(), "Omar GetSingle", "Customer name mismatch");
        assertEquals(order.getComment(), "Fragile items", "Comment mismatch");
        assertNotNull(order.getCreated(), "Created timestamp should not be null");
        assertNotNull(order.getItems(), "Order items list should not be null");
        assertEquals(order.getItems().size(), 1);
        assertEquals(order.getItems().get(0).getProductId(), product.getId());
        assertEquals(order.getItems().get(0).getQuantity(), Integer.valueOf(quantity));

        // Clean up
        OrdersApi.deleteOrder(getToken(), orderId);
    }

    @Test
    public void testGetSingleOrderInvoiceSuccessfully() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), 1);

        OrderRequest orderRequest = new OrderRequest(cartId, "Omar Invoice");
        Response createResponse = OrdersApi.createOrder(getToken(), orderRequest);
        assertEquals(createResponse.getStatusCode(), 201);
        String orderId = createResponse.as(OrderResponse.class).getOrderId();

        // Act
        Response response = OrdersApi.getOrderById(getToken(), orderId, true);

        // Assert
        assertEquals(response.getStatusCode(), 200, "Expected 200 status code for retrieving PDF invoice");
        // Verify response content-type is pdf or octet-stream (since it's a file download)
        String contentType = response.getHeader("Content-Type");
        assertNotNull(contentType, "Expected non-null Content-Type for invoice");
        assertTrue(contentType.contains("pdf") || contentType.contains("application"), 
                "Expected invoice response Content-Type to match PDF or binary format, but got: " + contentType);

        // Clean up
        OrdersApi.deleteOrder(getToken(), orderId);
    }
}

package com.grocerystore.testcases.order.get_single;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Allure;
import com.grocerystore.apis.OrdersApi;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.order.Order;
import com.grocerystore.models.order.OrderRequest;
import com.grocerystore.models.order.OrderResponse;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.steps.OrderSteps;
import com.grocerystore.testcases.BaseTest;

import static org.testng.Assert.*;

@Test(groups = {"orders", "happy-path"})
public class GetSingleOrderHappyPathTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_ORDER_018: Verify that GET /orders/{orderId} returns 200 OK and order details when retrieving a valid order by ID")
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

        Order createdOrder = OrderSteps.createOrderAndGetOrderDetails(getToken(), orderRequest);
        String orderId = createdOrder.getId();

        // Act
        Response response = Allure.step("Act: Get order by ID: " + orderId, () -> {
            return OrdersApi.getOrderById(getToken(), orderId);
        });

        // Assert
        Allure.step("Assert: Verify response status code is 200 and matches order details schema", () -> {
            assertEquals(response.getStatusCode(), 200, "Expected 200 status code for order lookup");
            assertResponseSchema(response, "schemas/order-schema.json");
            
            Order order = response.as(Order.class);
            assertEquals(order.getId(), orderId, "Order ID mismatch in lookup");
            assertEquals(order.getCustomerName(), customerName, "Customer name mismatch");
            assertEquals(order.getComment(), comment, "Comment mismatch");
            assertNotNull(order.getCreated(), "Created timestamp should not be null");
            assertNotNull(order.getItems(), "Order items list should not be null");
            assertEquals(order.getItems().size(), 1);
            assertEquals(order.getItems().get(0).getProductId(), cartItem.getProductId(), "Expected product ID to match");
            assertEquals(order.getItems().get(0).getQuantity(), cartItem.getQuantity(), "Expected quantity to match");
        });
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_ORDER_019: Verify that GET /orders/{orderId} returns 200 OK and includes invoice details when query parameter invoice is set to true")
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

        Order createdOrder = OrderSteps.createOrderAndGetOrderDetails(getToken(), orderRequest);
        String orderId = createdOrder.getId();

        // Act
        Response response = Allure.step("Act: Get order by ID: " + orderId + " with invoice query parameter", () -> {
            return OrdersApi.getOrderById(getToken(), orderId, true); // Pass 'true' to request invoice details
        });

        // Assert
        Allure.step("Assert: Verify response status code is 200 and invoice details are returned", () -> {
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
        });
    }
}

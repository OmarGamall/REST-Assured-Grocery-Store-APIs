package com.grocerystore.testcases.order.get_all;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import com.grocerystore.apis.OrdersApi;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.order.Order;
import com.grocerystore.models.order.OrderRequest;
import com.grocerystore.models.order.OrderResponse;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;

import static org.testng.Assert.*;

public class GetAllOrdersHappyPathTest extends BaseTest {

    @Test(description = "TC_ORDER_016: Verify retrieving all orders for authenticated client")
    public void testGetAllOrdersForCustomerSuccessfully() {
        // Arrange - Ensure there is at least one order
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

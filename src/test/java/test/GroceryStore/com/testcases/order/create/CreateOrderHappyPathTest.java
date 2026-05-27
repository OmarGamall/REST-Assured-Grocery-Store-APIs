package test.GroceryStore.com.testcases.order.create;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.apis.OrdersApi;
import test.GroceryStore.com.models.cart.CartItem;
import test.GroceryStore.com.models.order.Order;
import test.GroceryStore.com.models.order.OrderRequest;
import test.GroceryStore.com.models.order.OrderResponse;
import test.GroceryStore.com.steps.CartSteps;
import test.GroceryStore.com.testcases.BaseTest;
import test.GroceryStore.com.steps.OrderSteps;

import static org.testng.Assert.*;

public class CreateOrderHappyPathTest extends BaseTest {

    @Test
    public void testCreateOrderSuccessfully() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartItem cartItem = CartSteps.addRandomItemToCart(cartId);
        String customerName = FAKER.name().fullName();
        String comment = FAKER.lorem().sentence();
        OrderRequest orderRequest = new OrderRequest(cartId, customerName, comment);

        // Act
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert - Creation Status first
        assertEquals(response.getStatusCode(), 201, "Expected 201 status code for order creation");
        OrderResponse orderResponse = response.as(OrderResponse.class);
        assertTrue(orderResponse.getCreated(), "Expected 'created' flag to be true");
        assertNotNull(orderResponse.getOrderId(), "Expected a non-null order ID");

        // Verify the cart has been automatically deleted after order placement
        Response getCartResponse = CartApi.getCartById(cartId);
        assertErrorResponse(getCartResponse, 404, "No cart with id " + cartId);

        // Retrieve the order details
        Order createdOrder = OrderSteps.getOrderById(getToken(), orderResponse.getOrderId());

        // Assert - Order details validation
        assertEquals(createdOrder.getId(), orderResponse.getOrderId(), "Expected the created order ID to match the response");
        assertEquals(createdOrder.getCustomerName(), customerName, "Expected customer name to match");
        assertEquals(createdOrder.getComment(), comment, "Expected comment to match");
        assertNotNull(createdOrder.getItems(), "Expected order items to be present");
        assertEquals(createdOrder.getItems().size(), 1, "Expected exactly one item in the order");
        assertEquals(createdOrder.getItems().get(0).getProductId(), cartItem.getProductId(), "Expected product ID to match");
        assertEquals(createdOrder.getItems().get(0).getQuantity(), cartItem.getQuantity(), "Expected quantity to match");
    }

    @Test
    public void testCreateOrderWithCartHasMultipleItemsSuccessfully() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        int numberOfItemsToAdd = 5;
        CartItem[] cartItems = CartSteps.AddMultipleRandomItemsToCart(cartId, numberOfItemsToAdd); // Add 5 random items to the cart
        String customerName = FAKER.name().fullName();
        String comment = FAKER.lorem().sentence();
        OrderRequest orderRequest = new OrderRequest(cartId, customerName, comment);

        // Act
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert - Creation Status first
        assertEquals(response.getStatusCode(), 201, "Expected 201 status code for order creation");
        OrderResponse orderResponse = response.as(OrderResponse.class);
        assertTrue(orderResponse.getCreated(), "Expected 'created' flag to be true");
        assertNotNull(orderResponse.getOrderId(), "Expected a non-null order ID");

        // Verify the cart has been automatically deleted after order placement
        Response getCartResponse = CartApi.getCartById(cartId);
        assertErrorResponse(getCartResponse, 404, "No cart with id " + cartId);

        // Retrieve the order details
        Order createdOrder = OrderSteps.getOrderById(getToken(), orderResponse.getOrderId());
        // Assert - Order details validation
        assertEquals(createdOrder.getId(), orderResponse.getOrderId(), "Expected the created order ID to match the response");
        assertEquals(createdOrder.getCustomerName(), customerName, "Expected customer name to match");
        assertEquals(createdOrder.getComment(), comment, "Expected comment to match");
        assertEquals(createdOrder.getItems().size(), numberOfItemsToAdd, "Expected number of items in the order to match the cart");
        // Validate each item in the order matches the corresponding item in the cart
        for (int i = 0; i < numberOfItemsToAdd; i++) {
            assertEquals(createdOrder.getItems().get(i).getProductId(), cartItems[i].getProductId(), "Expected product ID to match for item " + (i + 1));
            assertEquals(createdOrder.getItems().get(i).getQuantity(), cartItems[i].getQuantity(), "Expected quantity to match for item " + (i + 1));
        }
    }
}

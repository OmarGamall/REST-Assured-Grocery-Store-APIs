package com.grocerystore.testcases.order.update;

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

public class UpdateOrderHappyPathTest extends BaseTest {

    @Test(description = "TC_ORDER_023: Verify updating order customerName and comment")
    public void testUpdateOrderSuccessfully() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId);

        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName("Original Name")
                .comment("Original Comment")
                .build();
        Response createResponse = OrdersApi.createOrder(getToken(), orderRequest);
        assertEquals(createResponse.getStatusCode(), 201);
        String orderId = createResponse.as(OrderResponse.class).getOrderId();

        // Prepare update request
        String updatedName = "Updated Name";
        String updatedComment = "Updated Comment";
        OrderRequest updateRequest = OrderRequest.builder()
                .customerName(updatedName)
                .comment(updatedComment)
                .build();

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

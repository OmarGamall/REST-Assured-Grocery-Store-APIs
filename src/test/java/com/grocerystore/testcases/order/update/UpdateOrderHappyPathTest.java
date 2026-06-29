package com.grocerystore.testcases.order.update;

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
public class UpdateOrderHappyPathTest extends BaseTest {

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_ORDER_023: Verify that PATCH /orders/{orderId} returns 204 No Content and updates the order's customerName and comment fields")
    public void testUpdateOrderSuccessfully() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId);

        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName("Original Name")
                .comment("Original Comment")
                .build();
        Order order = OrderSteps.createOrderAndGetOrderDetails(getToken(), orderRequest);
        String orderId = order.getId();

        // Prepare update request
        String updatedName = "Updated Name";
        String updatedComment = "Updated Comment";
        OrderRequest updateRequest = OrderRequest.builder()
                .customerName(updatedName)
                .comment(updatedComment)
                .build();

        // Act
        Response updateResponse = Allure.step("Act: Update order customerName to '" + updatedName + "' and comment to '" + updatedComment + "'", () -> {
            return OrdersApi.updateOrder(getToken(), orderId, updateRequest);
        });

        // Assert update status
        Allure.step("Assert: Verify response status code is 204 No Content", () -> {
            assertEquals(updateResponse.getStatusCode(), 204, "Expected 204 status code for order update");
        });

        // Verify changes are persisted
        Response getResponse = OrdersApi.getOrderById(getToken(), orderId);
        
        Allure.step("Assert: Verify updated order details are persisted correctly", () -> {
            assertEquals(getResponse.getStatusCode(), 200);
            Order updatedOrder = getResponse.as(Order.class);
            assertEquals(updatedOrder.getCustomerName(), updatedName, "Customer name was not updated");
            assertEquals(updatedOrder.getComment(), updatedComment, "Comment was not updated");
        });
    }
}

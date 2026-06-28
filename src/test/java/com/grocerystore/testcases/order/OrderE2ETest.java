package com.grocerystore.testcases.order;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.grocerystore.apis.OrdersApi;
import com.grocerystore.models.order.*;
import com.grocerystore.models.product.*;
import com.grocerystore.services.ProductService;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;
import com.grocerystore.models.ErrorResponse;

import static org.testng.Assert.*;

@Test(groups = {"orders", "happy-path"})
public class OrderE2ETest extends BaseTest {

    @Test(groups = {"e2e", "regression"}, description = "TC_ORDER_027: Verify end-to-end flow of creating, retrieving, and deleting an order")
    public void testCreateRetrieveAndDeleteOrder() {
        // 1. Arrange
        String cartId = CartSteps.createCartAndGetId();
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = 1;

        // Add item to cart so it is not empty
        CartSteps.addItemToCartAndGetResponse(cartId, product.getId(), quantity);

        // 2. Act - Create Order
        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName("Omar Gamal")
                .comment("Please pack carefully")
                .build();
        Response createResponse = OrdersApi.createOrder(getToken(), orderRequest);

        // 3. Assert - Create Order
        assertEquals(createResponse.getStatusCode(), 201, "Expected 201 status code for order creation");
        OrderResponse orderResponse = createResponse.as(OrderResponse.class);
        
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(orderResponse.getCreated(), "Expected 'created' flag to be true");
        String orderId = orderResponse.getOrderId();
        assertNotNull(orderId, "Expected non-null orderId");

        // 4. Act & Assert - Retrieve Single Order
        Response getResponse = OrdersApi.getOrderById(getToken(), orderId);
        assertEquals(getResponse.getStatusCode(), 200, "Expected 200 status code for retrieving order");
        assertResponseSchema(getResponse, "schemas/order-schema.json");
        Order order = getResponse.as(Order.class);
        softAssert.assertEquals(order.getId(), orderId, "Order ID mismatch in response");
        softAssert.assertEquals(order.getCustomerName(), "Omar Gamal", "Customer name mismatch in response");
        softAssert.assertEquals(order.getComment(), "Please pack carefully", "Comment mismatch in response");
        softAssert.assertNotNull(order.getCreated(), "Expected creation timestamp to be non-null");
        
        // Assert items in order
        softAssert.assertNotNull(order.getItems(), "Expected order items to be present");
        if (order.getItems() != null) {
            softAssert.assertEquals(order.getItems().size(), 1, "Expected exactly 1 item in order");
            if (!order.getItems().isEmpty()) {
                softAssert.assertEquals(order.getItems().get(0).getProductId(), product.getId(), "Product ID mismatch in order item");
                softAssert.assertEquals(order.getItems().get(0).getQuantity(), Integer.valueOf(quantity), "Quantity mismatch in order item");
            }
        }

        // 5. Act & Assert - Retrieve All Orders
        Response listResponse = OrdersApi.getAllOrders(getToken());
        assertEquals(listResponse.getStatusCode(), 200, "Expected 200 status code for retrieving all orders");
        Order[] orders = listResponse.as(Order[].class);
        softAssert.assertTrue(orders.length > 0, "Expected list of orders to contain at least one order");
        
        // Find our order in the list
        Order foundOrder = null;
        if (orders != null) {
            for (Order o : orders) {
                if (orderId.equals(o.getId())) {
                    foundOrder = o;
                    break;
                }
            }
        }
        softAssert.assertNotNull(foundOrder, "Expected created order to be found in orders list");
        if (foundOrder != null) {
            softAssert.assertEquals(foundOrder.getCustomerName(), "Omar Gamal", "Customer name mismatch in listed order");
        }

        // 6. Act - Delete Order
        Response deleteResponse = OrdersApi.deleteOrder(getToken(), orderId);

        // 7. Assert - Delete Order
        softAssert.assertEquals(deleteResponse.getStatusCode(), 204, "Expected 204 status code for successful deletion");

        // 8. Verify Deletion
        Response verifyGetResponse = OrdersApi.getOrderById(getToken(), orderId);
        assertErrorResponse(verifyGetResponse, 404, "No order with id " + orderId);
        
        softAssert.assertAll();
    }
}

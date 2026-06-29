package com.grocerystore.testcases.order;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Allure;
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

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"e2e", "regression"}, description = "TC_ORDER_027: Verify the end-to-end flow of creating a new order, retrieving it by ID, verifying it in the orders list, deleting it, and verifying its deletion returns 404")
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
        Response createResponse = Allure.step("Act: Create order with cart ID: " + cartId, () -> {
            return OrdersApi.createOrder(getToken(), orderRequest);
        });

        // 3. Assert - Create Order
        String orderId = Allure.step("Assert: Verify order is created successfully and obtain ID", () -> {
            assertEquals(createResponse.getStatusCode(), 201, "Expected 201 status code for order creation");
            OrderResponse orderResponse = createResponse.as(OrderResponse.class);
            assertTrue(orderResponse.getCreated(), "Expected 'created' flag to be true");
            String id = orderResponse.getOrderId();
            assertNotNull(id, "Expected non-null orderId");
            return id;
        });

        SoftAssert softAssert = new SoftAssert();

        // 4. Act & Assert - Retrieve Single Order
        Allure.step("Act & Assert: Retrieve order by ID and verify details match request", () -> {
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
        });

        // 5. Act & Assert - Retrieve All Orders
        Allure.step("Act & Assert: Retrieve all orders and verify the created order is listed", () -> {
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
        });

        // 6. Act - Delete Order
        Response deleteResponse = Allure.step("Act: Delete order ID: " + orderId, () -> {
            return OrdersApi.deleteOrder(getToken(), orderId);
        });

        // 7. Assert - Delete Order
        Allure.step("Assert: Verify delete response is 204 No Content", () -> {
            softAssert.assertEquals(deleteResponse.getStatusCode(), 204, "Expected 204 status code for successful deletion");
        });

        // 8. Verify Deletion
        Allure.step("Verify: Retrieve the deleted order and expect 404 error response", () -> {
            Response verifyGetResponse = OrdersApi.getOrderById(getToken(), orderId);
            assertErrorResponse(verifyGetResponse, 404, "No order with id " + orderId);
        });

        softAssert.assertAll();
    }
}

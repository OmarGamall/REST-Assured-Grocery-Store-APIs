package com.grocerystore.testcases.order.get_all;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Allure;
import org.testng.asserts.SoftAssert;
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
public class GetAllOrdersHappyPathTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_ORDER_016: Verify that GET /orders returns 200 OK and an array containing all orders placed by the authenticated API client")
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
        com.grocerystore.models.order.Order createdOrder = OrderSteps.createOrderAndGetOrderDetails(getToken(), orderRequest);
        String orderId = createdOrder.getId();

        // Act
        Response response = Allure.step("Act: Get all orders", () -> {
            return OrdersApi.getAllOrders(getToken());
        });

        // Assert
        Allure.step("Assert: Verify listed orders include the created order with correct details", () -> {
            assertEquals(response.getStatusCode(), 200, "Expected 200 status code for retrieving all orders");
            Order[] orders = response.as(Order[].class);
            assertNotNull(orders, "Expected non-null array of orders");
            assertTrue(orders.length > 0, "Expected list of orders to contain at least one order");

            // Verify our created order is present in the list
            boolean orderFound = false;
            for (Order order : orders) {
                if (orderId.equals(order.getId())) {
                    orderFound = true;
                    SoftAssert softAssert = new SoftAssert();
                    softAssert.assertEquals(order.getCustomerName(), customerName, "Customer name mismatch in listed order");
                    softAssert.assertEquals(order.getComment(), comment, "Comment mismatch in listed order");
                    softAssert.assertEquals(order.getItems().get(0).getProductId(), cartItem.getProductId(), "Product ID mismatch in listed order");
                    softAssert.assertEquals(order.getItems().get(0).getQuantity(), cartItem.getQuantity(), "Product quantity mismatch in listed order");
                    softAssert.assertAll();
                    break;
                }
            }
            assertTrue(orderFound, "Expected created order to be found in the orders list");
        });
    }
}

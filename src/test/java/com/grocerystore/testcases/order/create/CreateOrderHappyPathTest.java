package com.grocerystore.testcases.order.create;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Allure;
import org.testng.asserts.SoftAssert;
import com.grocerystore.apis.CartApi;
import com.grocerystore.apis.OrdersApi;
import com.grocerystore.models.cart.CartItem;
import com.grocerystore.models.order.Order;
import com.grocerystore.models.order.OrderRequest;
import com.grocerystore.models.order.OrderResponse;
import com.grocerystore.steps.CartSteps;
import com.grocerystore.testcases.BaseTest;
import com.grocerystore.steps.OrderSteps;
import com.grocerystore.models.ErrorResponse;

import static org.testng.Assert.*;

@Test(groups = {"orders", "happy-path"})
public class CreateOrderHappyPathTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"smoke"}, description = "TC_ORDER_001: Verify that POST /orders returns 201 Created and orderId when creating an order with one cart item, and that the cart is deleted")
    public void testCreateOrderSuccessfully() {
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

        // Act
        Response response = Allure.step("Act: Create order with 1 item for cart ID: " + cartId, () -> {
            return OrdersApi.createOrder(getToken(), orderRequest);
        });

        // Assert - Creation Status first
        String orderId = Allure.step("Assert: Verify order creation status is 201 and order ID is generated", () -> {
            assertEquals(response.getStatusCode(), 201, "Expected 201 status code for order creation");
            assertResponseSchema(response, "schemas/order-created-schema.json");
            OrderResponse orderResponse = response.as(OrderResponse.class);
            assertTrue(orderResponse.getCreated(), "Expected 'created' flag to be true");
            String id = orderResponse.getOrderId();
            assertNotNull(id, "Expected a non-null order ID");
            return id;
        });

        SoftAssert softAssert = new SoftAssert();

        // Verify the cart has been automatically deleted after order placement
        Allure.step("Assert: Verify cart " + cartId + " is automatically deleted (returns 404)", () -> {
            Response getCartResponse = CartApi.getCartById(cartId);
            softAssert.assertEquals(getCartResponse.getStatusCode(), 404, "Expected status code 404 for deleted cart");
        });

        // Retrieve the order details
        Order createdOrder = OrderSteps.getOrderById(getToken(), orderId);

        // Assert - Order details validation
        Allure.step("Assert: Verify created order details match original customer name and items", () -> {
            softAssert.assertEquals(createdOrder.getId(), orderId, "Expected the created order ID to match the response");
            softAssert.assertEquals(createdOrder.getCustomerName(), customerName, "Expected customer name to match");
            softAssert.assertEquals(createdOrder.getComment(), comment, "Expected comment to match");
            softAssert.assertNotNull(createdOrder.getItems(), "Expected order items to be present");
            if (createdOrder.getItems() != null) {
                softAssert.assertEquals(createdOrder.getItems().size(), 1, "Expected exactly one item in the order");
                if (!createdOrder.getItems().isEmpty()) {
                    softAssert.assertEquals(createdOrder.getItems().get(0).getProductId(), cartItem.getProductId(), "Expected product ID to match");
                    softAssert.assertEquals(createdOrder.getItems().get(0).getQuantity(), cartItem.getQuantity(), "Expected quantity to match");
                }
            }
        });
        softAssert.assertAll();
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_ORDER_002: Verify that POST /orders returns 201 Created and orderId when creating an order with a cart containing multiple unique items, and that the cart is deleted")
    public void testCreateOrderWithCartHasMultipleItemsSuccessfully() {
        // Arrange
        String cartId = CartSteps.createCartAndGetId();
        int numberOfItemsToAdd = 5;
        CartItem[] cartItems = CartSteps.addMultipleRandomItemsToCart(cartId, numberOfItemsToAdd); // Add 5 random items to the cart
        String customerName = FAKER.name().fullName();
        String comment = FAKER.lorem().sentence();
        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName(customerName)
                .comment(comment)
                .build();

        // Act
        Response response = Allure.step("Act: Create order with multiple items for cart ID: " + cartId, () -> {
            return OrdersApi.createOrder(getToken(), orderRequest);
        });

        // Assert - Creation Status first
        String orderId = Allure.step("Assert: Verify order creation status is 201 and order ID is generated", () -> {
            assertEquals(response.getStatusCode(), 201, "Expected 201 status code for order creation");
            OrderResponse orderResponse = response.as(OrderResponse.class);
            assertTrue(orderResponse.getCreated(), "Expected 'created' flag to be true");
            String id = orderResponse.getOrderId();
            assertNotNull(id, "Expected a non-null order ID");
            assertResponseSchema(response, "schemas/order-created-schema.json");
            return id;
        });

        SoftAssert softAssert = new SoftAssert();

        // Verify the cart has been automatically deleted after order placement
        Allure.step("Assert: Verify cart " + cartId + " is automatically deleted (returns 404)", () -> {
            Response getCartResponse = CartApi.getCartById(cartId);
            softAssert.assertEquals(getCartResponse.getStatusCode(), 404, "Expected status code 404 for deleted cart");
        });

        // Retrieve the order details
        Order createdOrder = OrderSteps.getOrderById(getToken(), orderId);

        // Assert - Order details validation
        Allure.step("Assert: Verify multiple items in created order match the cart contents", () -> {
            softAssert.assertEquals(createdOrder.getId(), orderId, "Expected the created order ID to match the response");
            softAssert.assertEquals(createdOrder.getCustomerName(), customerName, "Expected customer name to match");
            softAssert.assertEquals(createdOrder.getComment(), comment, "Expected comment to match");
            softAssert.assertNotNull(createdOrder.getItems(), "Expected order items to be present");
            if (createdOrder.getItems() != null) {
                softAssert.assertEquals(createdOrder.getItems().size(), numberOfItemsToAdd, "Expected number of items in the order to match the cart");
                if (createdOrder.getItems().size() == numberOfItemsToAdd) {
                    // Validate each item in the order matches the corresponding item in the cart
                    for (int i = 0; i < numberOfItemsToAdd; i++) {
                        softAssert.assertEquals(createdOrder.getItems().get(i).getProductId(), cartItems[i].getProductId(), "Expected product ID to match for item " + (i + 1));
                        softAssert.assertEquals(createdOrder.getItems().get(i).getQuantity(), cartItems[i].getQuantity(), "Expected quantity to match for item " + (i + 1));
                    }
                }
            }
        });
        softAssert.assertAll();
    }
}

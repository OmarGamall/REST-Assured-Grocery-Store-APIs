package com.grocerystore.testcases.order.create;

import io.restassured.response.Response;
import org.testng.annotations.Test;
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

    @Test(groups = {"smoke"}, description = "TC_ORDER_001: Verify creating an order with one cart item")
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
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert - Creation Status first
        assertEquals(response.getStatusCode(), 201, "Expected 201 status code for order creation");
        
        // Validate order creation response schema
        assertResponseSchema(response, "schemas/order-created-schema.json");
        
        OrderResponse orderResponse = response.as(OrderResponse.class);
        
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(orderResponse.getCreated(), "Expected 'created' flag to be true");
        String orderId = orderResponse.getOrderId();
        assertNotNull(orderId, "Expected a non-null order ID");

        // Verify the cart has been automatically deleted after order placement
        Response getCartResponse = CartApi.getCartById(cartId);
        softAssert.assertEquals(getCartResponse.getStatusCode(), 404, "Expected status code 404 for deleted cart");
        if (getCartResponse.getStatusCode() == 404) {
            try {
                assertResponseSchema(getCartResponse, "schemas/error-schema.json");
                ErrorResponse errorResponse = getCartResponse.as(ErrorResponse.class);
                softAssert.assertNotNull(errorResponse, "Expected error response body for deleted cart");
                if (errorResponse != null && errorResponse.getError() != null) {
                    softAssert.assertTrue(errorResponse.getError().contains("No cart with id " + cartId),
                            "Expected error message to contain cart deletion details");
                }
            } catch (AssertionError | Exception e) {
                softAssert.fail("Failed to validate error response for deleted cart: " + e.getMessage());
            }
        }

        // Retrieve the order details
        Order createdOrder = OrderSteps.getOrderById(getToken(), orderId);

        // Assert - Order details validation
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
        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, description = "TC_ORDER_002: Verify creating an order with multiple unique cart items")
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
        Response response = OrdersApi.createOrder(getToken(), orderRequest);

        // Assert - Creation Status first
        assertEquals(response.getStatusCode(), 201, "Expected 201 status code for order creation");
        OrderResponse orderResponse = response.as(OrderResponse.class);
        
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(orderResponse.getCreated(), "Expected 'created' flag to be true");
        String orderId = orderResponse.getOrderId();
        assertNotNull(orderId, "Expected a non-null order ID");

        // Validate order creation response schema
        assertResponseSchema(response, "schemas/order-created-schema.json");

        // Verify the cart has been automatically deleted after order placement
        Response getCartResponse = CartApi.getCartById(cartId);
        softAssert.assertEquals(getCartResponse.getStatusCode(), 404, "Expected status code 404 for deleted cart");
        if (getCartResponse.getStatusCode() == 404) {
            try {
                assertResponseSchema(getCartResponse, "schemas/error-schema.json");
                ErrorResponse errorResponse = getCartResponse.as(ErrorResponse.class);
                softAssert.assertNotNull(errorResponse, "Expected error response body for deleted cart");
                if (errorResponse != null && errorResponse.getError() != null) {
                    softAssert.assertTrue(errorResponse.getError().contains("No cart with id " + cartId),
                            "Expected error message to contain cart deletion details");
                }
            } catch (AssertionError | Exception e) {
                softAssert.fail("Failed to validate error response for deleted cart: " + e.getMessage());
            }
        }

        // Retrieve the order details
        Order createdOrder = OrderSteps.getOrderById(getToken(), orderId);
        // Assert - Order details validation
        softAssert.assertEquals(createdOrder.getId(), orderId, "Expected the created order ID to match the response");
        softAssert.assertEquals(createdOrder.getCustomerName(), customerName, "Expected customer name to match");
        softAssert.assertEquals(createdOrder.getComment(), comment, "Expected comment to match");
        softAssert.assertNotNull(createdOrder.getItems(), "Expected order items to be present");
        if (createdOrder.getItems() != null) {
            assertEquals(createdOrder.getItems().size(), numberOfItemsToAdd, "Expected number of items in the order to match the cart");
            if (createdOrder.getItems().size() == numberOfItemsToAdd) {
                // Validate each item in the order matches the corresponding item in the cart
                for (int i = 0; i < numberOfItemsToAdd; i++) {
                    softAssert.assertEquals(createdOrder.getItems().get(i).getProductId(), cartItems[i].getProductId(), "Expected product ID to match for item " + (i + 1));
                    softAssert.assertEquals(createdOrder.getItems().get(i).getQuantity(), cartItems[i].getQuantity(), "Expected quantity to match for item " + (i + 1));
                }
            }
        }
        softAssert.assertAll();
    }
}

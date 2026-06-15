package com.grocerystore.steps;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import com.grocerystore.apis.OrdersApi;
import com.grocerystore.models.order.Order;
import com.grocerystore.models.order.OrderRequest;
import com.grocerystore.models.order.OrderResponse;
import com.grocerystore.utils.TokenManager;

public class OrderSteps {

    public static Order getOrderById(String token, String orderId) {
        Response response = OrdersApi.getOrderById(token, orderId);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to retrieve order: " + response.getStatusLine());
        }
        return response.as(Order.class);
    }

    public static Order createOrderAndGetOrderDetails(String token, OrderRequest order) {
        Response response = OrdersApi.createOrder(token, order);
        if (response.getStatusCode() != 201) {
            throw new RuntimeException("Failed to create order: " + response.getStatusLine());
        }
        OrderResponse orderResponse = response.as(OrderResponse.class);
        if (orderResponse.getOrderId() == null) {
            throw new RuntimeException("Order ID is null in order creation response");
        }
        return getOrderById(token, orderResponse.getOrderId());
    }

    public static Order createOrderAndGetOrderDetails(String token, String cartId, String customerName) {
        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName(customerName)
                .build();
        return createOrderAndGetOrderDetails(token, orderRequest);
    }

    public static Order createOrderAndGetOrderDetails() {
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId);
        Faker faker = new Faker();
        String customerName = faker.name().fullName();
        String token = TokenManager.getToken();
        OrderRequest orderRequest = OrderRequest.builder()
                .cartId(cartId)
                .customerName(customerName)
                .build();
        return createOrderAndGetOrderDetails(token, orderRequest);
    }

    public static String createRandomOrderAndGetId() {
        return createOrderAndGetOrderDetails().getId();
    }
}

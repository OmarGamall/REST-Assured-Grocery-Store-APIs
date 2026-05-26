package test.GroceryStore.com.steps;

import test.GroceryStore.com.apis.OrdersApi;
import test.GroceryStore.com.models.order.Order;
import test.GroceryStore.com.models.order.OrderRequest;
import test.GroceryStore.com.models.order.OrderResponse;

public class OrderSteps {

    public static Order getOrderById(String token, String orderId) {
        return OrdersApi.getOrderById(token, orderId).as(Order.class);
    }

    public static Order createOrderAndGetOrderDetails(String token, OrderRequest order) {
        OrdersApi.createOrder(token, order).as(OrderResponse.class);
        return getOrderById(token, order.getCartId());
    }
}

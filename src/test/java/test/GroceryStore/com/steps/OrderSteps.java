package test.GroceryStore.com.steps;

import com.github.javafaker.Faker;
import test.GroceryStore.com.apis.OrdersApi;
import test.GroceryStore.com.models.order.Order;
import test.GroceryStore.com.models.order.OrderRequest;
import test.GroceryStore.com.models.order.OrderResponse;
import test.GroceryStore.com.utils.TokenManager;

public class OrderSteps {

    public static Order getOrderById(String token, String orderId) {
        return OrdersApi.getOrderById(token, orderId).as(Order.class);
    }

    public static Order createOrderAndGetOrderDetails(String token, OrderRequest order) {
        OrderResponse orderResponse = OrdersApi.createOrder(token, order).as(OrderResponse.class);
        return getOrderById(token, orderResponse.getOrderId());
    }

    public static Order createOrderAndGetOrderDetails(String token, String cartId, String customerName) {
        OrderRequest orderRequest = new OrderRequest(cartId, customerName);
        return createOrderAndGetOrderDetails(token, orderRequest);
    }

    public static Order createOrderAndGetOrderDetails() {
        String cartId = CartSteps.createCartAndGetId();
        CartSteps.addRandomItemToCart(cartId);
        Faker faker = new Faker();
        String customerName = faker.name().fullName();
        String token = TokenManager.getToken();
        OrderRequest orderRequest = new OrderRequest(cartId, customerName);
        return createOrderAndGetOrderDetails(token, orderRequest);
    }

}

package com.grocerystore.apis;

import io.restassured.response.Response;
import com.grocerystore.models.order.OrderRequest;
import com.grocerystore.utils.RestHelper;
import io.qameta.allure.Step;

public class OrdersApi extends BaseApi {

    @Step("API: Create order for cart ID: {body.cartId} and customer: {body.customerName}")
    public static Response createOrder(String token, OrderRequest body) {
        return RestHelper.build()
                .endpoint(Routes.ORDERS_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .post();
    }

    @Step("API: Get all orders")
    public static Response getAllOrders(String token) {
        return RestHelper.build()
                .endpoint(Routes.ORDERS_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .get();
    }

    @Step("API: Get order by ID: {orderId}")
    public static Response getOrderById(String token, String orderId) {
        return RestHelper.build()
                .endpoint(Routes.ORDER_BY_ID_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .pathParam("orderId", orderId)
                .get();
    }

    @Step("API: Get order by ID: {orderId} (include invoice: {invoice})")
    public static Response getOrderById(String token, String orderId, Boolean invoice) {
        return RestHelper.build()
                .endpoint(Routes.ORDER_BY_ID_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .pathParam("orderId", orderId)
                .queryParam("invoice", invoice)
                .get();
    }

    @Step("API: Update order ID: {orderId} (customer: {body.customerName})")
    public static Response updateOrder(String token, String orderId, OrderRequest body) {
        return RestHelper.build()
                .endpoint(Routes.ORDER_BY_ID_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .pathParam("orderId", orderId)
                .body(body)
                .patch();
    }

    @Step("API: Delete order ID: {orderId}")
    public static Response deleteOrder(String token, String orderId) {
        return RestHelper.build()
                .endpoint(Routes.ORDER_BY_ID_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .pathParam("orderId", orderId)
                .delete();
    }
}


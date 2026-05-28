package test.GroceryStore.com.apis;

import io.restassured.response.Response;
import test.GroceryStore.com.models.order.OrderRequest;
import test.GroceryStore.com.utils.RestHelper;

public class OrdersApi extends BaseApi {

    public static Response createOrder(String token, OrderRequest body) {
        return RestHelper.build()
                .endpoint(Routes.ORDERS_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .post();
    }

    public static Response getAllOrders(String token) {
        return RestHelper.build()
                .endpoint(Routes.ORDERS_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .get();
    }

    public static Response getOrderById(String token, String orderId) {
        return RestHelper.build()
                .endpoint(Routes.ORDER_BY_ID_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .pathParam("orderId", orderId)
                .get();
    }

    public static Response getOrderById(String token, String orderId, Boolean invoice) {
        return RestHelper.build()
                .endpoint(Routes.ORDER_BY_ID_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .pathParam("orderId", orderId)
                .queryParam("invoice", invoice)
                .get();
    }

    public static Response updateOrder(String token, String orderId, OrderRequest body) {
        return RestHelper.build()
                .endpoint(Routes.ORDER_BY_ID_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .pathParam("orderId", orderId)
                .body(body)
                .patch();
    }

    public static Response deleteOrder(String token, String orderId) {
        return RestHelper.build()
                .endpoint(Routes.ORDER_BY_ID_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .pathParam("orderId", orderId)
                .delete();
    }
}


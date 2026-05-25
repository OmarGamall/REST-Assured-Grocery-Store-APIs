package test.GroceryStore.com.apis;

import io.restassured.response.Response;
import test.GroceryStore.com.models.OrderRequest;

import static io.restassured.RestAssured.given;

public class OrdersApi extends BaseApi {

    public static Response createOrder(String token, OrderRequest body) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .when()
                .post(Routes.ORDERS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response getAllOrders(String token) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(Routes.ORDERS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response getOrderById(String token, String orderId) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("orderId", orderId)
                .when()
                .get(Routes.ORDER_BY_ID_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response getOrderById(String token, String orderId, Boolean invoice) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("orderId", orderId)
                .queryParam("invoice", invoice)
                .when()
                .get(Routes.ORDER_BY_ID_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response updateOrder(String token, String orderId, OrderRequest body) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("orderId", orderId)
                .body(body)
                .when()
                .patch(Routes.ORDER_BY_ID_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response deleteOrder(String token, String orderId) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("orderId", orderId)
                .when()
                .delete(Routes.ORDER_BY_ID_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }
}

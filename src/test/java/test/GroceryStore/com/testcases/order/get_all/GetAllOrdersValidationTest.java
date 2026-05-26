package test.GroceryStore.com.testcases.order.get_all;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.OrdersApi;
import test.GroceryStore.com.testcases.BaseTest;

public class GetAllOrdersValidationTest extends BaseTest {

    @Test
    public void testGetAllOrdersWithInvalidToken() {
        // Act
        Response response = OrdersApi.getAllOrders("invalid_token_12345");

        // Assert
        assertErrorResponse(response, 401, "bearer token");
    }
}

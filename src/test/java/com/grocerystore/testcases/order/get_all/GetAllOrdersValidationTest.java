package com.grocerystore.testcases.order.get_all;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import com.grocerystore.apis.OrdersApi;
import com.grocerystore.testcases.BaseTest;

@Test(groups = {"orders", "validation"})
public class GetAllOrdersValidationTest extends BaseTest {

    @Test(groups = {"regression"}, description = "TC_ORDER_017: Verify error when retrieving all orders with invalid token")
    public void testGetAllOrdersWithInvalidToken() {
        // Act
        Response response = OrdersApi.getAllOrders("invalid_token_12345");

        // Assert
        assertErrorResponse(response, 401, "bearer token");
    }
}

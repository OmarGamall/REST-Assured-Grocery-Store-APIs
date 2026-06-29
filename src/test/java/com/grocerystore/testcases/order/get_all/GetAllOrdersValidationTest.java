package com.grocerystore.testcases.order.get_all;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import com.grocerystore.apis.OrdersApi;
import com.grocerystore.testcases.BaseTest;
import static com.grocerystore.constants.ErrorMessages.*;

@Test(groups = {"orders", "validation"})
public class GetAllOrdersValidationTest extends BaseTest {    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_ORDER_017: Verify that GET /orders returns 401 Unauthorized and validation error when an invalid bearer token is used")
    public void testGetAllOrdersWithInvalidToken() {
        // Act
        Response response = OrdersApi.getAllOrders("invalid_token_12345");

        // Assert
        assertErrorResponse(response, 401, BEARER_TOKEN);
    }
}

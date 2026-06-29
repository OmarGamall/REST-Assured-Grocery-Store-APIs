package com.grocerystore.testcases.product.get_product_by_id;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import com.grocerystore.apis.ProductApi;
import com.grocerystore.testcases.BaseTest;
import static com.grocerystore.constants.ErrorMessages.*;

@Test(groups = {"products", "validation"})
public class GetProductByIdValidationTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_PROD_011: Verify that GET /products/{productId} returns 404 Not Found and error message when a non-existent product ID is requested")
    public void testGetSingleProductByInvalidId() {
        int productId = 9999; // Assuming this ID does not exist
        Response response = ProductApi.getProductById(productId);
        
        // Verify response code and error details
        assertErrorResponse(response, 404, NO_PRODUCT_WITH_ID + " " + productId);
    }
}

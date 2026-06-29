package com.grocerystore.testcases.product.get_products;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import com.grocerystore.apis.ProductApi;
import com.grocerystore.models.product.ProductsQueryParams;
import com.grocerystore.testcases.BaseTest;
import static com.grocerystore.constants.ErrorMessages.*;

@Test(groups = {"products", "validation"})
public class GetProductsValidationTest extends BaseTest {

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_PROD_007: Verify that GET /products returns 400 Bad Request and validation error when an invalid category query parameter is provided")
    public void testGetProductsWithInvalidCategory() {
        Response response = ProductApi.getAllProducts("invalid-category", null, null);
        
        // Verify response code and error details
        assertErrorResponse(response, 400, INVALID_CATEGORY_QUERY_PARAM);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_PROD_008: Verify that GET /products returns 400 Bad Request and validation error when results query parameter is below 0 (e.g., -1)")
    public void testGetProductsWithResultsBelowBounds() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(-1); // Invalid results value
        Response response = ProductApi.getAllProducts(queryParams);
        
        // Verify response code and error details
        assertErrorResponse(response, 400, INVALID_RESULTS_QUERY_PARAM);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_PROD_009: Verify that GET /products returns 400 Bad Request and validation error when results query parameter exceeds 20 (e.g., 1000)")
    public void testGetProductsWithResultsExceedingBounds() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(1000); // Exceeding total products
        Response response = ProductApi.getAllProducts(queryParams);
        
        // Verify response code and error details
        assertErrorResponse(response, 400, INVALID_RESULTS_QUERY_PARAM);
    }
}

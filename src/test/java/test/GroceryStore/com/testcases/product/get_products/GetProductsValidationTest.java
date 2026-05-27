package test.GroceryStore.com.testcases.product.get_products;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.ProductApi;
import test.GroceryStore.com.models.product.ProductsQueryParams;
import test.GroceryStore.com.testcases.BaseTest;

public class GetProductsValidationTest extends BaseTest {

    @Test(description = "TC_PROD_007: Verify error when category is invalid")
    public void testGetProductsWithInvalidCategory() {
        Response response = ProductApi.getAllProducts("invalid-category", null, null);
        
        // Verify response code and error details
        assertErrorResponse(response, 400, "Invalid value for query parameter 'category'");
    }

    @Test(description = "TC_PROD_008: Verify error when results parameter is below 0")
    public void testGetProductsWithResultsBelowBounds() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(-1); // Invalid results value
        Response response = ProductApi.getAllProducts(queryParams);
        
        // Verify response code and error details
        assertErrorResponse(response, 400, "Invalid value for query parameter 'results'");
    }

    @Test(description = "TC_PROD_009: Verify error when results parameter exceeds 20")
    public void testGetProductsWithResultsExceedingBounds() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(1000); // Exceeding total products
        Response response = ProductApi.getAllProducts(queryParams);
        
        // Verify response code and error details
        assertErrorResponse(response, 400, "Invalid value for query parameter 'results'");
    }
}

package test.GroceryStore.com.testcases.product.get_product_by_id;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.ProductApi;
import test.GroceryStore.com.testcases.BaseTest;

public class GetProductByIdValidationTest extends BaseTest {

    @Test
    public void testGetSingleProductByInvalidId() {
        int productId = 9999; // Assuming this ID does not exist
        Response response = ProductApi.getProductById(productId);
        
        // Verify response code and error details
        assertErrorResponse(response, 404, "No product with id " + productId);
    }
}

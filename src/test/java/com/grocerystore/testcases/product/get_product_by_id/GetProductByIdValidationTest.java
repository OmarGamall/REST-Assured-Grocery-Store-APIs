package com.grocerystore.testcases.product.get_product_by_id;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import com.grocerystore.apis.ProductApi;
import com.grocerystore.testcases.BaseTest;

public class GetProductByIdValidationTest extends BaseTest {

    @Test(description = "TC_PROD_011: Verify error when retrieving product with non-existent ID")
    public void testGetSingleProductByInvalidId() {
        int productId = 9999; // Assuming this ID does not exist
        Response response = ProductApi.getProductById(productId);
        
        // Verify response code and error details
        assertErrorResponse(response, 404, "No product with id " + productId);
    }
}

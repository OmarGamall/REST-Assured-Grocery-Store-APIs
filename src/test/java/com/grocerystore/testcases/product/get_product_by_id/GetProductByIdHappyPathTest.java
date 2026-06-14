package com.grocerystore.testcases.product.get_product_by_id;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import com.grocerystore.apis.ProductApi;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.testcases.BaseTest;

import static org.testng.Assert.*;

@Test(groups = {"products", "happy-path"})
public class GetProductByIdHappyPathTest extends BaseTest {

    @Test(groups = {"smoke"}, description = "TC_PROD_010: Verify retrieving product details by valid ID")
    public void testGetSingleProductById() {
        Product product = ProductService.getRandomAvailableProduct();
        Response response = ProductApi.getProductById(product.getId());
        
        // Verify the response status code
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of product");
        
        // Validate the response JSON schema
        assertResponseSchema(response, "schemas/product-schema.json");
        
        // Deserialize the response to a Product object
        Product responseProduct = response.as(Product.class);
        
        // Test the Response Body
        assertEquals(responseProduct.getId(), product.getId(), "Product ID is not correct");
        assertEquals(responseProduct.getCategory(), product.getCategory(), "Product category is not correct");
        assertEquals(responseProduct.getName(), product.getName(), "Product name is not correct");
        assertTrue(responseProduct.isInStock(), "Product stock status is not correct");
    }
}

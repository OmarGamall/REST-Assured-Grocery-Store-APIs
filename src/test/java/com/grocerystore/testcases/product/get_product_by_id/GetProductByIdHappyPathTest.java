package com.grocerystore.testcases.product.get_product_by_id;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Allure;
import org.testng.asserts.SoftAssert;
import com.grocerystore.apis.ProductApi;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;
import com.grocerystore.testcases.BaseTest;

import static org.testng.Assert.*;

@Test(groups = {"products", "happy-path"})
public class GetProductByIdHappyPathTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"smoke"}, description = "TC_PROD_010: Verify that GET /products/{productId} returns 200 OK and product details when a valid product ID is requested")
    public void testGetSingleProductById() {
        Product product = ProductService.getRandomAvailableProduct();
        
        // Act
        Response response = Allure.step("Act: Get product by ID: " + product.getId(), () -> {
            return ProductApi.getProductById(product.getId());
        });

        // Assert
        Allure.step("Assert: Verify response status code is 200 and matches product schema", () -> {
            assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of product");
            assertResponseSchema(response, "schemas/product-schema.json");
            
            // Deserialize the response to a Product object
            Product responseProduct = response.as(Product.class);
            
            // Test the Response Body
            SoftAssert softAssert = new SoftAssert();
            softAssert.assertEquals(responseProduct.getId(), product.getId(), "Product ID is not correct");
            softAssert.assertEquals(responseProduct.getCategory(), product.getCategory(), "Product category is not correct");
            softAssert.assertEquals(responseProduct.getName(), product.getName(), "Product name is not correct");
            softAssert.assertTrue(responseProduct.isInStock(), "Product stock status is not correct");
            softAssert.assertAll();
        });
    }
}

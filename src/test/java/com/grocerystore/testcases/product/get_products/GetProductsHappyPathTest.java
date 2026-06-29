package com.grocerystore.testcases.product.get_products;

import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Allure;
import org.testng.asserts.SoftAssert;
import com.grocerystore.apis.ProductApi;
import com.grocerystore.models.product.Product;
import com.grocerystore.models.product.ProductCategory;
import com.grocerystore.models.product.ProductsQueryParams;
import com.grocerystore.services.ProductService;
import com.grocerystore.testcases.BaseTest;

import static org.testng.Assert.*;

@Test(groups = {"products", "happy-path"})
public class GetProductsHappyPathTest extends BaseTest {

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"smoke"}, description = "TC_PROD_001: Verify that GET /products returns 200 OK and only in-stock products when available query parameter is set to true")
    public void testGetAllAvailableProducts() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setAvailable(true);
        
        // Act
        Response response = Allure.step("Act: Get all available products", () -> {
            return ProductApi.getAllProducts(queryParams);
        });

        // Assert
        Allure.step("Assert: Verify response status code is 200 and all products are in stock", () -> {
            assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of products");
            assertResponseSchema(response, "schemas/products-list-schema.json");
            
            Product[] products = response.as(Product[].class);
            assertNotNull(products, "Expected non-null products array");
            assertTrue(products.length > 0, "Expected at least one product in the response");
            
            SoftAssert softAssert = new SoftAssert();
            for (Product product : products) {
                softAssert.assertTrue(product.isInStock(), "Expected product ID " + product.getId() + " to be in stock");
            }
            softAssert.assertAll();
        });
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_PROD_002: Verify that GET /products returns 200 OK and only out-of-stock products when available query parameter is set to false")
    public void testGetAllNonAvailableProducts() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setAvailable(false);
        
        // Act
        Response response = Allure.step("Act: Get all out-of-stock products", () -> {
            return ProductApi.getAllProducts(queryParams);
        });

        // Assert
        Allure.step("Assert: Verify response status code is 200 and all products are out of stock", () -> {
            assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of products");
            Product[] products = response.as(Product[].class);
            assertNotNull(products, "Expected non-null products array");
            assertTrue(products.length > 0, "Expected at least one product in the response");
            
            SoftAssert softAssert = new SoftAssert();
            for (Product product : products) {
                softAssert.assertFalse(product.isInStock(), "Expected product ID " + product.getId() + " to be out of stock");
            }
            softAssert.assertAll();
        });
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "categoriesProvider", groups = {"regression"}, description = "TC_PROD_003: Verify that GET /products returns 200 OK and products belonging to the specified category when category query parameter is provided")
    public void testGetProductsByCategory(ProductCategory category) {
        // Act
        Product[] products = Allure.step("Act: Get products for category: " + category.getValue(), () -> {
            return ProductService.getAllProductsForGivenCategory(category);
        });

        // Assert
        Allure.step("Assert: Verify category of all products matches '" + category.getValue() + "'", () -> {
            assertNotNull(products, "Expected non-null products array");
            SoftAssert softAssert = new SoftAssert();
            for (Product product : products) {
                softAssert.assertEquals(product.getCategory(), category.getValue(), "Expected product ID " + product.getId() + " category to be '" + category.getValue() + "'");
            }
            softAssert.assertAll();
        });
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_PROD_004: Verify that GET /products returns 200 OK and limits the number of products to the value of results query parameter when results parameter is between 1 and 20")
    public void testGetProductsWithLimit() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(5);
        
        // Act
        Response response = Allure.step("Act: Get products with results limit: 5", () -> {
            return ProductApi.getAllProducts(queryParams);
        });

        // Assert
        Allure.step("Assert: Verify response status code is 200 and number of products is <= 5", () -> {
            assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of products");
            Product[] products = response.as(Product[].class);
            assertNotNull(products, "Expected non-null products array");
            assertTrue(products.length <= 5, "Expected at most 5 products in the response");
        });
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"}, description = "TC_PROD_005: Verify that GET /products returns 200 OK and filters products by category, availability, and limit when category, available, and results query parameters are provided")
    public void testGetAvailableProductsByCategoryWithLimit() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setCategory(ProductCategory.FRESH_PRODUCE);
        queryParams.setAvailable(true);
        queryParams.setResults(3);
        
        // Act
        Response response = Allure.step("Act: Get products for category: fresh-produce, available: true, limit: 3", () -> {
            return ProductApi.getAllProducts(queryParams);
        });

        // Assert
        Allure.step("Assert: Verify response filters match category, availability, and results limit", () -> {
            assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of products");
            Product[] products = response.as(Product[].class);
            assertNotNull(products, "Expected non-null products array");
            assertTrue(products.length <= 3, "Expected at most 3 products in the response");
            
            SoftAssert softAssert = new SoftAssert();
            for (Product product : products) {
                softAssert.assertEquals(product.getCategory(), ProductCategory.FRESH_PRODUCE.getValue(), "Expected product ID " + product.getId() + " category to be 'fresh-produce'");
                softAssert.assertTrue(product.isInStock(), "Expected product ID " + product.getId() + " to be in stock");
            }
            softAssert.assertAll();
        });
    }

    @Severity(SeverityLevel.MINOR)
    @Test(groups = {"regression"}, description = "TC_PROD_006: Verify that GET /products returns 200 OK and zero products when results query parameter is set to 0")
    public void testGetProductsWithZeroResults() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(0);
        
        // Act
        Response response = Allure.step("Act: Get products with results limit: 0", () -> {
            return ProductApi.getAllProducts(queryParams);
        });

        // Assert
        Allure.step("Assert: Verify response status code is 200 and products list is empty", () -> {
            assertEquals(response.getStatusCode(), 200, "Expected status code 200 for zero results parameter");
            Product[] products = response.as(Product[].class);
            assertNotNull(products, "Expected non-null products array");
            assertEquals(products.length, 0, "Expected zero products in the response");
        });
    }

    // ==========================================
    // DATA PROVIDERS
    // ==========================================
    @DataProvider(name = "categoriesProvider")
    public Object[][] categoriesProvider() {
        ProductCategory[] categories = ProductCategory.values();
        Object[][] data = new Object[categories.length][1];
        for (int i = 0; i < categories.length; i++) {
            data[i][0] = categories[i];
        }
        return data;
    }
}

package test.GroceryStore.com.testcases.product.get_products;

import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.ProductApi;
import test.GroceryStore.com.models.product.Product;
import test.GroceryStore.com.models.product.ProductCategory;
import test.GroceryStore.com.models.product.ProductsQueryParams;
import test.GroceryStore.com.services.ProductService;
import test.GroceryStore.com.testcases.BaseTest;

import static org.testng.Assert.*;

public class GetProductsHappyPathTest extends BaseTest {

    @Test
    public void testGetAllAvailableProducts() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setAvailable(true);
        Response response = ProductApi.getAllProducts(queryParams);
        
        // Verify the response status code
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of products");
        
        // Deserialize the response to an array of Product objects
        Product[] products = response.as(Product[].class);
        
        // Verify that the products array is not null and has at least one product
        assertNotNull(products, "Expected non-null products array");
        assertTrue(products.length > 0, "Expected at least one product in the response");
        
        // Verify that all products in the response are available
        for (Product product : products) {
            assertTrue(product.isInStock(), "Expected product to be in stock");
        }
    }

    @Test
    public void testGetAllNonAvailableProducts() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setAvailable(false);
        Response response = ProductApi.getAllProducts(queryParams);
        
        // Verify the response status code
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of products");
        
        // Deserialize the response to an array of Product objects
        Product[] products = response.as(Product[].class);
        
        // Verify that the products array is not null and has at least one product
        assertNotNull(products, "Expected non-null products array");
        assertTrue(products.length > 0, "Expected at least one product in the response");
        
        // Verify that all products in the response are available
        for (Product product : products) {
            assertFalse(product.isInStock(), "Expected product to be in stock");
        }
    }

    @Test(dataProvider = "categoriesProvider")
    public void testGetProductsByCategory(ProductCategory category) {
        Product[] products = ProductService.getAllProductsForGivenCategory(category);
        
        // Verify that the products array is not null
        assertNotNull(products, "Expected non-null products array");
        
        // Verify that all products in the response belong to the specified category
        for (Product product : products) {
            assertEquals(product.getCategory(), category.getValue(), "Expected product category to be '" + category.getValue() + "'");
        }
    }

    @Test
    public void testGetProductsWithLimit() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(5);
        Response response = ProductApi.getAllProducts(queryParams);
        
        // Verify the response status code
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of products");
        
        // Deserialize the response to an array of Product objects
        Product[] products = response.as(Product[].class);
        
        // Verify that the products array is not null and has at most 5 products
        assertNotNull(products, "Expected non-null products array");
        assertTrue(products.length <= 5, "Expected at most 5 products in the response");
    }

    @Test
    public void testGetAvailableProductsByCategoryWithLimit() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setCategory(ProductCategory.FRESH_PRODUCE);
        queryParams.setAvailable(true);
        queryParams.setResults(3);
        Response response = ProductApi.getAllProducts(queryParams);
        
        // Verify the response status code
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of products");
        
        // Deserialize the response to an array of Product objects
        Product[] products = response.as(Product[].class);
        
        // Verify that the products array is not null and has at most 3 products
        assertNotNull(products, "Expected non-null products array");
        assertTrue(products.length <= 3, "Expected at most 3 products in the response");
        
        // Verify that all products in the response belong to the specified category and are available
        for (Product product : products) {
            assertEquals(product.getCategory(), ProductCategory.FRESH_PRODUCE.getValue(), "Expected product category to be 'fresh-produce'");
            assertTrue(product.isInStock(), "Expected product to be in stock");
        }
    }

    @Test
    public void testGetProductsWithZeroResults() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(0); // Zero results value
        Response response = ProductApi.getAllProducts(queryParams);
        
        // Verify the response status code
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for zero results parameter");
        
        // Deserialize the response to an array of Product objects
        Product[] products = response.as(Product[].class);
        
        // Verify that the products array is not null and has zero products
        assertNotNull(products, "Expected non-null products array");
        assertEquals(products.length, 0, "Expected zero products in the response");
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

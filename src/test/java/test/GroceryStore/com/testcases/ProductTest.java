package test.GroceryStore.com.testcases;

import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.ProductApi;
import test.GroceryStore.com.models.Product;
import test.GroceryStore.com.models.ProductCategory;
import test.GroceryStore.com.models.ProductsQueryParams;
import test.GroceryStore.com.services.ProductService;

import static org.testng.Assert.*;

public class ProductTest extends BaseTest {

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
    public void testGetSingleProductById() {
        Product product = ProductService.getRandomAvailableProduct();
        Response response = ProductApi.getProductById(product.getId());
        
        // Verify the response status code
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of product");
        
        // Deserialize the response to a Product object
        Product responseProduct = response.as(Product.class);
        
        // Test the Response Body
        assertEquals(responseProduct.getId(), product.getId(), "Product ID is not correct");
        assertEquals(responseProduct.getCategory(), product.getCategory(), "Product category is not correct");
        assertEquals(responseProduct.getName(), product.getName(), "Product name is not correct");
        assertTrue(responseProduct.isInStock(), "Product stock status is not correct");
    }

    @Test
    public void testGetSingleProductByInvalidId() {
        int productId = 9999; // Assuming this ID does not exist
        Response response = ProductApi.getProductById(productId);
        
        // Verify response code and error details
        assertErrorResponse(response, 404, "No product with id " + productId);
    }

    @Test
    public void testGetProductsWithInvalidCategory() {
        Response response = ProductApi.getAllProducts("invalid-category", null, null);
        
        // Verify response code and error details
        assertErrorResponse(response, 400, "Invalid value for query parameter 'category'");
    }

    @Test
    public void testGetProductsWithResultsBelowBounds() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(-1); // Invalid results value
        Response response = ProductApi.getAllProducts(queryParams);
        
        // Verify response code and error details
        assertErrorResponse(response, 400, "Invalid value for query parameter 'results'");
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

    @Test
    public void testGetProductsWithResultsExceedingBounds() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(1000); // Exceeding total products
        Response response = ProductApi.getAllProducts(queryParams);
        
        // Verify response code and error details
        assertErrorResponse(response, 400, "Invalid value for query parameter 'results'");
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

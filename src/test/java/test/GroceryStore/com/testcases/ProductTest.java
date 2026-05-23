package test.GroceryStore.com.testcases;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.ProductApi;
import test.GroceryStore.com.models.ErrorResponse;
import test.GroceryStore.com.models.Product;
import test.GroceryStore.com.models.ProductCategory;
import test.GroceryStore.com.models.ProductsQueryParams;
import test.GroceryStore.com.services.ProductService;


public class ProductTest
{
    @Test
    public void testGetAllAvailableProducts() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setAvailable(true);
        Response response = ProductApi.getAllProducts(queryParams);
        // Verify the response status code
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of products");
        // Deserialize the response to an array of Product objects
        Product[] products = response.as(Product[].class);
        // Verify that the products array is not null and has at least one product
        Assert.assertNotNull(products, "Expected non-null products array");
        Assert.assertTrue(products.length > 0, "Expected at least one product in the response");
        // Verify that all products in the response are available
        for (Product product : products) {
            Assert.assertTrue(product.isInStock(), "Expected product to be in stock");
        }
    }

    @Test
    public void testGetAllNonAvailableProducts() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setAvailable(false);
        Response response = ProductApi.getAllProducts(queryParams);
        // Verify the response status code
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of products");
        // Deserialize the response to an array of Product objects
        Product[] products = response.as(Product[].class);
        // Verify that the products array is not null and has at least one product
        Assert.assertNotNull(products, "Expected non-null products array");
        Assert.assertTrue(products.length > 0, "Expected at least one product in the response");
        // Verify that all products in the response are available
        for (Product product : products) {
            Assert.assertFalse(product.isInStock(), "Expected product to be in stock");
        }
    }

    @Test
    public void testGetProductsByCategory() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setCategory(ProductCategory.FRESH_PRODUCE);
        Response response = ProductApi.getAllProducts(queryParams);
        // Verify the response status code
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of products");
        // Deserialize the response to an array of Product objects
        Product[] products = response.as(Product[].class);
        // Verify that the products array is not null and has at least one product
        Assert.assertNotNull(products, "Expected non-null products array");
        Assert.assertTrue(products.length > 0, "Expected at least one product in the response");
        // Verify that all products in the response belong to the specified category
        for (Product product : products) {
            Assert.assertEquals(product.getCategory(), ProductCategory.FRESH_PRODUCE.getValue(), "Expected product category to be 'fresh-produce'");
        }
    }

    @Test
    public void testGetProductsWithLimit() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(5);
        Response response = ProductApi.getAllProducts(queryParams);
        // Verify the response status code
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of products");
        // Deserialize the response to an array of Product objects
        Product[] products = response.as(Product[].class);
        // Verify that the products array is not null and has at most 5 products
        Assert.assertNotNull(products, "Expected non-null products array");
        Assert.assertTrue(products.length <= 5, "Expected at most 5 products in the response");
    }

    @Test
    public void testGetAvailableProductsByCategoryWithLimit() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setCategory(ProductCategory.FRESH_PRODUCE);
        queryParams.setAvailable(true);
        queryParams.setResults(3);
        Response response = ProductApi.getAllProducts(queryParams);
        // Verify the response status code
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of products");
        // Deserialize the response to an array of Product objects
        Product[] products = response.as(Product[].class);
        // Verify that the products array is not null and has at most 3 products
        Assert.assertNotNull(products, "Expected non-null products array");
        Assert.assertTrue(products.length <= 3, "Expected at most 3 products in the response");
        // Verify that all products in the response belong to the specified category and are available
        for (Product product : products) {
            Assert.assertEquals(product.getCategory(), ProductCategory.FRESH_PRODUCE.getValue(), "Expected product category to be 'fresh-produce'");
            Assert.assertTrue(product.isInStock(), "Expected product to be in stock");
        }
    }

    @Test
    public void testGetSingleProductById()
    {
        Product product = ProductService.getRandomAvailableProduct();
        Response response = ProductApi.getProductById(product.getId());
        // Verify the response status code
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 for successful retrieval of product");
        // Deserialize the response to a Product object
        Product responseProduct = response.as(Product.class);
        // Test the Response Body
        Assert.assertEquals(responseProduct.getId(), product.getId(), "Product ID is not correct");
        Assert.assertEquals(responseProduct.getCategory(), product.getCategory(), "Product category is not correct");
        Assert.assertEquals(responseProduct.getName(), product.getName(), "Product name is not correct");
        Assert.assertTrue(responseProduct.isInStock(), "Product stock status is not correct");
    }

    @Test
    public void testGetSingleProductByInvalidId()
    {
        int productId = 9999; // Assuming this ID does not exist
        Response response = ProductApi.getProductById(productId);
        // Deserialize the response to error message
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        // Verify the response status code
        Assert.assertEquals(response.getStatusCode(), 404, "Expected status code 404 for non-existent product");
        // Verify the error message
        Assert.assertTrue(errorResponse.getError().contains("No product with id"), "Expected error message to contain 'Product not found'");
        Assert.assertTrue(errorResponse.getError().contains(Integer.toString(productId)), "Expected error message to contain the invalid product ID");
    }

    @Test
    public void testGetProductsWithInvalidCategory() {
        Response response = ProductApi.getAllProducts("invalid-category", null, null);
        // Deserialize the response to error message
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        // Verify the response status code
        Assert.assertEquals(response.getStatusCode(), 400, "Expected status code 400 for invalid category");
        // Verify the error message
        Assert.assertTrue(errorResponse.getError().contains("Invalid value for query parameter 'category'"), "Expected error message to contain 'Invalid category'");
    }

    @Test
    public void testGetProductsWithResultsBelowBounds() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(-1); // Invalid results value
        Response response = ProductApi.getAllProducts(queryParams);
        // Verify the response status code
        Assert.assertEquals(response.getStatusCode(), 400, "Expected status code 400 for invalid results parameter");
        // Deserialize the response to error message
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        // Verify the error message
        Assert.assertTrue(errorResponse.getError().contains("Invalid value for query parameter 'results'"), "Expected error message to contain 'Invalid results'");
    }

    @Test
    public void testGetProductsWithZeroResults() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(0); // Zero results value
        Response response = ProductApi.getAllProducts(queryParams);
        // Verify the response status code
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 for zero results parameter");
        // Deserialize the response to an array of Product objects
        Product[] products = response.as(Product[].class);
        // Verify that the products array is not null and has zero products
        Assert.assertNotNull(products, "Expected non-null products array");
        Assert.assertEquals(products.length, 0, "Expected zero products in the response");
    }

    @Test
    public void testGetProductsWithResultsExceedingBounds() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setResults(1000); // Exceeding total products
        Response response = ProductApi.getAllProducts(queryParams);
        // Verify the response status code
        Assert.assertEquals(response.getStatusCode(), 400, "Expected status code 400 for results parameter exceeding total products");
        // Deserialize the response to error message
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        // Verify the error message
        Assert.assertTrue(errorResponse.getError().contains("Invalid value for query parameter 'results'"), "Expected error message to contain 'Invalid results'");
    }
}

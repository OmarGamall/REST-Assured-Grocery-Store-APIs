package test.GroceryStore.com;
import static io.restassured.RestAssured.*;
import io.restassured.response.Response;
import test.GroceryStore.com.apis.ProductApi;
import test.GroceryStore.com.models.Product;
import org.testng.Assert;
import org.testng.annotations.Test;
import test.GroceryStore.com.models.ProductsQueryParams;

import java.util.ArrayList;

public class ProductTest {
    @Test
    public void getAllAvailableProducts() {
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
    public void getSingleProductById()
    {
        Product response;

        response = given()
                .baseUri("https://simple-grocery-store-api.click")
                .pathParams("productId", 1225)
                .log().all()
        .when()
                .get("/products/{productId}")
        .then()
                .log().all()
                .statusCode(200)
                .extract().as(Product.class);

        // Test the Response Body
        Assert.assertEquals(response.getId(), 1225, "Product ID is not correct");
        Assert.assertEquals(response.getCategory(), "fresh-produce" , "Product category is not correct");
        Assert.assertEquals(response.getName(), "1/2 in. Brushless Hammer Drill", "Product name is not correct");
        Assert.assertTrue(response.isInStock(), "Product stock status is not correct");


    }
}

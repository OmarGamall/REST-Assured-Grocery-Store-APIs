package test.GroceryStore.com;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import io.restassured.response.Response;
import models.Product;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class ProductTest {
    @Test
    public void getAllAvailableProducts() {
        // 1. Preparation
        Response response = given()
                .baseUri("https://simple-grocery-store-api.click")
                .queryParam("available", true)
                .log().all()
        // 2. Execution
        .when()
                .get("/products")
        // 3. Validation
        .then()
                .log().all()
                .statusCode(200)
                .body("inStock", everyItem(is(true)))
                .extract().response();
        ArrayList<String> s = response.path("name");
        System.out.println(s);

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

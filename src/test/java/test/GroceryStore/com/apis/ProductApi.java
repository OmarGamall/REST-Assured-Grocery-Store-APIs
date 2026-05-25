package test.GroceryStore.com.apis;

import io.restassured.response.Response;
import test.GroceryStore.com.models.product.ProductsQueryParams;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class ProductApi extends BaseApi {

        public static Response getAllProducts(ProductsQueryParams queryParams) {
            // Convert the POJO into a Map cleanly using Jackson
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> mappedParams = mapper.convertValue(queryParams, Map.class);
            return given()
                    .spec(requestSpec)
                    .queryParams(mappedParams)
            .when()
                    .get(Routes.PRODUCTS_ENDPOINT)
            .then()
                    .log().all()
                    .extract().response();
        }

    public static Response getAllProducts(String category, Boolean available, Integer results) {
        return given()
                .spec(requestSpec)
                // If any of these are null, REST Assured completely drops them from the query string!
                .queryParam("category", category)
                .queryParam("available", available)
                .queryParam("results", results)
                .when()
                .get(Routes.PRODUCTS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

        public static Response getProductById(Integer productId) {
            return given()
                    .spec(requestSpec)
                    .pathParam("productId", productId)
            .when()
                    .get(Routes.PRODUCT_BY_ID_ENDPOINT)
            .then()
                    .log().all()
                    .extract().response();
        }
}

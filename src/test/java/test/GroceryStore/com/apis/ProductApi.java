package test.GroceryStore.com.apis;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Optional;
import test.GroceryStore.com.models.ProductsQueryParams;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class ProductApi {

        public static Response getAllProducts(ProductsQueryParams queryParams) {
            // Convert the POJO into a Map cleanly using Jackson
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> mappedParams = mapper.convertValue(queryParams, Map.class);
            return given()
                    .baseUri(Routes.BASE_URI)
                    .queryParams(mappedParams)
                    .contentType(ContentType.JSON)
                    .log().all()
            .when()
                    .get(Routes.PRODUCTS_ENDPOINT)
            .then()
                    .log().all()
                    .extract().response();
        }

    public static Response getAllProducts(String category, Boolean available, Integer results) {
        return given()
                .baseUri(Routes.BASE_URI)
                .contentType(ContentType.JSON)
                // If any of these are null, REST Assured completely drops them from the query string!
                .queryParam("category", category)
                .queryParam("available", available)
                .queryParam("results", results)
                .log().all()
                .when()
                .get(Routes.PRODUCTS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

        public static Response getProductById(int productId) {
            return given()
                    .baseUri(Routes.BASE_URI)
                    .pathParam("productId", productId)
                    .contentType(ContentType.JSON)
                    .log().all()
            .when()
                    .get(Routes.PRODUCT_BY_ID_ENDPOINT)
            .then()
                    .log().all()
                    .extract().response();
        }
}

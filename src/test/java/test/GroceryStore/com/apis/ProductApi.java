package test.GroceryStore.com.apis;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import test.GroceryStore.com.models.ProductsQueryParams;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class ProductApi {
        public static final String BASE_URI = "https://simple-grocery-store-api.click";
        public static final String PRODUCTS_ENDPOINT = "/products";

        public static Response getAllProducts(ProductsQueryParams queryParams) {
            // Convert the POJO into a Map cleanly using Jackson
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> mappedParams = mapper.convertValue(queryParams, Map.class);
            return io.restassured.RestAssured.given()
                    .baseUri(BASE_URI)
                    .queryParams(mappedParams)
                    .contentType(ContentType.JSON)
                    .log().all()
            .when()
                    .get(PRODUCTS_ENDPOINT)
            .then()
                    .log().all()
                    .extract().response();
        }
}

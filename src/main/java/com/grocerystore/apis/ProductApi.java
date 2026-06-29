package com.grocerystore.apis;

import io.restassured.response.Response;
import com.grocerystore.models.product.ProductsQueryParams;
import com.grocerystore.utils.RestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import java.util.Map;

public class ProductApi extends BaseApi {

    @Step("API: Get all products using query parameters filter")
    public static Response getAllProducts(ProductsQueryParams queryParams) {
        // Convert the POJO into a Map cleanly using Jackson
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mappedParams = mapper.convertValue(queryParams, Map.class);
        return RestHelper.build()
                .endpoint(Routes.PRODUCTS_ENDPOINT)
                .queryParams(mappedParams)
                .get();
    }

    @Step("API: Get all products (category: {category}, available: {available}, results: {results})")
    public static Response getAllProducts(String category, Boolean available, Integer results) {
        return RestHelper.build()
                .endpoint(Routes.PRODUCTS_ENDPOINT)
                // If any of these are null, REST Assured completely drops them from the query string!
                .queryParam("category", category)
                .queryParam("available", available)
                .queryParam("results", results)
                .get();
    }

    @Step("API: Get product by ID: {productId}")
    public static Response getProductById(Integer productId) {
        return RestHelper.build()
                .endpoint(Routes.PRODUCT_BY_ID_ENDPOINT)
                .pathParam("productId", productId)
                .get();
    }
}


package com.grocerystore.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.Map;

public class RestHelper {
    private String endpoint = "";
    private Object body;
    private final Map<String, Object> pathParams = new HashMap<>();
    private final Map<String, Object> queryParams = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();

    private RestHelper() {}

    public static RestHelper build() {
        return new RestHelper();
    }

    public RestHelper endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public RestHelper body(Object body) {
        this.body = body;
        return this;
    }

    public RestHelper pathParam(String key, Object value) {
        this.pathParams.put(key, value);
        return this;
    }

    public RestHelper queryParam(String key, Object value) {
        this.queryParams.put(key, value);
        return this;
    }

    public RestHelper queryParams(Map<String, ?> map) {
        if (map != null) {
            this.queryParams.putAll(map);
        }
        return this;
    }

    public RestHelper header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    private Response execute(String method) {
        RequestSpecification spec = RestAssured.given().relaxedHTTPSValidation();

        if (body != null) {
            spec.body(body);
        }
        if (!pathParams.isEmpty()) {
            spec.pathParams(pathParams);
        }
        if (!queryParams.isEmpty()) {
            spec.queryParams(queryParams);
        }
        if (!headers.isEmpty()) {
            spec.headers(headers);
        }

        return spec.when()
                .request(method, endpoint)
                .then()
                .log().all()
                .extract().response();
    }

    public Response get() { return execute("GET"); }
    public Response post() { return execute("POST"); }
    public Response put() { return execute("PUT"); }
    public Response patch() { return execute("PATCH"); }
    public Response delete() { return execute("DELETE"); }

    // Support for deserialized type-safe requests
    public <T> T get(Class<T> responseClass) {
        return get().as(responseClass);
    }

    public <T> T post(Class<T> responseClass) {
        return post().as(responseClass);
    }

    public <T> T put(Class<T> responseClass) {
        return put().as(responseClass);
    }

    public <T> T patch(Class<T> responseClass) {
        return patch().as(responseClass);
    }

    public <T> T delete(Class<T> responseClass) {
        return delete().as(responseClass);
    }
}

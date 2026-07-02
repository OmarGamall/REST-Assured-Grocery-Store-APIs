package com.grocerystore.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
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
        RequestSpecification spec = RestAssured.given()
                .relaxedHTTPSValidation()
                .filter(new AllureRestAssured())
                .filter(new RestLoggingFilter());

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

    // Custom RestAssured Filter to route HTTP logs to our LogsManager
    private static class RestLoggingFilter implements Filter {
        @Override
        public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
            StringBuilder requestLog = new StringBuilder();
            requestLog.append(">>> API Request: ").append(requestSpec.getMethod()).append(" ").append(requestSpec.getURI());
            if (requestSpec.getBody() != null) {
                String prettyRequest = prettyPrintJson(requestSpec.getBody().toString());
                requestLog.append("\nRequest Body: ").append(prettyRequest);
            }
            LogsManager.info(requestLog.toString());

            Response response = ctx.next(requestSpec, responseSpec);

            StringBuilder responseLog = new StringBuilder();
            responseLog.append("<<< API Response Status Code: ").append(response.getStatusCode());
            if (response.getBody() != null) {
                String bodyString = response.getBody().asString();
                if (bodyString != null && !bodyString.trim().isEmpty()) {
                    String prettyResponse = prettyPrintJson(bodyString);
                    // Prevent massive log files from long JSON array responses (e.g. GET products)
                    if (prettyResponse.length() > 3000) {
                        prettyResponse = prettyResponse.substring(0, 3000) + "\n... [TRUNCATED DUE TO SIZE]";
                    }
                    responseLog.append("\nResponse Body: ").append(prettyResponse);
                }
            }
            LogsManager.info(responseLog.toString());
            
            return response;
        }

        // Helper to format raw JSON strings with proper indentation
        private String prettyPrintJson(String jsonString) {
            if (jsonString == null || jsonString.trim().isEmpty()) {
                return "";
            }
            try {
                ObjectMapper mapper = new ObjectMapper();
                Object jsonObject = mapper.readValue(jsonString, Object.class);
                return "\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            } catch (Exception e) {
                return "\n" + jsonString;
            }
        }
    }
}

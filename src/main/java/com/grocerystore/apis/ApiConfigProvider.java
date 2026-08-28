package com.grocerystore.apis;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import com.grocerystore.utils.PropertyReader;

/**
 * API config provider providing common REST Assured request configurations.
 */
public class ApiConfigProvider {

    private ApiConfigProvider() {
        // Private constructor to prevent instantiation
    }

    /**
     * Factory method to return a fresh, thread-safe RequestSpecification.
     *
     * @return a new RequestSpecification instance
     */
    public static RequestSpecification getRequestSpec() {
        int connectionTimeout = Integer.parseInt(PropertyReader.getProperty("api.connection.timeout", "5000"));
        int socketTimeout = Integer.parseInt(PropertyReader.getProperty("api.socket.timeout", "10000"));

        RestAssuredConfig config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", connectionTimeout)
                        .setParam("http.socket.timeout", socketTimeout));

        return new RequestSpecBuilder()
                .setBaseUri(Routes.BASE_URI)
                .setContentType(ContentType.JSON)
                .setConfig(config)
                .setRelaxedHTTPSValidation()
                .log(LogDetail.ALL)
                .build();
    }
}


package com.grocerystore.apis;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Base API client class providing common REST Assured request configurations.
 */
public class BaseApi {

    /**
     * Factory method to return a fresh, thread-safe RequestSpecification.
     *
     * @return a new RequestSpecification instance
     */
    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(Routes.BASE_URI)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }
}

package test.GroceryStore.com.apis;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/*
 this code inside the block runs once at the very beginning of test suite
*/
public class BaseApi {
    protected static final RequestSpecification requestSpec;

    static {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(Routes.BASE_URI)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }
}

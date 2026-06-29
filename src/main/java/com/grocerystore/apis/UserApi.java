package com.grocerystore.apis;

import io.restassured.response.Response;
import com.grocerystore.utils.RestHelper;
import io.qameta.allure.Step;

public class UserApi extends BaseApi {

    @Step("API: Register a new client")
    public static Response registerClient(Object clientData) {
        return RestHelper.build()
                .endpoint(Routes.REGISTER_CLIENT_ENDPOINT)
                .body(clientData)
                .post();
    }
}


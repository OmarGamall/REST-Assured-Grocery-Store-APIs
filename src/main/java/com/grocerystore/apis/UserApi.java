package com.grocerystore.apis;

import io.restassured.response.Response;
import com.grocerystore.utils.RestHelper;

public class UserApi extends BaseApi {

    public static Response registerClient(Object clientData) {
        return RestHelper.build()
                .endpoint(Routes.REGISTER_CLIENT_ENDPOINT)
                .body(clientData)
                .post();
    }
}


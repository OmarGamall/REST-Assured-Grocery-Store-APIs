package test.GroceryStore.com.apis;

import io.restassured.response.Response;
import test.GroceryStore.com.utils.RestHelper;

public class UserApi extends BaseApi {

    public static Response registerClient(Object clientData) {
        return RestHelper.build()
                .endpoint(Routes.REGISTER_CLIENT_ENDPOINT)
                .body(clientData)
                .post();
    }
}


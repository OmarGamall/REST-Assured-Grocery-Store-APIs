package com.grocerystore.testcases.auth;

import org.testng.annotations.Test;
import com.grocerystore.models.client.Client;
import com.grocerystore.steps.ClientSteps;
import com.grocerystore.testcases.BaseTest;

import static org.testng.Assert.*;

@Test(groups = {"auth", "happy-path"})
public class AuthHappyPathTest extends BaseTest {

    @Test(groups = {"smoke"}, description = "TC_AUTH_001: Verify registration of new API client")
    public void testRegisterApiClient() {
        Client registeredClient = ClientSteps.registerClientAndGetClientDetails();
        // Verify the token is present in the response and not null
        assertNotNull(registeredClient.getAccessToken(), "Expected non-null access token");
    }
}

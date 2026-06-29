package com.grocerystore.testcases.auth;

import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import com.grocerystore.models.client.Client;
import com.grocerystore.steps.ClientSteps;
import com.grocerystore.testcases.BaseTest;

import static org.testng.Assert.*;

@Test(groups = {"auth", "happy-path"})
public class AuthHappyPathTest extends BaseTest {
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"smoke"}, description = "TC_AUTH_001: Verify that POST /api-clients returns 201 Created and access token when registering a new API client with valid name and unique email")
    public void testRegisterApiClient() {
        Client registeredClient = ClientSteps.registerClientAndGetClientDetails();
        // Verify the token is present in the response and not null
        assertNotNull(registeredClient.getAccessToken(), "Expected non-null access token");
    }
}

package com.grocerystore.testcases.auth;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Allure;
import com.grocerystore.apis.UserApi;
import com.grocerystore.models.client.Client;
import com.grocerystore.testcases.BaseTest;

import static org.testng.Assert.*;

@Test(groups = {"auth", "happy-path"})
public class AuthHappyPathTest extends BaseTest {
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"smoke"}, description = "TC_AUTH_001: Verify that POST /api-clients returns 201 Created and access token when registering a new API client with valid name and unique email")
    public void testRegisterApiClient() {
        // Arrange
        String clientName = FAKER.name().fullName();
        String clientEmail = FAKER.internet().emailAddress();
        Client clientRequest = Client.builder()
                .clientName(clientName)
                .clientEmail(clientEmail)
                .build();

        // Act
        Response response = UserApi.registerClient(clientRequest);

        // Assert
        Allure.step("Assert: Verify response status code is 201 and access token is present", () -> {
            assertEquals(response.getStatusCode(), 201, "Expected status code 201 for client registration");
            assertResponseSchema(response, "schemas/api-client-schema.json");
            
            Client responseClient = response.as(Client.class);
            assertNotNull(responseClient.getAccessToken(), "Expected non-null access token in response");
        });
    }
}

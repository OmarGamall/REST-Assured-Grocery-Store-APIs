package com.grocerystore.testcases;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import com.grocerystore.apis.UserApi;
import com.grocerystore.models.client.Client;
import com.grocerystore.steps.ClientSteps;

import static org.testng.Assert.*;

public class AuthTest extends BaseTest {

    @Test(description = "TC_AUTH_001: Verify registration of new API client")
    public void testRegisterApiClient() {
        Client registeredClient = ClientSteps.registerClientAndGetClientDetails();
        // Verify the token is present in the response and not null
        assertNotNull(registeredClient.getAccessToken(), "Expected non-null access token");
    }

    @Test(description = "TC_AUTH_002: Verify error when clientEmail is missing")
    public void testRegisterApiClientWithNoEmail() {
        String randomClientName = FAKER.name().fullName(); // Generate a random full name as client name
        Client clientData = Client.builder()
                .clientName(randomClientName)
                .build();
        Response response = UserApi.registerClient(clientData); // Use the UserApi to register the client
        
        // Verify response code and error details
        assertErrorResponse(response, 400, "missing client email");
    }

    @Test(description = "TC_AUTH_003: Verify error when clientName is missing")
    public void testRegisterApiClientWithNoClientName() {
        Client clientData = Client.builder()
                .clientEmail("test@example.com")
                .build();
        Response response = UserApi.registerClient(clientData); // Use the UserApi to register the client
        
        // Verify response code and error details
        assertErrorResponse(response, 400, "missing client name");
    }

    @Test(description = "TC_AUTH_004: Verify error when clientEmail has invalid format")
    public void testRegisterApiClientWithInvalidEmailFormat() {
        String randomClientName = FAKER.name().fullName(); // Generate a random full name as client name
        Client clientData = Client.builder()
                .clientName(randomClientName)
                .clientEmail("invalid-email-format")
                .build();
        Response response = UserApi.registerClient(clientData); // Use the UserApi to register the client
        
        // Verify response code and error details
        assertErrorResponse(response, 400, "Invalid or missing client email");
    }

    @Test(description = "TC_AUTH_005: Verify error when client email already exists")
    public void testRegisterApiClientWithEmailAlreadyExists()
    {
        String randomClientName1 = FAKER.name().fullName();
        String randomEmail = FAKER.internet().emailAddress();
        Client clientData1 = Client.builder()
                .clientName(randomClientName1)
                .clientEmail(randomEmail)
                .build();
        UserApi.registerClient(clientData1); // Register the first client
 
        String randomClientName2 = FAKER.name().fullName();
        Client clientData2 = Client.builder()
                .clientName(randomClientName2)
                .clientEmail(randomEmail)
                .build(); // Use the same email for the second client
        Response response = UserApi.registerClient(clientData2); // Attempt to register the second client

        // Verify response code and error details
        assertErrorResponse(response, 409, "API client already registered. Try a different email");
    }
}

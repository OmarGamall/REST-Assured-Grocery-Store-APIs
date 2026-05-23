package test.GroceryStore.com;

import static org.testng.Assert.*;

import io.restassured.response.Response;
import test.GroceryStore.com.models.Client;

import org.testng.annotations.Test;
import test.GroceryStore.com.apis.UserApi;
import test.GroceryStore.com.models.ErrorResponse;

import java.util.UUID;

public class AuthTest {
    @Test
    public void registerApiClient() {
        String randomClientName = "Client_" + UUID.randomUUID().toString().substring(0, 8);
        String randomEmailName = "email_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        Client clientData = new Client(randomClientName, randomEmailName);
        Response response = UserApi.registerClient(clientData); // Use the UserApi to register the client
        // Verify the response status code
        assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful client registration");
        // Verify the token is present in the response and not null
        assertNotNull(response.as(Client.class).getAccessToken(), "Expected non-null access token");
        // Extract the token from the response
        String token = response.as(Client.class).getAccessToken();
        System.out.println("Generated Token: " + token);
    }

    @Test
    public void registerApiClientWithNoEmail() {
        String randomEmailName = "email_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        Client clientData = new Client();
        clientData.setClientEmail(randomEmailName);
        Response response = UserApi.registerClient(clientData); // Use the UserApi to register the client
        // Verify the response status code
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for missing clientName"); // Verify bad request status code
        // Verify the error message in the response
        ErrorResponse errorResponse = response.as(ErrorResponse.class); // Deserialize response to Error object
        System.out.println("Error Message: " + errorResponse.getError());
        assertTrue(errorResponse.getError().contains("missing client name")); // Verify error message contains expected text
    }
}

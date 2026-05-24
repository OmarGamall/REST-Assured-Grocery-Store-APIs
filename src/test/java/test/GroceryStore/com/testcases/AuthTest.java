package test.GroceryStore.com.testcases;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.UserApi;
import test.GroceryStore.com.models.Client;
import test.GroceryStore.com.models.ErrorResponse;

import java.util.UUID;

import static org.testng.Assert.*;

public class AuthTest {
    @Test
    public void registerApiClient() {
        Faker faker = new Faker();
        String randomClientName = faker.name().fullName(); // Generate a random full name as client name
        String randomEmailName = faker.internet().emailAddress(); // Generate a random email address
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
        Faker faker = new Faker();
        String randomClientName = faker.name().fullName(); // Generate a random full name as client name
        Client clientData = new Client(randomClientName, null);
        Response response = UserApi.registerClient(clientData); // Use the UserApi to register the client
        // Verify the response status code
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for missing clientEmail"); // Verify bad request status code
        // Verify the error message in the response
        ErrorResponse errorResponse = response.as(ErrorResponse.class); // Deserialize response to Error object
        assertTrue(errorResponse.getError().contains("missing client email")); // Verify error message contains expected text
    }

    @Test
    public void registerApiClientWithInvalidEmailFormat() {
        Faker faker = new Faker();
        String randomClientName = faker.name().fullName(); // Generate a random full name as client name
        Client clientData = new Client();
        clientData.setClientName(randomClientName);
        clientData.setClientEmail("invalid-email-format"); // Set an invalid email format
        Response response = UserApi.registerClient(clientData); // Use the UserApi to register the client
        // Verify the response status code
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for invalid email format"); // Verify bad request status code
        // Verify the error message in the response
        ErrorResponse errorResponse = response.as(ErrorResponse.class); // Deserialize response to Error object
        assertTrue(errorResponse.getError().contains("Invalid or missing client email")); // Verify error message contains expected text
    }
}

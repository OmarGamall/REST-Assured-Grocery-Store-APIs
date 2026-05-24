package test.GroceryStore.com.testcases;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.UserApi;
import test.GroceryStore.com.models.Client;
import test.GroceryStore.com.models.ErrorResponse;
import test.GroceryStore.com.steps.ClientSteps;

import static org.testng.Assert.*;

public class AuthTest {
    
    private static final Faker FAKER = new Faker();

    @Test
    public void testRegisterApiClient() {
        Client registeredClient = ClientSteps.registerClientAndGetClientDetails();
        // Verify the token is present in the response and not null
        assertNotNull(registeredClient.getAccessToken(), "Expected non-null access token");
        System.out.println("Generated Token: " + registeredClient.getAccessToken());
    }

    @Test
    public void testRegisterApiClientWithNoEmail() {
        String randomClientName = FAKER.name().fullName(); // Generate a random full name as client name
        Client clientData = new Client(randomClientName, null);
        Response response = UserApi.registerClient(clientData); // Use the UserApi to register the client
        // Verify the response status code
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for missing clientEmail"); // Verify bad request status code
        // Verify the error message in the response
        ErrorResponse errorResponse = response.as(ErrorResponse.class); // Deserialize response to Error object
        assertTrue(errorResponse.getError().contains("missing client email")); // Verify error message contains expected text
    }

    @Test
    public void testRegisterApiClientWithNoClientName() {
        Client clientData = new Client(null, "test@example.com");
        Response response = UserApi.registerClient(clientData); // Use the UserApi to register the client
        // Verify the response status code
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for missing clientName"); // Verify bad request status code
        // Verify the error message in the response
        ErrorResponse errorResponse = response.as(ErrorResponse.class); // Deserialize response to Error object
        assertTrue(errorResponse.getError().contains("missing client name")); // Verify error message contains expected text
    }

    @Test
    public void testRegisterApiClientWithInvalidEmailFormat() {
        String randomClientName = FAKER.name().fullName(); // Generate a random full name as client name
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

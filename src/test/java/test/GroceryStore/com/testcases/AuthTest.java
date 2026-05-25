package test.GroceryStore.com.testcases;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.UserApi;
import test.GroceryStore.com.models.Client;
import test.GroceryStore.com.steps.ClientSteps;

import static org.testng.Assert.*;

public class AuthTest extends BaseTest {

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
        
        // Verify response code and error details
        assertErrorResponse(response, 400, "missing client email");
    }

    @Test
    public void testRegisterApiClientWithNoClientName() {
        Client clientData = new Client(null, "test@example.com");
        Response response = UserApi.registerClient(clientData); // Use the UserApi to register the client
        
        // Verify response code and error details
        assertErrorResponse(response, 400, "missing client name");
    }

    @Test
    public void testRegisterApiClientWithInvalidEmailFormat() {
        String randomClientName = FAKER.name().fullName(); // Generate a random full name as client name
        Client clientData = new Client();
        clientData.setClientName(randomClientName);
        clientData.setClientEmail("invalid-email-format"); // Set an invalid email format
        Response response = UserApi.registerClient(clientData); // Use the UserApi to register the client
        
        // Verify response code and error details
        assertErrorResponse(response, 400, "Invalid or missing client email");
    }
}

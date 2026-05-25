package test.GroceryStore.com.testcases;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import test.GroceryStore.com.apis.UserApi;
import test.GroceryStore.com.models.client.Client;
import test.GroceryStore.com.steps.ClientSteps;

import static org.testng.Assert.*;

public class AuthTest extends BaseTest {

    @Test
    public void testRegisterApiClient() {
        Client registeredClient = ClientSteps.registerClientAndGetClientDetails();
        // Verify the token is present in the response and not null
        assertNotNull(registeredClient.getAccessToken(), "Expected non-null access token");
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

    @Test
    public void testRegisterApiClientWithEmailAlreadyExists()
    {
        String randomClientName1 = FAKER.name().fullName();
        String randomEmail = FAKER.internet().emailAddress();
        Client clientData1 = new Client(randomClientName1, randomEmail);
        UserApi.registerClient(clientData1); // Register the first client

        String randomClientName2 = FAKER.name().fullName();
        Client clientData2 = new Client(randomClientName2, randomEmail); // Use the same email for the second client
        Response response = UserApi.registerClient(clientData2); // Attempt to register the second client

        // Verify response code and error details
        assertErrorResponse(response, 409, "API client already registered. Try a different email");
    }
}

package test.GroceryStore.com.testcases;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import test.GroceryStore.com.models.ErrorResponse;

import static org.testng.Assert.*;

public class BaseTest {

    protected static final Faker FAKER = new Faker();

    /**
     * Reusable assertion to verify that an API response returns a specific error status code
     * and contains the expected error message details in the response body.
     *
     * @param response             The RestAssured Response object.
     * @param expectedStatusCode   The expected HTTP status code (e.g. 400, 404).
     * @param expectedErrorMessage The substring expected to be present in the error message.
     */
    protected void assertErrorResponse(Response response, int expectedStatusCode, String expectedErrorMessage) {
        assertEquals(response.getStatusCode(), expectedStatusCode, 
            String.format("Expected status code %d but got %d", expectedStatusCode, response.getStatusCode()));
        
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertNotNull(errorResponse, "Expected response body to be deserializable into ErrorResponse");
        assertNotNull(errorResponse.getError(), "Expected 'error' field in ErrorResponse to be non-null");
        
        assertTrue(errorResponse.getError().contains(expectedErrorMessage), 
            String.format("Expected error message to contain '%s' but was: '%s'", expectedErrorMessage, errorResponse.getError()));
    }
}

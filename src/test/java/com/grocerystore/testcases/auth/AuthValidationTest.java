package com.grocerystore.testcases.auth;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Allure;
import com.grocerystore.apis.UserApi;
import com.grocerystore.models.client.Client;
import com.grocerystore.testcases.BaseTest;
import static com.grocerystore.constants.ErrorMessages.*;

@Test(groups = {"auth", "validation"})
public class AuthValidationTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_AUTH_002: Verify that POST /api-clients returns 400 Bad Request and validation error when clientEmail is missing")
    public void testRegisterApiClientWithNoEmail() {
        // Arrange
        String randomClientName = FAKER.name().fullName();
        Client clientData = Client.builder()
                .clientName(randomClientName)
                .build();
        
        // Act
        Response response = Allure.step("Act: Attempt to register client with missing email", () -> {
            return UserApi.registerClient(clientData);
        });
        
        // Assert
        assertErrorResponse(response, 400, MISSING_CLIENT_EMAIL);
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_AUTH_003: Verify that POST /api-clients returns 400 Bad Request and validation error when clientName is missing")
    public void testRegisterApiClientWithNoClientName() {
        // Arrange
        Client clientData = Client.builder()
                .clientEmail("test@example.com")
                .build();
        
        // Act
        Response response = Allure.step("Act: Attempt to register client with missing name", () -> {
            return UserApi.registerClient(clientData);
        });
        
        // Assert
        assertErrorResponse(response, 400, MISSING_CLIENT_NAME);
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_AUTH_004: Verify that POST /api-clients returns 400 Bad Request and validation error when clientEmail has an invalid email format")
    public void testRegisterApiClientWithInvalidEmailFormat() {
        // Arrange
        String randomClientName = FAKER.name().fullName();
        Client clientData = Client.builder()
                .clientName(randomClientName)
                .clientEmail("invalid-email-format")
                .build();
        
        // Act
        Response response = Allure.step("Act: Attempt to register client with invalid email format", () -> {
            return UserApi.registerClient(clientData);
        });
        
        // Assert
        assertErrorResponse(response, 400, INVALID_CLIENT_EMAIL);
    }

    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"regression"}, description = "TC_AUTH_005: Verify that POST /api-clients returns 409 Conflict and validation error when registering a client with an email address that is already registered")
    public void testRegisterApiClientWithEmailAlreadyExists() {
        // Arrange
        String randomClientName1 = FAKER.name().fullName();
        String randomEmail = FAKER.internet().emailAddress();
        Client clientData1 = Client.builder()
                .clientName(randomClientName1)
                .clientEmail(randomEmail)
                .build();
        
        Allure.step("Arrange: Register the first client with email: " + randomEmail, () -> {
            UserApi.registerClient(clientData1);
        });
 
        String randomClientName2 = FAKER.name().fullName();
        Client clientData2 = Client.builder()
                .clientName(randomClientName2)
                .clientEmail(randomEmail)
                .build();

        // Act
        Response response = Allure.step("Act: Attempt to register the second client with the same email", () -> {
            return UserApi.registerClient(clientData2);
        });

        // Assert
        assertErrorResponse(response, 409, CLIENT_EMAIL_ALREADY_EXISTS);
    }
}

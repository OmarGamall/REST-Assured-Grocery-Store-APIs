package test.GroceryStore.com;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Client;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthTest {
    @Test
    public void registerApiClient() {
        // 1. Define the Base URI
        RestAssured.baseURI = "https://simple-grocery-store-api.click";

        // 2. Prepare the Body using a Map
        String randomClientName = "Client_" + UUID.randomUUID().toString().substring(0, 8);
        String randomEmailName = "email_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        Client clientData = new Client(randomClientName, randomEmailName);

        // 3. Execute request and EXTRACT the token
        clientData = given()
                .contentType(ContentType.JSON) // Tells the API we are sending JSON
                .body(clientData)            // REST Assured converts the Object to JSON automatically
                .log().all()                   // Log the request for debugging
       .when()
                .post("/api-clients")
       .then()
                .log().all()                   // Log the response
                .statusCode(201)               // Verify creation success
                .extract()
                .response().as(Client.class); // Deserialize response to Client object

        String token = clientData.getAccessToken(); // Get the token from the Client object
        System.out.println("Generated Token: " + token);

    }
}

package com.grocerystore.steps;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import com.grocerystore.apis.UserApi;
import com.grocerystore.models.client.Client;

public class ClientSteps {



    /**
     * Overloaded method: Registers a client with custom details and returns details including token.
     * Useful for negative and custom scenario testing.
     */
    public static Client registerClientAndGetClientDetails(Client clientData) {
        Response response = UserApi.registerClient(clientData);
        
        if (response.getStatusCode() != 201) {
            throw new RuntimeException("Failed to register client: " + response.getStatusLine());
        }

        Client registeredClient = response.as(Client.class);
        if (registeredClient.getAccessToken() == null) {
            throw new RuntimeException("Access token is null in the response");
        }

        // Attach requested details since the API only returns the token
        registeredClient.setClientName(clientData.getClientName());
        registeredClient.setClientEmail(clientData.getClientEmail());
        
        return registeredClient;
    }

    /**
     * Registers a random client and returns the client details.
     */
    public static Client registerClientAndGetClientDetails() {
        Faker faker = new Faker();
        String randomClientName = faker.name().fullName();
        String randomEmailName = faker.internet().emailAddress();
        Client clientData = Client.builder()
                .clientName(randomClientName)
                .clientEmail(randomEmailName)
                .build();
        
        return registerClientAndGetClientDetails(clientData);
    }

    /**
     * Registers a random client and returns only the Access Token.
     */
    public static String registerClientAndGetToken() {
        return registerClientAndGetClientDetails().getAccessToken();
    }
}

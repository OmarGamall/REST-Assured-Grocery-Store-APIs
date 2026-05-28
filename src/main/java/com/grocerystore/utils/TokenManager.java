package com.grocerystore.utils;

import com.github.javafaker.Faker;
import com.grocerystore.models.client.Client;
import com.grocerystore.steps.ClientSteps;

public class TokenManager {
    private static String cachedToken;

    /**
     * Lazy-loads and caches the access token in memory.
     * Checks if custom token is provided. If not, registers a client using
     * either configured details or dynamically generated ones.
     *
     * @return The Bearer token in use for the test execution.
     */
    public static synchronized String getToken() {
        if (cachedToken == null) {
            String token = ConfigLoader.getProperty("api.token");

            if (token == null || token.trim().isEmpty()) {
                System.out.println("[TokenManager] No custom api.token found. Resolving client credentials for registration...");
                
                String clientName = ConfigLoader.getProperty("client.name");
                String clientEmail = ConfigLoader.getProperty("client.email");

                // Prioritize config values, fallback to Faker for any empty values
                Faker faker = new Faker();
                if (clientName == null || clientName.trim().isEmpty()) {
                    clientName = faker.name().fullName();
                }
                if (clientEmail == null || clientEmail.trim().isEmpty()) {
                    clientEmail = faker.internet().emailAddress();
                }

                System.out.println(String.format("[TokenManager] Registering client with details: [Name: %s, Email: %s]", clientName, clientEmail));
                Client clientData = Client.builder()
                        .clientName(clientName)
                        .clientEmail(clientEmail)
                        .build();
                Client registered = ClientSteps.registerClientAndGetClientDetails(clientData);
                token = registered.getAccessToken();
            } else {
                System.out.println("[TokenManager] Using custom api.token specified in configuration: " + token);
            }

            cachedToken = token;
        }
        return cachedToken;
    }
}

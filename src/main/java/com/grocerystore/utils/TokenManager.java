package com.grocerystore.utils;

import com.github.javafaker.Faker;
import com.grocerystore.models.client.Client;
import com.grocerystore.steps.ClientSteps;

public class TokenManager {
    private static volatile String cachedToken = null;

    /**
     * Lazily resolves and caches the access token globally.
     * Registers a new client using Faker if no custom token or client data are configured.
     *
     * @return Globally shared Bearer token.
     */
    public static String getToken() {
        if (cachedToken == null) {
            synchronized (TokenManager.class) {
                /* If another thread registered the client and updated cachedToken while this
                /thread was waiting for the lock, this check prevents registering another client */
                if (cachedToken == null) {
                    String token = PropertyReader.getProperty("api.token");

                    if (token == null || token.trim().isEmpty()) {
                        System.out.println(String.format("[TokenManager - Thread: %s] " +
                                "No custom api.token found. Resolving client credentials for registration...",
                                Thread.currentThread().getName()));
                        
                        String clientName = PropertyReader.getProperty("client.name");
                        String clientEmail = PropertyReader.getProperty("client.email");

                        // Fallback to Faker for missing credentials
                        Faker faker = new Faker();
                        if (clientName == null || clientName.trim().isEmpty()) {
                            clientName = faker.name().fullName();
                        }
                        if (clientEmail == null || clientEmail.trim().isEmpty()) {
                            clientEmail = faker.internet().emailAddress();
                        }

                        System.out.println(String.format("[TokenManager - Thread: %s] Registering client with " +
                                "details: [Name: %s, Email: %s]",
                                Thread.currentThread().getName(), clientName, clientEmail));
                        Client clientData = Client.builder()
                                .clientName(clientName)
                                .clientEmail(clientEmail)
                                .build();
                        Client registered = ClientSteps.registerClientAndGetClientDetails(clientData);
                        token = registered.getAccessToken();
                    } else {
                        System.out.println(String.format("[TokenManager - Thread: %s] " +
                                "Using custom api.token specified in configuration: %s",
                                Thread.currentThread().getName(), token));
                    }
                    cachedToken = token;
                }
            }
        }
        return cachedToken;
    }

    /**
     * Clears the cached token.
     */
    public static void clearToken() {
        synchronized (TokenManager.class) {
            if (cachedToken != null) {
                System.out.println(String.format("[TokenManager - Thread: %s] Clearing cached token...", Thread.currentThread().getName()));
                cachedToken = null;
            }
        }
    }
}

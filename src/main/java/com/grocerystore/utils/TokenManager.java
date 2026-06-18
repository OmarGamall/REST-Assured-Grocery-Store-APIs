package com.grocerystore.utils;

import com.github.javafaker.Faker;
import com.grocerystore.models.client.Client;
import com.grocerystore.steps.ClientSteps;

public class TokenManager {
    private static final ThreadLocal<String> threadLocalToken = new ThreadLocal<>();

    /**
     * Lazily resolves and caches the access token per thread.
     * Registers a new client using Faker if no custom token or client data are configured.
     *
     * @return Thread-isolated Bearer token.
     */
    public static String getToken() {
        if (threadLocalToken.get() == null) {
            String token = ConfigLoader.getProperty("api.token");

            if (token == null || token.trim().isEmpty()) {
                System.out.println(String.format("[TokenManager - Thread: %s] No custom api.token found. Resolving client credentials for registration...", Thread.currentThread().getName()));
                
                String clientName = ConfigLoader.getProperty("client.name");
                String clientEmail = ConfigLoader.getProperty("client.email");

                // Fallback to Faker for missing credentials
                Faker faker = new Faker();
                if (clientName == null || clientName.trim().isEmpty()) {
                    clientName = faker.name().fullName();
                }
                if (clientEmail == null || clientEmail.trim().isEmpty()) {
                    clientEmail = faker.internet().emailAddress();
                }

                System.out.println(String.format("[TokenManager - Thread: %s] Registering client with details: [Name: %s, Email: %s]", Thread.currentThread().getName(), clientName, clientEmail));
                Client clientData = Client.builder()
                        .clientName(clientName)
                        .clientEmail(clientEmail)
                        .build();
                Client registered = ClientSteps.registerClientAndGetClientDetails(clientData);
                token = registered.getAccessToken();
            } else {
                System.out.println(String.format("[TokenManager - Thread: %s] Using custom api.token specified in configuration: %s", Thread.currentThread().getName(), token));
            }

            threadLocalToken.set(token);
        }
        return threadLocalToken.get();
    }

    /**
     * Clears the thread-local token to prevent memory leaks.
     */
    public static void clearToken() {
        threadLocalToken.remove();
    }
}

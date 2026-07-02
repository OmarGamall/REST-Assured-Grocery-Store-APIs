package com.grocerystore.apis;

import com.grocerystore.utils.PropertyReader;
import com.grocerystore.utils.LogsManager;

public class Routes {
    // Base URL dynamically resolved via system property or configuration
    public static final String BASE_URI = resolveBaseUri();

    private static String resolveBaseUri() {
        String env = PropertyReader.getProperty("env");
        if (env == null || env.trim().isEmpty()) {
            env = "production";
        }
        
        env = env.trim().toLowerCase();
        String baseUrl = PropertyReader.getProperty(env + ".baseUrl");
        if (baseUrl != null && !baseUrl.trim().isEmpty()) {
            return baseUrl;
        }
        
        // Support direct URLs if user specifies a custom URL as the env parameter
        if (env.startsWith("http://") || env.startsWith("https://")) {
            return env;
        }
        
        LogsManager.warn("Unknown environment '{}'. Defaulting to Production.", env);
        return "https://simple-grocery-store-api.click";
    }

    // Products Endpoints
    public static final String PRODUCTS_ENDPOINT = "/products";
    public static final String PRODUCT_BY_ID_ENDPOINT = "/products/{productId}";

    // Carts Endpoints
    public static final String CARTS_ENDPOINT = "/carts";
    public static final String CART_BY_ID_ENDPOINT = "/carts/{cartId}";
    public static final String CART_ITEMS_ENDPOINT = "/carts/{cartId}/items";
    public static final String CART_ITEM_BY_ID_ENDPOINT = "/carts/{cartId}/items/{itemId}";

    // Authentication Endpoints
    public static final String REGISTER_CLIENT_ENDPOINT = "/api-clients";

    // Orders Endpoints
    public static final String ORDERS_ENDPOINT = "/orders";
    public static final String ORDER_BY_ID_ENDPOINT = "/orders/{orderId}";
}


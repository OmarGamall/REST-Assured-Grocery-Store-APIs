package test.GroceryStore.com.apis;

public class Routes {
    // Base URL (easily configurable later via properties file or system parameters)
    public static final String BASE_URI = "https://simple-grocery-store-api.click";

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


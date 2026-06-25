package com.grocerystore.constants;

public class ErrorMessages {
    // Client Registration Error Messages
    public static final String MISSING_CLIENT_EMAIL = "missing client email";
    public static final String MISSING_CLIENT_NAME = "missing client name";
    public static final String INVALID_CLIENT_EMAIL = "Invalid or missing client email";
    public static final String CLIENT_EMAIL_ALREADY_EXISTS = "API client already registered. Try a different email";

    // Cart Error Messages
    public static final String DUPLICATE_PRODUCT = "This product has already been added to cart";
    public static final String PRODUCT_OUT_OF_STOCK = "This product is not in stock and cannot be ordered";
    public static final String QUANTITY_EXCEEDS_STOCK = "The quantity requested exceeds the current stock";
    public static final String QUANTITY_MINIMUM = "Quantity must be at least 1";
    public static final String INVALID_PRODUCT_ID = "Invalid or missing productId";
    public static final String NO_CART_WITH_ID = "No cart with id";
    public static final String NO_ITEM_WITH_ID = "No item with id";
    public static final String QUANTITY_NOT_AVAILABLE = "The quantity requested is not available in stock";
    public static final String INVALID_QUANTITY = "Invalid or missing quantity";

    // Product Error Messages
    public static final String NO_PRODUCT_WITH_ID = "No product with id";
    public static final String INVALID_CATEGORY_QUERY_PARAM = "Invalid value for query parameter 'category'";
    public static final String INVALID_RESULTS_QUERY_PARAM = "Invalid value for query parameter 'results'";

    // Authentication Error Messages
    public static final String INVALID_BEARER_TOKEN = "Invalid bearer token";
    public static final String BEARER_TOKEN = "bearer token";

    // Order Error Messages
    public static final String CART_IS_EMPTY = "cart is empty";
    public static final String CART_ID_REQUIRED = "cartId";
    public static final String CUSTOMER_NAME_REQUIRED = "customer name";
    public static final String INVALID_OR_MISSING_CART_ID = "Invalid or missing cartId";
    public static final String COMMENT_LIMIT = "comment is very large";
    public static final String NO_ORDER_WITH_ID = "No order with id";
}

package com.grocerystore.steps;

import io.restassured.response.Response;
import com.grocerystore.apis.CartApi;
import com.grocerystore.models.cart.*;
import com.grocerystore.models.product.Product;
import com.grocerystore.services.ProductService;

import static com.grocerystore.services.ProductService.isProductAlreadySelected;

public class CartSteps {

    /**
     * Creates a cart and returns its valid ID.
     */
    public static String createCartAndGetId() {
        Response response = CartApi.createCart();
        if (response.getStatusCode() != 201) {
            throw new RuntimeException("Failed to create cart during test setup: " + response.getStatusLine());
        }
        CartResponse cartResponse = response.as(CartResponse.class);
        if (cartResponse.getCartId() == null) {
            throw new RuntimeException("Cart ID is null in the creation response");
        }
        return cartResponse.getCartId();
    }

    /**
     * Adds an item to the cart and returns the response metadata.
     */
    public static CartItemResponse addItemToCartAndGetResponse(String cartId, Integer productId, Integer quantity) {
        CartItem cartItem = CartItem.builder()
                .cartId(cartId)
                .productId(productId)
                .quantity(quantity)
                .build();
        return addItemToCartAndGetResponse(cartItem);
    }

    // Overloaded method to allow passing a CartItem object directly
    public static CartItemResponse addItemToCartAndGetResponse(CartItem cartItem) {
        Response response = CartApi.addItemToCart(cartItem);
        if (response.getStatusCode() != 201) {
            throw new RuntimeException("Failed to add item to cart: " + response.getStatusLine());
        }

        CartItemResponse responseBody = response.as(CartItemResponse.class);
        if (responseBody.getCreated() == null || !responseBody.getCreated() || responseBody.getItemId() == null) {
            throw new RuntimeException("Unexpected response payload when adding item to cart");
        }
        return responseBody;
    }

    /**
     * Adds an item to the cart and returns the fully populated CartItem with itemId.
     */
    public static CartItem addItemToCart(String cartId, Integer productId, Integer quantity) {
        CartItem cartItem = CartItem.builder()
                .cartId(cartId)
                .productId(productId)
                .quantity(quantity)
                .build();
        CartItemResponse responseBody = addItemToCartAndGetResponse(cartItem);
        cartItem.setItemId(responseBody.getItemId());
        return cartItem;
    }

    /**
     * Adds a random available product to the cart and returns the fully populated CartItem.
     */
    public static CartItem addRandomItemToCart(String cartId) {
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = ProductService.getRandomQuantity(product);
        return addItemToCart(cartId, product.getId(), quantity);
    }

    /**
     * Adds a random available product to the cart with a specific quantity and returns the fully populated CartItem.
     */
    public static CartItem addRandomItemToCart(String cartId, Integer quantity) {
        Product product = ProductService.getRandomAvailableProduct();
        return addItemToCart(cartId, product.getId(), quantity);
    }

    /**
     * Fetches the list of items inside a cart.
     */
    public static CartItem[] getCartItems(String cartId) {
        Response response = CartApi.getCartItems(cartId);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to retrieve cart items: " + response.getStatusLine());
        }
        return response.as(CartItem[].class);
    }

    /**
     * Adds multiple items to the cart and returns the updated list of cart items.
     */
    public static CartItem[] AddMultipleRandomItemsToCart(String cartId , int numberOfItemsToAdd) {
        Product[] products = new Product[numberOfItemsToAdd];
        Product productToAdd = null;
        int[] quantities = new int[numberOfItemsToAdd];
        for (int i = 0; i < numberOfItemsToAdd; i++) {
            do {
                productToAdd = ProductService.getRandomAvailableProduct();
            } while (isProductAlreadySelected(products, productToAdd));
            products[i] = productToAdd;
            quantities[i] = ProductService.getRandomQuantity(products[i]);
        }

        for (int i = 0; i < numberOfItemsToAdd; i++) {
            CartSteps.addItemToCartAndGetResponse(cartId, products[i].getId(), quantities[i]);
        }

        return CartSteps.getCartItems(cartId);
    }

    /**
     * Deletes an item from the cart and verifies the status code.
     */
    public static Response deleteCartItem(String cartId, String itemId) {
        Response response = CartApi.deleteCartItem(cartId, itemId);
        if (response.getStatusCode() != 204) {
            throw new RuntimeException("Failed to delete cart item: " + response.getStatusLine());
        }
        return response;
    }

    /**
     * Deletes all items from the cart.
     */
    public static void deleteAllCartItems(String cartId) {
        CartItem[] items = getCartItems(cartId);
        for (CartItem item : items) {
            deleteCartItem(cartId, item.getItemId());
        }
    }
}

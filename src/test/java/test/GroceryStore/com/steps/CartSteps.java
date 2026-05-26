package test.GroceryStore.com.steps;

import io.restassured.response.Response;
import test.GroceryStore.com.apis.CartApi;
import test.GroceryStore.com.models.cart.*;
import test.GroceryStore.com.models.product.Product;
import test.GroceryStore.com.services.ProductService;

import static org.testng.Assert.*;
import static test.GroceryStore.com.services.ProductService.isProductAlreadySelected;

public class CartSteps {

    /**
     * Creates a cart and returns its valid ID.
     */
    public static String createCartAndGetId() {
        Response response = CartApi.createCart();
        assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful cart creation");
        CartResponse cartResponse = response.as(CartResponse.class);
        assertNotNull(cartResponse.getCartId(), "Cart ID should not be null");
        return cartResponse.getCartId();
    }

    /**
     * Adds an item to the cart and returns the response metadata.
     */
    public static CartItemResponse addItemToCartAndGetResponse(String cartId, Integer productId, Integer quantity) {
        CartItem cartItem = new CartItem(cartId, productId, quantity);
        Response response = CartApi.addItemToCart(cartItem);
        assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful item addition");
        
        CartItemResponse responseBody = response.as(CartItemResponse.class);
        assertTrue(responseBody.getCreated(), "Expected 'created' to be true in response");
        assertNotNull(responseBody.getItemId(), "Expected 'itemId' to be returned");
        return responseBody;
    }
    // Overloaded method to allow passing a CartItem object directly
    public static CartItemResponse addItemToCartAndGetResponse(CartItem cartItem) {
        Response response = CartApi.addItemToCart(cartItem);
        assertEquals(response.getStatusCode(), 201, "Expected status code 201 for successful item addition");

        CartItemResponse responseBody = response.as(CartItemResponse.class);
        assertTrue(responseBody.getCreated(), "Expected 'created' to be true in response");
        assertNotNull(responseBody.getItemId(), "Expected 'itemId' to be returned");
        return responseBody;
    }

    public static CartItem addRandomItemToCart(String cartId) {
        Product product = ProductService.getRandomAvailableProduct();
        int quantity = ProductService.getRandomQuantity(product);
        CartItem cartItem = new CartItem(cartId, product.getId(), quantity);
        addItemToCartAndGetResponse(cartItem);
        return cartItem;
    }

    /**
     * Fetches the list of items inside a cart.
     */
    public static CartItem[] getCartItems(String cartId) {
        Response response = CartApi.getCartItems(cartId);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for retrieving cart items");
        return response.as(CartItem[].class);
    }

    /**
     * Adds multiple items to the cart and returns the updated list of cart items.
     */
    public static CartItem[] AddMultipleRandomItemsToCart(String cartId , int numberOfItemsToAdd) {
        // Use arrays for better scalability and maintainability
        Product[] products = new Product[numberOfItemsToAdd];
        Product productToAdd = null;
        int[] quantities = new int[numberOfItemsToAdd];
        for (int i = 0; i < numberOfItemsToAdd; i++) {
            // Ensure we don't add the same product multiple times to the cart
            do {
                productToAdd = ProductService.getRandomAvailableProduct();
            } while (isProductAlreadySelected(products, productToAdd));
            products[i] = productToAdd;
            quantities[i] = ProductService.getRandomQuantity(products[i]);
        }

        for (int i = 0; i < numberOfItemsToAdd; i++) {
            CartSteps.addItemToCartAndGetResponse(cartId, products[i].getId(), quantities[i]);
        }

        CartItem[] cartItems = CartSteps.getCartItems(cartId);
        return cartItems;
    }

    /**
     * Deletes an item from the cart and returns the response metadata.
     */
    public static Response deleteCartItem(String cartId, String itemId) {
        return CartApi.deleteCartItem(cartId, itemId);
    }

    /**
     * Deletes all items from the cart and returns the response metadata.
     */
    public static Response deleteAllCartItems(String cartId) {
        CartItem[] items = getCartItems(cartId);
        for (CartItem item : items) {
            deleteCartItem(cartId, item.getId());
        }
        return null;
    }


}

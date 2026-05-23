package test.GroceryStore.com.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItem {
    @JsonIgnore
    private String cartId;
    private String productId;
    private Integer quantity;

    public CartItem(String cartId, String productId, Integer quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public CartItem() {}

    public String getCartId() {
        return cartId;
    }

    public CartItem setCartId(String cartId) {
        this.cartId = cartId;
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public CartItem setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public CartItem setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
}

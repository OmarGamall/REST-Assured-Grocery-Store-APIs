package test.GroceryStore.com.models.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItem {
    @JsonIgnore
    private String cartId;
    private Integer productId;
    private Integer quantity;
    @JsonProperty("id")
    private String itemId;

    public CartItem(String cartId, Integer productId, Integer quantity) {
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

    public Integer getProductId() {
        return productId;
    }

    public CartItem setProductId(Integer productId) {
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

    public String getId() {
        return itemId;
    }

    public CartItem setId(String id) {
        this.itemId = id;
        return this;
    }
}

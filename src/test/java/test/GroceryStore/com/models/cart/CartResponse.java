package test.GroceryStore.com.models.cart;

public class CartResponse {
    private Boolean created;
    private String cartId;

    public CartResponse() {
    }

    public CartResponse(Boolean created, String cartId) {
        this.created = created;
        this.cartId = cartId;
    }

    public Boolean getCreated() {
        return created;
    }
    public void setCreated(Boolean created) {
        this.created = created;
    }
    public String getCartId() {
        return cartId;
    }
    public void setCartId(String cartId) {
        this.cartId = cartId;
    }
}

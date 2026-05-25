package test.GroceryStore.com.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequest {
    private String cartId;
    private String customerName;
    private String comment;

    public OrderRequest() {}

    public OrderRequest(String cartId, String customerName) {
        this.cartId = cartId;
        this.customerName = customerName;
    }

    public OrderRequest(String cartId, String customerName, String comment) {
        this.cartId = cartId;
        this.customerName = customerName;
        this.comment = comment;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

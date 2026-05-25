package test.GroceryStore.com.models.cart;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemResponse {
    private Boolean created;
    private String itemId;

    public CartItemResponse() {
    }

    public CartItemResponse(Boolean created, String itemId) {
        this.created = created;
        this.itemId = itemId;
    }

    public Boolean getCreated() {
        return created;
    }

    public void setCreated(Boolean created) {
        this.created = created;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}

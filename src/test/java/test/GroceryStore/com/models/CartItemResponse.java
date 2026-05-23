package test.GroceryStore.com.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemResponse {
    private Boolean created;
    private Integer itemId;

    public CartItemResponse() {
    }

    public CartItemResponse(Boolean created, Integer itemId) {
        this.created = created;
        this.itemId = itemId;
    }

    public Boolean getCreated() {
        return created;
    }

    public void setCreated(Boolean created) {
        this.created = created;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}

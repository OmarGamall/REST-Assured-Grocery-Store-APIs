package test.GroceryStore.com.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItem {
    private Integer productId;
    private Integer id;
    private Integer quantity;

    public OrderItem() {}

    public OrderItem(Integer productId, Integer id, Integer quantity) {
        this.productId = productId;
        this.id = id;
        this.quantity = quantity;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

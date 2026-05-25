package test.GroceryStore.com.models.order;

public class OrderResponse {
    private Boolean created;
    private String orderId;

    public OrderResponse() {}

    public OrderResponse(Boolean created, String orderId) {
        this.created = created;
        this.orderId = orderId;
    }

    public Boolean getCreated() {
        return created;
    }

    public void setCreated(Boolean created) {
        this.created = created;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

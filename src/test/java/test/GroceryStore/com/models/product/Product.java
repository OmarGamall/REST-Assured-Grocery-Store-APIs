package test.GroceryStore.com.models.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {

    private Integer id;
    private String category;
    private String name;
    private boolean inStock;
    private String manufacturer;
    private Float price;
    @JsonProperty("current-stock")
    private Integer currentStock;

    public Product(Integer id, String category, String name, boolean inStock, String manufacturer, Float price, Integer currentStock) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.inStock = inStock;
        this.manufacturer = manufacturer;
        this.price = price;
        this.currentStock = currentStock;
    }

    public Product() {
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

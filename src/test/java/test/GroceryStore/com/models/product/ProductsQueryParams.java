package test.GroceryStore.com.models.product;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductsQueryParams {
    private ProductCategory category;
    private Boolean available;
    private Integer results;

    public ProductsQueryParams() {
    }

    public ProductsQueryParams(ProductCategory category, Boolean available, Integer results) {
        this.category = category;
        this.available = available;
        this.results = results;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Integer getResults() {
        return results;
    }

    public void setResults(Integer results) {
        this.results = results;
    }
}

package test.GroceryStore.com.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductsQueryParams {
    private String category;
    private Boolean available;
    private Integer results;

    public ProductsQueryParams() {
    }

    public ProductsQueryParams(String category, Boolean available, Integer results) {
        this.category = category;
        this.available = available;
        this.results = results;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
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

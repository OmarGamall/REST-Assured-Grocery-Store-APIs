package com.grocerystore.models.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {

    private Integer id;
    private String category;
    private String name;
    private Boolean inStock;
    private String manufacturer;
    private Float price;
    @JsonProperty("current-stock")
    private Integer currentStock;

    // Custom getter to maintain compatibility with existing tests calling isInStock()
    public Boolean isInStock() {
        return inStock;
    }
}

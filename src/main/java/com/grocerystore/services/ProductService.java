package com.grocerystore.services;

import io.restassured.response.Response;
import com.grocerystore.apis.ProductApi;
import com.grocerystore.models.product.Product;
import com.grocerystore.models.product.ProductsQueryParams;
import com.grocerystore.models.product.ProductCategory;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class ProductService {

    public static Product getRandomAvailableProduct() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setAvailable(true);
        queryParams.setResults(20);

        Response response = ProductApi.getAllProducts(queryParams);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to fetch products: " + response.getStatusLine());
        }

        Product[] products = response.as(Product[].class);
        if (products == null || products.length == 0) {
            throw new RuntimeException("No available products found in the catalog");
        }

        // Pick a random product
        int randomIndex = ThreadLocalRandom.current().nextInt(products.length);
        int randomProductId = products[randomIndex].getId();

        // Get details of the random product
        Response detailResponse = ProductApi.getProductById(randomProductId);
        if (detailResponse.getStatusCode() != 200) {
            throw new RuntimeException("Failed to fetch product details for ID: " + randomProductId);
        }

        return detailResponse.as(Product.class);
    }

    public static Product getRandomNonAvailableProduct() {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setAvailable(false);
        queryParams.setResults(20);

        Response response = ProductApi.getAllProducts(queryParams);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to fetch products: " + response.getStatusLine());
        }

        Product[] products = response.as(Product[].class);
        if (products == null || products.length == 0) {
            throw new RuntimeException("No non-available products found in the catalog");
        }

        // Pick a random product
        int randomIndex = ThreadLocalRandom.current().nextInt(products.length);
        int randomProductId = products[randomIndex].getId();

        // Get details of the random product
        Response detailResponse = ProductApi.getProductById(randomProductId);
        if (detailResponse.getStatusCode() != 200) {
            throw new RuntimeException("Failed to fetch product details for ID: " + randomProductId);
        }
        return detailResponse.as(Product.class);
    }

    public static int getRandomQuantity(Product product) {
        int currentStock = (product.getCurrentStock() != null) ? product.getCurrentStock() : 1;
        return (currentStock > 0)
                ? ThreadLocalRandom.current().nextInt(1, Math.min(currentStock, 10) + 1)
                : 1;
    }

    public static Product[] getAllProductsForGivenCategory(ProductCategory category) {
        ProductsQueryParams queryParams = new ProductsQueryParams();
        queryParams.setCategory(category);

        Response response = ProductApi.getAllProducts(queryParams);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to fetch products for category ID: " + category.getValue());
        }

        Product[] products = response.as(Product[].class);
        if (products == null) {
            throw new RuntimeException("No products array in response for category ID: " + category.getValue());
        }

        return products;
    }

    public static boolean isProductAlreadySelected(Product[] products, Product productToCheck) {
        for (Product product : products) {
            if (product != null && Objects.equals(product.getId(), productToCheck.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a random available product that has a current stock level
     * of at least the specified minStock value.
     *
     * @param minStock The minimum required stock level.
     * @return A randomly chosen Product with stock >= minStock.
     */
    public static Product getRandomAvailableProductWithStock(int minStock) {
        Product product;
        do {
            product = getRandomAvailableProduct();
        } while (product.getCurrentStock() != null && product.getCurrentStock() < minStock);
        return product;
    }

    /**
     * Retrieves a random available product that is different from the specified product ID.
     * Useful to ensure test items are unique during updates or replacements.
     *
     * @param excludeId The product ID to exclude from selection.
     * @return A randomly chosen Product with an ID different from excludeId.
     */
    public static Product getRandomAvailableProductDifferentFrom(Integer excludeId) {
        Product product;
        do {
            product = getRandomAvailableProduct();
        } while (Objects.equals(product.getId(), excludeId));
        return product;
    }

    /**
     * Retrieves a random available product that is different from the specified product ID
     * and has a stock level of at least the specified minStock value.
     * Ensures both uniqueness and sufficient stock level in a single query loop.
     *
     * @param excludeId The product ID to exclude from selection.
     * @param minStock  The minimum required stock level.
     * @return A randomly chosen Product with ID != excludeId and stock >= minStock.
     */
    public static Product getRandomAvailableProductDifferentFromWithStock(Integer excludeId, int minStock) {
        Product product;
        do {
            product = getRandomAvailableProduct();
        } while (Objects.equals(product.getId(), excludeId)
                || (product.getCurrentStock() != null && product.getCurrentStock() < minStock));
        return product;
    }
}


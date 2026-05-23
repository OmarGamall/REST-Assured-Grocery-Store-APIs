package test.GroceryStore.com.services;

import io.restassured.response.Response;
import test.GroceryStore.com.apis.ProductApi;
import test.GroceryStore.com.models.Product;
import test.GroceryStore.com.models.ProductsQueryParams;

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

    public static boolean isProductAlreadySelected(Product[] products, Product productToCheck) {
        for (Product product : products) {
            if (product != null && Objects.equals(product.getId(), productToCheck.getId())) {
                return true;
            }
        }
        return false;
    }
}

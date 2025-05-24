package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.Product;
import com.accesa.pricecomparator.service.CsvDataLoaderService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for product-related endpoints.
 * Provides endpoints to query products by store and date.
 */
@RestController
@RequestMapping("/products")
public class ProductController {
    private final CsvDataLoaderService csvDataLoaderService;

    public ProductController(CsvDataLoaderService csvDataLoaderService) {
        this.csvDataLoaderService = csvDataLoaderService;
    }

    /**
     * Returns all products for a given store and date.
     * @param store The store name.
     * @param date The date (YYYY-MM-DD).
     * @return List of Product objects.
     */
    @GetMapping
    public List<Product> getProducts(@RequestParam String store, @RequestParam String date) {
        return csvDataLoaderService.loadProducts(store, date);
    }

    /**
     * Returns a product by its ID for a given store and date.
     * @param productId The product ID.
     * @param store The store name.
     * @param date The date (YYYY-MM-DD).
     * @return The Product object, or null if not found.
     */
    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable String productId, @RequestParam String store, @RequestParam String date) {
        return csvDataLoaderService.loadProducts(store, date).stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst().orElse(null);
    }
} 
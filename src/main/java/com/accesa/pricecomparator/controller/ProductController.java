package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.Product;
import com.accesa.pricecomparator.service.CsvDataLoaderService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final CsvDataLoaderService csvDataLoaderService;

    public ProductController(CsvDataLoaderService csvDataLoaderService) {
        this.csvDataLoaderService = csvDataLoaderService;
    }

    @GetMapping
    public List<Product> getProducts(@RequestParam String store, @RequestParam String date) {
        return csvDataLoaderService.loadProducts(store, date);
    }

    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable String productId, @RequestParam String store, @RequestParam String date) {
        return csvDataLoaderService.loadProducts(store, date).stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst().orElse(null);
    }
} 
package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.Product;
import com.accesa.pricecomparator.service.CsvDataLoaderService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    private final CsvDataLoaderService csvDataLoaderService;

    public RecommendationController(CsvDataLoaderService csvDataLoaderService) {
        this.csvDataLoaderService = csvDataLoaderService;
    }

    @GetMapping
    public List<java.util.Map<String, Object>> getBestValueProducts(@RequestParam String productName, @RequestParam String date) {
        java.util.List<java.util.Map<String, Object>> results = new java.util.ArrayList<>();
        java.util.List<String> stores = csvDataLoaderService.getAvailableStores();
        java.time.LocalDate localDate = java.time.LocalDate.parse(date);
        for (String store : stores) {
            java.util.List<Product> products = csvDataLoaderService.loadProducts(store, date);
            java.util.List<com.accesa.pricecomparator.model.Discount> discounts = csvDataLoaderService.loadAllDiscountsForStore(store);
            for (Product p : products) {
                if (p.getProductName().equalsIgnoreCase(productName)) {
                    double basePrice = p.getPrice();
                    double bestDiscount = 0.0;
                    for (com.accesa.pricecomparator.model.Discount d : discounts) {
                        if (d.getProductName().equalsIgnoreCase(productName)
                            && (!localDate.isBefore(d.getFromDate()) && !localDate.isAfter(d.getToDate()))) {
                            if (d.getPercentageOfDiscount() > bestDiscount) {
                                bestDiscount = d.getPercentageOfDiscount();
                            }
                        }
                    }
                    double finalPrice = basePrice;
                    if (bestDiscount > 0.0) {
                        finalPrice = basePrice - (basePrice * bestDiscount / 100.0);
                    }
                    // Calculate value per 100g, 100ml, or 1 unit
                    String unit = p.getPackageUnit().toLowerCase();
                    double quantity = p.getPackageQuantity();
                    double valuePerUnit = finalPrice / quantity;
                    String valuePerUnitLabel = "per 1 " + unit;
                    if (unit.equals("kg")) {
                        valuePerUnit = finalPrice / (quantity * 10); // 1kg = 10x100g
                        valuePerUnitLabel = "per 100g";
                    } else if (unit.equals("g")) {
                        valuePerUnit = finalPrice / (quantity / 100.0);
                        valuePerUnitLabel = "per 100g";
                    } else if (unit.equals("l")) {
                        valuePerUnit = finalPrice / (quantity * 10); // 1L = 10x100ml
                        valuePerUnitLabel = "per 100ml";
                    } else if (unit.equals("ml")) {
                        valuePerUnit = finalPrice / (quantity / 100.0);
                        valuePerUnitLabel = "per 100ml";
                    }
                    java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                    map.put("valuePerUnit", Math.round(valuePerUnit * 100.0) / 100.0);
                    map.put("valuePerUnitLabel", valuePerUnitLabel);
                    map.put("finalPrice", Math.round(finalPrice * 100.0) / 100.0);
                    map.put("discountPercent", bestDiscount);
                    map.put("basePrice", basePrice);
                    map.put("store", store);
                    map.put("productId", p.getProductId());
                    map.put("productName", p.getProductName());
                    map.put("brand", p.getBrand());
                    map.put("packageQuantity", p.getPackageQuantity());
                    map.put("packageUnit", p.getPackageUnit());
                    map.put("currency", p.getCurrency());
                    results.add(map);
                }
            }
        }
        return results.stream()
            .sorted(java.util.Comparator.comparingDouble(m -> (double) m.get("valuePerUnit")))
            .collect(java.util.stream.Collectors.toList());
    }
} 
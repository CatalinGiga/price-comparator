package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.Discount;
import com.accesa.pricecomparator.service.CsvDataLoaderService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for discount-related endpoints.
 * Provides endpoints to query discounts and best discounts for products.
 */
@RestController
@RequestMapping("/discounts")
public class DiscountController {
    private final CsvDataLoaderService csvDataLoaderService;

    public DiscountController(CsvDataLoaderService csvDataLoaderService) {
        this.csvDataLoaderService = csvDataLoaderService;
    }

    /**
     * Returns all discounts from all stores valid on the given date.
     * @param date The date to check (YYYY-MM-DD).
     * @return List of discounts with store info.
     */
    @GetMapping
    public List<java.util.Map<String, Object>> getDiscounts(@RequestParam String date) {
        LocalDate queryDate = LocalDate.parse(date);
        List<String> stores = csvDataLoaderService.getAvailableStores();
        List<java.util.Map<String, Object>> allDiscounts = new java.util.ArrayList<>();
        for (String store : stores) {
            List<Discount> discounts = csvDataLoaderService.loadAllDiscountsForStore(store);
            for (Discount d : discounts) {
                if ((d.getFromDate().isBefore(queryDate) || d.getFromDate().isEqual(queryDate)) &&
                    (d.getToDate().isAfter(queryDate) || d.getToDate().isEqual(queryDate))) {
                    java.util.Map<String, Object> discountWithStore = new java.util.HashMap<>();
                    discountWithStore.put("store", store);
                    discountWithStore.put("discount", d);
                    allDiscounts.add(discountWithStore);
                }
            }
        }
        return allDiscounts;
    }

    /**
     * Returns the top 10 discounts (by percentage) valid on the given date, across all stores.
     * @param date The date to check (YYYY-MM-DD).
     * @return List of best discounts with store info.
     */
    @GetMapping("/best")
    public List<java.util.Map<String, Object>> getBestDiscounts(@RequestParam String date) {
        LocalDate queryDate = LocalDate.parse(date);
        List<String> stores = csvDataLoaderService.getAvailableStores();
        List<java.util.Map<String, Object>> allDiscounts = new java.util.ArrayList<>();
        for (String store : stores) {
            List<Discount> discounts = csvDataLoaderService.loadAllDiscountsForStore(store);
            for (Discount d : discounts) {
                if ((d.getFromDate().isBefore(queryDate) || d.getFromDate().isEqual(queryDate)) &&
                    (d.getToDate().isAfter(queryDate) || d.getToDate().isEqual(queryDate))) {
                    java.util.Map<String, Object> discountWithStore = new java.util.HashMap<>();
                    discountWithStore.put("store", store);
                    discountWithStore.put("discount", d);
                    allDiscounts.add(discountWithStore);
                }
            }
        }
        return allDiscounts.stream()
            .sorted((a, b) -> Double.compare(
                ((Discount) b.get("discount")).getPercentageOfDiscount(),
                ((Discount) a.get("discount")).getPercentageOfDiscount()
            ))
            .limit(10)
            .collect(Collectors.toList());
    }

    @GetMapping("/new")
    public List<java.util.Map<String, Object>> getNewDiscounts(@RequestParam String date) {
        LocalDate queryDate = LocalDate.parse(date);
        List<String> stores = csvDataLoaderService.getAvailableStores();
        List<java.util.Map<String, Object>> newDiscounts = new java.util.ArrayList<>();
        for (String store : stores) {
            List<Discount> discounts = csvDataLoaderService.loadAllDiscountsForStore(store);
            for (Discount d : discounts) {
                if (d.getFromDate().isEqual(queryDate)) {
                    java.util.Map<String, Object> discountWithStore = new java.util.HashMap<>();
                    discountWithStore.put("store", store);
                    discountWithStore.put("discount", d);
                    newDiscounts.add(discountWithStore);
                }
            }
        }
        return newDiscounts;
    }

    /**
     * Returns, for each unique product name, the best discount valid for the date, including the store, limited to 10 products.
     * @param date The date to check (YYYY-MM-DD).
     * @return List of best discounts for unique products.
     */
    @GetMapping("/bestDiscountForProductsWithDifferentName")
    public List<java.util.Map<String, Object>> getBestDiscountsForProductsWithDifferentName(@RequestParam String date) {
        LocalDate queryDate = LocalDate.parse(date);
        List<String> stores = csvDataLoaderService.getAvailableStores();
        java.util.Map<String, java.util.Map<String, Object>> bestDiscountsByProduct = new java.util.HashMap<>();

        for (String store : stores) {
            List<Discount> discounts = csvDataLoaderService.loadAllDiscountsForStore(store);
            for (Discount d : discounts) {
                if ((d.getFromDate().isBefore(queryDate) || d.getFromDate().isEqual(queryDate)) &&
                    (d.getToDate().isAfter(queryDate) || d.getToDate().isEqual(queryDate))) {
                    
                    String productName = d.getProductName();
                    double currentPercentage = d.getPercentageOfDiscount();
                    
                    if (!bestDiscountsByProduct.containsKey(productName) ||
                        currentPercentage > ((Discount) bestDiscountsByProduct.get(productName).get("discount")).getPercentageOfDiscount()) {
                        
                        java.util.Map<String, Object> discountWithStore = new java.util.HashMap<>();
                        discountWithStore.put("store", store);
                        discountWithStore.put("discount", d);
                        discountWithStore.put("productName", productName);
                        discountWithStore.put("percentage", currentPercentage);
                        bestDiscountsByProduct.put(productName, discountWithStore);
                    }
                }
            }
        }

        return bestDiscountsByProduct.values().stream()
            .sorted((a, b) -> Double.compare(
                (double) b.get("percentage"),
                (double) a.get("percentage")))
            .limit(10)
            .collect(java.util.stream.Collectors.toList());
    }
} 
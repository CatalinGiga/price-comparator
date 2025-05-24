package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.Discount;
import com.accesa.pricecomparator.service.CsvDataLoaderService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/discounts")
public class DiscountController {
    private final CsvDataLoaderService csvDataLoaderService;

    public DiscountController(CsvDataLoaderService csvDataLoaderService) {
        this.csvDataLoaderService = csvDataLoaderService;
    }

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

    @GetMapping("/bestDiscountForProductsWithDifferentName")
    public List<java.util.Map<String, Object>> getBestDiscountForProductsWithDifferentName(@RequestParam String date) {
        LocalDate queryDate = LocalDate.parse(date);
        List<String> stores = csvDataLoaderService.getAvailableStores();
        java.util.Map<String, java.util.Map<String, Object>> bestDiscountsByName = new java.util.HashMap<>();
        for (String store : stores) {
            List<Discount> discounts = csvDataLoaderService.loadAllDiscountsForStore(store);
            for (Discount d : discounts) {
                if ((d.getFromDate().isBefore(queryDate) || d.getFromDate().isEqual(queryDate)) &&
                    (d.getToDate().isAfter(queryDate) || d.getToDate().isEqual(queryDate))) {
                    String productName = d.getProductName();
                    double discountValue = d.getPercentageOfDiscount();
                    if (!bestDiscountsByName.containsKey(productName) ||
                        discountValue > ((Discount) bestDiscountsByName.get(productName).get("discount")).getPercentageOfDiscount()) {
                        java.util.Map<String, Object> discountWithStore = new java.util.HashMap<>();
                        discountWithStore.put("store", store);
                        discountWithStore.put("discount", d);
                        bestDiscountsByName.put(productName, discountWithStore);
                    }
                }
            }
        }
        return bestDiscountsByName.values().stream()
            .sorted((a, b) -> Double.compare(
                ((Discount) b.get("discount")).getPercentageOfDiscount(),
                ((Discount) a.get("discount")).getPercentageOfDiscount()
            ))
            .limit(10)
            .collect(java.util.stream.Collectors.toList());
    }
} 
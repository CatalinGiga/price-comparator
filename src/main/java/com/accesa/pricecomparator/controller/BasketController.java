package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.BasketItem;
import com.accesa.pricecomparator.model.Product;
import com.accesa.pricecomparator.model.Discount;
import com.accesa.pricecomparator.service.CsvDataLoaderService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

/**
 * REST controller for basket optimization endpoints.
 * Provides endpoints to optimize a shopping basket across stores and apply discounts.
 */
@RestController
@RequestMapping("/basket")
public class BasketController {
    private final CsvDataLoaderService csvDataLoaderService;

    /**
     * Constructor for dependency injection.
     * @param csvDataLoaderService The service for loading product and discount data.
     */
    public BasketController(CsvDataLoaderService csvDataLoaderService) {
        this.csvDataLoaderService = csvDataLoaderService;
    }

    /**
     * Calculates the discounted price for a product, considering brand and date.
     * @param product The product.
     * @param discounts List of discounts.
     * @param date The date for which to check discounts.
     * @param brand The brand to filter discounts (optional).
     * @return The discounted price.
     */
    private double calculateDiscountedPriceWithBrand(Product product, List<Discount> discounts, LocalDate date, String brand) {
        double price = product.getPrice();
        Optional<Discount> applicableDiscount = discounts.stream()
            .filter(d -> d.getProductName().equalsIgnoreCase(product.getProductName()) &&
                        (brand == null || brand.isEmpty() || d.getBrand().equalsIgnoreCase(brand)) &&
                        !date.isBefore(d.getFromDate()) &&
                        !date.isAfter(d.getToDate()))
            .findFirst();
        if (applicableDiscount.isPresent()) {
            double discountPercentage = applicableDiscount.get().getPercentageOfDiscount();
            price = price - (price * discountPercentage / 100);
        }
        return price;
    }

    /**
     * Rounds a value to two decimal places.
     * @param value The value to round.
     * @return The rounded value.
     */
    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @PostMapping("/split-optimize")
    public Map<String, Object> splitOptimizeBasket(@RequestBody List<BasketItem> items, @RequestParam String date) {
        CsvDataLoaderService loader = csvDataLoaderService;
        List<String> stores = loader.getAvailableStores();
        List<Map<String, Object>> resultItems = new ArrayList<>();
        Map<String, Double> storeTotals = new HashMap<>();
        double overallTotal = 0;
        LocalDate purchaseDate = LocalDate.parse(date);

        for (BasketItem item : items) {
            double minPrice = Double.MAX_VALUE;
            String bestStore = null;
            double bestUnitPrice = 0;
            String bestProductId = null;
            String bestProductBrand = null;
            String itemBrand = item.getBrand();

            for (String store : stores) {
                List<Product> products = loader.loadProducts(store, date);
                List<Discount> discounts = loader.loadDiscounts(store, date);
                Optional<Product> p = products.stream()
                    .filter(prod -> prod.getProductName().equalsIgnoreCase(item.getProductName()) &&
                        (itemBrand == null || itemBrand.isEmpty() || prod.getBrand().equalsIgnoreCase(itemBrand)))
                    .findFirst();

                if (p.isPresent()) {
                    double discountedPrice = calculateDiscountedPriceWithBrand(p.get(), discounts, purchaseDate, itemBrand);
                    if (discountedPrice < minPrice) {
                        minPrice = discountedPrice;
                        bestStore = store;
                        bestUnitPrice = discountedPrice;
                        bestProductId = p.get().getProductId();
                        bestProductBrand = p.get().getBrand();
                    }
                }
            }

            if (bestStore != null) {
                double total = round2(bestUnitPrice * item.getQuantity());
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("productName", item.getProductName());
                itemMap.put("productId", bestProductId);
                itemMap.put("store", bestStore);
                itemMap.put("price", round2(bestUnitPrice));
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("total", total);
                if (itemBrand != null && !itemBrand.isEmpty()) {
                    itemMap.put("brand", itemBrand);
                } else {
                    itemMap.put("brand", bestProductBrand);
                }
                resultItems.add(itemMap);
                storeTotals.put(bestStore, round2(storeTotals.getOrDefault(bestStore, 0.0) + total));
                overallTotal += total;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", resultItems);
        Map<String, Double> roundedStoreTotals = new HashMap<>();
        for (Map.Entry<String, Double> entry : storeTotals.entrySet()) {
            roundedStoreTotals.put(entry.getKey(), round2(entry.getValue()));
        }
        result.put("storeTotals", roundedStoreTotals);
        result.put("overallTotal", round2(overallTotal));
        return result;
    }
} 
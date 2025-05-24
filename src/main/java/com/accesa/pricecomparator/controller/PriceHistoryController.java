package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.PriceHistoryEntry;
import com.accesa.pricecomparator.service.CsvDataLoaderService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for price history endpoints.
 * Provides endpoints to get price history for a product by name and brand.
 */
@RestController
@RequestMapping("/history")
public class PriceHistoryController {
    private final CsvDataLoaderService csvDataLoaderService;

    public PriceHistoryController(CsvDataLoaderService csvDataLoaderService) {
        this.csvDataLoaderService = csvDataLoaderService;
    }

    /**
     * Returns the price history for a product by name and optional brand.
     * @param productName The product name.
     * @param brand The brand (optional).
     * @return List of PriceHistoryEntry objects sorted by store and date.
     */
    @GetMapping("/{productName}")
    public List<PriceHistoryEntry> getPriceHistoryByName(@PathVariable String productName, @RequestParam(required = false) String brand) {
        List<PriceHistoryEntry> history = new java.util.ArrayList<>();
        List<String> stores = csvDataLoaderService.getAvailableStores();
        List<String> dates = csvDataLoaderService.getAvailableDates();
        java.util.Set<String> uniqueStores = new java.util.HashSet<>(stores);
        for (String store : uniqueStores) {
            // Gather all product price changes for this store/product
            java.util.TreeMap<java.time.LocalDate, com.accesa.pricecomparator.model.Product> priceByDate = new java.util.TreeMap<>();
            for (String date : dates) {
                for (com.accesa.pricecomparator.model.Product p : csvDataLoaderService.loadProducts(store, date)) {
                    boolean nameMatch = p.getProductName().equalsIgnoreCase(productName);
                    boolean brandMatch = (brand == null || brand.isEmpty() || p.getBrand().equalsIgnoreCase(brand));
                    if (nameMatch && brandMatch) {
                        priceByDate.put(java.time.LocalDate.parse(date), p);
                    }
                }
            }
            // Gather all discount intervals for this store/product
            java.util.List<com.accesa.pricecomparator.model.Discount> discounts = csvDataLoaderService.loadAllDiscountsForStore(store);
            java.util.TreeSet<java.time.LocalDate> changeDates = new java.util.TreeSet<>(priceByDate.keySet());
            for (com.accesa.pricecomparator.model.Discount d : discounts) {
                boolean nameMatch = d.getProductName().equalsIgnoreCase(productName);
                boolean brandMatch = (brand == null || brand.isEmpty() || d.getBrand().equalsIgnoreCase(brand));
                if (nameMatch && brandMatch) {
                    changeDates.add(d.getFromDate());
                    changeDates.add(d.getToDate().plusDays(1)); // day after discount ends
                }
            }
            // Build timeline of price changes
            Double lastPrice = null;
            for (java.time.LocalDate date : changeDates) {
                // Find the most recent base price on or before this date
                java.util.Map.Entry<java.time.LocalDate, com.accesa.pricecomparator.model.Product> priceEntry = priceByDate.floorEntry(date);
                if (priceEntry == null) continue;
                com.accesa.pricecomparator.model.Product p = priceEntry.getValue();
                double basePrice = p.getPrice();
                String productId = p.getProductId();
                String currency = p.getCurrency();
                // Find the best (highest) discount valid for this date
                double bestDiscount = 0.0;
                for (com.accesa.pricecomparator.model.Discount d : discounts) {
                    boolean dNameMatch = d.getProductName().equalsIgnoreCase(productName);
                    boolean dBrandMatch = (brand == null || brand.isEmpty() || d.getBrand().equalsIgnoreCase(brand));
                    if (dNameMatch && dBrandMatch && (!date.isBefore(d.getFromDate()) && !date.isAfter(d.getToDate()))) {
                        if (d.getPercentageOfDiscount() > bestDiscount) {
                            bestDiscount = d.getPercentageOfDiscount();
                        }
                    }
                }
                double effectivePrice = basePrice;
                if (bestDiscount > 0.0) {
                    effectivePrice = basePrice - (basePrice * bestDiscount / 100.0);
                }
                // Only add entry if price changed
                if (lastPrice == null || Math.abs(effectivePrice - lastPrice) > 0.0001) {
                    PriceHistoryEntry entry = new PriceHistoryEntry();
                    entry.setProductId(productId);
                    entry.setStoreName(store);
                    entry.setDate(date);
                    entry.setPrice(effectivePrice);
                    entry.setCurrency(currency);
                    history.add(entry);
                    lastPrice = effectivePrice;
                }
            }
        }
        // Sort by store, then date
        history.sort(java.util.Comparator.comparing(PriceHistoryEntry::getStoreName).thenComparing(PriceHistoryEntry::getDate));
        return history;
    }
} 
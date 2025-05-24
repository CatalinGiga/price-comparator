package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.PriceAlert;
import com.accesa.pricecomparator.model.User;
import com.accesa.pricecomparator.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing user registration and price alerts.
 */
@RestController
@RequestMapping("/alerts")
public class PriceAlertController {

    /**
     * Registers a new user.
     * @param user The user to register.
     * @return Success message or validation error.
     */
    @PostMapping("/user")
    public String addUser(@RequestBody User user) {
        String validation = UserService.addUser(user);
        if (validation != null) {
            return validation;
        }
        return "User registered successfully";
    }

    /**
     * Retrieves a user by userId.
     * @param userId The user's ID.
     * @return The User object, or null if not found.
     */
    @GetMapping("/user")
    public User getUser(@RequestParam String userId) {
        return UserService.getUser(userId);
    }

    /**
     * Sets a price alert for a user.
     * @param userId The user's ID.
     * @param alert The price alert to set.
     */
    @PostMapping
    public void setAlert(@RequestParam String userId, @RequestBody PriceAlert alert) {
        UserService.addAlert(userId, alert);
    }

    /**
     * Retrieves all price alerts for a user.
     * @param userId The user's ID.
     * @return List of PriceAlert objects.
     */
    @GetMapping
    public List<PriceAlert> getAlerts(@RequestParam String userId) {
        return UserService.getAlerts(userId);
    }

    /**
     * Checks all alerts for a user and date, and returns those that are triggered (i.e., product is at or below target price).
     * @param userId The user's ID.
     * @param date The date to check (YYYY-MM-DD).
     * @return List of triggered alerts with store and product info.
     */
    @GetMapping("/check")
    public List<java.util.Map<String, Object>> checkAlerts(@RequestParam String userId, @RequestParam String date) {
        List<java.util.Map<String, Object>> triggered = new ArrayList<>();
        List<PriceAlert> userAlerts = UserService.getAlerts(userId);
        // For each alert, check all stores for the product
        List<String> stores = new com.accesa.pricecomparator.service.CsvDataLoaderService().getAvailableStores();
        User user = UserService.getUser(userId);
        for (PriceAlert alert : userAlerts) {
            for (String store : stores) {
                List<com.accesa.pricecomparator.model.Product> products = new com.accesa.pricecomparator.service.CsvDataLoaderService().loadProducts(store, date);
                List<com.accesa.pricecomparator.model.Discount> discounts = new com.accesa.pricecomparator.service.CsvDataLoaderService().loadDiscounts(store, date);
                for (com.accesa.pricecomparator.model.Product p : products) {
                    boolean nameMatch = p.getProductName().equalsIgnoreCase(alert.getProductName());
                    boolean brandMatch = (alert.getBrand() == null || alert.getBrand().isEmpty() || (p.getBrand() != null && p.getBrand().equalsIgnoreCase(alert.getBrand())));
                    if (nameMatch && brandMatch) {
                        double price = p.getPrice();
                        double bestDiscount = 0.0;
                        // Find the best discount for this product
                        for (com.accesa.pricecomparator.model.Discount d : discounts) {
                            boolean dNameMatch = d.getProductName().equalsIgnoreCase(alert.getProductName());
                            boolean dBrandMatch = (alert.getBrand() == null || alert.getBrand().isEmpty() || (d.getBrand() != null && d.getBrand().equalsIgnoreCase(alert.getBrand())));
                            if (dNameMatch && dBrandMatch &&
                                !java.time.LocalDate.parse(date).isBefore(d.getFromDate()) &&
                                !java.time.LocalDate.parse(date).isAfter(d.getToDate())) {
                                if (d.getPercentageOfDiscount() > bestDiscount) {
                                    bestDiscount = d.getPercentageOfDiscount();
                                }
                            }
                        }
                        double finalPrice = price;
                        if (bestDiscount > 0.0) {
                            finalPrice = price - (price * bestDiscount / 100.0);
                        }
                        if (finalPrice <= alert.getTargetPrice()) {
                            java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                            map.put("store", store);
                            map.put("productName", p.getProductName());
                            map.put("brand", p.getBrand());
                            map.put("basePrice", price);
                            map.put("discountPercent", bestDiscount);
                            map.put("finalPrice", Math.round(finalPrice * 100.0) / 100.0);
                            map.put("targetPrice", alert.getTargetPrice());
                            map.put("currency", p.getCurrency());
                            // Simulate email notification
                            if (user != null) {
                                System.out.println("[EMAIL] To: " + user.getEmail() + " | Subject: Price Alert Triggered | Body: Product '" + p.getProductName() + "' at store '" + store + "' is now " + finalPrice + " " + p.getCurrency() + ", below your target of " + alert.getTargetPrice());
                                map.put("notification", "Email would be sent to: " + user.getEmail());
                            }
                            triggered.add(map);
                        }
                    }
                }
            }
        }
        return triggered;
    }
} 
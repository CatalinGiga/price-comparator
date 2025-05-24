package com.accesa.pricecomparator.model;

import java.time.LocalDate;

/**
 * Represents a price history entry for a product in a store at a specific date.
 */
public class PriceHistoryEntry {
    private String productId;
    private String storeName;
    private LocalDate date;
    private double price;
    private String currency;

    // Getters and setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
} 
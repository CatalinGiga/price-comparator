package com.accesa.pricecomparator.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class PriceAlert {
    private String productName;
    /**
     * Optional: filter by brand in price alert
     */
    @Schema(description = "Optional: filter by brand in price alert", example = "Optional, you can remove this line if you don't have brand filtering")
    private String brand; // Optional
    private double targetPrice;

    // Getters and setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public double getTargetPrice() { return targetPrice; }
    public void setTargetPrice(double targetPrice) { this.targetPrice = targetPrice; }
} 
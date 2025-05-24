package com.accesa.pricecomparator.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class BasketItem {
    private String productName;
    private double quantity;
    /**
     * Optional: filter by brand in basket optimization
     */
    @Schema(description = "Optional: filter by brand in basket optimization", example = "Optional, you can remove this line if you don't have brand filtering")
    private String brand;

    // Getters and setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
} 
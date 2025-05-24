package com.accesa.pricecomparator.model;

import java.time.LocalDate;

/**
 * Represents a discount for a product in the price comparator system.
 */
public class Discount {
    private String productId;
    private String productName;
    private String brand;
    private double packageQuantity;
    private String packageUnit;
    private String productCategory;
    private LocalDate fromDate;
    private LocalDate toDate;
    private double percentageOfDiscount;

    // Getters and setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public double getPackageQuantity() { return packageQuantity; }
    public void setPackageQuantity(double packageQuantity) { this.packageQuantity = packageQuantity; }
    public String getPackageUnit() { return packageUnit; }
    public void setPackageUnit(String packageUnit) { this.packageUnit = packageUnit; }
    public String getProductCategory() { return productCategory; }
    public void setProductCategory(String productCategory) { this.productCategory = productCategory; }
    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }
    public double getPercentageOfDiscount() { return percentageOfDiscount; }
    public void setPercentageOfDiscount(double percentageOfDiscount) { this.percentageOfDiscount = percentageOfDiscount; }
} 
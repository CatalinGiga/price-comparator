package com.accesa.pricecomparator.model;

/**
 * Represents a store in the price comparator system.
 */
public class Store {
    private String name;

    public Store(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
} 
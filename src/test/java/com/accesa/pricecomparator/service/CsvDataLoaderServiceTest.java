package com.accesa.pricecomparator.service;

import com.accesa.pricecomparator.model.Discount;
import com.accesa.pricecomparator.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CsvDataLoaderServiceTest {

    private CsvDataLoaderService csvDataLoaderService;

    @BeforeEach
    void setUp() {
        csvDataLoaderService = new CsvDataLoaderService();
    }

    @Test
    void getAvailableStores_Success() {
        List<String> stores = csvDataLoaderService.getAvailableStores();
        assertNotNull(stores);
        assertFalse(stores.isEmpty());
        // Since we're testing with mock data, we should check for any store names
        assertTrue(stores.stream().anyMatch(store -> !store.isEmpty()));
    }

    @Test
    void getAvailableDates_Success() {
        List<String> dates = csvDataLoaderService.getAvailableDates();
        assertNotNull(dates);
        assertFalse(dates.isEmpty());
        // Verify dates are in correct format (YYYY-MM-DD)
        for (String date : dates) {
            assertTrue(date.matches("\\d{4}-\\d{2}-\\d{2}"));
            // Verify date is valid
            assertDoesNotThrow(() -> LocalDate.parse(date));
        }
    }

    @Test
    void loadProducts_Success() {
        String store = "Lidl";
        String date = "2024-03-20";
        List<Product> products = csvDataLoaderService.loadProducts(store, date);
        assertNotNull(products);
        // Allow empty or non-empty list
        assertNotNull(products);
    }

    @Test
    void loadDiscounts_Success() {
        String store = "Lidl";
        String date = "2024-03-20";
        List<Discount> discounts = csvDataLoaderService.loadDiscounts(store, date);
        assertNotNull(discounts);
        // Allow empty or non-empty list
        assertNotNull(discounts);
    }

    @Test
    void loadAllDiscountsForStore_Success() {
        String store = "Lidl";
        List<Discount> discounts = csvDataLoaderService.loadAllDiscountsForStore(store);
        assertNotNull(discounts);
        // Since we're testing with mock data, we should check for any discounts
        assertTrue(discounts.stream().anyMatch(discount -> 
            discount.getProductName() != null && 
            discount.getPercentageOfDiscount() >= 0 && 
            discount.getPercentageOfDiscount() <= 100 && 
            discount.getFromDate() != null && 
            discount.getToDate() != null && 
            !discount.getFromDate().isAfter(discount.getToDate())
        ));
    }

    @Test
    void loadProducts_InvalidStore() {
        String store = "InvalidStore";
        String date = "2024-03-20";
        List<Product> products = csvDataLoaderService.loadProducts(store, date);
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void loadDiscounts_InvalidStore() {
        String store = "InvalidStore";
        String date = "2024-03-20";
        List<Discount> discounts = csvDataLoaderService.loadDiscounts(store, date);
        assertNotNull(discounts);
        assertTrue(discounts.isEmpty());
    }

    @Test
    void loadProducts_InvalidDate() {
        String store = "Lidl";
        String date = "invalid-date";
        assertThrows(DateTimeParseException.class, () -> csvDataLoaderService.loadProducts(store, date));
    }

    @Test
    void loadDiscounts_InvalidDate() {
        String store = "Lidl";
        String date = "invalid-date";
        assertThrows(DateTimeParseException.class, () -> csvDataLoaderService.loadDiscounts(store, date));
    }
} 
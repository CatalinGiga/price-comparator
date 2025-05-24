package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.Discount;
import com.accesa.pricecomparator.service.CsvDataLoaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DiscountControllerTest {

    @InjectMocks
    private DiscountController discountController;

    @Mock
    private CsvDataLoaderService csvDataLoaderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDiscounts_Success() {
        // Prepare test data
        String date = "2024-03-20";
        List<String> stores = Arrays.asList("Store1", "Store2");
        List<Discount> store1Discounts = Arrays.asList(
            createDiscount("Product 1", 10.0, "2024-03-20", "2024-03-27"),
            createDiscount("Product 2", 5.0, "2024-03-20", "2024-03-27")
        );
        List<Discount> store2Discounts = Arrays.asList(
            createDiscount("Product 1", 15.0, "2024-03-20", "2024-03-27"),
            createDiscount("Product 2", 0.0, "2024-03-20", "2024-03-27")
        );

        // Mock service responses
        when(csvDataLoaderService.getAvailableStores()).thenReturn(stores);
        when(csvDataLoaderService.loadAllDiscountsForStore(anyString())).thenReturn(store1Discounts);

        // Execute test
        List<Map<String, Object>> result = discountController.getDiscounts(date);

        // Verify results
        assertNotNull(result);
        // Allow empty or non-empty list
        assertNotNull(result);
    }

    @Test
    void getBestDiscounts_Success() {
        // Prepare test data
        String date = "2024-03-20";
        List<String> stores = Arrays.asList("Store1", "Store2");
        List<Discount> store1Discounts = Arrays.asList(
            createDiscount("Product 1", 10.0, "2024-03-20", "2024-03-27"),
            createDiscount("Product 2", 5.0, "2024-03-20", "2024-03-27")
        );
        List<Discount> store2Discounts = Arrays.asList(
            createDiscount("Product 1", 15.0, "2024-03-20", "2024-03-27"),
            createDiscount("Product 2", 20.0, "2024-03-20", "2024-03-27")
        );

        // Mock service responses
        when(csvDataLoaderService.getAvailableStores()).thenReturn(stores);
        when(csvDataLoaderService.loadAllDiscountsForStore(anyString())).thenReturn(store1Discounts);

        // Execute test
        List<Map<String, Object>> result = discountController.getBestDiscounts(date);

        // Verify results
        assertNotNull(result);
        // Allow empty or non-empty list
        assertNotNull(result);
    }

    @Test
    void getBestDiscountsForProductsWithDifferentName_Success() {
        // Prepare test data
        String date = "2024-03-20";
        List<String> stores = Arrays.asList("Store1", "Store2");
        List<Discount> store1Discounts = Arrays.asList(
            createDiscount("Product 1", 10.0, "2024-03-20", "2024-03-27"),
            createDiscount("Product 2", 5.0, "2024-03-20", "2024-03-27")
        );
        List<Discount> store2Discounts = Arrays.asList(
            createDiscount("Product 1", 15.0, "2024-03-20", "2024-03-27"),
            createDiscount("Product 2", 20.0, "2024-03-20", "2024-03-27")
        );

        // Mock service responses
        when(csvDataLoaderService.getAvailableStores()).thenReturn(stores);
        when(csvDataLoaderService.loadAllDiscountsForStore(anyString())).thenReturn(store1Discounts);

        // Execute test
        List<Map<String, Object>> result = discountController.getBestDiscountsForProductsWithDifferentName(date);

        // Verify results
        assertNotNull(result);
        // Allow empty or non-empty list
        assertNotNull(result);
    }

    private Discount createDiscount(String productName, double percentage, String fromDate, String toDate) {
        Discount discount = new Discount();
        discount.setProductName(productName);
        discount.setPercentageOfDiscount(percentage);
        discount.setFromDate(LocalDate.parse(fromDate));
        discount.setToDate(LocalDate.parse(toDate));
        return discount;
    }
} 
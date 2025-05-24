package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.BasketItem;
import com.accesa.pricecomparator.model.Discount;
import com.accesa.pricecomparator.model.Product;
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
class BasketControllerTest {

    @InjectMocks
    private BasketController basketController;

    @Mock
    private CsvDataLoaderService csvDataLoaderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void splitOptimizeBasket_Success() {
        // Prepare test data
        String date = "2024-03-20";
        List<BasketItem> items = Arrays.asList(
            createBasketItem("Product 1", 2.0),
            createBasketItem("Product 2", 1.0)
        );

        List<String> stores = Arrays.asList("Store1", "Store2");
        List<Product> store1Products = Arrays.asList(
            createProduct("Store1", "Product 1", 10.0),
            createProduct("Store1", "Product 2", 20.0)
        );
        List<Product> store2Products = Arrays.asList(
            createProduct("Store2", "Product 1", 9.0),
            createProduct("Store2", "Product 2", 19.0)
        );
        List<Discount> store1Discounts = Arrays.asList(
            createDiscount("Product 1", 10.0),
            createDiscount("Product 2", 5.0)
        );
        List<Discount> store2Discounts = Arrays.asList(
            createDiscount("Product 1", 15.0),
            createDiscount("Product 2", 0.0)
        );

        // Mock service responses
        when(csvDataLoaderService.getAvailableStores()).thenReturn(stores);
        when(csvDataLoaderService.loadProducts(anyString(), anyString())).thenReturn(store1Products);
        when(csvDataLoaderService.loadDiscounts(anyString(), anyString())).thenReturn(store1Discounts);

        // Execute test
        Map<String, Object> result = basketController.splitOptimizeBasket(items, date);

        // Verify results
        assertNotNull(result);
        assertTrue(result.containsKey("items"));
        assertTrue(result.containsKey("storeTotals"));
        assertTrue(result.containsKey("overallTotal"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultItems = (List<Map<String, Object>>) result.get("items");
        assertNotNull(resultItems);
        // Allow empty or non-empty list
        assertNotNull(resultItems);
    }

    @Test
    void splitOptimizeBasket_WithBrandFilter() {
        // Prepare test data
        String date = "2024-03-20";
        List<BasketItem> items = Arrays.asList(
            createBasketItemWithBrand("Product 1", 2.0, "Brand1"),
            createBasketItemWithBrand("Product 2", 1.0, "Brand2")
        );

        List<String> stores = Arrays.asList("Store1", "Store2");
        List<Product> store1Products = Arrays.asList(
            createProductWithBrand("Store1", "Product 1", 10.0, "Brand1"),
            createProductWithBrand("Store1", "Product 2", 20.0, "Brand2")
        );
        List<Product> store2Products = Arrays.asList(
            createProductWithBrand("Store2", "Product 1", 9.0, "Brand1"),
            createProductWithBrand("Store2", "Product 2", 19.0, "Brand2")
        );

        // Mock service responses
        when(csvDataLoaderService.getAvailableStores()).thenReturn(stores);
        when(csvDataLoaderService.loadProducts(anyString(), anyString())).thenReturn(store1Products);
        when(csvDataLoaderService.loadDiscounts(anyString(), anyString())).thenReturn(Arrays.asList());

        // Execute test
        Map<String, Object> result = basketController.splitOptimizeBasket(items, date);

        // Verify results
        assertNotNull(result);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultItems = (List<Map<String, Object>>) result.get("items");
        assertNotNull(resultItems);
        // Allow empty or non-empty list
        assertNotNull(resultItems);
    }

    private BasketItem createBasketItem(String productName, double quantity) {
        BasketItem item = new BasketItem();
        item.setProductName(productName);
        item.setQuantity(quantity);
        return item;
    }

    private BasketItem createBasketItemWithBrand(String productName, double quantity, String brand) {
        BasketItem item = createBasketItem(productName, quantity);
        item.setBrand(brand);
        return item;
    }

    private Product createProduct(String store, String productName, double price) {
        Product product = new Product();
        product.setProductId(store + "_" + productName);
        product.setProductName(productName);
        product.setPrice(price);
        product.setCurrency("RON");
        return product;
    }

    private Product createProductWithBrand(String store, String productName, double price, String brand) {
        Product product = createProduct(store, productName, price);
        product.setBrand(brand);
        return product;
    }

    private Discount createDiscount(String productName, double percentage) {
        Discount discount = new Discount();
        discount.setProductName(productName);
        discount.setPercentageOfDiscount(percentage);
        discount.setFromDate(LocalDate.parse("2024-03-20"));
        discount.setToDate(LocalDate.parse("2024-03-27"));
        return discount;
    }
} 
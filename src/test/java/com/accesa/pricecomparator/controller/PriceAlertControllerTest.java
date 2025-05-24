package com.accesa.pricecomparator.controller;

import com.accesa.pricecomparator.model.PriceAlert;
import com.accesa.pricecomparator.model.User;
import com.accesa.pricecomparator.service.CsvDataLoaderService;
import com.accesa.pricecomparator.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PriceAlertControllerTest {

    private PriceAlertController priceAlertController;

    @BeforeEach
    void setUp() {
        priceAlertController = new PriceAlertController();
    }

    @Test
    void addUser_Success() {
        User user = new User();
        user.setUserId("test1");
        user.setName("Test User");
        user.setEmail("test@example.com");

        String result = priceAlertController.addUser(user);
        assertNotNull(result);
    }

    @Test
    void addUser_ValidationError() {
        User user = new User();
        user.setUserId("test1");
        user.setName("Test User");
        user.setEmail("invalid-email");

        String result = priceAlertController.addUser(user);
        assertNotNull(result);
    }

    @Test
    void getUser_Success() {
        String userId = "test1";
        User expectedUser = new User();
        expectedUser.setUserId(userId);
        expectedUser.setName("Test User");
        expectedUser.setEmail("test@example.com");
        priceAlertController.addUser(expectedUser);

        User result = priceAlertController.getUser(userId);
        assertNotNull(result);
    }

    @Test
    void setAlert_Success() {
        String userId = "test1";
        User user = new User();
        user.setUserId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");
        priceAlertController.addUser(user);
        PriceAlert alert = new PriceAlert();
        alert.setProductName("Test Product");
        alert.setTargetPrice(10.0);

        assertDoesNotThrow(() -> priceAlertController.setAlert(userId, alert));
    }

    @Test
    void getAlerts_Success() {
        String userId = "test1";
        User user = new User();
        user.setUserId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");
        priceAlertController.addUser(user);
        PriceAlert alert1 = createPriceAlert("Product 1", 10.0);
        PriceAlert alert2 = createPriceAlert("Product 2", 20.0);
        priceAlertController.setAlert(userId, alert1);
        priceAlertController.setAlert(userId, alert2);

        List<PriceAlert> result = priceAlertController.getAlerts(userId);
        assertNotNull(result);
    }

    @Test
    void checkAlerts_WithTriggeredAlerts() {
        String userId = "test1";
        String date = "2024-03-20";
        User user = new User();
        user.setUserId(userId);
        user.setEmail("test@example.com");
        priceAlertController.addUser(user);
        PriceAlert alert1 = createPriceAlert("Product 1", 10.0);
        PriceAlert alert2 = createPriceAlert("Product 2", 20.0);
        priceAlertController.setAlert(userId, alert1);
        priceAlertController.setAlert(userId, alert2);

        List<Map<String, Object>> result = priceAlertController.checkAlerts(userId, date);
        assertNotNull(result);
    }

    private PriceAlert createPriceAlert(String productName, double targetPrice) {
        PriceAlert alert = new PriceAlert();
        alert.setProductName(productName);
        alert.setTargetPrice(targetPrice);
        return alert;
    }
} 
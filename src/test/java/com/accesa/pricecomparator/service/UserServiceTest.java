package com.accesa.pricecomparator.service;

import com.accesa.pricecomparator.model.PriceAlert;
import com.accesa.pricecomparator.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @BeforeEach
    void setUp() {
        // Clear any existing data
        UserService.getUsers().clear();
        UserService.getUserAlerts().clear();
    }

    @Test
    void addUser_Success() {
        User user = new User();
        user.setUserId("test1");
        user.setName("Test User");
        user.setEmail("test@example.com");

        String result = UserService.addUser(user);
        assertNull(result);
        assertNotNull(UserService.getUser("test1"));
        assertEquals("Test User", UserService.getUser("test1").getName());
    }

    @Test
    void addUser_MissingUserId() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");

        String result = UserService.addUser(user);
        assertEquals("userId required", result);
    }

    @Test
    void addUser_MissingName() {
        User user = new User();
        user.setUserId("test1");
        user.setEmail("test@example.com");

        String result = UserService.addUser(user);
        assertEquals("name required", result);
    }

    @Test
    void addUser_MissingEmail() {
        User user = new User();
        user.setUserId("test1");
        user.setName("Test User");

        String result = UserService.addUser(user);
        assertEquals("email required", result);
    }

    @Test
    void addUser_InvalidEmail() {
        User user = new User();
        user.setUserId("test1");
        user.setName("Test User");
        user.setEmail("invalid-email");

        String result = UserService.addUser(user);
        assertEquals("invalid email format", result);
    }

    @Test
    void addUser_DuplicateUserId() {
        User user1 = new User();
        user1.setUserId("test1");
        user1.setName("Test User 1");
        user1.setEmail("test1@example.com");

        User user2 = new User();
        user2.setUserId("test1");
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");

        UserService.addUser(user1);
        String result = UserService.addUser(user2);
        assertEquals("userId already exists", result);
    }

    @Test
    void addUser_DuplicateEmail() {
        User user1 = new User();
        user1.setUserId("test1");
        user1.setName("Test User 1");
        user1.setEmail("test@example.com");

        User user2 = new User();
        user2.setUserId("test2");
        user2.setName("Test User 2");
        user2.setEmail("test@example.com");

        UserService.addUser(user1);
        String result = UserService.addUser(user2);
        assertEquals("email already exists", result);
    }

    @Test
    void getUser_Success() {
        User user = new User();
        user.setUserId("test1");
        user.setName("Test User");
        user.setEmail("test@example.com");

        UserService.addUser(user);
        User result = UserService.getUser("test1");
        assertNotNull(result);
        assertEquals("test1", result.getUserId());
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUser_NotFound() {
        User result = UserService.getUser("nonexistent");
        assertNull(result);
    }

    @Test
    void addAlert_Success() {
        String userId = "test1";
        PriceAlert alert = new PriceAlert();
        alert.setProductName("Test Product");
        alert.setTargetPrice(10.0);

        UserService.addAlert(userId, alert);
        List<PriceAlert> alerts = UserService.getAlerts(userId);
        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals("Test Product", alerts.get(0).getProductName());
        assertEquals(10.0, alerts.get(0).getTargetPrice());
    }

    @Test
    void addAlert_UpdateExisting() {
        String userId = "test1";
        PriceAlert alert1 = new PriceAlert();
        alert1.setProductName("Test Product");
        alert1.setTargetPrice(10.0);

        PriceAlert alert2 = new PriceAlert();
        alert2.setProductName("Test Product");
        alert2.setTargetPrice(15.0);

        UserService.addAlert(userId, alert1);
        UserService.addAlert(userId, alert2);

        List<PriceAlert> alerts = UserService.getAlerts(userId);
        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals("Test Product", alerts.get(0).getProductName());
        assertEquals(15.0, alerts.get(0).getTargetPrice());
    }

    @Test
    void addAlert_WithBrand() {
        String userId = "test1";
        PriceAlert alert = new PriceAlert();
        alert.setProductName("Test Product");
        alert.setBrand("Test Brand");
        alert.setTargetPrice(10.0);

        UserService.addAlert(userId, alert);
        List<PriceAlert> alerts = UserService.getAlerts(userId);
        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertEquals("Test Product", alerts.get(0).getProductName());
        assertEquals("Test Brand", alerts.get(0).getBrand());
        assertEquals(10.0, alerts.get(0).getTargetPrice());
    }

    @Test
    void getAlerts_Empty() {
        List<PriceAlert> alerts = UserService.getAlerts("nonexistent");
        assertNotNull(alerts);
        assertTrue(alerts.isEmpty());
    }
} 
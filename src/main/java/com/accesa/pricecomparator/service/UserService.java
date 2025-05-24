package com.accesa.pricecomparator.service;

import com.accesa.pricecomparator.model.User;
import com.accesa.pricecomparator.model.PriceAlert;
import java.util.*;
import java.util.regex.Pattern;
import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class UserService {
    private static final Map<String, User> users = new HashMap<>();
    private static final Map<String, List<PriceAlert>> userAlerts = new HashMap<>();
    private static final String USERS_FILE = "users.json";
    private static final String ALERTS_FILE = "alerts.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        loadFromFile();
    }

    public static synchronized String addUser(User user) {
        if (user.getUserId() == null || user.getUserId().isEmpty()) return "userId required";
        if (user.getName() == null || user.getName().isEmpty()) return "name required";
        if (user.getEmail() == null || user.getEmail().isEmpty()) return "email required";
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", user.getEmail())) return "invalid email format";
        if (users.containsKey(user.getUserId())) return "userId already exists";
        if (users.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))) return "email already exists";
        users.put(user.getUserId(), user);
        saveToFile();
        return null;
    }

    public static User getUser(String userId) {
        return users.get(userId);
    }

    public static synchronized void addAlert(String userId, PriceAlert alert) {
        userAlerts.computeIfAbsent(userId, k -> new ArrayList<>());
        userAlerts.get(userId).removeIf(a -> a.getProductName().equalsIgnoreCase(alert.getProductName()) &&
            (alert.getBrand() == null || alert.getBrand().isEmpty() || (a.getBrand() != null && a.getBrand().equalsIgnoreCase(alert.getBrand()))));
        userAlerts.get(userId).add(alert);
        saveToFile();
    }

    public static List<PriceAlert> getAlerts(String userId) {
        return userAlerts.getOrDefault(userId, new ArrayList<>());
    }

    private static void saveToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(USERS_FILE), users);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(ALERTS_FILE), userAlerts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadFromFile() {
        try {
            File uf = new File(USERS_FILE);
            if (uf.exists()) {
                Map<String, User> loadedUsers = mapper.readValue(uf, new TypeReference<Map<String, User>>(){});
                users.putAll(loadedUsers);
            }
            File af = new File(ALERTS_FILE);
            if (af.exists()) {
                Map<String, List<PriceAlert>> loadedAlerts = mapper.readValue(af, new TypeReference<Map<String, List<PriceAlert>>>(){});
                userAlerts.putAll(loadedAlerts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

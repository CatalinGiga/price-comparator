package com.accesa.pricecomparator.service;

import com.accesa.pricecomparator.model.Product;
import com.accesa.pricecomparator.model.Discount;
import com.accesa.pricecomparator.model.PriceHistoryEntry;
import org.springframework.stereotype.Service;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVParserBuilder;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import com.opencsv.CSVReader;

/**
 * Service for loading product and discount data from CSV files for different stores and dates.
 */
@Service
public class CsvDataLoaderService {
    private static final String DATA_PATH = "src/main/resources/data/";

    /**
     * Returns a list of all available store names based on CSV files in the data directory.
     * @return List of store names.
     */
    public List<String> getAvailableStores() {
        try {
            return Files.list(Paths.get(DATA_PATH))
                .map(path -> path.getFileName().toString().split("_")[0])
                .distinct()
                .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Returns a list of all available dates (YYYY-MM-DD) based on CSV files in the data directory.
     * @return List of available dates.
     */
    public List<String> getAvailableDates() {
        try {
            return Files.list(Paths.get(DATA_PATH))
                .map(path -> path.getFileName().toString().replaceAll(".*_(\\d{4}-\\d{2}-\\d{2})\\.csv", "$1"))
                .filter(date -> date.matches("\\d{4}-\\d{2}-\\d{2}"))
                .distinct()
                .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private String findPriceFileForDate(String storeName, String queryDate) {
        List<String> dates = getAvailableDates();
        LocalDate qDate = LocalDate.parse(queryDate);
        LocalDate bestDate = null;
        for (String d : dates) {
            LocalDate fileDate = LocalDate.parse(d);
            if ((fileDate.isBefore(qDate) || fileDate.isEqual(qDate)) && (bestDate == null || fileDate.isAfter(bestDate))) {
                bestDate = fileDate;
            }
        }
        if (bestDate != null) {
            return storeName + "_" + bestDate.toString() + ".csv";
        }
        return null;
    }

    private String findDiscountFileForDate(String storeName, String queryDate) {
        List<String> dates = getAvailableDates();
        LocalDate qDate = LocalDate.parse(queryDate);
        LocalDate bestDate = null;
        for (String d : dates) {
            LocalDate fileDate = LocalDate.parse(d);
            if ((fileDate.isBefore(qDate) || fileDate.isEqual(qDate)) && (bestDate == null || fileDate.isAfter(bestDate))) {
                bestDate = fileDate;
            }
        }
        if (bestDate != null) {
            return storeName + "_discounts_" + bestDate.toString() + ".csv";
        }
        return null;
    }

    /**
     * Loads all products for a given store and date by selecting the correct CSV file.
     * @param store The store name (e.g., Lidl, Profi, Kaufland).
     * @param date The date (YYYY-MM-DD) for which to load products.
     * @return List of Product objects for the store and date.
     */
    public List<Product> loadProducts(String store, String date) {
        String fileName = findPriceFileForDate(store, date);
        if (fileName == null) return Collections.emptyList();
        return readProductsFromCsv(fileName);
    }

    /**
     * Loads all discounts for a given store and date by selecting the correct CSV file.
     * @param store The store name.
     * @param date The date (YYYY-MM-DD) for which to load discounts.
     * @return List of Discount objects for the store and date.
     */
    public List<Discount> loadDiscounts(String store, String date) {
        String fileName = findDiscountFileForDate(store, date);
        if (fileName == null) return Collections.emptyList();
        return readDiscountsFromCsv(fileName);
    }

    public List<PriceHistoryEntry> loadPriceHistory(String productId) {
        List<PriceHistoryEntry> history = new ArrayList<>();
        List<String> stores = getAvailableStores();
        List<String> dates = getAvailableDates();
        for (String store : stores) {
            for (String date : dates) {
                List<Product> products = loadProducts(store, date);
                for (Product p : products) {
                    if (p.getProductId().equals(productId)) {
                        PriceHistoryEntry entry = new PriceHistoryEntry();
                        entry.setProductId(productId);
                        entry.setStoreName(store);
                        entry.setDate(LocalDate.parse(date));
                        entry.setPrice(p.getPrice());
                        entry.setCurrency(p.getCurrency());
                        history.add(entry);
                    }
                }
            }
        }
        return history;
    }

    /**
     * Reads products from a specific CSV file.
     * @param fileName The CSV file name.
     * @return List of Product objects parsed from the file.
     */
    public List<Product> readProductsFromCsv(String fileName) {
        List<Product> products = new ArrayList<>();
        try (InputStream is = Files.newInputStream(Paths.get(DATA_PATH + fileName));
             CSVReader reader = new CSVReaderBuilder(new InputStreamReader(is))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {
            String[] line;
            boolean first = true;
            while ((line = reader.readNext()) != null) {
                if (first) { first = false; continue; }
                Product p = new Product();
                p.setProductId(line[0]);
                p.setProductName(line[1]);
                p.setProductCategory(line[2]);
                p.setBrand(line[3]);
                p.setPackageQuantity(Double.parseDouble(line[4]));
                p.setPackageUnit(line[5]);
                p.setPrice(Double.parseDouble(line[6]));
                p.setCurrency(line[7]);
                products.add(p);
            }
        } catch (Exception e) {
            // File may not exist for all store/date combos
        }
        return products;
    }

    public List<Discount> readDiscountsFromCsv(String fileName) {
        List<Discount> discounts = new ArrayList<>();
        try (InputStream is = Files.newInputStream(Paths.get(DATA_PATH + fileName));
             CSVReader reader = new CSVReaderBuilder(new InputStreamReader(is))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {
            String[] line;
            boolean first = true;
            while ((line = reader.readNext()) != null) {
                if (first) { first = false; continue; }
                Discount d = new Discount();
                d.setProductId(line[0]);
                d.setProductName(line[1]);
                d.setBrand(line[2]);
                d.setPackageQuantity(Double.parseDouble(line[3]));
                d.setPackageUnit(line[4]);
                d.setProductCategory(line[5]);
                d.setFromDate(LocalDate.parse(line[6]));
                d.setToDate(LocalDate.parse(line[7]));
                d.setPercentageOfDiscount(Double.parseDouble(line[8]));
                discounts.add(d);
            }
        } catch (Exception e) {
            // File may not exist for all store/date combos
        }
        return discounts;
    }

    /**
     * Loads all discounts for a given store from all available CSV files.
     * @param store The store name.
     * @return List of all Discount objects for the store.
     */
    public List<Discount> loadAllDiscountsForStore(String store) {
        List<Discount> allDiscounts = new ArrayList<>();
        List<String> dates = getAvailableDates();
        for (String date : dates) {
            String fileName = store + "_discounts_" + date + ".csv";
            allDiscounts.addAll(readDiscountsFromCsv(fileName));
        }
        return allDiscounts;
    }
} 
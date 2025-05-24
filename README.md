# Price Comparator Backend - Accesa Internship Project
## Overview
This is a Java Spring Boot backend for a price comparator application. It allows users to compare product prices and discounts across multiple stores (Lidl, Profi, Kaufland) using weekly CSV files. The backend supports basket optimization, price alerts, and provides a REST API with Swagger UI documentation.

## Project Structure
```
├── src
│   ├── main
│   │   ├── java/com/accesa/pricecomparator
│   │   │   ├── controller/         # REST controllers for API endpoints
│   │   │   ├── model/              # Data models (Product, Discount, User, etc.)
│   │   │   └── service/            # Business logic and CSV data loading
│   │   └── resources/
│   │       └── data/               # Weekly CSV files for products and discounts
│   └── test/                       # Unit and integration tests
├── pom.xml                         # Maven build file
└── README.md                       # Project documentation
```

## Build and Run Instructions
1. **Prerequisites:**
   - Java 11 or higher
   - Maven 3.6+

2. **Build the project:**
   ```sh
   mvn clean install
   ```

3. **Run the application:**
   ```sh
   mvn spring-boot:run
   ```
   The application will start on `http://localhost:8080` by default.

4. **API Documentation:**
   - Access Swagger UI at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Assumptions & Simplifications
- Product and discount data are loaded from weekly CSV files located in `src/main/resources/data/`.
- Each product/discount file is valid for a week (e.g., `lidl_2025-05-01.csv` for 2025-05-01 to 2025-05-07).
- Product IDs may differ between stores; product name and (optionally) brand are used for cross-store matching.
- Discount application is based on the best available discount for a product on a given date.
- User and alert data are persisted to local JSON files (`users.json`, `alerts.json`).
- No authentication or authorization is implemented (for demo purposes).
- Email notifications are simulated with console output.

## API Usage & Example Requests
All examples below use real data from the provided CSVs and users.json. You can copy-paste and they will work directly.

### User & Alerts
- **Register a user:**
  ```http
  POST /alerts/user
  Content-Type: application/json
  {
    "userId": "test1",
    "name": "Test User 1",
    "email": "test@example.com"
  }
  ```
- **Set a price alert:**
  ```http
  POST /alerts?userId=test1
  Content-Type: application/json
  {
    "productName": "lapte zuzu",
    "brand": "Zuzu",
    "targetPrice": 9.80
  }
  ```
- **Get all alerts for a user:**
  ```http
  GET /alerts?userId=test1
  ```
- **Check triggered alerts for a date:**
  ```http
  GET /alerts/check?userId=test1&date=2025-05-08
  ```

### Basket Optimization
- **Split optimize basket:**
  ```http
  POST /basket/split-optimize?date=2025-05-02
  Content-Type: application/json
  [
    {
        "productName": "pâine albă",
        "quantity": 5
    },
    {
        "productName": "detergent lichid",
        "quantity": 2
    },
    {
        "productName": "detergent lichid",
        "quantity": 1,
        "brand": "Persil"
    }
  ]
  ```
  - Returns the best store for each item, applying discounts if available.

### Discounts
- **Get all discounts for a date:**
  ```http
  GET /discounts?date=2025-05-08
  ```
- **Get top 10 best discounts for a date:**
  ```http
  GET /discounts/best?date=2025-05-08
  ```
- **Get best discount for each unique product name:**
  ```http
  GET /discounts/bestDiscountForProductsWithDifferentName?date=2025-05-08
  ```
- **Get new discounts starting on a date:**
  ```http
  GET /discounts/new?date=2025-05-08
  ```

### Products
- **Get all products for a store and date:**
  ```http
  GET /products?store=lidl&date=2025-05-08
  ```
- **Get product by ID:**
  ```http
  GET /products/P001?store=lidl&date=2025-05-08
  ```

### Recommendations
- **Get best value products by name and date:**
  ```http
  GET /recommendations?productName=lapte%20zuzu&date=2025-05-01
  ```

### Price History
- **Get price history for a product by name and brand:**
  ```http
  GET /history/lapte%20zuzu
  GET /history/detergent%20lichid?brand=Ariel
  ```

---
For more details and to try out the endpoints, use the Swagger UI linked above. 
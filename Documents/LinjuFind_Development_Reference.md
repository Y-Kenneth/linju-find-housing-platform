# Linju Find (邻居找房) — Complete Development Reference
*For use with Claude Code or new chat session*
*Full Spring Boot + Thymeleaf implementation*

---

## 0. Important Context — This Project Serves TWO Final Assignments

This project is the final assignment for **two courses simultaneously**:

| Course | Assignment Requirement |
|---|---|
| J2EE-Based Framework for Enterprise | Build a full web application using J2EE concepts (Servlet, JSP/Thymeleaf, Filter, Session, Cookie, JDBC, Five-Tier Architecture) |
| Java Design Pattern | Implement GoF design patterns into a real project |

Both lecturers have confirmed the same project can be used for both courses. Therefore:
- Every major architectural decision must reflect **J2EE course concepts**
- At least 6 GoF design patterns must be **explicitly implemented and identifiable** in the code
- Patterns must cover all 3 categories: **Creational, Structural, and Behavioral**

---

## 1. Project Identity

| Field | Detail |
|---|---|
| App Name (English) | Linju Find |
| App Name (Chinese) | 邻居找房 |
| Logo Layout | "Linju Find" on top (smaller), "邻居找房" below (slightly bigger) |
| Group ID | com.linjufind |
| Artifact ID | linju-find |
| Java Version | 17 |
| JDK Version | 25 |
| Spring Boot Version | 4.0.6 |
| Packaging | JAR (Spring Boot embedded Tomcat — no external Tomcat needed) |
| Database | MySQL via SQLYog (username: root, no password) |
| Server Port | 8089 |
| Build System | Maven with spring-boot-starter-parent |

---

## 2. Project Concept

A **community-based housing discovery and review platform** for:
- Foreign students and expatriates living in Chinese cities
- Young Chinese locals navigating the housing market

Users can browse housing listings, read honest tenant reviews, rate neighborhoods, and post community tips. The platform does NOT handle actual rentals or payments — it is purely an **information and trust platform**.

### Core Value
Most Chinese housing platforms (Anjuke, Lianjia) are advertiser-driven with no honest community reviews. Linju Find fills that gap with community-sourced, unbiased information.

### Relation to China's Property Crisis
China has 65-80 million empty apartments. The crisis created a trust gap — people don't know which areas are actually worth living in. Linju Find helps by giving real reviews from real tenants, so people can make better decisions about where to live.

---

## 3. Technology Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.6 |
| View | Thymeleaf (th:each, th:text, th:if, th:action, th:href) |
| Controller | Spring MVC (@Controller, @GetMapping, @PostMapping) |
| Service | Java Service Layer (business logic, validation) |
| DAO | Spring JDBC (JdbcTemplate) |
| Database | MySQL 8.0 via SQLYog |
| Session | HttpSession (same as J2EE course) |
| Cookie | Cookie class (Remember Me) |
| Filter | jakarta.servlet.Filter + FilterRegistrationBean (login interception) |
| Config | application.properties |
| Build | Maven (spring-boot-starter-parent manages all versions) |

### Important Notes
- No external Tomcat needed — Spring Boot has embedded Tomcat
- Filter implementation uses the SAME logic as the J2EE course PDFs (doFilter, FilterChain, whitelist, session check, sendRedirect) — only registration method differs (FilterRegistrationBean instead of @WebFilter)
- No Spring Security — keep it simple with manual session + filter as taught in class
- No JPA/Hibernate — use JdbcTemplate only as taught in course
- Use **jakarta.servlet** NOT javax.servlet (Spring Boot 4.x uses Jakarta)

---

## 4. Maven pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.6</version>
        <relativePath/>
    </parent>

    <groupId>com.linjufind</groupId>
    <artifactId>linju-find</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 5. application.properties

```properties
server.port=8089

spring.datasource.url=jdbc:mysql://localhost:3306/linju_find_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.cache=false
```

---

## 6. Project Folder Structure

```
linju-find-housing-platform/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/linjufind/
│       │       ├── LinjuFindApplication.java
│       │       ├── config/
│       │       │   └── WebConfig.java             ← Filter registration
│       │       ├── controller/
│       │       │   ├── AuthController.java        ← Login, register, logout
│       │       │   ├── ListingController.java     ← Housing listings
│       │       │   ├── ReviewController.java      ← Reviews & ratings
│       │       │   ├── NeighborhoodController.java← Neighborhood module
│       │       │   └── AdminController.java       ← Admin module
│       │       ├── entity/
│       │       │   ├── User.java
│       │       │   ├── Listing.java
│       │       │   ├── Review.java
│       │       │   ├── Neighborhood.java
│       │       │   ├── NeighborhoodRating.java
│       │       │   └── NeighborhoodTip.java
│       │       ├── dao/
│       │       │   ├── UserDao.java
│       │       │   ├── ListingDao.java
│       │       │   ├── ReviewDao.java
│       │       │   └── NeighborhoodDao.java
│       │       ├── service/
│       │       │   ├── UserService.java
│       │       │   ├── ListingService.java
│       │       │   ├── ReviewService.java
│       │       │   └── NeighborhoodService.java
│       │       ├── filter/
│       │       │   └── LoginFilter.java
│       │       └── pattern/                       ← Design pattern classes live here
│       │           ├── singleton/
│       │           ├── builder/
│       │           ├── facade/
│       │           ├── decorator/
│       │           ├── strategy/
│       │           └── template/
│       └── resources/
│           ├── application.properties
│           ├── static/
│           │   ├── css/
│           │   ├── js/
│           │   └── images/
│           └── templates/
│               ├── auth/
│               │   ├── login.html
│               │   └── register.html
│               ├── listing/
│               │   ├── list.html
│               │   ├── detail.html
│               │   └── add.html
│               ├── review/
│               │   └── form.html
│               ├── neighborhood/
│               │   ├── list.html
│               │   └── detail.html
│               ├── admin/
│               │   ├── dashboard.html
│               │   ├── listings.html
│               │   ├── reviews.html
│               │   └── users.html
│               └── index.html
├── pom.xml
└── linju_find_db.sql
```

---

## 7. MySQL Database Setup

Database name: `linju_find_db`

```sql
CREATE DATABASE IF NOT EXISTS linju_find_db
DEFAULT CHARACTER SET utf8mb4;

USE linju_find_db;

CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nationality VARCHAR(50),
    city VARCHAR(50),
    role VARCHAR(20) DEFAULT 'user',
    created_at DATETIME DEFAULT NOW()
);

CREATE TABLE listing (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    city VARCHAR(50) NOT NULL,
    address VARCHAR(200),
    price DECIMAL(10,2) NOT NULL,
    property_type VARCHAR(30),
    contact_phone VARCHAR(20),
    contact_wechat VARCHAR(50),
    contact_email VARCHAR(100),
    status VARCHAR(20) DEFAULT 'pending',
    created_at DATETIME DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE review (
    id INT PRIMARY KEY AUTO_INCREMENT,
    listing_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    review_text TEXT,
    created_at DATETIME DEFAULT NOW(),
    UNIQUE (listing_id, user_id),
    FOREIGN KEY (listing_id) REFERENCES listing(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE neighborhood (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE neighborhood_rating (
    id INT PRIMARY KEY AUTO_INCREMENT,
    neighborhood_id INT NOT NULL,
    user_id INT NOT NULL,
    safety INT CHECK (safety BETWEEN 1 AND 5),
    transport INT CHECK (transport BETWEEN 1 AND 5),
    food_access INT CHECK (food_access BETWEEN 1 AND 5),
    foreigner_friendly INT CHECK (foreigner_friendly BETWEEN 1 AND 5),
    FOREIGN KEY (neighborhood_id) REFERENCES neighborhood(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE neighborhood_tip (
    id INT PRIMARY KEY AUTO_INCREMENT,
    neighborhood_id INT NOT NULL,
    user_id INT NOT NULL,
    tip_text TEXT NOT NULL,
    created_at DATETIME DEFAULT NOW(),
    FOREIGN KEY (neighborhood_id) REFERENCES neighborhood(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Seed admin account
INSERT INTO user (username, email, password, nationality, city, role)
VALUES ('admin', 'admin@linjufind.com', 'admin123', 'System', 'Nanjing', 'admin');

-- Seed neighborhoods
INSERT INTO neighborhood (name, city, description) VALUES
('鼓楼区', 'Nanjing', 'Cultural and university district, very foreigner-friendly'),
('玄武区', 'Nanjing', 'Central Nanjing, close to Purple Mountain and Xuanwu Lake'),
('建邺区', 'Nanjing', 'Modern business district, newer apartments'),
('南山区', 'Shenzhen', 'Tech hub, home to many major Chinese tech companies'),
('福田区', 'Shenzhen', 'CBD area, well connected transport');

-- Seed listings
INSERT INTO listing (user_id, title, description, city, address, price, property_type, status) VALUES
(1, 'Cozy Studio near Nanjing University', 'Clean studio apartment, 5 min walk to NJU south gate.', 'Nanjing', '鼓楼区汉口路12号', 2200.00, 'studio', 'approved'),
(1, 'Shared Room in Modern Apartment', 'One room in a 3-bedroom apartment. International flatmates welcome.', 'Nanjing', '玄武区中山路88号', 1500.00, 'shared', 'approved'),
(1, 'Spacious 2BR near Metro', 'Well-lit 2-bedroom apartment, 3 min walk to Line 4 metro.', 'Shenzhen', '南山区科技园路15号', 4500.00, 'apartment', 'approved');
```

---

## 8. Five Modules — Full Detail

### Module 1 — User Module (Auth)
**Controller:** `AuthController.java`

| URL | Method | Description |
|---|---|---|
| `/login` | GET | Show login page |
| `/login` | POST | Process login |
| `/register` | GET | Show register page |
| `/register` | POST | Process registration |
| `/logout` | GET | Destroy session, clear cookie, redirect to login |

**Logic:**
- Login: check username + password → store User in HttpSession (key: `"loginUser"`) → redirect to homepage
- Remember Me: Cookie with username, 7-day expiry
- Register: validate → check duplicate username/email → INSERT into user table
- Logout: session.invalidate() → clear cookie → redirect to /login

---

### Module 2 — Housing Listings Module
**Controller:** `ListingController.java`

| URL | Method | Description |
|---|---|---|
| `/listings` | GET | Browse all listings with optional filters |
| `/listings/{id}` | GET | Listing detail page |
| `/listings/add` | GET | Show add listing form |
| `/listings/add` | POST | Submit new listing |
| `/listings/my` | GET | View own listings |
| `/listings/delete/{id}` | POST | Delete own listing |

**Logic:**
- Browse: optional @RequestParam (city, minPrice, maxPrice, propertyType) → dynamic SQL
- Detail: listing + all reviews + average rating
- New listing: status = 'pending' by default, admin must approve
- Pagination: SQL LIMIT and OFFSET

---

### Module 3 — Reviews and Ratings Module
**Controller:** `ReviewController.java`

| URL | Method | Description |
|---|---|---|
| `/reviews/add/{listingId}` | GET | Show review form |
| `/reviews/add/{listingId}` | POST | Submit review |
| `/reviews/edit/{id}` | GET | Show edit form |
| `/reviews/edit/{id}` | POST | Update review |
| `/reviews/delete/{id}` | POST | Delete own review |

**Logic:**
- Must be logged in to submit
- One review per user per listing (UNIQUE constraint + service check)
- Average rating: SQL AVG(rating) GROUP BY listing_id
- Only author can edit/delete their own review

---

### Module 4 — Neighborhood Module
**Controller:** `NeighborhoodController.java`

| URL | Method | Description |
|---|---|---|
| `/neighborhoods` | GET | Browse all neighborhoods |
| `/neighborhoods/{id}` | GET | Neighborhood detail |
| `/neighborhoods/rate/{id}` | POST | Submit liveability rating |
| `/neighborhoods/tip/{id}` | POST | Post community tip |

**Logic:**
- Detail page: name, city, description, avg liveability scores, tips, listings in area
- Scores: AVG per category (safety, transport, food_access, foreigner_friendly)
- Tips: newest first

---

### Module 5 — Admin Module
**Controller:** `AdminController.java`

| URL | Method | Description |
|---|---|---|
| `/admin/dashboard` | GET | Stats overview |
| `/admin/listings` | GET | All listings |
| `/admin/listings/approve/{id}` | POST | Approve listing |
| `/admin/listings/delete/{id}` | POST | Delete listing |
| `/admin/reviews` | GET | All reviews |
| `/admin/reviews/delete/{id}` | POST | Delete review |
| `/admin/users` | GET | All users |
| `/admin/users/deactivate/{id}` | POST | Deactivate user |

**Logic:**
- Admin identity: user.role = 'admin'
- After login: redirect to /admin/dashboard if role = admin
- LoginFilter checks role for all /admin/* URLs
- Dashboard: COUNT queries for total users, listings, reviews, pending listings

---

## 9. Login Filter (Exactly as Taught in J2EE Course)

```java
package com.linjufind.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        // Whitelist — always allowed through
        if (uri.contains("/login")
                || uri.contains("/register")
                || uri.contains("/css/")
                || uri.contains("/js/")
                || uri.contains("/images/")) {
            chain.doFilter(request, response);
            return;
        }

        // Check session
        Object loginUser = req.getSession().getAttribute("loginUser");
        if (loginUser != null) {
            chain.doFilter(request, response);
        } else {
            resp.sendRedirect("/login");
        }
    }
}
```

**Registration in WebConfig.java:**
```java
@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilter() {
        FilterRegistrationBean<LoginFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LoginFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(1);
        return bean;
    }
}
```

---

## 10. Entity Classes Summary

### User.java
```java
private Integer id;
private String username;
private String email;
private String password;
private String nationality;
private String city;
private String role;          // "user" or "admin"
private Date createdAt;
```

### Listing.java
```java
private Integer id;
private Integer userId;
private String title;
private String description;
private String city;
private String address;
private Double price;
private String propertyType;  // "apartment", "studio", "shared"
private String status;        // "pending", "approved"
private Date createdAt;
// Calculated (not in DB):
private Double averageRating;
private Integer reviewCount;
```

### Review.java
```java
private Integer id;
private Integer listingId;
private Integer userId;
private Integer rating;
private String reviewText;
private Date createdAt;
private String username;      // joined from user table
```

### Neighborhood.java
```java
private Integer id;
private String name;
private String city;
private String description;
// Calculated:
private Double avgSafety;
private Double avgTransport;
private Double avgFoodAccess;
private Double avgForeignerFriendly;
```

### NeighborhoodTip.java
```java
private Integer id;
private Integer neighborhoodId;
private Integer userId;
private String tipText;
private Date createdAt;
private String username;
```

---

## 11. DAO Layer — Key SQL Patterns

### UserDao
```sql
SELECT * FROM user WHERE username = ? AND password = ?
INSERT INTO user (username, email, password, nationality, city) VALUES (?, ?, ?, ?, ?)
SELECT * FROM user WHERE username = ?
```

### ListingDao
```sql
SELECT * FROM listing WHERE status = 'approved'
  AND (city = ? OR ? IS NULL)
  AND (price >= ? OR ? IS NULL)
  AND (price <= ? OR ? IS NULL)
ORDER BY created_at DESC LIMIT ? OFFSET ?

SELECT listing_id, AVG(rating) as avg_rating, COUNT(*) as review_count
FROM review GROUP BY listing_id
```

### ReviewDao
```sql
SELECT r.*, u.username FROM review r
JOIN user u ON r.user_id = u.id
WHERE r.listing_id = ? ORDER BY r.created_at DESC

SELECT * FROM review WHERE listing_id = ? AND user_id = ?
SELECT AVG(rating) FROM review WHERE listing_id = ?
```

### NeighborhoodDao
```sql
SELECT AVG(safety) as avg_safety, AVG(transport) as avg_transport,
  AVG(food_access) as avg_food, AVG(foreigner_friendly) as avg_foreigner
FROM neighborhood_rating WHERE neighborhood_id = ?

SELECT t.*, u.username FROM neighborhood_tip t
JOIN user u ON t.user_id = u.id
WHERE t.neighborhood_id = ? ORDER BY t.created_at DESC
```

---

## 12. Development Order (Recommended)

Build in this exact order:

1. Project setup — confirm Spring Boot runs, DB connected, Whitelabel page shows
2. Entity classes — all 6 Java entity classes
3. User Module — UserDao, UserService, AuthController, login.html, register.html
4. Login Filter — LoginFilter + WebConfig, test protection works
5. Listing Module — ListingDao, ListingService, ListingController, list.html, detail.html, add.html
6. Review Module — ReviewDao, ReviewService, ReviewController, form.html
7. Neighborhood Module — NeighborhoodDao, NeighborhoodService, NeighborhoodController, list.html, detail.html
8. Admin Module — AdminController, dashboard.html, listings.html, reviews.html, users.html
9. Design Patterns — implement all 6 patterns explicitly into existing code
10. Frontend — custom dark luxury CSS applied across all 14 templates ✓ **COMPLETE**

---

## 13. Design Pattern Implementation (Java Design Pattern Course)

This section is required for the **Java Design Pattern final assignment**.
All 6 patterns must be explicitly implemented in the codebase and identifiable by the lecturer.
Patterns are organized by category — all 3 categories (Creational, Structural, Behavioral) must be covered.

---

### CREATIONAL PATTERNS

#### Pattern 1 — Singleton
**Where:** Database connection / JdbcTemplate configuration
**Why:** The entire application should share one single database connection instance. Creating multiple connections wastes resources.
**Implementation:** Create a `DatabaseConfig.java` class that provides a single shared `JdbcTemplate` bean using `@Bean` + `@Configuration`. Spring's IoC container ensures only one instance exists.

```java
// com/linjufind/pattern/singleton/DatabaseConfig.java
@Configuration
public class DatabaseConfig {
    // Spring @Bean is Singleton by default — only one JdbcTemplate instance
    // exists across the entire application
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
```

**Key point to explain to lecturer:** Spring beans are Singleton scope by default — this is a direct implementation of the Singleton pattern.

---

#### Pattern 2 — Builder
**Where:** `Listing` object construction in `ListingController` when processing form submission
**Why:** A Listing has many fields (title, description, city, address, price, propertyType) — some optional. Builder pattern avoids messy constructors with many parameters.
**Implementation:** Create a `ListingBuilder.java` inner class or separate class with method chaining.

```java
// com/linjufind/pattern/builder/ListingBuilder.java
public class ListingBuilder {
    private String title;
    private String description;
    private String city;
    private String address;
    private Double price;
    private String propertyType;
    private Integer userId;

    public ListingBuilder title(String title) {
        this.title = title;
        return this;
    }
    public ListingBuilder description(String description) {
        this.description = description;
        return this;
    }
    public ListingBuilder city(String city) {
        this.city = city;
        return this;
    }
    public ListingBuilder address(String address) {
        this.address = address;
        return this;
    }
    public ListingBuilder price(Double price) {
        this.price = price;
        return this;
    }
    public ListingBuilder propertyType(String propertyType) {
        this.propertyType = propertyType;
        return this;
    }
    public ListingBuilder userId(Integer userId) {
        this.userId = userId;
        return this;
    }
    public Listing build() {
        Listing listing = new Listing();
        listing.setTitle(this.title);
        listing.setDescription(this.description);
        listing.setCity(this.city);
        listing.setAddress(this.address);
        listing.setPrice(this.price);
        listing.setPropertyType(this.propertyType);
        listing.setUserId(this.userId);
        listing.setStatus("pending");
        return listing;
    }
}
```

**Usage in ListingController (POST /listings/add):**
```java
Listing listing = new ListingBuilder()
    .title(title)
    .description(description)
    .city(city)
    .price(price)
    .propertyType(propertyType)
    .userId(currentUser.getId())
    .build();
```

---

### STRUCTURAL PATTERNS

#### Pattern 3 — Facade
**Where:** Service layer as a facade over the DAO layer
**Why:** Controllers should not need to know which DAOs are involved or how many queries run. The Service class provides one simple method that hides all the complexity underneath.
**Implementation:** `ListingService.getListingDetail(int id)` internally calls ListingDao + ReviewDao + calculates average — but the controller just calls one method.

```java
// com/linjufind/pattern/facade/ListingFacade.java
// (or documented within ListingService.java)
public class ListingService {

    // FACADE: One simple method call hides:
    // 1. Fetching the listing from ListingDao
    // 2. Fetching all reviews from ReviewDao
    // 3. Calculating average rating
    // 4. Counting total reviews
    // Controller knows nothing about these steps
    public ListingDetailDTO getListingDetail(int listingId) {
        Listing listing = listingDao.findById(listingId);
        List<Review> reviews = reviewDao.findByListingId(listingId);
        Double avgRating = reviewDao.getAverageRating(listingId);
        listing.setAverageRating(avgRating);
        listing.setReviewCount(reviews.size());
        return new ListingDetailDTO(listing, reviews);
    }
}
```

---

#### Pattern 4 — Decorator
**Where:** Wrapping `Listing` objects with extra calculated information before sending to view
**Why:** The base `Listing` entity only has raw database fields. We need to add calculated fields (average rating, review count, neighborhood name) without modifying the original entity class.
**Implementation:** Create a `ListingDecorator.java` that wraps a `Listing` and adds extra fields.

```java
// com/linjufind/pattern/decorator/ListingDecorator.java
public class ListingDecorator {
    private final Listing listing;
    private Double averageRating;
    private Integer reviewCount;
    private String neighborhoodName;

    public ListingDecorator(Listing listing) {
        this.listing = listing;
    }

    public ListingDecorator withAverageRating(Double avgRating) {
        this.averageRating = avgRating;
        return this;
    }

    public ListingDecorator withReviewCount(Integer count) {
        this.reviewCount = count;
        return this;
    }

    public ListingDecorator withNeighborhoodName(String name) {
        this.neighborhoodName = name;
        return this;
    }

    // Delegates all original Listing methods
    public String getTitle() { return listing.getTitle(); }
    public Double getPrice() { return listing.getPrice(); }
    public String getCity() { return listing.getCity(); }
    // ... all other getters

    // New decorated getters
    public Double getAverageRating() { return averageRating; }
    public Integer getReviewCount() { return reviewCount; }
    public String getNeighborhoodName() { return neighborhoodName; }
}
```

---

### BEHAVIORAL PATTERNS

#### Pattern 5 — Strategy
**Where:** Listing search and filter functionality
**Why:** Users can filter listings in different ways — by city, by price range, by property type, or combinations. Each filter strategy has different SQL logic. Strategy pattern lets us switch between filter algorithms without changing the controller or service.
**Implementation:** Create a `ListingFilterStrategy` interface with multiple implementations.

```java
// com/linjufind/pattern/strategy/ListingFilterStrategy.java
public interface ListingFilterStrategy {
    List<Listing> filter(List<Listing> listings);
}

// com/linjufind/pattern/strategy/CityFilterStrategy.java
public class CityFilterStrategy implements ListingFilterStrategy {
    private final String city;
    public CityFilterStrategy(String city) { this.city = city; }

    @Override
    public List<Listing> filter(List<Listing> listings) {
        return listings.stream()
            .filter(l -> l.getCity().equalsIgnoreCase(city))
            .collect(Collectors.toList());
    }
}

// com/linjufind/pattern/strategy/PriceRangeFilterStrategy.java
public class PriceRangeFilterStrategy implements ListingFilterStrategy {
    private final Double minPrice;
    private final Double maxPrice;

    public PriceRangeFilterStrategy(Double min, Double max) {
        this.minPrice = min;
        this.maxPrice = max;
    }

    @Override
    public List<Listing> filter(List<Listing> listings) {
        return listings.stream()
            .filter(l -> l.getPrice() >= minPrice && l.getPrice() <= maxPrice)
            .collect(Collectors.toList());
    }
}

// com/linjufind/pattern/strategy/PropertyTypeFilterStrategy.java
public class PropertyTypeFilterStrategy implements ListingFilterStrategy {
    private final String type;
    public PropertyTypeFilterStrategy(String type) { this.type = type; }

    @Override
    public List<Listing> filter(List<Listing> listings) {
        return listings.stream()
            .filter(l -> l.getPropertyType().equalsIgnoreCase(type))
            .collect(Collectors.toList());
    }
}
```

**Usage in ListingService:**
```java
// Apply strategy based on what filter the user selected
ListingFilterStrategy strategy = null;
if (city != null) strategy = new CityFilterStrategy(city);
else if (minPrice != null) strategy = new PriceRangeFilterStrategy(minPrice, maxPrice);
else if (propertyType != null) strategy = new PropertyTypeFilterStrategy(propertyType);

if (strategy != null) {
    listings = strategy.filter(listings);
}
```

---

#### Pattern 6 — Template Method
**Where:** Base DAO class defining the common database operation flow
**Why:** Every DAO operation follows the same steps: get connection → prepare statement → execute → handle results → close resources. Template Method defines this skeleton once, subclasses override only the specific SQL and result mapping.
**Implementation:** Create an abstract `BaseDao.java` with a template method.

```java
// com/linjufind/pattern/template/BaseDao.java
public abstract class BaseDao<T> {

    protected final JdbcTemplate jdbcTemplate;

    public BaseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // TEMPLATE METHOD — defines the skeleton of a query operation
    // Subclasses override getQuery() and mapResult() only
    public List<T> findAll() {
        String sql = getQuery();
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapResult(rs));
    }

    // Steps that subclasses must implement
    protected abstract String getQuery();
    protected abstract T mapResult(ResultSet rs) throws SQLException;
}

// UserDao extends BaseDao and only provides the SQL and mapping:
public class UserDao extends BaseDao<User> {

    @Override
    protected String getQuery() {
        return "SELECT * FROM user";
    }

    @Override
    protected User mapResult(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        return user;
    }
}
```

---

### Design Pattern Summary Table

| Pattern | Category | Where in Project | Purpose |
|---|---|---|---|
| Singleton | Creational | DatabaseConfig / JdbcTemplate bean | One shared DB connection instance |
| Builder | Creational | ListingBuilder — creating Listing objects | Clean construction of complex objects |
| Facade | Structural | ListingService.getListingDetail() | Hides DAO complexity from Controller |
| Decorator | Structural | ListingDecorator — adding calculated fields | Extend Listing without modifying entity |
| Strategy | Behavioral | ListingFilterStrategy — search filters | Switchable filter algorithms |
| Template Method | Behavioral | BaseDao — common DB operation skeleton | Reusable query flow across all DAOs |

---

## 14. Frontend — Completed

**Status:** Fully styled across all 14 Thymeleaf templates.

**Design system:**
- Custom hand-written CSS — no Bootstrap or Tailwind framework
- `static/css/style.css` — global styles (all pages except auth)
- `static/css/auth.css` — login + register pages
- `static/js/main.js`, `three-room.js`, `auth-marquee.js`, `auth-username.js`

**Theme:**
- Color palette: Charcoal (`#0d0d0d`–`#4a4a4a`) with Gold (`#c9a84c`) accents and warm off-white text
- Typography: Cinzel (headings) + Josefin Sans (body), from Google Fonts
- Visual style: Dark glassmorphism cards, gold-glow shadows, smooth transitions

**Logo (implemented):**
```
  Linju Find        ← English, Cinzel, smaller
邻居找房             ← Chinese, slightly bigger
```

---

## 15. Key Decisions & Constraints

| Decision | Detail |
|---|---|
| No Spring Security | Manual session + Filter as taught in J2EE course |
| No external Tomcat | Spring Boot embedded Tomcat, run as JAR |
| No external APIs | User-generated + admin-seeded data only |
| No payment/booking | Information platform only |
| No JPA/Hibernate | JdbcTemplate only as taught in course |
| Password storage | Plain text for now (course project) |
| Session key | `"loginUser"` stores User object |
| Admin role | user.role = "admin", seeded manually in DB |
| Listing approval | New listings default to status = "pending" |
| Filter package | jakarta.servlet (Spring Boot 4.x — NOT javax.servlet) |
| Port | 8089 (8080 already in use on developer machine) |
| MySQL credentials | username: root, password: empty |

---

## 16. Aliyun Maven Mirror (If Needed)

If Maven dependency downloads fail in China:
`C:\Users\[username]\.m2\settings.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings>
    <mirrors>
        <mirror>
            <id>aliyun</id>
            <mirrorOf>central</mirrorOf>
            <name>Aliyun Maven Mirror</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
    </mirrors>
</settings>
```

---

## 17. Important Notes for Claude Code

- Developer: Yakhe Kenneth Sugiharto (Indonesian student at Nanjing Xiaozhuang University)
- Environment: IntelliJ IDEA Ultimate, Windows, ASUS TUF Gaming F15
- Spring Boot 4.0.6, Java 17, JDK 25, MySQL no password, port 8089
- **DO NOT use javax.servlet** — use **jakarta.servlet** only
- **DO NOT add Spring Security** — manual LoginFilter only
- **DO NOT use JPA/Hibernate** — JdbcTemplate only
- **DO NOT create two separate projects** — everything in one Spring Boot project
- **DO implement all 6 design patterns** from Section 13 — they are required for the Design Pattern course
- Filter registered via FilterRegistrationBean in WebConfig, NOT @WebFilter
- Thymeleaf templates in `src/main/resources/templates/`
- Static files in `src/main/resources/static/`
- Database: MySQL 8.0, tool: SQLYog, DB name: linju_find_db
- Developer is in China — be mindful of Maven download issues (use Aliyun mirror if needed)
- This project serves TWO final assignment courses — both J2EE concepts AND design patterns must be clearly visible in the code

---

*This document contains everything needed to build Linju Find from scratch.*
*Start from Section 12 (Development Order) and work through each step.*
*When starting Claude Code, say: "Read this markdown file and help me start building from Step 1 of Section 12 — entity classes."*

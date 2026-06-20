# Linju Find — 邻居找房

A community-based housing discovery and review platform built for foreign students and expatriates navigating the Chinese rental market.

---

## Background

China is experiencing one of the worst property crises in its history — an estimated 65–80 million empty apartments exist across the country, yet young workers, migrants, and international students still struggle to find affordable, trustworthy housing in cities where they actually want to live. The supply exists. The connection between available housing and people who need it is broken.

The root cause is **trust**. Dominant Chinese housing platforms (Anjuke 安居客, Lianjia 链家) are advertiser-driven, agent-filled, written entirely in Chinese, and contain zero honest community reviews. International students face an additional layer — language barriers, unfamiliarity with local systems, and no local connections.

**Linju Find** fills that gap. It is a community-driven information platform where real tenants post listings, write honest reviews, rate neighborhoods on liveability, and share community tips — all in one place, accessible to both Chinese locals and foreigners.

> This project does not handle rentals or payments. It is purely an information and trust platform.

For the full project background and motivation, see [Documents/LinjuFind_Project_Background.md](Documents/LinjuFind_Project_Background.md).

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.6 |
| View | Thymeleaf |
| Controller | Spring MVC |
| Service | Java Service Layer |
| DAO | Spring JDBC (`JdbcTemplate`) |
| Database | MariaDB 10.4.28 |
| Session | `HttpSession` (manual — no Spring Security) |
| Cookie | `Cookie` class (Remember Me) |
| Filter | `jakarta.servlet.Filter` + `FilterRegistrationBean` |
| Build | Maven |

**Key constraints (intentional):**
- No Spring Security — manual `LoginFilter` + `HttpSession` only
- No JPA / Hibernate — `JdbcTemplate` only
- `jakarta.servlet` (not `javax.servlet`) — Spring Boot 4.x

---

## Project Structure

```
linju-find-housing-platform/
├── src/main/java/com/linjufind/
│   ├── LinjuFindHousingPlatformApplication.java
│   ├── config/
│   │   ├── DatabaseConfig.java         ← JdbcTemplate bean
│   │   └── WebConfig.java              ← registers LoginFilter
│   ├── controller/
│   │   ├── AuthController.java         ← /login /register /logout
│   │   ├── ListingController.java      ← /listings/**
│   │   ├── ReviewController.java       ← /reviews/**
│   │   ├── NeighborhoodController.java ← /neighborhoods/**
│   │   └── AdminController.java        ← /admin/**
│   ├── dao/
│   │   ├── UserDao.java
│   │   ├── ListingDao.java
│   │   ├── ReviewDao.java
│   │   └── NeighborhoodDao.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── Listing.java
│   │   ├── Review.java
│   │   ├── Neighborhood.java
│   │   ├── NeighborhoodRating.java
│   │   └── NeighborhoodTip.java
│   ├── service/
│   │   ├── UserService.java
│   │   ├── ListingService.java
│   │   ├── ReviewService.java
│   │   └── NeighborhoodService.java
│   ├── filter/
│   │   └── LoginFilter.java
│   └── pattern/                        ← all GoF design pattern classes
│       ├── singleton/
│       ├── factory/
│       ├── builder/
│       ├── facade/
│       ├── decorator/
│       ├── composite/
│       ├── observer/
│       ├── command/
│       ├── strategy/
│       └── template/
├── src/main/resources/
│   ├── application.properties.example  ← copy to application.properties and fill in credentials
│   ├── static/
│   │   ├── css/                        ← style.css + auth.css (dark luxury theme)
│   │   ├── js/                         ← main.js, three-room.js, auth-marquee.js, auth-username.js
│   │   └── images/                     ← logos + decorative images
│   └── templates/
│       ├── auth/                       ← login.html, register.html
│       ├── listing/                    ← list.html, detail.html, add.html, my.html
│       ├── review/                     ← form.html, edit.html
│       ├── neighborhood/               ← list.html, detail.html
│       ├── admin/                      ← dashboard.html, listings.html, reviews.html, users.html
│       └── fragments/
├── db/
│   └── migration_add_listing_contact.sql
├── Documents/
│   ├── LinjuFind_Project_Background.md
│   └── LinjuFind_DesignPatterns.md
└── pom.xml
```

---

## Database Setup

Database name: `linju_find_db`

```sql
CREATE DATABASE IF NOT EXISTS linju_find_db DEFAULT CHARACTER SET utf8mb4;
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
```

---

## Running the Project

**1. Clone the repo and set up config**

Copy the example config and fill in your credentials:
```
src/main/resources/application.properties.example  →  application.properties
```

```properties
server.port=8089

spring.datasource.url=jdbc:mysql://localhost:3306/linju_find_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_DB_PASSWORD

spring.thymeleaf.cache=false
```

**2. Create the database**

Run the SQL above in MySQL (or SQLYog), then run the contact migration:
```
db/migration_add_listing_contact.sql
```

**3. Run**

```bash
./mvnw spring-boot:run        # macOS / Linux
mvnw.cmd spring-boot:run      # Windows
```

Then open: `http://localhost:8089/login`

> **Maven in China?** Add the Aliyun mirror to `~/.m2/settings.xml`:
> `https://maven.aliyun.com/repository/public`

---

## Pages & Routes

| Module | URL | Access |
|---|---|---|
| Login | `/login` | Public |
| Register | `/register` | Public |
| Browse Listings | `/listings` | Logged in |
| Listing Detail | `/listings/{id}` | Logged in |
| Post a Listing | `/listings/add` | Logged in |
| My Listings | `/listings/my` | Logged in |
| Browse Neighborhoods | `/neighborhoods` | Logged in |
| Neighborhood Detail | `/neighborhoods/{id}` | Logged in |
| Write a Review | `/reviews/add/{listingId}` | Logged in |
| Admin Dashboard | `/admin/dashboard` | Admin only |
| Admin — Listings | `/admin/listings` | Admin only |
| Admin — Reviews | `/admin/reviews` | Admin only |
| Admin — Users | `/admin/users` | Admin only |

**Roles:** `user` (default) · `admin` · `deactivated` (banned — session invalidated on login)

---

## Authentication & Security

Authentication is handled manually without Spring Security:

- **`LoginFilter`** intercepts every request except the whitelist (`/login`, `/register`, `/css/`, `/js/`, `/images/`)
- Logged-in user is stored in `HttpSession` under the key `"loginUser"` as a `User` object
- Deactivated accounts are detected in the filter and their session is immediately invalidated
- Admin-only routes are protected per-method inside `AdminController` via an `isAdmin()` check
- Remember Me is implemented with a plain `Cookie` (7-day expiry)

---

## Design Patterns

This project implements **10 GoF Design Patterns** across all three categories as part of a Java Design Pattern course requirement:

| # | Pattern | Category |
|---|---|---|
| 1 | Singleton | Creational |
| 2 | Factory | Creational |
| 3 | Builder | Creational |
| 4 | Facade | Structural |
| 5 | Decorator | Structural |
| 6 | Composite | Structural |
| 7 | Observer | Behavioral |
| 8 | Command | Behavioral |
| 9 | Strategy | Behavioral |
| 10 | Template Method | Behavioral |

All pattern classes live under `src/main/java/com/linjufind/pattern/`.

For detailed explanations of each pattern — including how it works in this project, the exact workflow, and code examples — see [Documents/LinjuFind_DesignPatterns.md](Documents/LinjuFind_DesignPatterns.md).

---

## UI

Custom hand-written CSS with a dark luxury theme — no Bootstrap or Tailwind.

- **Color palette:** Charcoal (`#0d0d0d`–`#4a4a4a`) + Gold (`#c9a84c`) accents
- **Typography:** Cinzel (headings) + Josefin Sans (body) via Google Fonts
- **Style:** Dark glassmorphism cards, gold-glow shadows, smooth transitions
- 14 fully styled Thymeleaf templates

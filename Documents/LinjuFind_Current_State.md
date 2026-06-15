# Linju Find — Current Build State
*Full-stack application — backend and frontend both 100% complete.*

---

## What Is Done

All 10 steps of the Section 12 build order are fully complete and verified working.
All 10 GoF Design Patterns are implemented.
**The project is complete — backend and frontend are both finished.**

---

## Project Identity

| Field | Value |
|---|---|
| App Name | Linju Find / 邻居找房 |
| Group ID | com.linjufind |
| Main Class | `com.linjufind.LinjuFindHousingPlatformApplication` |
| Spring Boot | 4.0.6 |
| Java | 17 (JDK 25) |
| Port | 8089 |
| Database | MySQL — `linju_find_db` — root / no password |
| Build | Maven (`./mvnw.cmd spring-boot:run`) |
| View | Thymeleaf (all pages — no JSP anywhere) |

---

## Hard Constraints — Do Not Change These

- Use **jakarta.servlet** — NOT javax.servlet
- **No Spring Security** — manual `LoginFilter` + `HttpSession` only
- **No JPA / Hibernate** — `JdbcTemplate` only
- Session key for logged-in user is `"loginUser"` (stores a `User` object)
- Filter registered via `FilterRegistrationBean` in `WebConfig` — NOT `@WebFilter`
- Admin identified by `user.role = "admin"` only
- New listings default to `status = "pending"` — admin must approve
- Passwords are plain text (course project)

---

## Full Folder Structure

```
src/main/java/com/linjufind/
├── LinjuFindHousingPlatformApplication.java   ← entry point
├── config/
│   └── WebConfig.java                         ← registers LoginFilter
├── controller/
│   ├── AuthController.java                    ← /login /register /logout
│   ├── ListingController.java                 ← /listings/**
│   ├── ReviewController.java                  ← /reviews/**
│   ├── NeighborhoodController.java            ← /neighborhoods/**
│   └── AdminController.java                   ← /admin/**
├── dao/
│   ├── UserDao.java                           ← extends BaseDao<User>
│   ├── ListingDao.java
│   ├── ReviewDao.java
│   └── NeighborhoodDao.java
├── entity/
│   ├── User.java
│   ├── Listing.java                           ← has averageRating, reviewCount (not in DB)
│   ├── Review.java                            ← has username (joined, not in DB)
│   ├── Neighborhood.java                      ← has avgSafety etc. (calculated)
│   ├── NeighborhoodRating.java
│   └── NeighborhoodTip.java                   ← has username (joined, not in DB)
├── filter/
│   └── LoginFilter.java
├── service/
│   ├── UserService.java
│   ├── ListingService.java                    ← delegates detail to ListingFacade
│   ├── ReviewService.java
│   └── NeighborhoodService.java
└── pattern/
    ├── singleton/
    │   ├── AppConfig.java                     ← Singleton pattern (hand-written, double-checked locking)
    │   └── DatabaseConfig.java                ← Spring @Bean wiring for JdbcTemplate
    ├── factory/
    │   └── ReviewFactory.java                 ← Factory pattern
    ├── builder/
    │   └── ListingBuilder.java                ← Builder pattern
    ├── facade/
    │   └── ListingFacade.java                 ← Facade pattern
    ├── decorator/
    │   └── ListingDecorator.java              ← Decorator pattern
    ├── composite/
    │   ├── NeighborhoodScoreComponent.java    ← Composite pattern (interface)
    │   ├── NeighborhoodScoreLeaf.java         ← Composite pattern (leaf)
    │   └── NeighborhoodScoreComposite.java    ← Composite pattern (root — overall liveability)
    ├── observer/
    │   ├── ListingObserver.java               ← Observer pattern (interface)
    │   ├── ListingEventPublisher.java         ← Observer pattern (publisher)
    │   ├── ListingApprovalLogger.java         ← Observer pattern (concrete observer 1)
    │   └── PendingListingCounter.java         ← Observer pattern (concrete observer 2)
    ├── command/
    │   ├── AdminCommand.java                  ← Command pattern (interface)
    │   ├── ApproveListingCommand.java         ← Command pattern (concrete command 1)
    │   ├── DeactivateUserCommand.java         ← Command pattern (concrete command 2)
    │   └── CommandHistory.java                ← Command pattern (invoker + undo stack)
    ├── strategy/
    │   ├── ListingFilterStrategy.java         ← Strategy pattern (interface)
    │   ├── CityFilterStrategy.java
    │   ├── PriceRangeFilterStrategy.java
    │   └── PropertyTypeFilterStrategy.java
    └── template/
        └── BaseDao.java                       ← Template Method pattern

src/main/resources/
├── application.properties
├── static/
│   ├── css/                                   ← style.css + auth.css (dark luxury theme)
│   ├── js/                                    ← main.js, three-room.js, auth-marquee.js, auth-username.js
│   └── images/                                ← logo PNGs + decorative wall images
└── templates/
    ├── auth/
    │   ├── login.html
    │   └── register.html
    ├── listing/
    │   ├── list.html
    │   ├── detail.html
    │   ├── add.html
    │   └── my.html
    ├── review/
    │   ├── form.html
    │   └── edit.html
    ├── neighborhood/
    │   ├── list.html
    │   └── detail.html
    └── admin/
        ├── dashboard.html
        ├── listings.html
        ├── reviews.html
        └── users.html
```

---

## All URLs

### Auth
| URL | Method | Controller Method | Description |
|---|---|---|---|
| `/login` | GET | `AuthController.showLogin` | Show login page |
| `/login` | POST | `AuthController.processLogin` | Process login |
| `/register` | GET | `AuthController.showRegister` | Show register page |
| `/register` | POST | `AuthController.processRegister` | Process registration |
| `/logout` | GET | `AuthController.logout` | Destroy session, clear cookie |

### Listings
| URL | Method | Controller Method | Description |
|---|---|---|---|
| `/listings` | GET | `ListingController.list` | Browse approved listings with filters + pagination |
| `/listings/{id}` | GET | `ListingController.detail` | Listing detail + reviews + avg rating |
| `/listings/add` | GET | `ListingController.showAddForm` | Show add listing form |
| `/listings/add` | POST | `ListingController.processAdd` | Submit new listing (status = pending) |
| `/listings/my` | GET | `ListingController.myListings` | View own listings |
| `/listings/delete/{id}` | POST | `ListingController.delete` | Delete own listing |

### Reviews
| URL | Method | Controller Method | Description |
|---|---|---|---|
| `/reviews/add/{listingId}` | GET | `ReviewController.showAddForm` | Show review form |
| `/reviews/add/{listingId}` | POST | `ReviewController.processAdd` | Submit review |
| `/reviews/edit/{id}` | GET | `ReviewController.showEditForm` | Show edit form (own review only) |
| `/reviews/edit/{id}` | POST | `ReviewController.processEdit` | Update review |
| `/reviews/delete/{id}` | POST | `ReviewController.delete` | Delete review |

### Neighborhoods
| URL | Method | Controller Method | Description |
|---|---|---|---|
| `/neighborhoods` | GET | `NeighborhoodController.list` | Browse all neighborhoods |
| `/neighborhoods/{id}` | GET | `NeighborhoodController.detail` | Neighborhood detail + scores + tips |
| `/neighborhoods/rate/{id}` | POST | `NeighborhoodController.submitRating` | Submit liveability rating |
| `/neighborhoods/tip/{id}` | POST | `NeighborhoodController.submitTip` | Post community tip |

### Admin
| URL | Method | Controller Method | Description |
|---|---|---|---|
| `/admin/dashboard` | GET | `AdminController.dashboard` | Stats: users, listings, reviews, pending |
| `/admin/listings` | GET | `AdminController.listings` | All listings table |
| `/admin/listings/approve/{id}` | POST | `AdminController.approveListing` | Set status = approved |
| `/admin/listings/delete/{id}` | POST | `AdminController.deleteListing` | Delete any listing |
| `/admin/reviews` | GET | `AdminController.reviews` | All reviews table |
| `/admin/reviews/delete/{id}` | POST | `AdminController.deleteReview` | Delete any review |
| `/admin/users` | GET | `AdminController.users` | All users table |
| `/admin/users/deactivate/{id}` | POST | `AdminController.deactivateUser` | Set role = deactivated |
| `/admin/undo` | POST | `AdminController.undo` | Undo last reversible admin action (Command pattern) |

---

## Login Filter Whitelist

These URLs always pass through without a session check:
- `/login`
- `/register`
- `/css/`
- `/js/`
- `/images/`

Everything else requires `session.getAttribute("loginUser")` to be non-null, or the user is redirected to `/login`.

---

## What Each HTML Page Currently Looks Like

All 14 pages are **fully styled** with a custom dark luxury theme:

- **Color palette:** Charcoal (`#0d0d0d` – `#4a4a4a`) + Gold (`#c9a84c`) accents
- **Typography:** Cinzel (headings) + Josefin Sans (body), loaded from Google Fonts
- **Visual style:** Dark glassmorphism cards, gold-glow shadows, smooth transitions
- **CSS files:** `static/css/style.css` (global) + `static/css/auth.css` (login/register pages)
- **JS files:** `main.js`, `three-room.js`, `auth-marquee.js`, `auth-username.js`

All Thymeleaf attributes (`th:action`, `th:href`, `th:each`, `th:text`, `th:if`, etc.) are intact — styling is layered on top via CSS classes and HTML structure.

---

## Thymeleaf Variables Available Per Page

### `auth/login.html`
- `${param.registered}` — truthy if redirected after successful registration
- `${error}` — error message string if login failed
- `${rememberedUsername}` — pre-filled from Remember Me cookie

### `auth/register.html`
- `${error}` — error message string (e.g. "Username already taken.")

### `listing/list.html`
- `${listings}` — `List<Listing>` — each has: `id, title, city, propertyType, price, address, status`
- `${currentPage}` — current page number (int)
- `${totalPages}` — total pages (int)
- `${city}`, `${minPrice}`, `${maxPrice}`, `${propertyType}` — current filter values (may be null)

### `listing/detail.html`
- `${listing}` — `ListingDecorator` — has: `id, title, city, address, propertyType, price, description, averageRating, reviewCount`
- `${reviews}` — `List<Review>` — each has: `id, userId, username, rating, reviewText, createdAt`
- `${existingReview}` — the logged-in user's existing review, or `null`
- `${session.loginUser}` — the logged-in `User` object

### `listing/add.html`
- No model variables — pure form

### `listing/my.html`
- `${listings}` — `List<Listing>` — the logged-in user's own listings

### `review/form.html`
- `${listingId}` — the listing being reviewed
- `${error}` — error string if submission failed

### `review/edit.html`
- `${review}` — `Review` object with current `rating` and `reviewText`

### `neighborhood/list.html`
- `${neighborhoods}` — `List<Neighborhood>` — each has: `id, name, city, description, avgSafety, avgTransport, avgFoodAccess, avgForeignerFriendly` (avg fields may be null if no ratings yet)

### `neighborhood/detail.html`
- `${neighborhood}` — `Neighborhood` with avg scores
- `${tips}` — `List<NeighborhoodTip>` — each has: `username, tipText, createdAt`
- `${userRating}` — the logged-in user's existing `NeighborhoodRating`, or `null`
- `${scoreTree}` — `NeighborhoodScoreComposite` — call `.score` for overall liveability average

### `admin/dashboard.html`
- `${totalUsers}`, `${totalListings}`, `${totalReviews}`, `${pendingListings}` — all `long`
- `${canUndo}` — `boolean` — true if there is a reversible action in the Command history stack
- `${lastAction}` — `String` description of the last command, or `null`

### `admin/listings.html`
- `${listings}` — `List<Listing>` — all listings, any status

### `admin/reviews.html`
- `${reviews}` — `List<Review>` — all reviews with `username` populated

### `admin/users.html`
- `${users}` — `List<User>` — all users with `role` and `createdAt`

---

## Design Patterns (Required for Design Pattern Course)

All 10 patterns have explicit classes in `src/main/java/com/linjufind/pattern/`.

| Pattern | Category | File | Where It's Used |
|---|---|---|---|
| Singleton | Creational | `pattern/singleton/AppConfig.java` | Hand-written double-checked locking; `ListingController` reads `getDefaultPageSize()` |
| Factory | Creational | `pattern/factory/ReviewFactory.java` | `ReviewService.addReview()` — centralised `Review` construction with validation |
| Builder | Creational | `pattern/builder/ListingBuilder.java` | `ListingController.processAdd()` — assembles `Listing` from form fields |
| Facade | Structural | `pattern/facade/ListingFacade.java` | `ListingService.getListingDetail()` — hides 3 DAO calls behind one method |
| Decorator | Structural | `pattern/decorator/ListingDecorator.java` | Wraps `Listing` and adds `averageRating` + `reviewCount` for display |
| Composite | Structural | `pattern/composite/NeighborhoodScore*.java` | `NeighborhoodController.detail()` — overall liveability score averaged from 4 leaf scores |
| Observer | Behavioral | `pattern/observer/Listing*.java` | `AdminController` — approval/deletion fires events to logger + pending counter |
| Command | Behavioral | `pattern/command/Admin*.java` + `CommandHistory.java` | `AdminController` approve/deactivate go through `CommandHistory`; `/admin/undo` reverses last action |
| Strategy | Behavioral | `pattern/strategy/ListingFilterStrategy.java` + 3 impls | `ListingController.list()` — switchable filter algorithms |
| Template Method | Behavioral | `pattern/template/BaseDao.java` | `UserDao extends BaseDao<User>` — `findAll()` skeleton defined once |

---

## Database Seed Data (Already in MySQL)

### Users
| username | password | role |
|---|---|---|
| admin | admin123 | admin |

### Neighborhoods
| name | city |
|---|---|
| 鼓楼区 | Nanjing |
| 玄武区 | Nanjing |
| 建邺区 | Nanjing |
| 南山区 | Shenzhen |
| 福田区 | Shenzhen |

### Listings (all approved, posted by admin)
| title | city | price | type |
|---|---|---|---|
| Cozy Studio near Nanjing University | Nanjing | 2200 | studio |
| Shared Room in Modern Apartment | Nanjing | 1500 | shared |
| Spacious 2BR near Metro | Shenzhen | 4500 | apartment |

---

## application.properties

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

## How to Run

```
./mvnw.cmd spring-boot:run
```

Then open: `http://localhost:8089/login`

---

## Frontend — Completed

All 14 HTML templates are fully styled. The frontend is complete.

**Design system summary:**
- Custom hand-written CSS (no external framework like Bootstrap or Tailwind)
- Dark luxury theme: charcoal backgrounds, gold accents, warm off-white text
- Fonts: Cinzel (headings)
- Components: glassmorphism cards, gold-border inputs, animated auth marquee, 3D room ticker
- Logo: "Linju Find" on top (smaller, Cinzel), "邻居找房" below (slightly bigger)
- Static files: `static/css/style.c + Josefin Sans (body) via Google Fontsss`, `static/css/auth.css`, `static/js/*.js`, `static/images/*.png`


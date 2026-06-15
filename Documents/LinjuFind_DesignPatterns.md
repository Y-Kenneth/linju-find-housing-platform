# Linju Find — Design Patterns Reference

---

## Pattern Files — Folder Structure

```
src/main/java/com/linjufind/pattern/
│
├── singleton/
│   └── DatabaseManager.java               ← THE Singleton (hand-written, synchronized getInstance)
│
├── factory/
│   └── ReviewFactory.java                 ← Factory pattern
│
├── builder/
│   └── ListingBuilder.java                ← Builder pattern
│
├── facade/
│   └── ListingFacade.java                 ← Facade pattern
│
├── decorator/
│   └── ListingDecorator.java              ← Decorator pattern
│
├── composite/
│   ├── NeighborhoodScoreComponent.java    ← interface (shared contract for leaf and composite)
│   ├── NeighborhoodScoreLeaf.java         ← one individual score category
│   └── NeighborhoodScoreComposite.java    ← overall liveability root (averages all leaves)
│
├── observer/
│   ├── ListingObserver.java               ← interface (what every observer must implement)
│   ├── ListingEventPublisher.java         ← the subject / publisher
│   ├── ListingApprovalLogger.java         ← observer 1: prints audit log to console
│   └── PendingListingCounter.java         ← observer 2: keeps live count of pending listings
│
├── command/
│   ├── AdminCommand.java                  ← interface (execute + undo contract)
│   ├── ApproveListingCommand.java         ← command 1: approve a listing (undoable)
│   ├── DeactivateUserCommand.java         ← command 2: deactivate a user (undoable)
│   └── CommandHistory.java                ← invoker: runs commands and holds undo stack
│
├── strategy/
│   ├── ListingFilterStrategy.java         ← interface (every filter must implement this)
│   ├── CityFilterStrategy.java            ← filter by city
│   ├── PriceRangeFilterStrategy.java      ← filter by price range
│   └── PropertyTypeFilterStrategy.java    ← filter by property type
│
└── template/
    └── BaseDao.java                       ← Template Method base class
```

---

## Quick Reference Table

| # | Pattern | Category | One-line purpose |
|---|---|---|---|
| 1 | Singleton | Creational | One shared `DatabaseManager` instance wraps the database connection pool |
| 2 | Factory | Creational | `ReviewFactory` builds and validates `Review` objects |
| 3 | Builder | Creational | `ListingBuilder` assembles a `Listing` field by field |
| 4 | Facade | Structural | `ListingFacade` hides 3 DAO calls behind one method |
| 5 | Decorator | Structural | `ListingDecorator` adds `averageRating` + `reviewCount` to a `Listing` |
| 6 | Composite | Structural | `NeighborhoodScoreComposite` computes overall liveability from 4 sub-scores |
| 7 | Observer | Behavioral | Admin approval fires events to 2 independent observers |
| 8 | Command | Behavioral | Admin actions are objects — stored in a stack and undoable |
| 9 | Strategy | Behavioral | Filter algorithms (city / price / type) are swappable at runtime |
| 10 | Template Method | Behavioral | `BaseDao.findAll()` skeleton — subclasses fill in SQL + row mapping |

---

## 1. Singleton — `DatabaseManager.java`
**Category:** Creational

### What is it
A class that can only ever have one instance. No matter how many times you ask for it, you always get the same object back.

**Real-world example:** A restaurant has one shared cash register. Every cashier uses that same register — nobody brings their own. If each cashier had their own register, the sales records would be split and inconsistent.

### What it does in this project
`DatabaseManager` wraps the database connection pool (JdbcTemplate) and ensures the entire application shares one instance of it. Creating multiple database connection wrappers would waste memory and risk inconsistent behavior.

### How it works
```java
private DatabaseManager(DataSource dataSource) {
    setupDatabaseConnection(dataSource);
}

public static synchronized DatabaseManager initialize(DataSource dataSource) {
    if (instance == null) {
        instance = new DatabaseManager(dataSource);
    }
    return instance;
}

public static synchronized DatabaseManager getInstance() {
    return instance;
}

public JdbcTemplate getConnection() {
    return connection;
}
```
- The constructor is private — nobody can call `new DatabaseManager()` from outside.
- `initialize()` is called once at startup by `DatabaseConfig`. It creates the single instance.
- `getInstance()` is called by any class that needs it — always returns the same object.
- Every DAO receives `DatabaseManager` and calls `getConnection()` to get the shared `JdbcTemplate`.

### Without it
Every DAO would create its own database connection object independently. There would be no guarantee they all share the same pool, and the pattern would be invisible in the code.

---

## 2. Factory — `ReviewFactory.java`
**Category:** Creational

### What is it
A class with a static method that builds objects for you — including validation — so every caller gets a correctly constructed object without writing the same checks themselves.

**Real-world example:** A coffee machine is a factory. You press one button and it measures the water, grinds the beans, and brews the coffee correctly. You don't do each step yourself — the machine handles it the same way every time.

### What it does in this project
When a user submits a review, `ReviewFactory.createReview()` validates the rating (must be 1–5) and ensures the review text is not blank before assembling the `Review` object. `ReviewService` calls it instead of building the object manually.

### How it works
```java
public static Review createReview(int listingId, int userId, int rating, String reviewText) {
    if (rating < 1 || rating > 5)
        throw new IllegalArgumentException("Rating must be between 1 and 5.");
    if (reviewText == null || reviewText.isBlank())
        throw new IllegalArgumentException("Review text must not be empty.");

    Review review = new Review();
    review.setListingId(listingId);
    review.setUserId(userId);
    review.setRating(rating);
    review.setReviewText(reviewText.trim());
    return review;
}
```

**Called from `ReviewService.addReview()`:**
```java
Review review = ReviewFactory.createReview(listingId, userId, rating, reviewText);
reviewDao.insert(review);
```

### Without it
`ReviewService` would construct the `Review` object manually every time — and would have to repeat the validation logic there. If a second class ever needed to create a `Review`, it would copy-paste the same code, including the risk of missing the validation.

---

## 3. Builder — `ListingBuilder.java`
**Category:** Creational

### What is it
A helper object that lets you construct a complex object field by field in a readable chain, then finalize it with `build()`.

**Real-world example:** Ordering a custom burger. You say: "I want a beef patty, add cheese, add lettuce, no onions." You specify each part one at a time instead of shouting one long sentence with every ingredient at once.

### What it does in this project
When a user submits the Add Listing form, `ListingController` uses `ListingBuilder` to assemble the `Listing` object field by field. The `build()` method also automatically sets `status = "pending"` — the caller cannot forget this.

### How it works
```java
Listing listing = new ListingBuilder()
    .userId(loginUser.getId())
    .title(title)
    .description(description)
    .city(city)
    .address(address)
    .price(price)
    .propertyType(propertyType)
    .contactPhone(contactPhone)
    .contactWechat(contactWechat)
    .contactEmail(loginUser.getEmail())
    .build();
```

Each method sets one field and returns `this`, allowing the chain. `build()` assembles the final `Listing` object and forces `status = "pending"`.

### Without it
The controller would call `new Listing(userId, title, description, city, address, price, propertyType, ...)` — a constructor with many parameters in a fixed order. Swapping two arguments of the same type (like `contactPhone` and `contactWechat`) would cause a silent bug the compiler cannot catch.

---

## 4. Facade — `ListingFacade.java`
**Category:** Structural

### What is it
A single simple method that hides many complex steps happening behind it. The caller only sees one clean entry point.

**Real-world example:** A travel agent. You tell them "I want to go to Tokyo next month." They book the flight, reserve the hotel, and arrange the transfer — you make one call and get everything done. You don't deal with each airline and hotel separately.

### What it does in this project
When a user opens a listing detail page, three separate things need to happen: fetch the listing, fetch all its reviews, and calculate the average rating. `ListingFacade.getListingDetail()` does all three in one method call and returns a ready-to-display object.

### How it works
```java
public ListingDecorator getListingDetail(int listingId) {
    Listing listing      = listingDao.findById(listingId);
    List<Review> reviews = reviewDao.findByListingId(listingId);
    Double avgRating     = reviewDao.getAverageRating(listingId);

    return new ListingDecorator(listing)
            .withAverageRating(avgRating)
            .withReviewCount(reviews.size());
}
```

`ListingController` calls `listingService.getListingDetail(id)` — one call — and gets everything it needs. It does not know that `ListingDao` and `ReviewDao` were involved.

### Without it
The controller would have to call `listingDao`, then `reviewDao` twice, then construct the decorator itself — mixing database-layer logic into the controller, which is the wrong layer for it.

---

## 5. Decorator — `ListingDecorator.java`
**Category:** Structural

### What is it
A wrapper around an existing object that adds new data or behaviour without modifying the original class.

**Real-world example:** A plain coffee cup. You add a sleeve for heat protection and a lid for spill prevention. The cup itself is unchanged — the sleeve and lid are added on top of it. You can remove them without affecting the cup.

### What it does in this project
The `Listing` database entity only has fields that exist in the database. `averageRating` and `reviewCount` are calculated from the `review` table — they are not stored in `listing`. `ListingDecorator` wraps a `Listing` and adds those two calculated fields for the detail page to display.

### How it works
```java
public class ListingDecorator {
    private final Listing listing;
    private Double averageRating;
    private Integer reviewCount;

    public ListingDecorator(Listing listing) { this.listing = listing; }

    public ListingDecorator withAverageRating(Double avg) {
        this.averageRating = avg;
        return this;
    }

    public ListingDecorator withReviewCount(int count) {
        this.reviewCount = count;
        return this;
    }

    // Delegates original fields to the wrapped Listing
    public String getTitle() { return listing.getTitle(); }
    public Double getPrice() { return listing.getPrice(); }
    // ... all other original getters

    // New fields
    public Double  getAverageRating() { return averageRating; }
    public Integer getReviewCount()   { return reviewCount; }
}
```

The Thymeleaf template calls `listing.averageRating` and `listing.reviewCount` without knowing it is a decorator, not a plain `Listing`.

### Without it
You would have to add `averageRating` and `reviewCount` directly to the `Listing` entity class — mixing calculated display fields into the database entity, which pollutes the data model.

---

## 6. Composite — `NeighborhoodScore*.java`
**Category:** Structural

### What is it
A tree structure where individual items (leaves) and groups of items (composites) share the same interface. You can call the same method on one item or on the whole group — the group automatically computes the result from its children.

**Real-world example:** A company org chart. You can ask one employee "how many hours did you work?" or ask a department manager the same question — the manager automatically sums up all employees under them. Same question, different level of the tree.

### What it does in this project
A neighborhood has four rating categories: Safety, Transport, Food Access, and Foreigner-Friendly. Each is a leaf with its own score. The root composite holds all four and automatically calculates the Overall Liveability score as their average. The detail page calls `getScore()` on the root — it does not manually add up the four numbers.

### How it works
```
NeighborhoodScoreComposite ("Overall Liveability")  ← root, getScore() = average of children
├── NeighborhoodScoreLeaf ("Safety",             4.2)
├── NeighborhoodScoreLeaf ("Transport",          3.8)
├── NeighborhoodScoreLeaf ("Food Access",        4.5)
└── NeighborhoodScoreLeaf ("Foreigner-Friendly", 4.0)
```

`NeighborhoodService.buildScoreTree()` builds this tree and passes it to the template. The composite's `getScore()` loops through its children and returns the average automatically.

Both `NeighborhoodScoreLeaf` and `NeighborhoodScoreComposite` implement the same `NeighborhoodScoreComponent` interface with `getName()` and `getScore()`. The template does not need to know which type it is calling.

### Without it
The service would calculate the overall score manually: `(safety + transport + foodAccess + foreignerFriendly) / 4.0`. This formula is hardcoded to exactly four categories. Adding a fifth category in the future would require updating every place the formula appears.

---

## 7. Observer — `pattern/observer/`
**Category:** Behavioral

### What is it
A publish-subscribe system. One object fires an event. Any number of observers are subscribed and react to it automatically — the publisher does not know who they are or what they do.

**Real-world example:** A YouTube channel. When the channel uploads a new video, all subscribers get notified automatically. The channel does not call each subscriber individually — it just publishes, and everyone who subscribed reacts.

### What it does in this project
When an admin approves a listing, two things need to happen: log the event to the console, and update the cached pending listing count. Instead of `AdminController` calling the logger and counter directly, it fires an event through `ListingEventPublisher` and both observers react independently.

### How it works
**Publisher — `ListingEventPublisher`:**
```java
public void publishApproved(int listingId) {
    for (ListingObserver o : observers) o.onListingApproved(listingId);
}
```

**Observer 1 — `ListingApprovalLogger`:** prints an audit log line to the console.

**Observer 2 — `PendingListingCounter`:** decrements its cached pending count by 1 (no database query needed).

All three observers are registered once at startup in `AdminController` via `@PostConstruct`. When the admin approves a listing, `AdminController` calls `listingEventPublisher.publishApproved(id)` — one line — and both observers react automatically.

### Without it
`AdminController` would call the logger and update the counter directly after every approval — tightly coupling the controller to those two classes. Adding a third side-effect (like sending an email notification) would require editing `AdminController` again.

---

## 8. Command — `pattern/command/`
**Category:** Behavioral

### What is it
Each action is wrapped as an object with `execute()` and `undo()`. A history stack stores these objects so any action can be reversed by calling `undo()` on the most recent one.

**Real-world example:** Ctrl+Z in any text editor. Every action you perform is stored as an object. Pressing Ctrl+Z pops the last action and reverses it. The editor does not need to know what kind of action it was — it just calls undo on whatever is on top.

### What it does in this project
When an admin approves a listing or deactivates a user, the action is wrapped in a command object and passed to `CommandHistory`, which executes it and pushes it onto a stack. If the admin made a mistake, they click "Undo Last Action" on the dashboard — `CommandHistory` pops the last command and calls `undo()` on it, reversing the action without any manual database work.

### How it works
```java
// Approve a listing
commandHistory.executeCommand(new ApproveListingCommand(listingDao, id));

// Deactivate a user
commandHistory.executeCommand(new DeactivateUserCommand(userDao, id));

// Undo the last action
commandHistory.undoLast();
```

`CommandHistory` uses a `Deque<AdminCommand>` as a stack (LIFO). `push()` adds commands after execution, `pop()` removes the most recent one for undo.

`ApproveListingCommand.execute()` sets status to `"approved"`. Its `undo()` sets it back to `"pending"`.
`DeactivateUserCommand.execute()` sets role to `"deactivated"`. Its `undo()` sets it back to `"user"`.

`CommandHistory` never knows which type of command it is running — it only calls `execute()` and `undo()` through the `AdminCommand` interface.

### Without it
`listingDao.updateStatus(id, "approved")` would be called directly with no record of it happening and no way to reverse it. If the admin approved a fraudulent listing by mistake, they would need to fix it manually in the database.

---

## 9. Strategy — `pattern/strategy/`
**Category:** Behavioral

### What is it
A family of algorithms behind a shared interface. You choose which algorithm to use at runtime. The calling code never changes — only the selected strategy does.

**Real-world example:** A navigation app like Google Maps. You choose "driving," "walking," or "transit" — the app calculates the route using a different algorithm each time, but you always interact with the same "Get Directions" button.

### What it does in this project
The listings browse page lets users filter by city, price range, or property type. Each filter is a separate strategy that implements the same `ListingFilterStrategy` interface. `ListingService` checks which filters the user provided and applies the matching strategies in sequence.

### How it works
**The interface:**
```java
public interface ListingFilterStrategy {
    List<Listing> filter(List<Listing> listings);
}
```

**Runtime selection in `ListingService`:**
```java
if (city != null)
    strategy = new CityFilterStrategy(city);
    all = strategy.filter(all);

if (minPrice != null || maxPrice != null)
    strategy = new PriceRangeFilterStrategy(minPrice, maxPrice);
    all = strategy.filter(all);

if (propertyType != null)
    strategy = new PropertyTypeFilterStrategy(propertyType);
    all = strategy.filter(all);
```

Each strategy uses Java streams to filter the list. For example, `PriceRangeFilterStrategy` keeps only listings whose price falls within the given range, skipping null bounds if the user left one side empty.

### Without it
All filter logic would live as `if/else` blocks directly inside `ListingService`, mixed together in one large method. Adding a new filter type (for example, filter by number of rooms) would require editing the service method and adding another `else if` block.

---

## 10. Template Method — `BaseDao.java`
**Category:** Behavioral

### What is it
A base class defines the fixed steps of an operation as a skeleton. The specific details of each step are left as abstract methods for subclasses to fill in. The structure never changes — only the varying parts are overridden.

**Real-world example:** A recipe card that says "1. Prepare ingredients. 2. Cook. 3. Plate." The steps are always the same, but what you cook and how you plate it depends on the dish. The recipe gives the structure; the chef fills in the specifics.

### What it does in this project
`BaseDao` defines the fixed skeleton for fetching all records from the database: get the SQL, run the query, map each row to a Java object. `UserDao` extends `BaseDao` and only provides the SQL string and the row mapping. It inherits `findAll()` without writing the `jdbcTemplate.query()` call itself.

### How it works
```java
// BaseDao.java — defines the skeleton
public List<T> findAll() {
    String sql = getQuery();                             // step 1: subclass provides SQL
    return jdbcTemplate.query(sql,
        (rs, rowNum) -> mapResult(rs));                  // step 2: run query, subclass maps each row
}

protected abstract String getQuery();
protected abstract T mapResult(ResultSet rs) throws SQLException;
```

```java
// UserDao.java — fills in the two blanks
@Override
protected String getQuery() {
    return "SELECT * FROM user ORDER BY created_at DESC";
}

@Override
protected User mapResult(ResultSet rs) throws SQLException {
    User u = new User();
    u.setId(rs.getInt("id"));
    u.setUsername(rs.getString("username"));
    // ...
    return u;
}
```

`AdminController` calls `userDao.findAll()` — it runs `BaseDao`'s algorithm using `UserDao`'s SQL and mapping. `UserDao` never writes the query-execution logic itself.

### Without it
Every DAO would write its own `jdbcTemplate.query(...)` call with its own row-mapping logic inline. If that pattern ever needed to change — for example, adding logging around every query — it would need to be updated in every DAO separately.

---

## Summary: The 3 Categories

### Creational — how objects are created
| Pattern | Simple summary |
|---|---|
| Singleton | Only one instance ever. `DatabaseManager.getInstance()` always returns the same database connection wrapper. |
| Factory | One static method creates and validates the object. `ReviewFactory.createReview()` ensures every review is valid before it exists. |
| Builder | Build step by step with a readable chain. `new ListingBuilder().title(...).price(...).build()` — readable, safe, enforces defaults. |

### Structural — how objects are connected
| Pattern | Simple summary |
|---|---|
| Facade | One method hides many behind it. `ListingFacade.getListingDetail()` = 2 DAOs + decorator in one call. |
| Decorator | Wrap an object and add extra data. `ListingDecorator` adds `averageRating` and `reviewCount` to a plain `Listing`. |
| Composite | Tree where leaves and groups share one interface. Leaf = one score. Root = average of all leaves, computed automatically. |

### Behavioral — how objects communicate
| Pattern | Simple summary |
|---|---|
| Observer | Publisher fires event, observers react. Approval → logger logs, counter decrements — controller does not call them directly. |
| Command | Action = object. Execute stores it on a stack. Undo reverses it. Admin dashboard has a working "Undo Last Action" button. |
| Strategy | Swap algorithms at runtime. Same `filter()` call, different rule applied depending on what the user searched for. |
| Template Method | Base class defines the steps. Subclass fills in the specifics. `BaseDao.findAll()` = fixed skeleton, `UserDao` fills in SQL and mapping. |

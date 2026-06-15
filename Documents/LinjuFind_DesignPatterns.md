# Linju Find — Design Patterns Reference
*10 GoF Design Patterns implemented in this project. Read this once and you can explain every pattern to your lecturer.*

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
| 9 | Strategy | Behavioral | Filter algorithms (city / price / type) are swappable |
| 10 | Template Method | Behavioral | `BaseDao.findAll()` skeleton — subclasses fill in SQL + row mapping |

---

## How to Read Each Pattern Below

Every pattern entry has the same structure:
1. **What is it** — one simple sentence definition
2. **The problem without it** — what the code would look like without this pattern
3. **How it works in this project** — exact workflow, class by class
4. **What your lecturer will ask** — anticipated questions and your answers

---

## 1. Singleton — `DatabaseManager.java`
**Category:** Creational

### What is it
A class that can only ever have **one instance**. Everyone who needs it calls `getInstance()` and gets the same object.

### The problem without it
Without a Singleton, every DAO could create its own database connection object. A database connection pool is an expensive resource — creating multiple instances wastes memory and can exhaust the connection pool under load. All DAOs should share one single managed instance.

### How it works in this project

**Step 1 — Private constructor**
```java
// DatabaseManager.java
private DatabaseManager(DataSource dataSource) {
    setupDatabaseConnection(dataSource);
}
```
Nobody outside the class can call `new DatabaseManager()`. The only way to get an instance is through `initialize()` or `getInstance()`.

**Step 2 — Synchronized `getInstance()`**
```java
private static DatabaseManager instance;

public static synchronized DatabaseManager initialize(DataSource dataSource) {
    if (instance == null) {
        instance = new DatabaseManager(dataSource);
    }
    return instance;
}

public static synchronized DatabaseManager getInstance() {
    return instance;
}
```
`synchronized` ensures only one thread can create the instance at a time. `initialize()` is called once at startup by `DatabaseConfig`. After that, every DAO calls `getInstance()` to get the same object.

**Step 3 — Wraps the database connection**
```java
private void setupDatabaseConnection(DataSource dataSource) {
    this.connection = new JdbcTemplate(dataSource);
}

public JdbcTemplate getConnection() {
    return connection;
}
```

**Step 4 — Used in every DAO**
```java
// BaseDao.java
public BaseDao(DatabaseManager databaseManager) {
    this.jdbcTemplate = databaseManager.getConnection();
}
```
Every DAO receives the single `DatabaseManager` instance and calls `getConnection()` to get the shared `JdbcTemplate`. The Singleton is explicitly visible in every DAO constructor.

### What your lecturer will ask
- *"Why not just let Spring inject JdbcTemplate directly?"* — Spring could handle this automatically, but then the Singleton pattern would be invisible in the code. Writing `DatabaseManager` manually makes the pattern explicit and traceable.
- *"Why is `getInstance()` synchronized?"* — To prevent two threads from both reading `instance == null` at the same time and each creating a separate instance.

---

## 2. Factory — `ReviewFactory.java`
**Category:** Creational

### What is it
A class with a **static method that builds objects** for you. You tell it what you want; it figures out how to create it correctly.

### The problem without it
Before Factory, `ReviewService` created a `Review` like this:
```java
Review review = new Review();
review.setListingId(listingId);
review.setUserId(userId);
review.setRating(rating);
review.setReviewText(reviewText);
```
No validation. Any caller could pass rating = 0 or an empty text. If a second piece of code ever needed to create a `Review`, it would copy-paste this block — including forgetting the validation.

### How it works in this project

**`ReviewFactory.createReview()`** — the only place a `Review` is ever built:
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

**`ReviewService.addReview()`** — calls the factory instead of building manually:
```java
Review review = ReviewFactory.createReview(listingId, userId, rating, reviewText);
reviewDao.insert(review);
```

**Workflow when a user submits a review:**
```
User clicks "Submit Review"
  → ReviewController.processAdd()
    → ReviewService.addReview()
      → ReviewFactory.createReview()   ← validation happens here
        → new Review() assembled
      → ReviewDao.insert(review)       ← saved to DB
```

### What your lecturer will ask
- *"What is the difference between Factory and Builder?"* — Factory creates the whole object in one static call with validation. Builder is for when you want to set fields one at a time in a readable chain before calling `build()`. Factory = "give me one thing now". Builder = "let me put it together step by step".

---

## 3. Builder — `ListingBuilder.java`
**Category:** Creational

### What is it
A helper object that lets you construct a complex object **field by field** in a readable chain, then get the final object with `build()`.

### The problem without it
A `Listing` has 7 fields. Without Builder:
```java
new Listing(loginUser.getId(), title, description, city, address, price, propertyType);
```
Which argument is which? Easy to swap `price` and `userId` accidentally. The compiler won't catch it if both are the same type.

### How it works in this project

**`ListingBuilder`** — each method sets one field and returns `this` (so you can chain):
```java
public ListingBuilder title(String title)   { this.title = title; return this; }
public ListingBuilder price(Double price)   { this.price = price; return this; }
// ... all 7 fields
public Listing build() {
    Listing listing = new Listing();
    listing.setTitle(this.title);
    listing.setPrice(this.price);
    listing.setStatus("pending");   // always forced to pending — caller can't forget
    ...
    return listing;
}
```

**`ListingController.processAdd()`** — uses the builder:
```java
Listing listing = new ListingBuilder()
    .userId(loginUser.getId())
    .title(title)
    .description(description)
    .city(city)
    .address(address)
    .price(price)
    .propertyType(propertyType)
    .build();
listingService.addListing(listing);
```

**Workflow when a user posts a new listing:**
```
User fills the "Add Listing" form → POST /listings/add
  → ListingController.processAdd()
    → new ListingBuilder()
        .userId(...)
        .title(...)
        ...
        .build()           ← status="pending" forced here
    → ListingService.addListing(listing)
      → ListingDao.insert(listing)   ← saved to DB, awaits admin approval
```

### What your lecturer will ask
- *"Why not just use setters?"* — You can, but the builder chains them in one expression that reads like a sentence. It also lets `build()` enforce rules (like always setting `status = "pending"`) that plain setters can't enforce by themselves.

---

## 4. Facade — `ListingFacade.java`
**Category:** Structural

### What is it
A **single simple method** that hides many complex steps happening behind it. Like pressing one button on a remote instead of configuring each component separately.

### The problem without it
To show a listing detail page, you need data from 3 different places. Without Facade, the controller would do:
```java
Listing listing = listingDao.findById(id);
List<Review> reviews = reviewDao.findByListingId(id);
Double avg = reviewDao.getAverageRating(id);
ListingDecorator decorated = new ListingDecorator(listing).withAverageRating(avg).withReviewCount(reviews.size());
```
That is 4 lines of database-layer logic sitting inside a controller — the wrong layer.

### How it works in this project

**`ListingFacade.getListingDetail()`** — all 4 steps in one place:
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

**`ListingController.detail()`** — one call, gets everything:
```java
ListingDecorator listing = listingService.getListingDetail(id);
```

**Workflow when a user opens a listing detail page:**
```
User clicks on a listing → GET /listings/{id}
  → ListingController.detail()
    → ListingService.getListingDetail()
      → ListingFacade.getListingDetail()   ← the facade
          → ListingDao.findById()          ← call 1
          → ReviewDao.findByListingId()    ← call 2
          → ReviewDao.getAverageRating()   ← call 3
          → new ListingDecorator(...)      ← call 4
        ← returns one ready-to-display object
    ← passed to Thymeleaf → listing/detail.html
```

### What your lecturer will ask
- *"What subsystem does the Facade hide?"* — It hides the coordination between `ListingDao` and `ReviewDao`, and the construction of `ListingDecorator`. The controller only knows about the facade, not those three classes.

---

## 5. Decorator — `ListingDecorator.java`
**Category:** Structural

### What is it
A **wrapper** around an existing object that adds new data or behaviour without touching the original class.

### The problem without it
The `Listing` database table has no `average_rating` or `review_count` columns — these are calculated from the `review` table. You cannot store them in `Listing` because they would go stale the moment a new review is added. But the detail page needs to display them. Without Decorator, you'd either modify the entity class (mixing display concerns into the data model) or pass separate variables to every template.

### How it works in this project

**`ListingDecorator`** — wraps a `Listing` and adds two extra fields:
```java
public class ListingDecorator {
    private final Listing listing;      // the real object
    private Double averageRating;
    private Integer reviewCount;

    // Delegates everything to the wrapped object
    public String getTitle()  { return listing.getTitle(); }
    public Double getPrice()  { return listing.getPrice(); }
    // ... all original fields

    // New fields not in the DB
    public Double  getAverageRating() { return averageRating; }
    public Integer getReviewCount()   { return reviewCount; }
}
```

**`ListingFacade`** attaches the extra data at request time:
```java
return new ListingDecorator(listing)
        .withAverageRating(avgRating)
        .withReviewCount(reviews.size());
```

**In `listing/detail.html`**, Thymeleaf calls `listing.averageRating` and `listing.reviewCount` — it doesn't know or care that `listing` is a Decorator, not a plain `Listing`.

### What your lecturer will ask
- *"Why not just add those fields directly to the `Listing` class?"* — Because `Listing` is the database entity. Adding display-only calculated fields to it mixes two concerns. The Decorator keeps the entity clean and adds display data only when it's needed.

---

## 6. Composite — `NeighborhoodScore*.java`
**Category:** Structural

### What is it
A tree structure where **individual items (leaves) and groups of items (composites) share the same interface**. You can call the same method on one item or the whole group — the group automatically aggregates.

### The problem without it
A neighborhood has 4 rating categories: Safety, Transport, Food Access, Foreigner-Friendly. To show an overall score on the detail page you'd write:
```java
double overall = (safety + transport + foodAccess + foreignerFriendly) / 4.0;
```
This formula is hardcoded to exactly 4 categories. If a 5th category is added tomorrow, you find and update every place this formula appears.

### How it works in this project

**The shared interface — `NeighborhoodScoreComponent`:**
```java
public interface NeighborhoodScoreComponent {
    String getName();
    double getScore();
}
```
Both the leaf and the composite implement this. You can call `getScore()` on either.

**The leaf — `NeighborhoodScoreLeaf`:**
Holds one category score. `getScore()` just returns the stored value.
```java
new NeighborhoodScoreLeaf("Safety", 4.2)       // getScore() → 4.2
new NeighborhoodScoreLeaf("Transport", 3.8)    // getScore() → 3.8
```

**The composite — `NeighborhoodScoreComposite`:**
Holds a list of children. `getScore()` averages all children.
```java
public double getScore() {
    double sum = 0;
    for (NeighborhoodScoreComponent child : children) sum += child.getScore();
    return Math.round((sum / children.size()) * 10.0) / 10.0;
}
```

**`NeighborhoodService.buildScoreTree()`** — builds the tree for a neighborhood:
```java
NeighborhoodScoreComposite root = new NeighborhoodScoreComposite("Overall Liveability");
root.add(new NeighborhoodScoreLeaf("Safety",             n.getAvgSafety()));
root.add(new NeighborhoodScoreLeaf("Transport",          n.getAvgTransport()));
root.add(new NeighborhoodScoreLeaf("Food Access",        n.getAvgFoodAccess()));
root.add(new NeighborhoodScoreLeaf("Foreigner-Friendly", n.getAvgForeignerFriendly()));
// root.getScore() → average of the 4 leaves
```

**Workflow when a user views a neighborhood detail page:**
```
User opens 鼓楼区 → GET /neighborhoods/1
  → NeighborhoodController.detail()
    → NeighborhoodService.buildScoreTree(neighborhood)
        → creates NeighborhoodScoreComposite (root)
        → adds 4 NeighborhoodScoreLeaf children
    → model.addAttribute("scoreTree", root)
  → neighborhood/detail.html
      Safety:              4.2 / 5
      Transport:           3.8 / 5
      Food Access:         4.5 / 5
      Foreigner-Friendly:  4.0 / 5
      Overall Liveability: 4.1 / 5   ← root.getScore() called by Thymeleaf
```

### What your lecturer will ask
- *"What is the difference between a leaf and a composite?"* — A leaf has no children and just holds its own value. A composite holds children and its `getScore()` is computed from them. Both look the same from the outside because they share the same interface.

---

## 7. Observer — `pattern/observer/`
**Category:** Behavioral

### What is it
A **publish-subscribe system**. One object (the publisher) fires an event. Any number of observers are subscribed and react to it automatically — the publisher does not know who they are or what they do.

### The problem without it
When the admin approves a listing, two things need to happen: log the event, and update the pending count. Without Observer, `AdminController.approveListing()` would call the logger and update the counter directly:
```java
listingDao.updateStatus(id, "approved");
logger.log("Approved listing " + id);          // now tightly coupled
counter.decrement();                            // now tightly coupled
```
Every new side-effect (e.g., send an email) means editing `AdminController` again.

### How it works in this project

**The observer interface — `ListingObserver`:**
```java
public interface ListingObserver {
    void onListingApproved(int listingId);
    void onListingDeleted(int listingId);
}
```

**The publisher — `ListingEventPublisher`:**
```java
private final List<ListingObserver> observers = new ArrayList<>();

public void register(ListingObserver observer) { observers.add(observer); }

public void publishApproved(int listingId) {
    for (ListingObserver o : observers) o.onListingApproved(listingId);
}
```

**Observer 1 — `ListingApprovalLogger`:** implements `ListingObserver`, prints to console.

**Observer 2 — `PendingListingCounter`:** implements `ListingObserver`, maintains a cached count.
- First access: loads count from DB once.
- `onListingApproved()`: decrements the cached count by 1 (no DB query needed).
- `onListingDeleted()`: resets to -1 (forces a fresh DB load next time, since the deleted listing may have been pending).

**Registration in `AdminController`** — happens once at startup via `@PostConstruct`:
```java
@PostConstruct
public void registerObservers() {
    listingEventPublisher.register(listingApprovalLogger);
    listingEventPublisher.register(pendingListingCounter);
}
```

**Workflow when admin approves a listing:**
```
Admin clicks "Approve" → POST /admin/listings/approve/3
  → AdminController.approveListing(3)
    → commandHistory.executeCommand(new ApproveListingCommand(...))
      → listingDao.updateStatus(3, "approved")   ← DB updated
    → listingEventPublisher.publishApproved(3)
        → listingApprovalLogger.onListingApproved(3)
            → prints: "[LOG] Listing #3 was APPROVED by admin."
        → pendingListingCounter.onListingApproved(3)
            → pendingCount--
  → redirect to /admin/listings
```

### What your lecturer will ask
- *"Why use Observer instead of just calling those methods directly?"* — Because the publisher (`AdminController`) should not know about the logger or the counter. With Observer, you can add a third observer (e.g., email notification) by registering one new class — no changes to `AdminController`.

---

## 8. Command — `pattern/command/`
**Category:** Behavioral

### What is it
Each action is wrapped as an **object** with `execute()` and `undo()`. A history stack stores these objects. To undo, pop the last one and call `undo()`.

### The problem without it
When the admin approves a listing, `listingDao.updateStatus(id, "approved")` is called directly. There is no record that this action happened, no way to reverse it. If the admin approves the wrong listing, they need to go into the database manually.

### How it works in this project

**The interface — `AdminCommand`:**
```java
public interface AdminCommand {
    void execute();
    void undo();
    String getDescription();
}
```

**Concrete command 1 — `ApproveListingCommand`:**
```java
public void execute() { listingDao.updateStatus(listingId, "approved"); }
public void undo()    { listingDao.updateStatus(listingId, "pending");  }
```

**Concrete command 2 — `DeactivateUserCommand`:**
```java
public void execute() { userDao.updateRole(userId, "deactivated"); }
public void undo()    { userDao.updateRole(userId, "user");        }
```

**The invoker — `CommandHistory`:**
```java
private final Deque<AdminCommand> history = new ArrayDeque<>();

public void executeCommand(AdminCommand command) {
    command.execute();   // run the action
    history.push(command);  // remember it
}

public String undoLast() {
    AdminCommand last = history.pop();
    last.undo();             // reverse the action
    return last.getDescription();
}
```

**`AdminController`** uses `CommandHistory` for reversible actions:
```java
// Approve
commandHistory.executeCommand(new ApproveListingCommand(listingDao, id));

// Deactivate user
commandHistory.executeCommand(new DeactivateUserCommand(userDao, id));

// Undo (new endpoint: POST /admin/undo)
@PostMapping("/undo")
public String undo(HttpSession session) {
    commandHistory.undoLast();
    return "redirect:/admin/dashboard";
}
```

**The admin dashboard** shows the last action and an Undo button:
```html
<p>Last action: <strong th:text="${lastAction}"></strong></p>
<form th:action="@{/admin/undo}" method="post">
    <button type="submit">Undo Last Action</button>
</form>
```

**Workflow — admin approves then undoes:** 
```
Admin clicks "Approve" on listing #3
  → commandHistory.executeCommand(new ApproveListingCommand(listingDao, 3))
      → listingDao.updateStatus(3, "approved")   ← DB: status = approved
      → history stack: [ApproveListingCommand(3)]

Admin realises it was wrong, clicks "Undo Last Action"
  → POST /admin/undo
    → commandHistory.undoLast()
        → ApproveListingCommand.undo()
          → listingDao.updateStatus(3, "pending")  ← DB: status = pending again
      → history stack: []  (empty)
```

### What your lecturer will ask
- *"What is the history stack data structure?"* — `ArrayDeque<AdminCommand>`. `push()` adds to the top, `pop()` removes from the top — LIFO (Last In, First Out), so the most recent action is always undone first.
- *"Why does `CommandHistory` not know what kind of command it is running?"* — Because it only calls `execute()` and `undo()` through the `AdminCommand` interface. It doesn't care if it's approving a listing or deactivating a user — the polymorphism handles it.

---

## 9. Strategy — `pattern/strategy/`
**Category:** Behavioral

### What is it
A family of algorithms (strategies) behind a shared interface. You pick which algorithm to use at runtime. The code that uses them doesn't change — only which strategy is selected.

### The problem without it
The listings browse page filters results. Without Strategy:
```java
if (city != null) {
    // filter by city
} else if (minPrice != null || maxPrice != null) {
    // filter by price
} else if (propertyType != null) {
    // filter by type
}
```
Every new filter type adds another `else if`. The controller grows and the filter logic is stuck inside it permanently.

### How it works in this project

**The interface — `ListingFilterStrategy`:**
```java
public interface ListingFilterStrategy {
    List<Listing> filter(List<Listing> listings);
}
```

**Three concrete strategies:**

`CityFilterStrategy` — keeps only listings where `city` matches:
```java
return listings.stream()
    .filter(l -> l.getCity().equalsIgnoreCase(city))
    .collect(Collectors.toList());
```

`PriceRangeFilterStrategy` — keeps listings between `minPrice` and `maxPrice`:
```java
return listings.stream()
    .filter(l -> (minPrice == null || l.getPrice() >= minPrice)
              && (maxPrice == null || l.getPrice() <= maxPrice))
    .collect(Collectors.toList());
```

`PropertyTypeFilterStrategy` — keeps only listings matching the type (studio, apartment, shared):
```java
return listings.stream()
    .filter(l -> l.getPropertyType().equalsIgnoreCase(propertyType))
    .collect(Collectors.toList());
```

**`ListingService`** applies whichever strategies are relevant:
```java
List<ListingFilterStrategy> strategies = new ArrayList<>();
if (city != null)         strategies.add(new CityFilterStrategy(city));
if (minPrice != null || maxPrice != null) strategies.add(new PriceRangeFilterStrategy(minPrice, maxPrice));
if (propertyType != null) strategies.add(new PropertyTypeFilterStrategy(propertyType));

for (ListingFilterStrategy strategy : strategies) {
    listings = strategy.filter(listings);
}
```

**Workflow when a user searches listings:**
```
User sets filter: city = "Nanjing", propertyType = "studio"
  → GET /listings?city=Nanjing&propertyType=studio
    → ListingController.list()
      → ListingService.getApprovedListings(city, null, null, propertyType, page, pageSize)
          → CityFilterStrategy("Nanjing").filter(listings)       ← removes non-Nanjing
          → PropertyTypeFilterStrategy("studio").filter(listings) ← keeps only studios
        ← returns filtered, paginated list
    → model: listings = [Cozy Studio near Nanjing University]
```

### What your lecturer will ask
- *"Can multiple strategies apply at the same time?"* — Yes. The service builds a list of all applicable strategies and applies them in sequence. City filter runs first, then price filter, then type filter.

---

## 10. Template Method — `BaseDao.java`
**Category:** Behavioral

### What is it
A base class defines the **skeleton of an operation** — the steps are fixed. Specific steps are left as `abstract` methods for subclasses to fill in. The structure never changes; only the varying parts are overridden.

### The problem without it
Every DAO that fetches all rows does the same thing: build SQL, run query, map each row. Without Template Method, `UserDao`, `ListingDao`, and `ReviewDao` would all have nearly identical `findAll()` code with only the SQL string and row-mapper lambda different. Any bug in the fetch pattern is fixed three times.

### How it works in this project

**`BaseDao<T>`** — defines the skeleton:
```java
public abstract class BaseDao<T> {

    protected final JdbcTemplate jdbcTemplate;

    // Template method — fixed structure, two steps delegated to subclass
    public List<T> findAll() {
        String sql = getQuery();                               // step 1: subclass provides SQL
        return jdbcTemplate.query(sql,                         // step 2: run the query
            (rs, rowNum) -> mapResult(rs));                   // step 3: subclass provides mapping
    }

    protected abstract String getQuery();                      // subclass fills in
    protected abstract T mapResult(ResultSet rs) throws SQLException;  // subclass fills in
}
```

**`UserDao extends BaseDao<User>`** — only provides the two varying parts:
```java
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
`UserDao` gets `findAll()` for free. It never writes the `jdbcTemplate.query()` call itself.

**Workflow when admin opens the Users page:**
```
Admin opens /admin/users
  → AdminController.users()
    → UserDao.findAll()          ← inherited from BaseDao, not defined in UserDao
        → getQuery()             ← UserDao: "SELECT * FROM user ORDER BY created_at DESC"
        → jdbcTemplate.query()   ← BaseDao runs the query
        → mapResult(rs)          ← UserDao: maps each row to a User object
      ← returns List<User>
  → admin/users.html rendered
```

### What your lecturer will ask
- *"Which class defines `findAll()` and which class uses it?"* — `BaseDao` defines it. `UserDao` inherits it and uses it without writing it.
- *"What are the abstract methods and why are they abstract?"* — `getQuery()` and `mapResult()`. They are abstract because the SQL and the row mapping are different for every table — only the concrete DAO knows them. `BaseDao` cannot know them in advance.
- *"Is this the same as inheritance?"* — It uses inheritance, but the pattern is specifically about the algorithm skeleton. The key idea is the **inversion of control**: the base class calls the subclass methods (`getQuery`, `mapResult`), not the other way around.

---

## Summary: The 3 Categories

### Creational (how objects are created)
| Pattern | Simple summary |
|---|---|
| Singleton | Only one instance ever. `DatabaseManager.getInstance()` always returns the same database connection wrapper. |
| Factory | One static method creates and validates the object. `ReviewFactory.createReview()`. |
| Builder | Build step by step with a readable chain. `new ListingBuilder().title(...).price(...).build()`. |

### Structural (how objects are connected)
| Pattern | Simple summary |
|---|---|
| Facade | One method hides many behind it. `ListingFacade.getListingDetail()` = 3 DAO calls + decorator. |
| Decorator | Wrap an object and add extra data. `ListingDecorator` adds `averageRating` to a plain `Listing`. |
| Composite | Tree where leaves and groups share one interface. Leaf = one score. Root = average of all leaves. |

### Behavioral (how objects communicate)
| Pattern | Simple summary |
|---|---|
| Observer | Publisher fires event, observers react. Approval → logger logs, counter decrements. |
| Command | Action = object. Execute stores it. Undo reverses it. Admin dashboard has "Undo Last Action". |
| Strategy | Swap algorithms at runtime. Same filter method, different rule applied based on user input. |
| Template Method | Base class defines the steps. Subclass fills in the specifics. `BaseDao.findAll()` = fixed skeleton. |

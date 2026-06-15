# Linju Find — Project Background

---

## The Real-World Problem This Project Addresses

China is going through one of the worst property crises in its history.

For two decades, real estate was treated as the safest investment in the country. Developers borrowed enormous amounts of money to build as fast as possible. Families poured their savings into apartments — not to live in, but because they believed prices would always go up.

That belief has broken. Major developers like **Evergrande** have collapsed under hundreds of billions in debt. Across China, there are an estimated **65 to 80 million empty apartments** that nobody is living in. Entire districts were built faster than the population needed them — what people now call **"ghost cities."**

Two deeper problems make it worse:
- China's **birth rate has been falling** for years — fewer young families forming, less natural demand
- People who could afford to buy are **choosing not to**, afraid prices will keep dropping — a self-fulfilling cycle

**The painful result:** millions of empty apartments exist across China, while young workers, migrants, and international students are struggling to find affordable, trustworthy housing in the cities where they actually want to live. The supply exists. The connection between available housing and the people who need it is broken.

---

## Why the Connection Is Broken — The Trust Gap

The biggest reason people can't connect with available housing is **trust**.

Most Chinese housing platforms (Anjuke 安居客, Lianjia 链家) are:
- Flooded with developer advertisements and inflated listings
- Entirely agent-driven — all content comes from sellers, not tenants
- Written entirely in Chinese — unusable for foreigners without Mandarin skills
- Offering zero honest, community-sourced information about what it actually feels like to live somewhere

International students face an additional layer of difficulty — language barriers, unfamiliarity with local systems, no local connections. Many end up finding housing through informal WeChat groups or by relying on whoever helps them when they first arrive.

---

## What Linju Find Is

A **community-based housing discovery and review platform** built for:
- Foreign students and expatriates living in Chinese cities
- Young Chinese locals navigating the housing market

**What users can do:**
- Browse housing listings
- Read honest tenant reviews
- Rate neighborhoods on liveability (safety, transport, food, foreigner-friendliness)
- Post community tips about specific areas

**What the platform is NOT:**
- It does not handle actual rentals or payments
- It is not trying to compete with Anjuke or Lianjia
- It is not trying to fix the entire property crisis

It is purely an **information and trust platform** — helping people make better decisions about where to live through honest, community-sourced information.

---

## What Makes It Different From Existing Platforms

| Feature | Anjuke | Lianjia | Trulia (US) | Linju Find |
|---|---|---|---|---|
| Housing listings | Yes | Yes | Yes | Yes |
| Verified listings | No | Yes | Partial | Partial (admin approval) |
| Tenant / resident reviews | No | No | Yes | Yes |
| Neighborhood ratings | No | No | Yes | Yes |
| English / multilingual support | No | No | English only | Yes |
| Foreigner-focused | No | No | No | Yes |
| Community-sourced content | No | No | Partial | Yes |
| Relevant to China's market | Yes | Yes | No | Yes |

**Key insight from the research:**
- Anjuke has the listings volume
- Lianjia has the verification and trust
- Trulia (US) has the community review model

None of them combine all three. None of them serve foreigners in China. That is the gap Linju Find targets.

---

## The Goal

Linju Find does not aim to fix the entire property crisis — that would be unrealistic.

Its specific goal is to **make one part of the problem smaller**: helping people who genuinely need housing find, evaluate, and trust their options through honest community reviews and neighborhood ratings.

Every person who finds a good apartment through honest community information is one fewer empty unit sitting idle.

---

## Academic Context

This project demonstrates the full J2EE technology stack studied throughout the course:

- HTTP / Request-Response model
- Session and Cookie management
- Filter and FilterChain
- Five-Tier Architecture (View → Controller → Service → DAO → Database)
- Spring Boot with Thymeleaf (modern alternative to JSP)
- JdbcTemplate for database access (no ORM)
- MySQL database design

It also explicitly implements **10 GoF Design Patterns** across all 3 categories (Creational, Structural, Behavioral) for the Java Design Pattern course:

| Pattern | Category |
|---|---|
| Singleton | Creational |
| Factory | Creational |
| Builder | Creational |
| Facade | Structural |
| Decorator | Structural |
| Composite | Structural |
| Observer | Behavioral |
| Command | Behavioral |
| Strategy | Behavioral |
| Template Method | Behavioral |

---

## App Name

| | |
|---|---|
| English name | **Linju Find** |
| Chinese name | **邻居找房** |
| Meaning | 邻居 = neighbor / community, 找房 = find housing |

The name reflects the core idea: finding housing through your community, not through advertisers.

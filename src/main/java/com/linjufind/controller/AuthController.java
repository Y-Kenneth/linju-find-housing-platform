package com.linjufind.controller;

import com.linjufind.dto.ShowcaseListing;
import com.linjufind.entity.Listing;
import com.linjufind.entity.Review;
import com.linjufind.entity.User;
import com.linjufind.service.ListingService;
import com.linjufind.service.ReviewService;
import com.linjufind.service.UserService;
import com.linjufind.util.CityNames;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {

    /** How many listings feed the login-page marquee (it loops, so we want a healthy spread). */
    private static final int SHOWCASE_LIMIT = 12;
    private static final int SNIPPET_MAX_CHARS = 120;

    /** Remember Me cookie name + how many distinct usernames we keep in the dropdown. */
    private static final String REMEMBER_COOKIE = "rememberedUsers";
    private static final int MAX_REMEMBERED = 5;

    /**
     * Builds an ordered name → Hanzi map for the given cities (keyed exactly as the
     * city strings the template iterates, so {@code ${cityHanzi.get(c)}} resolves).
     * Cities with no known Hanzi are omitted, leaving the chip English-only.
     */
    private Map<String, String> cityHanziFor(List<String> cities) {
        Map<String, String> out = new LinkedHashMap<>();
        if (cities == null) {
            return out;
        }
        for (String city : cities) {
            if (city == null) {
                continue;
            }
            String hanzi = cityNames.hanzi(city);
            if (hanzi != null) {
                out.put(city, hanzi);
            }
        }
        return out;
    }

    /** Fixed set of cities shown on the brand panel — kept to 6 so the chips fit on one line. */
    private static final List<String> FEATURED_CITIES =
            List.of("Beijing", "Shanghai", "Shenzhen", "Nanjing", "Guangzhou", "Chongqing");

    private final UserService userService;
    private final ListingService listingService;
    private final ReviewService reviewService;
    private final CityNames cityNames;

    public AuthController(UserService userService,
                          ListingService listingService,
                          ReviewService reviewService,
                          CityNames cityNames) {
        this.userService = userService;
        this.listingService = listingService;
        this.reviewService = reviewService;
        this.cityNames = cityNames;
    }

    @GetMapping("/login")
    public String showLogin(HttpServletRequest request, Model model) {
        // Pull the remembered usernames from the Remember Me cookie. The first one
        // (most recent) pre-fills the field; the full list feeds the dropdown.
        List<String> remembered = readRememberedUsers(request);
        if (!remembered.isEmpty()) {
            model.addAttribute("rememberedUsername", remembered.get(0));
            model.addAttribute("rememberedUsernames", remembered);
        }

        // Public marquee of recent approved listings (login is whitelisted, so no session needed)
        model.addAttribute("showcaseListings", buildShowcase());

        // Real trust-strip figures for the brand panel
        model.addAttribute("statCities", listingService.countApprovedCities());
        model.addAttribute("statListings", listingService.countApprovedListings(null, null, null, null));
        model.addAttribute("statReviews", reviewService.count());
        model.addAttribute("topCities", FEATURED_CITIES);
        model.addAttribute("cityHanzi", cityHanziFor(FEATURED_CITIES));
        return "auth/login";
    }

    // Same curated pool used by the browse page JS fallback — same index formula
    // (id % pool size) means a listing always gets the same photo on both pages.
    private static final String[] FALLBACK_IMAGES = {
        // Living rooms
        "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1554995207-c18c203602cb?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1583847268964-b28dc8f51f92?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1616594039964-ae9021a400a0?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1600566753086-00f18fb6b3ea?w=600&h=400&fit=crop",
        // Bedrooms
        "https://images.unsplash.com/photo-1505691938895-1758d7feb511?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1540518614846-7eded433c457?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1567767292278-a4f21aa2d36e?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1560185007-cde436f6a4d0?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1616594039964-ae9021a400a0?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&h=400&fit=crop",
        // Kitchens
        "https://images.unsplash.com/photo-1484154218962-a197022b5858?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1556912172-45b7abe8b7e1?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1565538810643-b5bdb714032a?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1588854337236-6889d631faa8?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1507089947368-19c1da9775ae?w=600&h=400&fit=crop",
        // Studios & compact apartments
        "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1600047509807-ba8f99d2cdde?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1519710164239-da123dc03ef4?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1617103996702-96ff29b1c467?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1534430480872-3498386e7856?w=600&h=400&fit=crop",
        // Dining & open-plan
        "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1617098900591-3f90928e8c54?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1592928302636-c83cf1e1c887?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1556020685-ae41abfc9365?w=600&h=400&fit=crop",
        // Bathrooms
        "https://images.unsplash.com/photo-1552321554-5fefe8c9ef14?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1507652313519-d4e9174996dd?w=600&h=400&fit=crop",
        // Apartment exteriors & building facades
        "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1486325212027-8081e485255e?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1460317442991-0ec209397118?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=600&h=400&fit=crop",
        // Shared spaces & common areas
        "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1600566752355-35792bedcfea?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=600&h=400&fit=crop",
        // Balconies & views
        "https://images.unsplash.com/photo-1582268611958-ebfd161ef9cf?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1571939228382-b2f2b585ce15?w=600&h=400&fit=crop",
        "https://images.unsplash.com/photo-1480074568708-e7b720bb3f09?w=600&h=400&fit=crop"
    };

    /** Builds the display-only listing cards (image, rating, review snippet) for the login marquee. */
    private List<ShowcaseListing> buildShowcase() {
        List<Listing> approved =
                listingService.getApprovedListings(null, null, null, null, 1, SHOWCASE_LIMIT);

        List<ShowcaseListing> showcase = new ArrayList<>();
        for (Listing l : approved) {
            ShowcaseListing s = new ShowcaseListing();
            s.setId(l.getId());
            s.setTitle(l.getTitle());
            s.setCity(l.getCity());
            s.setPrice(l.getPrice());
            s.setPropertyType(l.getPropertyType());
            String img = (l.getImageUrl() != null && !l.getImageUrl().isBlank())
                    ? l.getImageUrl()
                    : FALLBACK_IMAGES[l.getId() % FALLBACK_IMAGES.length];
            s.setImageUrl(img);

            List<Review> reviews = reviewService.getByListing(l.getId());
            s.setReviewCount(reviews.size());
            s.setAverageRating(reviewService.getAverageRating(l.getId()));

            // First review that actually has text becomes the card's quote
            for (Review r : reviews) {
                String text = r.getReviewText();
                if (text != null && !text.isBlank()) {
                    text = text.trim();
                    if (text.length() > SNIPPET_MAX_CHARS) {
                        text = text.substring(0, SNIPPET_MAX_CHARS).trim() + "…";
                    }
                    s.setReviewSnippet(text);
                    s.setReviewAuthor(r.getUsername());
                    break;
                }
            }
            showcase.add(s);
        }
        return showcase;
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam(required = false) String rememberMe,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               Model model) {
        User user = userService.login(username, password);
        if (user == null) {
            model.addAttribute("error", "Invalid username or password.");
            return "auth/login";
        }
        if ("deactivated".equals(user.getRole())) {
            model.addAttribute("error", "This account has been deactivated. Please contact an admin.");
            return "auth/login";
        }

        // Store user in session — key must be "loginUser"
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);

        // Remember Me — add this username to the 7-day cookie (most-recent-first,
        // de-duplicated, capped at MAX_REMEMBERED) so the login dropdown can offer it.
        if ("on".equals(rememberMe)) {
            List<String> remembered = readRememberedUsers(request);
            remembered.remove(username);           // drop any existing entry...
            remembered.add(0, username);           // ...and re-add it at the front
            if (remembered.size() > MAX_REMEMBERED) {
                remembered = remembered.subList(0, MAX_REMEMBERED);
            }
            writeRememberedUsers(response, remembered);
        }

        if ("admin".equals(user.getRole())) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/listings";
    }

    /**
     * Reads the remembered usernames from the Remember Me cookie. The cookie stores a
     * pipe-separated, URL-encoded list. (Pipe — not comma — because a comma is not a
     * legal cookie-value character and Tomcat's RFC 6265 parser drops such cookies;
     * URL-encoding the names means they never themselves contain a pipe.)
     * Returns a mutable, most-recent-first list (possibly empty).
     */
    private List<String> readRememberedUsers(HttpServletRequest request) {
        List<String> users = new ArrayList<>();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return users;
        }
        for (Cookie c : cookies) {
            if (!REMEMBER_COOKIE.equals(c.getName()) || c.getValue() == null) {
                continue;
            }
            for (String part : c.getValue().split("\\|")) {
                if (part.isBlank()) {
                    continue;
                }
                String name = URLDecoder.decode(part, StandardCharsets.UTF_8);
                if (!users.contains(name)) {   // guard against a malformed cookie with dupes
                    users.add(name);
                }
            }
        }
        return users;
    }

    /** Writes the remembered usernames back as a single 7-day, URL-encoded cookie. */
    private void writeRememberedUsers(HttpServletResponse response, List<String> users) {
        LinkedHashSet<String> encoded = new LinkedHashSet<>();
        for (String name : users) {
            encoded.add(URLEncoder.encode(name, StandardCharsets.UTF_8));
        }
        Cookie cookie = new Cookie(REMEMBER_COOKIE, String.join("|", encoded));
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        // Same shared brand panel as the login page
        model.addAttribute("showcaseListings", buildShowcase());
        model.addAttribute("statCities", listingService.countApprovedCities());
        model.addAttribute("statListings", listingService.countApprovedListings(null, null, null, null));
        model.addAttribute("statReviews", reviewService.count());
        model.addAttribute("topCities", FEATURED_CITIES);
        model.addAttribute("cityHanzi", cityHanziFor(FEATURED_CITIES));
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String username,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam(required = false) String nationality,
                                  @RequestParam(required = false) String city,
                                  Model model) {
        String error = userService.register(username, email, password, nationality, city);
        if (error != null) {
            model.addAttribute("error", error);
            return "auth/register";
        }
        return "redirect:/login?registered=true";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Only end the session. The "rememberedUser" cookie is intentionally
        // left intact so the username keeps pre-filling for its full 7 days,
        // which is the whole point of "Remember Me (7 days)".
        request.getSession().invalidate();
        return "redirect:/login";
    }
}

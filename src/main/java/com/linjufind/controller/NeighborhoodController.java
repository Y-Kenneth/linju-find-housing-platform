package com.linjufind.controller;

import com.linjufind.entity.Neighborhood;
import com.linjufind.entity.NeighborhoodRating;
import com.linjufind.entity.NeighborhoodTip;
import com.linjufind.entity.User;
import com.linjufind.pattern.composite.NeighborhoodScoreComposite;
import com.linjufind.service.NeighborhoodService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/neighborhoods")
public class NeighborhoodController {

    private final NeighborhoodService neighborhoodService;

    public NeighborhoodController(NeighborhoodService neighborhoodService) {
        this.neighborhoodService = neighborhoodService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("neighborhoods", neighborhoodService.getAllNeighborhoods());
        return "neighborhood/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable int id, HttpSession session, Model model) {
        Neighborhood neighborhood = neighborhoodService.getNeighborhoodDetail(id);
        if (neighborhood == null) return "redirect:/neighborhoods";

        List<NeighborhoodTip> tips = neighborhoodService.getTips(id);

        User loginUser = (User) session.getAttribute("loginUser");
        NeighborhoodRating userRating = neighborhoodService.getUserRating(id, loginUser.getId());

        // Composite Pattern Implementation — score tree computes overall liveability from individual category scores
        NeighborhoodScoreComposite scoreTree = neighborhoodService.buildScoreTree(neighborhood);

        model.addAttribute("neighborhood", neighborhood);
        model.addAttribute("tips", tips);
        model.addAttribute("userRating", userRating);
        model.addAttribute("scoreTree", scoreTree);
        return "neighborhood/detail";
    }

    @PostMapping("/rate/{id}")
    public String submitRating(@PathVariable int id,
                               @RequestParam int safety,
                               @RequestParam int transport,
                               @RequestParam int foodAccess,
                               @RequestParam int foreignerFriendly,
                               HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if ("admin".equals(loginUser.getRole())) return "redirect:/neighborhoods/" + id;
        neighborhoodService.submitRating(id, loginUser.getId(), safety, transport, foodAccess, foreignerFriendly);
        return "redirect:/neighborhoods/" + id;
    }

    @PostMapping("/tip/{id}")
    public String submitTip(@PathVariable int id,
                            @RequestParam String tipText,
                            HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if ("admin".equals(loginUser.getRole())) return "redirect:/neighborhoods/" + id;
        neighborhoodService.submitTip(id, loginUser.getId(), tipText);
        return "redirect:/neighborhoods/" + id;
    }
}

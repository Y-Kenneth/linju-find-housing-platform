package com.linjufind.pattern.composite;

import java.util.ArrayList;
import java.util.List;

// DESIGN PATTERN: Composite (Structural) — Composite Node
/*
    Represents the overall liveability score for a neighborhood — the root of the score tree.
    It holds child NeighborhoodScoreComponents (the four individual category leaves) and
    computes getScore() as the average of all its children.

    Real-world use in Linju Find:
    When a user views 鼓楼区 (Gulou District, Nanjing), they see four separate scores for
    Safety, Transport, Food Access, and Foreigner-Friendliness. The Composite adds a fifth
    number — "Overall Liveability" — which is automatically the average of those four.

    Without Composite, this average would be calculated as a manual formula scattered
    across the service layer. With Composite, you just call getScore() on the root and
    the tree computes it for you — regardless of how many categories exist.
*/

public class NeighborhoodScoreComposite implements NeighborhoodScoreComponent {
    private final String name;
    private final List<NeighborhoodScoreComponent> children = new ArrayList<>();

    public NeighborhoodScoreComposite(String name) {
        this.name = name;
    }
    public void add(NeighborhoodScoreComponent component) {
        children.add(component);
    }
    @Override
    public String getName() { return name; }

    // Overall score = average of all children
    @Override
    public double getScore() {
        if (children.isEmpty()) return 0.0;
        double sum = 0;
        for (NeighborhoodScoreComponent child : children) {
            sum += child.getScore();
        }
        return Math.round((sum / children.size()) * 10.0) / 10.0;
    }

    public List<NeighborhoodScoreComponent> getChildren() {
        return children;
    }
}

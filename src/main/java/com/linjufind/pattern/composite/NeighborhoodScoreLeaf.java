package com.linjufind.pattern.composite;

// DESIGN PATTERN: Composite (Structural) — Leaf
/*
    Represents one individual score category for a neighborhood:
    e.g. Safety = 4.2, Transport = 3.8, Food Access = 4.5, Foreigner-Friendly = 4.0.
    A leaf has no children — it just holds its own value.
*/

public class NeighborhoodScoreLeaf implements NeighborhoodScoreComponent {

    private final String name;
    private final double score;

    public NeighborhoodScoreLeaf(String name, double score) {
        this.name  = name;
        this.score = score;
    }

    @Override
    public String getName() { return name; }

    @Override
    public double getScore() { return score; }
}

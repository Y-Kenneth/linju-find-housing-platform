package com.linjufind.pattern.composite;

// DESIGN PATTERN: Composite (Structural) — Component interface
/*
    The uniform interface for both a single score (leaf) and a group of scores (composite).
    Calling getScore() works the same way regardless of whether you're asking one category
    or the whole liveability tree.
*/

public interface NeighborhoodScoreComponent {
    String getName();
    double getScore();
}

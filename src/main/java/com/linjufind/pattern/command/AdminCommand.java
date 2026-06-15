package com.linjufind.pattern.command;

// DESIGN PATTERN: Command (Behavioral) — Command interface
/*
    execute() performs the action; undo() reverses it.
    Keeping execute and undo together in one object means
    CommandHistory can undo any action without knowing what kind of action it was.
*/

// Defines the contract for every admin action.
public interface AdminCommand {
    void execute();
    void undo();
    String getDescription();
}

package com.linjufind.pattern.command;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

// DESIGN PATTERN: Command (Behavioral) — Command History / Invoker
/*
    Acts as the invoker. It executes commands and pushes them onto a stack.
    undoLast() pops the most recent command and calls undo() on it.

    The stack is in-memory and scoped to the application lifetime (Spring singleton bean).
    This means undo history is available as long as the server is running — which is
    sufficient for a course project admin panel.
*/

@Component
public class CommandHistory {

    private final Deque<AdminCommand> history = new ArrayDeque<>();

    public void executeCommand(AdminCommand command) {
        command.execute();
        history.push(command);
    }

    public String undoLast() {
        if (history.isEmpty()) {
            return null; // nothing to undo
        }
        AdminCommand last = history.pop();
        last.undo();
        return last.getDescription();
    }

    public boolean hasHistory() {
        return !history.isEmpty();
    }

    public String peekLastDescription() {
        return history.isEmpty() ? null : history.peek().getDescription();
    }

    // Returns descriptions most-recent-first for display in the dashboard timeline
    public java.util.List<String> getHistoryDescriptions() {
        java.util.List<String> descriptions = new java.util.ArrayList<>();
        for (AdminCommand cmd : history) {
            descriptions.add(cmd.getDescription());
        }
        return descriptions;
    }
}

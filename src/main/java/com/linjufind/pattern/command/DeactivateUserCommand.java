package com.linjufind.pattern.command;

import com.linjufind.dao.UserDao;

// DESIGN PATTERN: Command (Behavioral) — Concrete Command 2
/*
    Encapsulates the "deactivate a user" admin action.
    execute() sets role = "deactivated".
    undo()    sets role = "user"         (restores normal access).

    Real-world scenario: the admin deactivates the wrong account — perhaps
    a legitimate Indonesian student who had a username that looked suspicious.
    Undo restores their access in one click.
*/

public class DeactivateUserCommand implements AdminCommand {

    private final UserDao userDao;
    private final int userId;

    public DeactivateUserCommand(UserDao userDao, int userId) {
        this.userDao = userDao;
        this.userId  = userId;
    }

    @Override
    public void execute() {
        userDao.updateRole(userId, "deactivated");
    }

    @Override
    public void undo() {
        userDao.updateRole(userId, "user");
    }

    @Override
    public String getDescription() {
        return "Deactivate user #" + userId;
    }
}

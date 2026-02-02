package com.mch.unicoursehub.model.enums;

/**
 * Enum representing the different roles a user can have in the system.
 *
 * <p>Each role also defines a maximum number of sessions (maxSession) a user with that role
 * can attend or be assigned to.</p>
 */
public enum Role {
    ADMIN(1),
    STUDENT(2),
    PROFESSOR(2);

    private final int maxSession;

    Role(int maxSession) {
        this.maxSession = maxSession;
    }

    public int getMaxSession() {
        return maxSession;
    }
}

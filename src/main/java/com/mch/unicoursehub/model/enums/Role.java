package com.mch.unicoursehub.model.enums;

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

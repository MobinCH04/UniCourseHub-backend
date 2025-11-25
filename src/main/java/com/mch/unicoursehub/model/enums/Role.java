package com.mch.unicoursehub.model.enums;

public enum Role {
    ADMIN(5),
    STUDENT(2),
    PROFESSOR(3);

    private final int maxSession;

    Role(int maxSession) {
        this.maxSession = maxSession;
    }

    public int getMaxSession() {
        return maxSession;
    }
}

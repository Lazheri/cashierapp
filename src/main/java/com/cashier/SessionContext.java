package com.cashier;

import com.cashier.model.User;

public final class SessionContext {

    private static User currentUser;

    private SessionContext() {
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return isLoggedIn() && currentUser.getRole() != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    public static void clear() {
        currentUser = null;
    }
}

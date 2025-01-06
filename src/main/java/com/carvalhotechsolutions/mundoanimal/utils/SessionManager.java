package com.carvalhotechsolutions.mundoanimal.utils;

import com.carvalhotechsolutions.mundoanimal.model.Usuario;

public class SessionManager {

    private static Usuario currentUser;

    public static Usuario getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Usuario user) {
        currentUser = user;
    }
  
    public static void clearSession() {
        currentUser = null;
    }

}

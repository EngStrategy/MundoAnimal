package com.carvalhotechsolutions.mundoanimal.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    private PasswordUtils() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada");
    }

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

}

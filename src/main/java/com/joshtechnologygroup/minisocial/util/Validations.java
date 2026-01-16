package com.joshtechnologygroup.minisocial.util;

public final class Validations {
    public static boolean isPasswordStrong(String password) {
        if (password == null) return true;
        boolean lowercase = false;
        boolean uppercase = false;
        boolean number = false;
        boolean symbol = false;
        for(char ch: password.toCharArray()) {
            if(Character.isUpperCase(ch)) uppercase = true;
            else if (Character.isLowerCase(ch)) lowercase = true;
            else if (Character.isDigit(ch)) number = true;
            else symbol = true;
        }

        return lowercase && uppercase && number && symbol;
    }
}

package com.joshtechnologygroup.minisocial.constants;

public final class ValidationConstants {
    public static final String PHONE_NUMBER_REGEX = "^\\+\\d{12}$";
    public static final String STRONG_PASSWORD_REGEX = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,255}$";
}

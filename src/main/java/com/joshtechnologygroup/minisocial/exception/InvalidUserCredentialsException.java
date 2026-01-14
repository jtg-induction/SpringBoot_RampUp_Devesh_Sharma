package com.joshtechnologygroup.minisocial.exception;

public class InvalidUserCredentialsException extends MiniSocialException {
    public InvalidUserCredentialsException() {
        super("User with provided details not found");
    }
}

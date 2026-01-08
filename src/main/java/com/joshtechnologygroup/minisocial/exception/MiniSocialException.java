package com.joshtechnologygroup.minisocial.exception;

public class MiniSocialException extends RuntimeException {
    public MiniSocialException(String message) {
        super(message);
    }

    public MiniSocialException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.joshtechnologygroup.minisocial.exception;

public class MiniSocialException extends RuntimeException {
    private String message;

    public MiniSocialException() {
    }

    public MiniSocialException(String msg) {
        super(msg);
        message = msg;
    }
}

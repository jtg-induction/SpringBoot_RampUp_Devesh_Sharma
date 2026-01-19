package com.joshtechnologygroup.minisocial.exception;

public class IllegalActionException extends MiniSocialException {
    // The user attempted to perform an action that is not allowed
    public IllegalActionException(String message) {
        super(message);
    }
}

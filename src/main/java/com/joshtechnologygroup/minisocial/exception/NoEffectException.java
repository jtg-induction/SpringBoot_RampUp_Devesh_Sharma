package com.joshtechnologygroup.minisocial.exception;

public class NoEffectException extends MiniSocialException {
    // User performing an action that has no effect or was redundant
    public NoEffectException() {
        super("User is already followed.");
    }
}

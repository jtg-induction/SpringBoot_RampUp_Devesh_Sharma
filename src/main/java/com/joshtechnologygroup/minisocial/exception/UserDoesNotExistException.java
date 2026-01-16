package com.joshtechnologygroup.minisocial.exception;

public class UserDoesNotExistException extends MiniSocialException {
    public UserDoesNotExistException() {
        super("The user you attempted to access does not exist in the database");
    }
}

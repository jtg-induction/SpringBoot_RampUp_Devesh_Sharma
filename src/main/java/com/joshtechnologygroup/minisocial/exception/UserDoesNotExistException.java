package com.joshtechnologygroup.minisocial.exception;

public class UserDoesNotExistException extends MiniSocialException {
    public UserDoesNotExistException() {
        super("THe user you attempted to access does not exist in the database");
    }
}

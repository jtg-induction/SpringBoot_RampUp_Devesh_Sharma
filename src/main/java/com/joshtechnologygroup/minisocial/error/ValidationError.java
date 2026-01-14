package com.joshtechnologygroup.minisocial.error;

public record ValidationError(
        String name,
        String message
) {}

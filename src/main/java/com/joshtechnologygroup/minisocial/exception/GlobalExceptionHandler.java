package com.joshtechnologygroup.minisocial.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Class with handlers for custom exceptions
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserDoesNotExistException.class)
    public ProblemDetail handleUserDoesNotExistException(
            UserDoesNotExistException e
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
        problemDetail.setTitle("User Not Found");

        return problemDetail;
    }

    @ExceptionHandler(InvalidUserCredentialsException.class)
    public ProblemDetail handleInvalidUserCredentialsException(
            InvalidUserCredentialsException e
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                e.getMessage()
        );
        problemDetail.setTitle("Invalid username or password");

        return problemDetail;
    }

    // Default Exception Handler
    @ExceptionHandler(MiniSocialException.class)
    public ProblemDetail handleMiniSocialException(MiniSocialException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage()
        );
        problemDetail.setTitle("Internal Server Error");

        return problemDetail;
    }
}

package com.joshtechnologygroup.minisocial.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorizedException(
            UnauthorizedException e
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                e.getMessage()
        );
        problemDetail.setTitle("Access Denied");

        return problemDetail;
    }

    // Validation Exception Handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        // Create a standard RFC 7807 ProblemDetail object
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_CONTENT);
        problemDetail.setTitle("Validation Failed");
        problemDetail.setDetail("One or more fields in the request are invalid.");

        // Extract each specific field error and message
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        // Add the field-level errors as custom properties
        problemDetail.setProperty("invalid_params", errors);

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

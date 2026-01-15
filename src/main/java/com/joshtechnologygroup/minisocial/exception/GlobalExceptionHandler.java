package com.joshtechnologygroup.minisocial.exception;

import com.joshtechnologygroup.minisocial.error.ValidationError;
import com.joshtechnologygroup.minisocial.error.ValidationProblemDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.ArrayList;
import java.util.List;

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
    @ExceptionHandler(ValueConflictException.class)
    public ProblemDetail handleValueConflictException(
            ValueConflictException e
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                e.getMessage()
        );
        problemDetail.setTitle("Value Conflict");

        return problemDetail;
    }

    // Validation Exception Handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationProblemDetail handleBodyValidation(MethodArgumentNotValidException ex) {

        List<ValidationError> errors =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(err -> new ValidationError(
                                err.getField(),
                                err.getDefaultMessage()
                        ))
                        .toList();

        return new ValidationProblemDetail(errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ValidationProblemDetail handleMethodValidation(
            HandlerMethodValidationException ex
    ) {

        List<ValidationError> errors = new ArrayList<>();

        ex.getParameterValidationResults().forEach(result -> {
            String paramName = result.getMethodParameter().getParameterName();

            result.getResolvableErrors().forEach(error ->
                    errors.add(new ValidationError(
                            paramName,
                            error.getDefaultMessage()
                    ))
            );
        });

        return new ValidationProblemDetail(errors);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex
    ) {
        String message = ex.getMessage();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                message
        );
        problemDetail.setTitle("Invalid Request Body - Deserialization Failed");

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

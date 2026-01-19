package com.joshtechnologygroup.minisocial.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.List;

@Getter
public class ValidationProblemDetail extends ProblemDetail {

    private final List<ValidationError> validationErrors;

    public ValidationProblemDetail(List<ValidationError> validationErrors) {
        super();
        this.validationErrors = validationErrors;

        setStatus(HttpStatus.UNPROCESSABLE_CONTENT);
        setTitle("Validation Failed");
        setDetail("One or more request parameters are invalid.");
    }
}

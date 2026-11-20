package com.chx.claims.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalTransitionException.class)
    ProblemDetail transition(IllegalTransitionException ex) { return problem(HttpStatus.CONFLICT, "Illegal Claim Transition", ex.getMessage()); }
    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail notFound(ResourceNotFoundException ex) { return problem(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage()); }
    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail badRequest(IllegalArgumentException ex) { return problem(HttpStatus.BAD_REQUEST, "Invalid Request", ex.getMessage()); }
    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(status, detail); p.setTitle(title); return p;
    }
}

package com.example.test.exception.controller;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.example.test.exception.MemberException;
import com.example.test.exception.status.MemberStatus;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import jakarta.validation.ConstraintViolation;


import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatus status,
                                                                  final WebRequest request) {
        final List<String> messages = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("Method argument not valid exception occurrence: {}", messages);

        return ResponseEntity.badRequest().body(new ExceptionResponse(messages));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException ex) {
        final List<String> messages = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        log.warn("Constraint violation exception occurrence: {}", messages);

        return ResponseEntity.badRequest().body(new ExceptionResponse(messages));
    }

    @ExceptionHandler({JWTCreationException.class})
    public ResponseEntity<Object> handleJWTCreationException(final JWTCreationException ex) {
        final String message = ex.getMessage();

        log.warn("JWT creation exception occurrence: {}", message);

        return ResponseEntity.internalServerError().body(new ExceptionResponse(List.of(message)));
    }

    @ExceptionHandler({MemberException.class})
    public ResponseEntity<Object> handleMemberException(final MemberException ex) {
        final MemberStatus status = ex.getStatus();

        log.warn("Member exception occurrence: {}", status.getMessage());

        return ResponseEntity.status(status.getHttpStatus()).body(new ExceptionResponse(List.of(status.getMessage())));
    }

    @RequiredArgsConstructor
    @Getter
    public static class ExceptionResponse {

        private final List<String> messages;

    }

}
package com.example.test.exception.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum MemberStatus {

    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "must be a correct password"),
    NOT_EXISTING_EMAIL(HttpStatus.NOT_FOUND, "must be an existing email"),
    EXISTING_EMAIL(HttpStatus.CONFLICT, "must not be an existing email");

    private final HttpStatus httpStatus;
    private final String message;

}
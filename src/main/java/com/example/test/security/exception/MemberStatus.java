package com.example.test.security.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {
    EXISTING_NICKNAME(HttpStatus.CONFLICT, "must be a unique nickname"),
    NOT_EXISTING_PROFILE(HttpStatus.NOT_FOUND, "must be an existing profile"),
    NOT_EXISTING_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "must have a content type"),
    NOT_SUPPORTING_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "must be a proper content type");

    private final HttpStatus httpStatus;
    private final String message;
}
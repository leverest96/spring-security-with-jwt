package com.example.test.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ExceptionResponse {
    private final List<String> messages;
}
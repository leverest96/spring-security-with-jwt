package com.example.test.exception;

import com.example.test.exception.status.MemberStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberException extends RuntimeException {
    private final MemberStatus status;
}
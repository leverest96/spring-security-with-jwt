package com.example.test.security.etc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {

    USER("사용자"),
    ADMIN("관리자");

    private final String displayName;

}
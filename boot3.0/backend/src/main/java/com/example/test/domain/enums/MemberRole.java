package com.example.test.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    MEMBER("member"), ADMIN("admin");

    private final String roleName;
}

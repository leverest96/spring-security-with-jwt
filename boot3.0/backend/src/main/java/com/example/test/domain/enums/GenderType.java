package com.example.test.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GenderType {
    MAN("man"), WOMAN("woman"), BLANK("blank");

    private final String genderName;
}

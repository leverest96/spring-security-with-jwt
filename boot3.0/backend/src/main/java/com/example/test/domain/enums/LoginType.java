package com.example.test.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {
    GOOGLE("google"), NAVER("naver"), KAKAO("kakao"), NONE("none");

    private final String socialName;
}
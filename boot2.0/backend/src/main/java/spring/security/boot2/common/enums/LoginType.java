package spring.security.boot2.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {
    GOOGLE("google"), NAVER("naver"), KAKAO("kakao");

    private final String socialName;
}
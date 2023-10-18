package spring.security.boot2.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialType{
    GOOGLE("google"), NAVER("naver"), KAKAO("kakao");

    private final String socialName;
}
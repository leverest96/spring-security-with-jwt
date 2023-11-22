package com.example.test.properties.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt.refresh-token")
public class RefreshTokenProperties extends JwtProperties {
    public static final String COOKIE_NAME = "refresh_token";

    public RefreshTokenProperties(final String secretKey, final int validSeconds) {
        super(secretKey, validSeconds);
    }

    @RequiredArgsConstructor
    @Getter
    public enum RefreshTokenClaim {
        MEMBER_ID("memberId");

        private final String claim;
    }
}
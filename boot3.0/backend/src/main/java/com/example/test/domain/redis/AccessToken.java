package com.example.test.domain.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@Getter
@RedisHash(value = "access_token", timeToLive = 60*60*30)
public class AccessToken {
    public static final String ACCESS_TOKEN_KEY = "access_token";

    @Id
    private String id;

    private String accessToken;
}

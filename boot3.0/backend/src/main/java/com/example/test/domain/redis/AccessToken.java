package com.example.test.domain.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@AllArgsConstructor
@Getter
@RedisHash(value = "jwtAccessToken", timeToLive = 60*60*30)
public class AccessToken {
    @Id
    private long id;

    private String accessToken;

    @Indexed
    private long memberId;
}
